package com.avstream.media.config;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * 媒体服务配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "media")
public class MediaServiceConfig {
    
    private static final Logger log = LoggerFactory.getLogger(MediaServiceConfig.class);
    
    /** 最大文件大小（字节） */
    private long maxFileSize = 1024L * 1024 * 1024; // 1GB
    
    /** 允许的文件类型 */
    private String[] allowedFileTypes = {"mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", 
                                        "mp3", "wav", "aac", "flac", "ogg", 
                                        "jpg", "jpeg", "png", "gif", "bmp", "webp"};
    
    /** 临时文件存储路径 */
    private String tempStoragePath = "/tmp/media-service";
    
    /** 是否启用文件分片上传 */
    private boolean chunkedUploadEnabled = true;
    
    /** 分片大小（字节） */
    private long chunkSize = 5L * 1024 * 1024; // 5MB
    
    /** 最大分片数量 */
    private int maxChunks = 1000;
    
    /** 分片过期时间（分钟） */
    private int chunkExpireMinutes = 60;
    
    /** 是否启用转码服务 */
    private boolean transcodeEnabled = true;
    
    /** 转码任务队列大小 */
    private int transcodeQueueSize = 100;
    
    /** 转码任务超时时间（分钟） */
    private int transcodeTimeoutMinutes = 60;
    
    /** 是否启用缩略图生成 */
    private boolean thumbnailEnabled = true;
    
    /** 缩略图尺寸 */
    private String thumbnailSize = "320x240";
    
    /** 是否启用WebRTC服务 */
    private boolean webrtcEnabled = true;
    
    /** WebRTC会话超时时间（分钟） */
    private int webrtcSessionTimeoutMinutes = 30;
    
    /** 是否启用MediaMTX服务 */
    private boolean mediamtxEnabled = true;
    
    /** MediaMTX API地址 */
    private String mediamtxApiUrl = "http://localhost:9997";
    
    /** 是否启用文件清理任务 */
    private boolean cleanupEnabled = true;
    
    /** 文件清理周期（小时） */
    private int cleanupIntervalHours = 24;
    
    /** 临时文件保留时间（小时） */
    private int tempFileRetentionHours = 24;
    
    /** 是否启用监控 */
    private boolean monitoringEnabled = true;
    
    /** 监控指标收集间隔（秒） */
    private int monitoringIntervalSeconds = 30;
    
    /** 是否启用缓存 */
    private boolean cacheEnabled = true;
    
    /** 缓存过期时间（分钟） */
    private int cacheExpireMinutes = 30;
    
    /** 是否启用压缩 */
    private boolean compressionEnabled = true;
    
    /** 压缩质量（0-100） */
    private int compressionQuality = 80;
    
    /**
     * 初始化配置
     */
    @PostConstruct
    public void init() {
        log.info("媒体服务配置初始化...");
        log.info("最大文件大小: {} bytes ({} MB)", maxFileSize, maxFileSize / (1024 * 1024));
        log.info("分片上传: {}", chunkedUploadEnabled ? "启用" : "禁用");
        log.info("分片大小: {} bytes ({} MB)", chunkSize, chunkSize / (1024 * 1024));
        log.info("转码服务: {}", transcodeEnabled ? "启用" : "禁用");
        log.info("WebRTC服务: {}", webrtcEnabled ? "启用" : "禁用");
        log.info("MediaMTX服务: {}", mediamtxEnabled ? "启用" : "禁用");
        log.info("文件清理: {}", cleanupEnabled ? "启用" : "禁用");
        log.info("监控: {}", monitoringEnabled ? "启用" : "禁用");
    }
    
    /**
     * 检查文件类型是否允许
     */
    public boolean isFileTypeAllowed(String fileType) {
        if (fileType == null || fileType.trim().isEmpty()) {
            return false;
        }
        
        String lowerCaseType = fileType.toLowerCase();
        for (String allowedType : allowedFileTypes) {
            if (allowedType.equalsIgnoreCase(lowerCaseType)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查文件大小是否超过限制
     */
    public boolean isFileSizeValid(long fileSize) {
        return fileSize <= maxFileSize;
    }
    
    /**
     * 获取最大文件大小（格式化）
     */
    public String getFormattedMaxFileSize() {
        if (maxFileSize < 1024) {
            return maxFileSize + " B";
        } else if (maxFileSize < 1024 * 1024) {
            return String.format("%.2f KB", maxFileSize / 1024.0);
        } else if (maxFileSize < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", maxFileSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", maxFileSize / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * 获取分片大小（格式化）
     */
    public String getFormattedChunkSize() {
        if (chunkSize < 1024) {
            return chunkSize + " B";
        } else if (chunkSize < 1024 * 1024) {
            return String.format("%.2f KB", chunkSize / 1024.0);
        } else {
            return String.format("%.2f MB", chunkSize / (1024.0 * 1024.0));
        }
    }
}