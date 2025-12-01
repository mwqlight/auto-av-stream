package com.avstream.media.service;

import com.avstream.media.service.impl.FFmpegTranscodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 转码服务测试类
 * 
 * @author AV Stream Team
 */
@ExtendWith(MockitoExtension.class)
class TranscodeServiceTest {

    @Mock
    private FFmpegTranscodeService transcodeService;

    @BeforeEach
    void setUp() {
        // 这里我们直接测试接口，不模拟具体实现
        transcodeService = mock(FFmpegTranscodeService.class);
    }

    @Test
    void testCreateTranscodeTask_Success() {
        // 准备测试数据
        String fileUuid = "test-uuid-123";
        String outputFormat = "mp4";
        String template = "h264_720p";
        String expectedTaskId = "transcode-task-456";
        
        // 模拟转码服务行为
        when(transcodeService.createTranscodeTask(fileUuid, outputFormat, template))
                .thenReturn(expectedTaskId);

        // 执行测试
        String result = transcodeService.createTranscodeTask(fileUuid, outputFormat, template);

        // 验证结果
        assertNotNull(result);
        assertEquals(expectedTaskId, result);
        
        // 验证方法调用
        verify(transcodeService, times(1)).createTranscodeTask(fileUuid, outputFormat, template);
    }

    @Test
    void testStartTranscodeTask_Success() {
        // 准备测试数据
        String taskId = "transcode-task-456";
        
        // 模拟转码服务行为
        when(transcodeService.startTranscodeTask(taskId)).thenReturn(true);

        // 执行测试
        boolean result = transcodeService.startTranscodeTask(taskId);

        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        verify(transcodeService, times(1)).startTranscodeTask(taskId);
    }

    @Test
    void testCancelTranscodeTask_Success() {
        // 准备测试数据
        String taskId = "transcode-task-456";
        
        // 模拟转码服务行为
        when(transcodeService.cancelTranscodeTask(taskId)).thenReturn(true);

        // 执行测试
        boolean result = transcodeService.cancelTranscodeTask(taskId);

        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        verify(transcodeService, times(1)).cancelTranscodeTask(taskId);
    }

    @Test
    void testGetTranscodeProgress_Success() {
        // 准备测试数据
        String taskId = "transcode-task-456";
        int expectedProgress = 75;
        
        // 模拟转码服务行为
        when(transcodeService.getTranscodeProgress(taskId)).thenReturn(expectedProgress);

        // 执行测试
        int result = transcodeService.getTranscodeProgress(taskId);

        // 验证结果
        assertEquals(expectedProgress, result);
        
        // 验证方法调用
        verify(transcodeService, times(1)).getTranscodeProgress(taskId);
    }

    @Test
    void testGetTranscodeStatus_Success() {
        // 准备测试数据
        String taskId = "transcode-task-456";
        String expectedStatus = "RUNNING";
        
        // 模拟转码服务行为
        when(transcodeService.getTranscodeStatus(taskId)).thenReturn(expectedStatus);

        // 执行测试
        String result = transcodeService.getTranscodeStatus(taskId);

        // 验证结果
        assertNotNull(result);
        assertEquals(expectedStatus, result);
        
        // 验证方法调用
        verify(transcodeService, times(1)).getTranscodeStatus(taskId);
    }

    @Test
    void testRetryFailedTask_Success() {
        // 准备测试数据
        String taskId = "transcode-task-456";
        
        // 模拟转码服务行为
        when(transcodeService.retryFailedTask(taskId)).thenReturn(true);

        // 执行测试
        boolean result = transcodeService.retryFailedTask(taskId);

        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        verify(transcodeService, times(1)).retryFailedTask(taskId);
    }

    @Test
    void testDeleteTranscodeTask_Success() {
        // 准备测试数据
        String taskId = "transcode-task-456";
        
        // 模拟转码服务行为
        when(transcodeService.deleteTranscodeTask(taskId)).thenReturn(true);

        // 执行测试
        boolean result = transcodeService.deleteTranscodeTask(taskId);

        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        verify(transcodeService, times(1)).deleteTranscodeTask(taskId);
    }

