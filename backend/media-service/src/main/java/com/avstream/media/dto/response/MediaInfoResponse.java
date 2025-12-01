package com.avstream.media.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 媒体信息响应DTO
 * 
 * @author AV Stream Team
 */
public class MediaInfoResponse {

    /** 文件ID */
    private Long id;

    /** 文件UUID */
    private String fileUuid;

    /** 文件名 */
    private String filename;

    /** 原始文件名 */
    private String originalFilename;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件类型 */
    private String fileType;

    /** MIME类型 */
    private String mimeType;

    /** 文件格式 */
    private String format;

    /** 存储桶名称 */
    private String bucketName;

    /** 文件路径 */
    private String filePath;

    /** 用户ID */
    private Long userId;

    /** 媒体状态 */
    private String status;

    /** 是否公开 */
    private Boolean isPublic;

    /** 是否启用 */
    private Boolean enabled;

    /** 上传进度（0-100） */
    private Integer uploadProgress;

    /** 上传完成时间 */
    private LocalDateTime uploadCompletedAt;

    /** 处理完成时间 */
    private LocalDateTime processingCompletedAt;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 媒体元数据 */
    private MediaMetadata metadata;

    /** 缩略图信息 */
    private ThumbnailInfo thumbnail;

    /** 转码文件列表 */
    private List<TranscodeInfo> transcodes;

    /** 播放URL */
    private String playUrl;

    /** 下载URL */
    private String downloadUrl;

    /** 预览URL */
    private String previewUrl;

    /** 自定义标签 */
    private List<String> tags;

    /** 文件描述 */
    private String description;

