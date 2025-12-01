package com.avstream.media.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 上传响应DTO
 * 
 * @author AV Stream Team
 */
public class UploadResponse {

    /** 上传ID */
    private String uploadId;

    /** 文件ID */
    private Long fileId;

    /** 文件UUID */
    private String fileUuid;

    /** 上传URL */
    private String uploadUrl;

    /** 分片大小（字节） */
    private Long chunkSize;

    /** 总分片数 */
    private Integer totalChunks;

    /** 已上传分片数 */
    private Integer uploadedChunks = 0;

    /** 上传状态 */
    private UploadStatus status;

    /** 上传进度（0-100） */
    private Integer progress = 0;

    /** 上传开始时间 */
    private LocalDateTime startedAt;

    /** 上传完成时间 */
    private LocalDateTime completedAt;

    /** 错误信息 */
    private String errorMessage;

    /** 文件信息 */
    private FileInfo fileInfo;

    // Getter and Setter methods
    public String getUploadId() { return uploadId; }
    public void setUploadId(String uploadId) { this.uploadId = uploadId; }
    
    public Long getFileId() { return fileId; }
    public void setFileId(Long fileId) { this.fileId = fileId; }
    
    public String getFileUuid() { return fileUuid; }
    public void setFileUuid(String fileUuid) { this.fileUuid = fileUuid; }
    
    public String getUploadUrl() { return uploadUrl; }
    public void setUploadUrl(String uploadUrl) { this.uploadUrl = uploadUrl; }
    
    public Long getChunkSize() { return chunkSize; }
    public void setChunkSize(Long chunkSize) { this.chunkSize = chunkSize; }
    
    public Integer getTotalChunks() { return totalChunks; }
    public void setTotalChunks(Integer totalChunks) { this.totalChunks = totalChunks; }
    
    public Integer getUploadedChunks() { return uploadedChunks; }
    public void setUploadedChunks(Integer uploadedChunks) { this.uploadedChunks = uploadedChunks; }
    
    public UploadStatus getStatus() { return status; }
    public void setStatus(UploadStatus status) { this.status = status; }
    
    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public FileInfo getFileInfo() { return fileInfo; }
    public void setFileInfo(FileInfo fileInfo) { this.fileInfo = fileInfo; }

    /**
     * 上传状态枚举
     */
    public enum UploadStatus {
        INITIALIZED,    // 已初始化
        UPLOADING,      // 上传中
        COMPLETED,      // 上传完成
        FAILED,         // 上传失败
        CANCELLED       // 上传取消
    }

    /**
     * 文件信息类
     */
    public static class FileInfo {
        
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

        /** 是否公开 */
        private Boolean isPublic;

        /** 存储桶名称 */
        private String bucketName;

        /** 文件路径 */
        private String filePath;

        /** 文件URL */
        private String fileUrl;

        /** 缩略图URL */
        private String thumbnailUrl;

        /** 预览URL */
        private String previewUrl;

        // Getter and Setter methods for FileInfo
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
        
        public Boolean getIsPublic() { return isPublic; }
        public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
        
        public String getBucketName() { return bucketName; }
        public void setBucketName(String bucketName) { this.bucketName = bucketName; }
        
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        
        public String getFileUrl() { return fileUrl; }
        public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
        
        public String getThumbnailUrl() { return thumbnailUrl; }
        public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
        
        public String getPreviewUrl() { return previewUrl; }
        public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    }

    /**
     * 检查上传是否完成
     */
    public boolean isCompleted() {
        return this.status == UploadStatus.COMPLETED;
    }

    /**
     * 检查上传是否失败
     */
    public boolean isFailed() {
        return this.status == UploadStatus.FAILED;
    }

    /**
     * 检查上传是否可继续
     */
    public boolean canContinue() {
        return this.status == UploadStatus.UPLOADING || this.status == UploadStatus.INITIALIZED;
    }

    /**
     * 获取上传进度百分比
     */
    public String getProgressPercentage() {
        return this.progress + "%";
    }