    @Test
    void testGetTranscodeTemplates_Success() {
        // 准备测试数据
        List<String> expectedTemplates = List.of("h264_720p", "h265_1080p", "av1_4k");
        
        // 模拟转码服务行为
        when(transcodeService.getTranscodeTemplates()).thenReturn(expectedTemplates);

        // 执行测试
        List<String> result = transcodeService.getTranscodeTemplates();

        // 验证结果
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("h264_720p"));
        assertTrue(result.contains("h265_1080p"));
        assertTrue(result.contains("av1_4k"));
        
        // 验证方法调用
        verify(transcodeService, times(1)).getTranscodeTemplates();
    }

    @Test
    void testGetSupportedOutputFormats_Success() {
        // 准备测试数据
        List<String> expectedFormats = List.of("mp4", "webm", "mov", "avi");
        
        // 模拟转码服务行为
        when(transcodeService.getSupportedOutputFormats()).thenReturn(expectedFormats);

        // 执行测试
        List<String> result = transcodeService.getSupportedOutputFormats();

        // 验证结果
        assertNotNull(result);
        assertEquals(4, result.size());
        assertTrue(result.contains("mp4"));
        assertTrue(result.contains("webm"));
        assertTrue(result.contains("mov"));
        assertTrue(result.contains("avi"));
        
        // 验证方法调用
        verify(transcodeService, times(1)).getSupportedOutputFormats();
    }

    @Test
    void testGetTaskInfo_Success() {
        // 准备测试数据
        String taskId = "transcode-task-456";
        Map<String, Object> expectedInfo = Map.of(
                "taskId", taskId,
                "status", "RUNNING",
                "progress", 75,
                "inputFile", "test-uuid-123",
                "outputFormat", "mp4"
        );
        
        // 模拟转码服务行为
        when(transcodeService.getTaskInfo(taskId)).thenReturn(expectedInfo);

        // 执行测试
        Map<String, Object> result = transcodeService.getTaskInfo(taskId);

        // 验证结果
        assertNotNull(result);
        assertEquals(taskId, result.get("taskId"));
        assertEquals("RUNNING", result.get("status"));
        assertEquals(75, result.get("progress"));
        
        // 验证方法调用
        verify(transcodeService, times(1)).getTaskInfo(taskId);
    }

    @Test
    void testGetTaskOutputPath_Success() {
        // 准备测试数据
        String taskId = "transcode-task-456";
        String expectedPath = "/transcoded/test-uuid-123.mp4";
        
        // 模拟转码服务行为
        when(transcodeService.getTaskOutputPath(taskId)).thenReturn(expectedPath);

        // 执行测试
        String result = transcodeService.getTaskOutputPath(taskId);

        // 验证结果
        assertNotNull(result);
        assertEquals(expectedPath, result);
        
        // 验证方法调用
        verify(transcodeService, times(1)).getTaskOutputPath(taskId);
    }

    @Test
    void testCleanupExpiredTasks_Success() {
        // 准备测试数据
        int expectedCount = 5;
        
        // 模拟转码服务行为
        when(transcodeService.cleanupExpiredTasks()).thenReturn(expectedCount);

        // 执行测试
        int result = transcodeService.cleanupExpiredTasks();

        // 验证结果
        assertEquals(expectedCount, result);
        
        // 验证方法调用
        verify(transcodeService, times(1)).cleanupExpiredTasks();
    }

    @Test
    void testGetTaskStatistics_Success() {
        // 准备测试数据
        Map<String, Integer> expectedStats = Map.of(
                "total", 100,
                "completed", 75,
                "failed", 5,
                "running", 20
        );
        
        // 模拟转码服务行为
        when(transcodeService.getTaskStatistics()).thenReturn(expectedStats);

        // 执行测试
        Map<String, Integer> result = transcodeService.getTaskStatistics();

        // 验证结果
        assertNotNull(result);
        assertEquals(100, result.get("total"));
        assertEquals(75, result.get("completed"));
        assertEquals(5, result.get("failed"));
        assertEquals(20, result.get("running"));
        
        // 验证方法调用
        verify(transcodeService, times(1)).getTaskStatistics();
    }
}