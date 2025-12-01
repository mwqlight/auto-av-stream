package com.avstream.media.util;

import com.avstream.media.entity.MediaFile;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 测试工具类
 * 
 * @author AV Stream Team
 */
public class TestUtils {

    /**
     * 创建测试媒体文件
     */
    public static MediaFile createTestMediaFile() {
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileUuid(UUID.randomUUID().toString());
        mediaFile.setFilename("test-video.mp4");
        mediaFile.setFileSize(1024L * 1024L); // 1MB
        mediaFile.setFileType("VIDEO");
        mediaFile.setMimeType("video/mp4");
        mediaFile.setUserId(1L);
        mediaFile.setDescription("测试视频文件");
        mediaFile.setStatus(MediaFile.FileStatus.ACTIVE);
        mediaFile.setVisibility(MediaFile.FileVisibility.PRIVATE);
        mediaFile.setCreatedAt(LocalDateTime.now());
        mediaFile.setUpdatedAt(LocalDateTime.now());
        return mediaFile;
    }

    /**
     * 创建测试媒体文件（带自定义参数）
     */
    public static MediaFile createTestMediaFile(String fileUuid, Long userId, String filename, String fileType) {
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileUuid(fileUuid);
        mediaFile.setFilename(filename);
        mediaFile.setFileSize(1024L * 1024L); // 1MB
        mediaFile.setFileType(fileType);
        mediaFile.setMimeType(getMimeType(fileType));
        mediaFile.setUserId(userId);
        mediaFile.setDescription("测试文件");
        mediaFile.setStatus(MediaFile.FileStatus.ACTIVE);
        mediaFile.setVisibility(MediaFile.FileVisibility.PRIVATE);
        mediaFile.setCreatedAt(LocalDateTime.now());
        mediaFile.setUpdatedAt(LocalDateTime.now());
        return mediaFile;
    }

    /**
     * 创建测试多部分文件
     */
    public static MockMultipartFile createTestMultipartFile(String filename, String contentType, byte[] content) {
        return new MockMultipartFile(
                "file",
                filename,
                contentType,
                content
        );
    }

    /**
     * 创建视频测试文件
     */
    public static MockMultipartFile createVideoTestFile() {
        byte[] videoContent = "fake video content".getBytes();
        return createTestMultipartFile("test-video.mp4", "video/mp4", videoContent);
    }

    /**
     * 创建音频测试文件
     */
    public static MockMultipartFile createAudioTestFile() {
        byte[] audioContent = "fake audio content".getBytes();
        return createTestMultipartFile("test-audio.mp3", "audio/mp3", audioContent);
    }

    /**
     * 创建图片测试文件
     */
    public static MockMultipartFile createImageTestFile() {
        byte[] imageContent = "fake image content".getBytes();
        return createTestMultipartFile("test-image.jpg", "image/jpeg", imageContent);
    }

    /**
     * 创建分片测试文件
     */
    public static MockMultipartFile createChunkTestFile(int chunkNumber) {
        byte[] chunkContent = ("chunk " + chunkNumber + " content").getBytes();
        return createTestMultipartFile("chunk-" + chunkNumber, "application/octet-stream", chunkContent);
    }

    /**
     * 根据文件类型获取MIME类型
     */
    private static String getMimeType(String fileType) {
        switch (fileType.toUpperCase()) {
            case "VIDEO":
                return "video/mp4";
            case "AUDIO":
                return "audio/mp3";
            case "IMAGE":
                return "image/jpeg";
            case "DOCUMENT":
                return "application/pdf";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 生成随机文件UUID
     */
    public static String generateFileUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成随机用户ID
     */
    public static Long generateUserId() {
        return (long) (Math.random() * 1000) + 1;
    }

    /**
     * 生成随机文件大小（1MB - 100MB）
     */
    public static Long generateFileSize() {
        return (long) (Math.random() * 99 * 1024 * 1024) + 1024 * 1024;
    }

    /**
     * 创建测试转码任务ID
     */
    public static String generateTranscodeTaskId() {
        return "transcode-task-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 创建测试文件路径
     */
    public static String generateFilePath(String fileUuid, String extension) {
        return "/media/" + fileUuid + "." + extension;
    }

    /**
     * 创建测试预览URL
     */
    public static String generatePreviewUrl(String fileUuid) {
        return "http://minio.example.com/preview/" + fileUuid;
    }

    /**
     * 创建测试播放URL
     */
    public static String generatePlayUrl(String fileUuid) {
        return "http://minio.example.com/play/" + fileUuid;
    }

    /**
     * 创建测试下载URL
     */
    public static String generateDownloadUrl(String fileUuid) {
        return "http://minio.example.com/download/" + fileUuid;
    }

    /**
     * 创建测试存储使用情况
     */
    public static MediaService.StorageUsage createStorageUsage(Long userId, Long usedStorage, Long totalStorage) {
        return new MediaService.StorageUsage(userId, usedStorage, totalStorage);
    }

    /**
     * 模拟存储使用情况类
     */
    public static class MediaService {
        public static class StorageUsage {
            private Long userId;
            private Long usedStorage;
            private Long totalStorage;

            public StorageUsage(Long userId, Long usedStorage, Long totalStorage) {
                this.userId = userId;
                this.usedStorage = usedStorage;
                this.totalStorage = totalStorage;
            }

            public Long getUserId() { return userId; }
            public Long getUsedStorage() { return usedStorage; }
            public Long getTotalStorage() { return totalStorage; }
        }
    }
}