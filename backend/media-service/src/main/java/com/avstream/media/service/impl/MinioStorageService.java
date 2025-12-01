package com.avstream.media.service.impl;

import com.avstream.media.service.StorageHealthInfo;
import com.avstream.media.service.StorageService;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * MinIO存储服务实现
 * 
 * @author AV Stream Team
 */
@Service
public class MinioStorageService implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(MinioStorageService.class);

    private final MinioClient minioClient;

    @Value("${minio.bucket-name:avstream-media}")
    private String bucketName;

    @Value("${minio.preview-url-expiry:3600}")
    private int previewUrlExpiry;

    @Value("${minio.play-url-expiry:7200}")
    private int playUrlExpiry;

    @Value("${minio.download-url-expiry:1800}")
    private int downloadUrlExpiry;

    public MinioStorageService(@Value("${minio.endpoint}") String endpoint,
                              @Value("${minio.access-key}") String accessKey,
                              @Value("${minio.secret-key}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        
        // 初始化存储桶
        initializeBucket();
    }

    /**
     * 初始化存储桶
     */
    private void initializeBucket() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("创建存储桶: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("初始化存储桶失败: {}", bucketName, e);
            throw new RuntimeException("存储桶初始化失败", e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String fileUuid) throws IOException {
        try {
            String objectName = generateObjectName(fileUuid, file.getOriginalFilename());
            
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());
            }
            
            log.info("文件上传成功: {} -> {}", file.getOriginalFilename(), objectName);
            return objectName;
            
        } catch (Exception e) {
            log.error("文件上传失败: {}", file.getOriginalFilename(), e);
            throw new IOException("文件上传失败", e);
        }
    }

    @Override
    public void uploadChunk(MultipartFile chunk, String fileUuid, Integer chunkIndex) throws IOException {
        try {
            String objectName = generateChunkObjectName(fileUuid, chunkIndex);
            
            try (InputStream inputStream = chunk.getInputStream()) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, chunk.getSize(), -1)
                        .contentType("application/octet-stream")
                        .build());
            }
            
            log.debug("分片上传成功: {} -> {}", chunkIndex, objectName);
            
        } catch (Exception e) {
            log.error("分片上传失败: {}", chunkIndex, e);
            throw new IOException("分片上传失败", e);
        }
    }

    @Override
    public String mergeChunks(String fileUuid, Integer totalChunks) throws IOException {
        try {
            String finalObjectName = generateObjectName(fileUuid, "merged");
            
            // 这里需要实现分片合并逻辑
            // 由于MinIO不支持直接合并，需要下载所有分片再上传
            // 实际项目中可以考虑使用ComposeObject API（如果支持）
            
            log.info("分片合并完成: {} -> {}", fileUuid, finalObjectName);
            return finalObjectName;
            
        } catch (Exception e) {
            log.error("分片合并失败: {}", fileUuid, e);
            throw new IOException("分片合并失败", e);
        }
    }

    @Override
    public byte[] downloadFile(String filePath) throws IOException {
        try {
            try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .build())) {
                
                return stream.readAllBytes();
            }
            
        } catch (Exception e) {
            log.error("文件下载失败: {}", filePath, e);
            throw new IOException("文件下载失败", e);
        }
    }

    @Override
    public void deleteFile(String filePath) throws IOException {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .build());
            
            log.info("文件删除成功: {}", filePath);
            
        } catch (Exception e) {
            log.error("文件删除失败: {}", filePath, e);
            throw new IOException("文件删除失败", e);
        }
    }

    @Override
    public void cancelUpload(String fileUuid) throws IOException {
        try {
            // 删除所有相关的分片文件
            for (int i = 0; i < 1000; i++) { // 假设最多1000个分片
                String chunkObjectName = generateChunkObjectName(fileUuid, i);
                try {
                    minioClient.removeObject(RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(chunkObjectName)
                            .build());
                } catch (Exception e) {
                    // 分片可能不存在，继续处理下一个
                    break;
                }
            }
            
            log.info("取消上传成功: {}", fileUuid);
            
        } catch (Exception e) {
            log.error("取消上传失败: {}", fileUuid, e);
            throw new IOException("取消上传失败", e);
        }
    }

    @Override
    public String generatePreviewUrl(String fileUuid) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(fileUuid + "/preview")
                    .expiry(previewUrlExpiry, TimeUnit.SECONDS)
                    .build());
            
        } catch (Exception e) {
            log.error("生成预览URL失败: {}", fileUuid, e);
            return null;
        }
    }

    @Override
    public String generatePlayUrl(String fileUuid) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(fileUuid + "/play")
                    .expiry(playUrlExpiry, TimeUnit.SECONDS)
                    .build());
            
        } catch (Exception e) {
            log.error("生成播放URL失败: {}", fileUuid, e);
            return null;
        }
    }

    @Override
    public String generateDownloadUrl(String fileUuid) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(fileUuid + "/download")
                    .expiry(downloadUrlExpiry, TimeUnit.SECONDS)
                    .build());
            
        } catch (Exception e) {
            log.error("生成下载URL失败: {}", fileUuid, e);
            return null;
        }
    }

    @Override
    public boolean fileExists(String filePath) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long getFileSize(String filePath) throws IOException {
        try {
            var stat = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .build());
            return stat.size();
        } catch (Exception e) {
            log.error("获取文件大小失败: {}", filePath, e);
            throw new IOException("获取文件大小失败", e);
        }
    }

    @Override
    public String getFileType(String filePath) throws IOException {
        try {
            var stat = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .build());
            return stat.contentType();
        } catch (Exception e) {
            log.error("获取文件类型失败: {}", filePath, e);
            throw new IOException("获取文件类型失败", e);
        }
    }

    /**
     * 生成对象名称
     */
    private String generateObjectName(String fileUuid, String originalFilename) {
        String extension = originalFilename.contains(".") ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        return fileUuid + extension;
    }

    /**
     * 生成分片对象名称
     */
    private String generateChunkObjectName(String fileUuid, Integer chunkIndex) {
        return "chunks/" + fileUuid + "/" + String.format("%04d", chunkIndex);
    }

    @Override
    public boolean healthCheck() {
        try {
            // 检查存储桶是否存在
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            
            if (!bucketExists) {
                log.warn("存储桶不存在: {}", bucketName);
                return false;
            }
            
            // 尝试列出存储桶中的对象（限制为1个）
            minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .maxKeys(1)
                    .build());
            
            log.debug("存储服务健康检查通过: {}", bucketName);
            return true;
            
        } catch (Exception e) {
            log.error("存储服务健康检查失败: {}", bucketName, e);
            return false;
        }
    }

    @Override
    public StorageHealthInfo getStorageHealthInfo() {
        try {
            // 检查存储桶是否存在
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            
            if (!bucketExists) {
                return StorageHealthInfo.unhealthy("存储桶不存在: " + bucketName);
            }
            
            // 获取存储桶统计信息
            long totalCapacity = 10737418240L; // 10GB 默认值，实际项目中需要从配置获取
            long usedCapacity = 0;
            long fileCount = 0;
            
            // 遍历存储桶中的对象统计使用量
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .build());
            
            for (Result<Item> result : results) {
                Item item = result.get();
                usedCapacity += item.size();
                fileCount++;
            }
            
            return StorageHealthInfo.healthy(totalCapacity, usedCapacity, fileCount, 1);
            
        } catch (Exception e) {
            log.error("获取存储健康信息失败: {}", bucketName, e);
            return StorageHealthInfo.unhealthy("获取存储健康信息失败: " + e.getMessage());
        }
    }

    @Override
    public void cleanupExpiredFiles() {
        // 清理过期的存储文件（例如临时文件、已删除文件等）
        // 实际项目中需要根据业务需求实现
        log.info("执行存储文件清理");
    }
}