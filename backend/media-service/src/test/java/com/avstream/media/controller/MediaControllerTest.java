package com.avstream.media.controller;

import com.avstream.media.entity.MediaFile;
import com.avstream.media.service.MediaService;
import com.avstream.media.service.StorageService;
import com.avstream.media.service.TranscodeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 媒体控制器集成测试类
 * 
 * @author AV Stream Team
 */
@WebMvcTest(MediaController.class)
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaService mediaService;

    @MockBean
    private StorageService storageService;

    @MockBean
    private TranscodeService transcodeService;

    @Autowired
    private ObjectMapper objectMapper;

    private MediaFile testMediaFile;

    @BeforeEach
    void setUp() {
        testMediaFile = new MediaFile();
        testMediaFile.setFileUuid("test-uuid-123");
        testMediaFile.setFilename("test-video.mp4");
        testMediaFile.setFileSize(1024L * 1024L); // 1MB
        testMediaFile.setFileType("VIDEO");
        testMediaFile.setUserId(1L);
        testMediaFile.setStatus(MediaFile.FileStatus.ACTIVE);
    }

    @Test
    void testUploadFile_Success() throws Exception {
        // 准备测试数据
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-video.mp4",
                "video/mp4",
                "test video content".getBytes()
        );

        // 模拟服务行为
        when(mediaService.uploadFile(any(), eq(1L), eq("测试视频")))
                .thenReturn(testMediaFile);

        // 执行测试
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/media/upload")
                        .file(file)
                        .param("userId", "1")
                        .param("description", "测试视频")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.fileUuid").value("test-uuid-123"))
                .andExpect(jsonPath("$.data.filename").value("test-video.mp4"));
    }

    @Test
    void testUploadChunk_Success() throws Exception {
        // 准备测试数据
        MockMultipartFile chunk = new MockMultipartFile(
                "chunk",
                "chunk-1",
                "application/octet-stream",
                "chunk content".getBytes()
        );

        // 模拟服务行为
        when(storageService.uploadChunk(any(), eq("test-uuid-123"), eq(1)))
                .thenReturn(true);

        // 执行测试
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/media/upload-chunk")
                        .file(chunk)
                        .param("fileUuid", "test-uuid-123")
                        .param("chunkNumber", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testMergeChunks_Success() throws Exception {
        // 模拟服务行为
        when(storageService.mergeChunks(eq("test-uuid-123"), eq(3)))
                .thenReturn(true);

        // 执行测试
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/media/merge-chunks")
                        .param("fileUuid", "test-uuid-123")
                        .param("totalChunks", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testGetMediaFile_Success() throws Exception {
        // 模拟服务行为
        when(mediaService.getMediaFile(eq("test-uuid-123"), eq(1L)))
                .thenReturn(testMediaFile);

        // 执行测试
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/media/{fileUuid}", "test-uuid-123")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.fileUuid").value("test-uuid-123"))
                .andExpect(jsonPath("$.data.filename").value("test-video.mp4"));
    }

    @Test
    void testUpdateMediaInfo_Success() throws Exception {
        // 准备测试数据
        MediaController.UpdateMediaInfoRequest request = new MediaController.UpdateMediaInfoRequest();
        request.setDescription("更新后的描述");
        request.setVisibility("PUBLIC");

        // 模拟服务行为
        when(mediaService.updateMediaInfo(eq("test-uuid-123"), eq(1L), any(), any()))
                .thenReturn(testMediaFile);

        // 执行测试
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/media/{fileUuid}", "test-uuid-123")
                        .param("userId", "1")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.fileUuid").value("test-uuid-123"));
    }

    @Test
    void testDeleteMediaFile_Success() throws Exception {
        // 模拟服务行为
        when(mediaService.deleteMediaFile(eq("test-uuid-123"), eq(1L)))
                .thenReturn(true);

        // 执行测试
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/media/{fileUuid}", "test-uuid-123")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testPermanentlyDeleteMediaFile_Success() throws Exception {
        // 模拟服务行为
        when(mediaService.permanentlyDeleteMediaFile(eq("test-uuid-123"), eq(1L)))
                .thenReturn(true);

        // 执行测试
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/media/{fileUuid}/permanent", "test-uuid-123")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testListUserMediaFiles_Success() throws Exception {
        // 准备测试数据
        List<MediaFile> mediaFiles = Arrays.asList(testMediaFile);

        // 模拟服务行为
        when(mediaService.listUserMediaFiles(eq(1L), eq(0), eq(10), any(), any()))
                .thenReturn(mediaFiles);

        // 执行测试
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/media/user/{userId}", "1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].fileUuid").value("test-uuid-123"));
    }

    @Test
    void testGeneratePreviewUrl_Success() throws Exception {
        // 模拟服务行为
        when(mediaService.generatePreviewUrl(eq("test-uuid-123"), eq(1L)))
                .thenReturn("http://minio.example.com/preview/test-uuid-123");

        // 执行测试
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/media/{fileUuid}/preview", "test-uuid-123")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("http://minio.example.com/preview/test-uuid-123"));
    }

    @Test
    void testGeneratePlayUrl_Success() throws Exception {
        // 模拟服务行为
        when(mediaService.generatePlayUrl(eq("test-uuid-123"), eq(1L)))
                .thenReturn("http://minio.example.com/play/test-uuid-123");

        // 执行测试
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/media/{fileUuid}/play", "test-uuid-123")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("http://minio.example.com/play/test-uuid-123"));
    }

    @Test
    void testGenerateDownloadUrl_Success() throws Exception {
        // 模拟服务行为
        when(mediaService.generateDownloadUrl(eq("test-uuid-123"), eq(1L)))
                .thenReturn("http://minio.example.com/download/test-uuid-123");

        // 执行测试
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/media/{fileUuid}/download", "test-uuid-123")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("http://minio.example.com/download/test-uuid-123"));
    }

    @Test
    void testCalculateStorageUsage_Success() throws Exception {
        // 准备测试数据
        MediaService.StorageUsage storageUsage = new MediaService.StorageUsage(1L, 1024L * 1024L * 100L, 1024L * 1024L * 1024L * 10L);

        // 模拟服务行为
        when(mediaService.calculateUserStorageUsage(eq(1L)))
                .thenReturn(storageUsage);

        // 执行测试
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/media/user/{userId}/storage-usage", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.usedStorage").value(1024L * 1024L * 100L))
                .andExpect(jsonPath("$.data.totalStorage").value(1024L * 1024L * 1024L * 10L));
    }

    @Test
    void testCreateTranscodeTask_Success() throws Exception {
        // 模拟服务行为
        when(transcodeService.createTranscodeTask(eq("test-uuid-123"), eq("mp4"), eq("h264_720p")))
                .thenReturn("transcode-task-456");

        // 执行测试
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/media/{fileUuid}/transcode", "test-uuid-123")
                        .param("outputFormat", "mp4")
                        .param("template", "h264_720p"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("transcode-task-456"));
    }

    @Test
    void testGetTranscodeProgress_Success() throws Exception {
        // 模拟服务行为
        when(transcodeService.getTranscodeProgress(eq("transcode-task-456")))
                .thenReturn(75);

        // 执行测试
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/media/transcode/{taskId}/progress", "transcode-task-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(75));
    }

    @Test
    void testGetTranscodeStatus_Success() throws Exception {
        // 模拟服务行为
        when(transcodeService.getTranscodeStatus(eq("transcode-task-456")))
                .thenReturn("RUNNING");

        // 执行测试
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/media/transcode/{taskId}/status", "transcode-task-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("RUNNING"));
    }

    @Test
    void testCancelTranscodeTask_Success() throws Exception {
        // 模拟服务行为
        when(transcodeService.cancelTranscodeTask(eq("transcode-task-456")))
                .thenReturn(true);

        // 执行测试
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/media/transcode/{taskId}", "transcode-task-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
}