    /**
     * 获取已上传文件大小（格式化显示）
     */
    public String getFormattedUploadedSize() {
        if (this.fileInfo == null || this.fileInfo.fileSize == null) return "0 B";
        
        long uploadedSize = this.fileInfo.fileSize * this.progress / 100;
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        double size = uploadedSize;
        int unitIndex = 0;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", size, units[unitIndex]);
    }

    /**
     * 获取剩余上传时间估算（秒）
     */
    public Long getEstimatedRemainingTime() {
        if (this.progress == null || this.progress <= 0 || this.progress >= 100) {
            return 0L;
        }
        
        long elapsedSeconds = java.time.Duration.between(this.startedAt, LocalDateTime.now()).getSeconds();
        if (elapsedSeconds <= 0) {
            return null;
        }
        
        double uploadSpeed = (double) this.progress / elapsedSeconds;
        if (uploadSpeed <= 0) {
            return null;
        }
        
        return (long) ((100 - this.progress) / uploadSpeed);
    }

    /**
     * 获取格式化剩余时间
     */
    public String getFormattedRemainingTime() {
        Long remainingSeconds = getEstimatedRemainingTime();
        if (remainingSeconds == null) return "计算中...";
        
        if (remainingSeconds <= 0) return "即将完成";
        
        long hours = remainingSeconds / 3600;
        long minutes = (remainingSeconds % 3600) / 60;
        long seconds = remainingSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d小时%d分%d秒", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d分%d秒", minutes, seconds);
        } else {
            return String.format("%d秒", seconds);
        }
    }

    /**
     * 创建Builder实例
     */
    public static UploadResponseBuilder builder() {
        return new UploadResponseBuilder();
    }

    /**
     * Builder类
     */
    public static class UploadResponseBuilder {
        private UploadResponse uploadResponse;

        public UploadResponseBuilder() {
            this.uploadResponse = new UploadResponse();
        }

        public UploadResponseBuilder uploadId(String uploadId) {
            uploadResponse.setUploadId(uploadId);
            return this;
        }

        public UploadResponseBuilder fileId(Long fileId) {
            uploadResponse.setFileId(fileId);
            return this;
        }

        public UploadResponseBuilder fileUuid(String fileUuid) {
            uploadResponse.setFileUuid(fileUuid);
            return this;
        }

        public UploadResponseBuilder uploadUrl(String uploadUrl) {
            uploadResponse.setUploadUrl(uploadUrl);
            return this;
        }

        public UploadResponseBuilder chunkSize(Long chunkSize) {
            uploadResponse.setChunkSize(chunkSize);
            return this;
        }

        public UploadResponseBuilder totalChunks(Integer totalChunks) {
            uploadResponse.setTotalChunks(totalChunks);
            return this;
        }

        public UploadResponseBuilder uploadedChunks(Integer uploadedChunks) {
            uploadResponse.setUploadedChunks(uploadedChunks);
            return this;
        }

        public UploadResponseBuilder status(UploadStatus status) {
            uploadResponse.setStatus(status);
            return this;
        }

        public UploadResponseBuilder progress(Integer progress) {
            uploadResponse.setProgress(progress);
            return this;
        }

        public UploadResponseBuilder startedAt(LocalDateTime startedAt) {
            uploadResponse.setStartedAt(startedAt);
            return this;
        }

        public UploadResponseBuilder completedAt(LocalDateTime completedAt) {
            uploadResponse.setCompletedAt(completedAt);
            return this;
        }

        public UploadResponseBuilder errorMessage(String errorMessage) {
            uploadResponse.setErrorMessage(errorMessage);
            return this;
        }

        public UploadResponseBuilder message(String message) {
            uploadResponse.setErrorMessage(message);
            return this;
        }

        public UploadResponseBuilder uploadTime(LocalDateTime uploadTime) {
            uploadResponse.setStartedAt(uploadTime);
            return this;
        }

        public UploadResponseBuilder fileInfo(FileInfo fileInfo) {
            uploadResponse.setFileInfo(fileInfo);
            return this;
        }

        public UploadResponse build() {
            return uploadResponse;
        }
    }
}