    /** 分类信息 */
    private CategoryInfo category;

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFileUuid() { return fileUuid; }
    public void setFileUuid(String fileUuid) { this.fileUuid = fileUuid; }
    
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public String getBucketName() { return bucketName; }
    public void setBucketName(String bucketName) { this.bucketName = bucketName; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    
    public Integer getUploadProgress() { return uploadProgress; }
    public void setUploadProgress(Integer uploadProgress) { this.uploadProgress = uploadProgress; }
    
    public LocalDateTime getUploadCompletedAt() { return uploadCompletedAt; }
    public void setUploadCompletedAt(LocalDateTime uploadCompletedAt) { this.uploadCompletedAt = uploadCompletedAt; }
    
    public LocalDateTime getProcessingCompletedAt() { return processingCompletedAt; }
    public void setProcessingCompletedAt(LocalDateTime processingCompletedAt) { this.processingCompletedAt = processingCompletedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public MediaMetadata getMetadata() { return metadata; }
    public void setMetadata(MediaMetadata metadata) { this.metadata = metadata; }
    
    public ThumbnailInfo getThumbnail() { return thumbnail; }
    public void setThumbnail(ThumbnailInfo thumbnail) { this.thumbnail = thumbnail; }
    
    public List<TranscodeInfo> getTranscodes() { return transcodes; }
    public void setTranscodes(List<TranscodeInfo> transcodes) { this.transcodes = transcodes; }
    
    public String getPlayUrl() { return playUrl; }
    public void setPlayUrl(String playUrl) { this.playUrl = playUrl; }
    
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    
    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public CategoryInfo getCategory() { return category; }
    public void setCategory(CategoryInfo category) { this.category = category; }

    /**
     * 媒体元数据类
     */
    public static class MediaMetadata {
        
        /** 视频时长（秒） */
        private BigDecimal duration;

        /** 视频宽度（像素） */
        private Integer width;

        /** 视频高度（像素） */
        private Integer height;

        /** 视频编码器 */
        private String videoCodec;

        /** 视频码率（bps） */
        private Long videoBitrate;

        /** 视频帧率（fps） */
        private BigDecimal frameRate;

        /** 音频编码器 */
        private String audioCodec;

        /** 音频码率（bps） */
        private Long audioBitrate;

        /** 音频采样率（Hz） */
        private Integer sampleRate;

        /** 音频声道数 */
        private Integer channels;

        /** 文件编码格式 */
        private String containerFormat;

        /** 文件创建时间 */
        private String fileCreationTime;

        /** 文件修改时间 */
        private String fileModificationTime;

        /** 文件编码器 */
        private String encoder;

        /** 元数据提取时间 */
        private String metadataExtractedAt;

        /** 自定义元数据（JSON格式） */
        private String customMetadata;

        // Getter and Setter methods for MediaMetadata
        public BigDecimal getDuration() { return duration; }
        public void setDuration(BigDecimal duration) { this.duration = duration; }
        
        public Integer getWidth() { return width; }
        public void setWidth(Integer width) { this.width = width; }
        
        public Integer getHeight() { return height; }
        public void setHeight(Integer height) { this.height = height; }
        
        public String getVideoCodec() { return videoCodec; }
        public void setVideoCodec(String videoCodec) { this.videoCodec = videoCodec; }
        
        public Long getVideoBitrate() { return videoBitrate; }
        public void setVideoBitrate(Long videoBitrate) { this.videoBitrate = videoBitrate; }
        
        public BigDecimal getFrameRate() { return frameRate; }
        public void setFrameRate(BigDecimal frameRate) { this.frameRate = frameRate; }
        
        public String getAudioCodec() { return audioCodec; }
        public void setAudioCodec(String audioCodec) { this.audioCodec = audioCodec; }
        
        public Long getAudioBitrate() { return audioBitrate; }
        public void setAudioBitrate(Long audioBitrate) { this.audioBitrate = audioBitrate; }
        
        public Integer getSampleRate() { return sampleRate; }
        public void setSampleRate(Integer sampleRate) { this.sampleRate = sampleRate; }
        
        public Integer getChannels() { return channels; }
        public void setChannels(Integer channels) { this.channels = channels; }
        
        public String getContainerFormat() { return containerFormat; }
        public void setContainerFormat(String containerFormat) { this.containerFormat = containerFormat; }
        
        public String getFileCreationTime() { return fileCreationTime; }
        public void setFileCreationTime(String fileCreationTime) { this.fileCreationTime = fileCreationTime; }
        
        public String getFileModificationTime() { return fileModificationTime; }
        public void setFileModificationTime(String fileModificationTime) { this.fileModificationTime = fileModificationTime; }
        
        public String getEncoder() { return encoder; }
        public void setEncoder(String encoder) { this.encoder = encoder; }
        
        public String getMetadataExtractedAt() { return metadataExtractedAt; }
        public void setMetadataExtractedAt(String metadataExtractedAt) { this.metadataExtractedAt = metadataExtractedAt; }
        
        public String getCustomMetadata() { return customMetadata; }
        public void setCustomMetadata(String customMetadata) { this.customMetadata = customMetadata; }
    }

    /**
     * 缩略图信息类
     */
    public static class ThumbnailInfo {
        
        /** 缩略图文件路径 */
        private String filePath;

        /** 缩略图文件名 */
        private String filename;

        /** 缩略图文件大小（字节） */
        private Long fileSize;

        /** 缩略图宽度（像素） */
        private Integer width;

        /** 缩略图高度（像素） */
        private Integer height;

        /** 缩略图格式 */
        private String format;

        /** 缩略图类型 */
        private String thumbnailType;

        /** 截图时间点（秒，仅视频） */
        private Double captureTime;

        /** 缩略图URL */
        private String thumbnailUrl;

        // Getter and Setter methods for ThumbnailInfo
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        
        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }
        
        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
        
        public Integer getWidth() { return width; }
        public void setWidth(Integer width) { this.width = width; }
        
        public Integer getHeight() { return height; }
        public void setHeight(Integer height) { this.height = height; }
        
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        
        public String getThumbnailType() { return thumbnailType; }
        public void setThumbnailType(String thumbnailType) { this.thumbnailType = thumbnailType; }
        
        public Double getCaptureTime() { return captureTime; }
        public void setCaptureTime(Double captureTime) { this.captureTime = captureTime; }
        
        public String getThumbnailUrl() { return thumbnailUrl; }
        public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    }

    /**
     * 转码信息类
     */
    public static class TranscodeInfo {
        
        /** 转码ID */
        private Long id;

        /** 转码文件UUID */
        private String transcodeUuid;

        /** 转码文件名 */
        private String filename;

        /** 转码文件路径 */
        private String filePath;

        /** 转码文件大小（字节） */
        private Long fileSize;

        /** 转码预设名称 */
        private String presetName;

        /** 转码格式 */
        private String format;

        /** 视频编码器 */
        private String videoCodec;

        /** 视频码率（bps） */
        private Long videoBitrate;

        /** 视频分辨率宽度 */
        private Integer width;

        /** 视频分辨率高度 */
        private Integer height;

        /** 视频帧率（fps） */
        private BigDecimal frameRate;

        /** 音频编码器 */
        private String audioCodec;

        /** 音频码率（bps） */
        private Long audioBitrate;

        /** 转码状态 */
        private String status;

        /** 转码进度（0-100） */
        private Integer progress;

        /** 转码开始时间 */
        private LocalDateTime startedAt;

        /** 转码完成时间 */
        private LocalDateTime completedAt;

        /** 转码耗时（秒） */
        private Integer processingTime;

        /** 播放URL */
        private String playUrl;

        /** 下载URL */
        private String downloadUrl;

        // Getter and Setter methods for TranscodeInfo
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getTranscodeUuid() { return transcodeUuid; }
        public void setTranscodeUuid(String transcodeUuid) { this.transcodeUuid = transcodeUuid; }
        
        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }
        
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        
        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
        
        public String getPresetName() { return presetName; }
        public void setPresetName(String presetName) { this.presetName = presetName; }
        
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        
        public String getVideoCodec() { return videoCodec; }
        public void setVideoCodec(String videoCodec) { this.videoCodec = videoCodec; }
        
        public Long getVideoBitrate() { return videoBitrate; }
        public void setVideoBitrate(Long videoBitrate) { this.videoBitrate = videoBitrate; }
        
        public Integer getWidth() { return width; }
        public void setWidth(Integer width) { this.width = width; }
        
        public Integer getHeight() { return height; }
        public void setHeight(Integer height) { this.height = height; }
        
        public BigDecimal getFrameRate() { return frameRate; }
        public void setFrameRate(BigDecimal frameRate) { this.frameRate = frameRate; }
        
        public String getAudioCodec() { return audioCodec; }
        public void setAudioCodec(String audioCodec) { this.audioCodec = audioCodec; }
        
        public Long getAudioBitrate() { return audioBitrate; }
        public void setAudioBitrate(Long audioBitrate) { this.audioBitrate = audioBitrate; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public Integer getProgress() { return progress; }
        public void setProgress(Integer progress) { this.progress = progress; }
        
        public LocalDateTime getStartedAt() { return startedAt; }
        public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
        
        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
        
        public Integer getProcessingTime() { return processingTime; }
        public void setProcessingTime(Integer processingTime) { this.processingTime = processingTime; }
        
        public String getPlayUrl() { return playUrl; }
        public void setPlayUrl(String playUrl) { this.playUrl = playUrl; }
        
        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    }

    /**
     * 分类信息类
     */
    public static class CategoryInfo {
        
        /** 分类ID */
        private Long id;

        /** 分类名称 */
        private String name;

        /** 分类描述 */
        private String description;

        /** 父分类ID */
        private Long parentId;

        /** 分类层级 */
        private Integer level;

        /** 分类图标 */
        private String icon;

        /** 分类颜色 */
        private String color;

        // Getter and Setter methods for CategoryInfo
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Long getParentId() { return parentId; }
        public void setParentId(Long parentId) { this.parentId = parentId; }
        
        public Integer getLevel() { return level; }
        public void setLevel(Integer level) { this.level = level; }
        
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }

    /**
     * 检查文件是否可播放
     */
    public boolean isPlayable() {
        return this.enabled && 
               ("PROCESSED".equals(this.status) || "UPLOADED".equals(this.status)) &&
               ("VIDEO".equals(this.fileType) || "AUDIO".equals(this.fileType));
    }

    /**
     * 检查文件是否可转码
     */
    public boolean isTranscodable() {
        return this.enabled && 
               "UPLOADED".equals(this.status) &&
               ("VIDEO".equals(this.fileType) || "AUDIO".equals(this.fileType));
    }

    /**
     * 获取文件大小（格式化显示）
     */
    public String getFormattedFileSize() {
        if (this.fileSize == null) return "0 B";
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        double size = this.fileSize;
        int unitIndex = 0;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", size, units[unitIndex]);
    }

    /**
     * 获取视频时长（格式化显示）
     */
    public String getFormattedDuration() {
        if (this.metadata == null || this.metadata.duration == null) return "00:00";
        
        long totalSeconds = this.metadata.duration.longValue();
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    /**
     * 获取视频分辨率
     */
    public String getResolution() {
        if (this.metadata == null || this.metadata.width == null || this.metadata.height == null) {
            return null;
        }
        return this.metadata.width + "x" + this.metadata.height;
    }

    /**
     * 检查是否为高清视频
     */
    public boolean isHdVideo() {
        if (this.metadata == null || this.metadata.width == null || this.metadata.height == null) {
            return false;
        }
        return this.metadata.width >= 1280 && this.metadata.height >= 720;
    }

    /**
     * 检查是否为4K视频
     */
    public boolean is4kVideo() {
        if (this.metadata == null || this.metadata.width == null || this.metadata.height == null) {
            return false;
        }
        return this.metadata.width >= 3840 && this.metadata.height >= 2160;
    }
    
    // Builder pattern implementation
    public static MediaInfoResponseBuilder builder() {
        return new MediaInfoResponseBuilder();
    }
    
    public static class MediaInfoResponseBuilder {
        private MediaInfoResponse response;
        
        public MediaInfoResponseBuilder() {
            this.response = new MediaInfoResponse();
        }
        
        public MediaInfoResponseBuilder fileUuid(String fileUuid) {
            response.setFileUuid(fileUuid);
            return this;
        }
        
        public MediaInfoResponseBuilder filename(String filename) {
            response.setFilename(filename);
            return this;
        }
        
        public MediaInfoResponseBuilder fileSize(Long fileSize) {
            response.setFileSize(fileSize);
            return this;
        }
        
        public MediaInfoResponseBuilder fileType(String fileType) {
            response.setFileType(fileType);
            return this;
        }
        
        public MediaInfoResponseBuilder status(String status) {
            response.setStatus(status);
            return this;
        }
        
        public MediaInfoResponseBuilder isPublic(Boolean isPublic) {
            response.setIsPublic(isPublic);
            return this;
        }
        
        public MediaInfoResponseBuilder description(String description) {
            response.setDescription(description);
            return this;
        }
        
        public MediaInfoResponseBuilder uploadProgress(Integer uploadProgress) {
            response.setUploadProgress(uploadProgress);
            return this;
        }
        
        public MediaInfoResponseBuilder createdAt(LocalDateTime createdAt) {
            response.setCreatedAt(createdAt);
            return this;
        }
        
        public MediaInfoResponseBuilder updatedAt(LocalDateTime updatedAt) {
            response.setUpdatedAt(updatedAt);
            return this;
        }
        
        public MediaInfoResponse build() {
            return response;
        }
    }
}