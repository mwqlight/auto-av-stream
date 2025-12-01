package com.avstream.media.service;

import com.avstream.media.entity.MediaFile;
import com.avstream.media.repository.MediaFileRepository;
import com.avstream.media.service.impl.MediaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 媒体服务测试类
 * 
 * @author AV Stream Team
 */
@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private MediaFileRepository mediaFileRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private TranscodeService transcodeService;

    @InjectMocks
    private MediaServiceImpl mediaService;

    @Mock
    private MultipartFile multipartFile;

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
    void testUploadFile_Success() {
        // 准备测试数据
        when(multipartFile.getOriginalFilename()).thenReturn("test-video.mp4");
        when(multipartFile.getSize()).thenReturn(1024L * 1024L);
        when(multipartFile.getContentType()).thenReturn("video/mp4");
        when(storageService.uploadFile(any(), any())).thenReturn("test-object-path");
        when(mediaFileRepository.save(any(MediaFile.class))).thenReturn(testMediaFile);

        // 执行测试
        MediaFile result = mediaService.uploadFile(multipartFile, 1L, "测试视频");

        // 验证结果
        assertNotNull(result);
        assertEquals("test-video.mp4", result.getFilename());
        assertEquals(MediaFile.FileStatus.ACTIVE, result.getStatus());
        
        // 验证方法调用
        verify(storageService, times(1)).uploadFile(any(), any());
        verify(mediaFileRepository, times(1)).save(any(MediaFile.class));
    }

    @Test
    void testGetMediaFile_Success() {
        // 准备测试数据
        when(mediaFileRepository.findByFileUuid("test-uuid-123"))
                .thenReturn(Optional.of(testMediaFile));

        // 执行测试
        MediaFile result = mediaService.getMediaFile("test-uuid-123", 1L);

        // 验证结果
        assertNotNull(result);
        assertEquals("test-uuid-123", result.getFileUuid());
        assertEquals("test-video.mp4", result.getFilename());
        
        // 验证方法调用
        verify(mediaFileRepository, times(1)).findByFileUuid("test-uuid-123");
    }

    @Test
    void testGetMediaFile_NotFound() {
        // 准备测试数据
        when(mediaFileRepository.findByFileUuid("non-existent-uuid"))
                .thenReturn(Optional.empty());

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> {
            mediaService.getMediaFile("non-existent-uuid", 1L);
        });

        // 验证方法调用
        verify(mediaFileRepository, times(1)).findByFileUuid("non-existent-uuid");
    }

    @Test
    void testDeleteMediaFile_Success() {
        // 准备测试数据
        when(mediaFileRepository.findByFileUuid("test-uuid-123"))
                .thenReturn(Optional.of(testMediaFile));

        // 执行测试
        boolean result = mediaService.deleteMediaFile("test-uuid-123", 1L);

        // 验证结果
        assertTrue(result);
        assertEquals(MediaFile.FileStatus.DELETED, testMediaFile.getStatus());
        
        // 验证方法调用
        verify(mediaFileRepository, times(1)).findByFileUuid("test-uuid-123");
        verify(mediaFileRepository, times(1)).save(testMediaFile);
        verify(storageService, times(1)).deleteFile(any());
    }

    @Test
    void testDeleteMediaFile_NotFound() {
        // 准备测试数据
        when(mediaFileRepository.findByFileUuid("non-existent-uuid"))
                .thenReturn(Optional.empty());

        // 执行测试
        boolean result = mediaService.deleteMediaFile("non-existent-uuid", 1L);

        // 验证结果
        assertFalse(result);
        
        // 验证方法调用
        verify(mediaFileRepository, times(1)).findByFileUuid("non-existent-uuid");
        verify(mediaFileRepository, never()).save(any());
        verify(storageService, never()).deleteFile(any());
    }

    @Test
    void testPermanentlyDeleteMediaFile_Success() {
        // 准备测试数据
        testMediaFile.setStatus(MediaFile.FileStatus.DELETED);
        when(mediaFileRepository.findByFileUuid("test-uuid-123"))
                .thenReturn(Optional.of(testMediaFile));

        // 执行测试
        boolean result = mediaService.permanentlyDeleteMediaFile("test-uuid-123", 1L);

        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        verify(mediaFileRepository, times(1)).findByFileUuid("test-uuid-123");
        verify(mediaFileRepository, times(1)).delete(testMediaFile);
        verify(storageService, times(1)).deleteFile(any());
    }

    @Test
    void testCalculateUserStorageUsage_Success() {
        // 准备测试数据
        when(mediaFileRepository.calculateStorageUsageByUserId(1L))
                .thenReturn(1024L * 1024L * 100L); // 100MB

        // 执行测试
        MediaService.StorageUsage result = mediaService.calculateUserStorageUsage(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(1024L * 1024L * 100L, result.getUsedStorage());
        assertEquals(1024L * 1024L * 1024L * 10L, result.getTotalStorage()); // 10GB
        
        // 验证方法调用
        verify(mediaFileRepository, times(1)).calculateStorageUsageByUserId(1L);
    }

    @Test
    void testIsFileAccessible_OwnerAccess() {
        // 准备测试数据
        testMediaFile.setUserId(1L);

        // 执行测试
        boolean result = mediaService.isFileAccessible(testMediaFile, 1L);

        // 验证结果
        assertTrue(result);
    }

    @Test
    void testIsFileAccessible_PublicFile() {
        // 准备测试数据
        testMediaFile.setUserId(2L);
        testMediaFile.setVisibility(MediaFile.FileVisibility.PUBLIC);

        // 执行测试
        boolean result = mediaService.isFileAccessible(testMediaFile, 1L);

        // 验证结果
        assertTrue(result);
    }

    @Test
    void testIsFileAccessible_PrivateFile_NoAccess() {
        // 准备测试数据
        testMediaFile.setUserId(2L);
        testMediaFile.setVisibility(MediaFile.FileVisibility.PRIVATE);

        // 执行测试
        boolean result = mediaService.isFileAccessible(testMediaFile, 1L);

        // 验证结果
        assertFalse(result);
    }

    @Test
    void testGeneratePreviewUrl_Success() {
        // 准备测试数据
        when(mediaFileRepository.findByFileUuid("test-uuid-123"))
                .thenReturn(Optional.of(testMediaFile));
        when(storageService.generatePreviewUrl("test-uuid-123"))
                .thenReturn("http://minio.example.com/preview/test-uuid-123");

        // 执行测试
        String result = mediaService.generatePreviewUrl("test-uuid-123", 1L);

        // 验证结果
        assertNotNull(result);
        assertEquals("http://minio.example.com/preview/test-uuid-123", result);
        
        // 验证方法调用
        verify(mediaFileRepository, times(1)).findByFileUuid("test-uuid-123");
        verify(storageService, times(1)).generatePreviewUrl("test-uuid-123");
    }

    @Test
    void testGeneratePlayUrl_Success() {
        // 准备测试数据
        when(mediaFileRepository.findByFileUuid("test-uuid-123"))
                .thenReturn(Optional.of(testMediaFile));
        when(storageService.generatePlayUrl("test-uuid-123"))
                .thenReturn("http://minio.example.com/play/test-uuid-123");

        // 执行测试
        String result = mediaService.generatePlayUrl("test-uuid-123", 1L);

        // 验证结果
        assertNotNull(result);
        assertEquals("http://minio.example.com/play/test-uuid-123", result);
        
        // 验证方法调用
        verify(mediaFileRepository, times(1)).findByFileUuid("test-uuid-123");
        verify(storageService, times(1)).generatePlayUrl("test-uuid-123");
    }

    @Test
    void testGenerateDownloadUrl_Success() {
        // 准备测试数据
        when(mediaFileRepository.findByFileUuid("test-uuid-123"))
                .thenReturn(Optional.of(testMediaFile));
        when(storageService.generateDownloadUrl("test-uuid-123"))
                .thenReturn("http://minio.example.com/download/test-uuid-123");

        // 执行测试
        String result = mediaService.generateDownloadUrl("test-uuid-123", 1L);

        // 验证结果
        assertNotNull(result);
        assertEquals("http://minio.example.com/download/test-uuid-123", result);
        
        // 验证方法调用
        verify(mediaFileRepository, times(1)).findByFileUuid("test-uuid-123");
        verify(storageService, times(1)).generateDownloadUrl("test-uuid-123");
    }
}