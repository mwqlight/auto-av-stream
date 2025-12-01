package com.avstream.media.service;

import com.avstream.media.service.impl.MinioStorageService;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 存储服务测试类
 * 
 * @author AV Stream Team
 */
@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private GetObjectResponse getObjectResponse;

    private MinioStorageService storageService;

    @BeforeEach
    void setUp() {
        storageService = new MinioStorageService(minioClient);
    }

    @Test
    void testUploadFile_Success() throws Exception {
        // 准备测试数据
        String fileUuid = "test-uuid-123";
        byte[] fileContent = "test file content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(fileContent);
        
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(multipartFile.getSize()).thenReturn((long) fileContent.length);
        when(multipartFile.getContentType()).thenReturn("text/plain");
        
        // 模拟MinIO客户端行为
        doNothing().when(minioClient).putObject(any(PutObjectArgs.class));

        // 执行测试
        String result = storageService.uploadFile(multipartFile, fileUuid);

        // 验证结果
        assertNotNull(result);
        assertEquals(fileUuid, result);
        
        // 验证方法调用
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    void testUploadChunk_Success() throws Exception {
        // 准备测试数据
        String fileUuid = "test-uuid-123";
        int chunkNumber = 1;
        byte[] chunkContent = "chunk content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(chunkContent);
        
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(multipartFile.getSize()).thenReturn((long) chunkContent.length);
        
        // 模拟MinIO客户端行为
        doNothing().when(minioClient).putObject(any(PutObjectArgs.class));

        // 执行测试
        boolean result = storageService.uploadChunk(multipartFile, fileUuid, chunkNumber);

        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    void testMergeChunks_Success() throws Exception {
        // 准备测试数据
        String fileUuid = "test-uuid-123";
        int totalChunks = 3;
        
        // 模拟MinIO客户端行为
        when(minioClient.listObjects(any(ListObjectsArgs.class)))
                .thenReturn(mock(Iterable.class));
        doNothing().when(minioClient).putObject(any(PutObjectArgs.class));

        // 执行测试
        boolean result = storageService.mergeChunks(fileUuid, totalChunks);

        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        verify(minioClient, times(1)).listObjects(any(ListObjectsArgs.class));
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    void testDownloadFile_Success() throws Exception {
        // 准备测试数据
        String fileUuid = "test-uuid-123";
        
        // 模拟MinIO客户端行为
        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenReturn(getObjectResponse);

        // 执行测试
        InputStream result = storageService.downloadFile(fileUuid);

        // 验证结果
        assertNotNull(result);
        assertEquals(getObjectResponse, result);
        
        // 验证方法调用
        verify(minioClient, times(1)).getObject(any(GetObjectArgs.class));
    }

    @Test
    void testDeleteFile_Success() throws Exception {
        // 准备测试数据
        String fileUuid = "test-uuid-123";
        
        // 模拟MinIO客户端行为
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        // 执行测试
        boolean result = storageService.deleteFile(fileUuid);

        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        verify(minioClient, times(1)).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void testCancelUpload_Success() throws Exception {
        // 准备测试数据
        String fileUuid = "test-uuid-123";
        
        // 模拟MinIO客户端行为
        when(minioClient.listObjects(any(ListObjectsArgs.class)))
                .thenReturn(mock(Iterable.class));
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        // 执行测试
        boolean result = storageService.cancelUpload(fileUuid);

        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        verify(minioClient, times(1)).listObjects(any(ListObjectsArgs.class));
    }

    @Test
    void testGeneratePreviewUrl_Success() throws Exception {
        // 准备测试数据
        String fileUuid = "test-uuid-123";
        String expectedUrl = "http://minio.example.com/preview/test-uuid-123";
        
        // 模拟MinIO客户端行为
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn(expectedUrl);

        // 执行测试
        String result = storageService.generatePreviewUrl(fileUuid);

        // 验证结果
        assertNotNull(result);
        assertEquals(expectedUrl, result);
        
        // 验证方法调用
        verify(minioClient, times(1)).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    void testGeneratePlayUrl_Success() throws Exception {
        // 准备测试数据
        String fileUuid = "test-uuid-123";
        String expectedUrl = "http://minio.example.com/play/test-uuid-123";
        
        // 模拟MinIO客户端行为
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn(expectedUrl);

        // 执行测试
        String result = storageService.generatePlayUrl(fileUuid);

        // 验证结果
        assertNotNull(result);
        assertEquals(expectedUrl, result);
        
        // 验证方法调用
        verify(minioClient, times(1)).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    void testGenerateDownloadUrl_Success() throws Exception {
        // 准备测试数据
        String fileUuid = "test-uuid-123";
        String expectedUrl = "http://minio.example.com/download/test-uuid-123";
        
        // 模拟MinIO客户端行为
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn(expectedUrl);

        // 执行测试
        String result = storageService.generateDownloadUrl(fileUuid);

        // 验证结果
        assertNotNull(result);
        assertEquals(expectedUrl, result);
        
        // 验证方法调用
        verify(minioClient, times(1)).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    void testFileExists_Success() throws Exception {
        // 准备测试数据
        String fileUuid = "test-uuid-123";
        
        // 模拟MinIO客户端行为
        when(minioClient.statObject(any(StatObjectArgs.class)))
                .thenReturn(mock(StatObjectResponse.class));

        // 执行测试
        boolean result = storageService.fileExists(fileUuid);

        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        verify(minioClient, times(1)).statObject(any(StatObjectArgs.class));
    }

    @Test
    void testFileExists_NotFound() throws Exception {
        // 准备测试数据
        String fileUuid = "non-existent-uuid";
        
        // 模拟MinIO客户端行为 - 抛出异常表示文件不存在
        when(minioClient.statObject(any(StatObjectArgs.class)))
                .thenThrow(new MinioException("Object does not exist"));

        // 执行测试
        boolean result = storageService.fileExists(fileUuid);

        // 验证结果
        assertFalse(result);
        
        // 验证方法调用
        verify(minioClient, times(1)).statObject(any(StatObjectArgs.class));
    }

    @Test
    void testGetFileSize_Success() throws Exception {
        // 准备测试数据
        String fileUuid = "test-uuid-123";
        long expectedSize = 1024L * 1024L; // 1MB
        
        // 模拟MinIO客户端行为
        StatObjectResponse statResponse = mock(StatObjectResponse.class);
        when(statResponse.size()).thenReturn(expectedSize);
        when(minioClient.statObject(any(StatObjectArgs.class)))
                .thenReturn(statResponse);

        // 执行测试
        long result = storageService.getFileSize(fileUuid);

        // 验证结果
        assertEquals(expectedSize, result);
        
        // 验证方法调用
        verify(minioClient, times(1)).statObject(any(StatObjectArgs.class));
    }

    @Test
    void testGetFileType_Success() throws Exception {
        // 准备测试数据
        String fileUuid = "test-uuid-123";
        String expectedType = "video/mp4";
        
        // 模拟MinIO客户端行为
        StatObjectResponse statResponse = mock(StatObjectResponse.class);
        when(statResponse.contentType()).thenReturn(expectedType);
        when(minioClient.statObject(any(StatObjectArgs.class)))
                .thenReturn(statResponse);

        // 执行测试
        String result = storageService.getFileType(fileUuid);

        // 验证结果
        assertEquals(expectedType, result);
        
        // 验证方法调用
        verify(minioClient, times(1)).statObject(any(StatObjectArgs.class));
    }

    @Test
    void testInitializeBucket_Success() throws Exception {
        // 模拟MinIO客户端行为
        when(minioClient.bucketExists(any(BucketExistsArgs.class)))
                .thenReturn(false);
        doNothing().when(minioClient).makeBucket(any(MakeBucketArgs.class));

        // 执行测试
        storageService.initializeBucket();

        // 验证方法调用
        verify(minioClient, times(1)).bucketExists(any(BucketExistsArgs.class));
        verify(minioClient, times(1)).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    void testInitializeBucket_AlreadyExists() throws Exception {
        // 模拟MinIO客户端行为 - 存储桶已存在
        when(minioClient.bucketExists(any(BucketExistsArgs.class)))
                .thenReturn(true);

        // 执行测试
        storageService.initializeBucket();

        // 验证方法调用
        verify(minioClient, times(1)).bucketExists(any(BucketExistsArgs.class));
        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));
    }
}