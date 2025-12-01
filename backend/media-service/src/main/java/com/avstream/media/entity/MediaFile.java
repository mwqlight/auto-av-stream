package com.avstream.media.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 媒体文件实体类
 * 
 * @author AV Stream Team
 */
@Entity
@Table(name = "media_files")
@Data
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 文件唯一标识 */
    @Column(name = "file_uuid", unique = true, nullable = false, length = 36)
    private String fileUuid;

    /** 文件名 */
    @Column(name = "filename", nullable = false, length = 255)
    private String filename;

    /** 原始文件名 */
    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    /** 文件路径（在存储系统中的路径） */
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    /** 文件大小（字节） */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    /** 文件类型 */
    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false, length = 20)
    private FileType fileType;

    /** MIME类型 */
    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    /** 文件格式 */
    @Column(name = "format", nullable = false, length = 10)
    private String format;

    /** 存储桶名称 */
    @Column(name = "bucket_name", nullable = false, length = 100)
    private String bucketName;

    /** 用户ID */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 总块数（分片上传时使用） */
    @Column(name = "total_chunks")
    private Integer totalChunks;

    /** 媒体状态 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MediaStatus status = MediaStatus.UPLOADING;

    /** 是否公开 */
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    /** 文件描述 */
    @Column(name = "description", length = 1000)
    private String description;

    /** 是否启用 */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    /** 上传进度（0-100） */
    @Column(name = "upload_progress")
    private Integer uploadProgress = 0;

    /** 上传完成时间 */
    @Column(name = "upload_completed_at")
    private LocalDateTime uploadCompletedAt;

    /** 处理完成时间 */
    @Column(name = "processing_completed_at")
    private LocalDateTime processingCompletedAt;

    /** 创建时间 */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** 删除时间 */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /** 错误信息 */
    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    /** 转码文件列表 */
    @OneToMany(mappedBy = "originalFile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MediaTranscode> transcodes = new ArrayList<>();

    /** 媒体元数据 */
    @OneToOne(mappedBy = "mediaFile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MediaMetadata metadata;

    /** 缩略图信息 */
    @OneToOne(mappedBy = "mediaFile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MediaThumbnail thumbnail;

    /**
     * 文件类型枚举
     */
    public enum FileType {
        VIDEO,      // 视频文件
        AUDIO,      // 音频文件
        IMAGE,      // 图片文件
        DOCUMENT,   // 文档文件
        OTHER       // 其他文件
    }

    /**
     * 媒体状态枚举
     */
    public enum MediaStatus {
        UPLOADING,      // 上传中
        UPLOADED,       // 上传完成
        PROCESSING,     // 处理中
        PROCESSED,      // 处理完成
        FAILED,         // 处理失败
        DELETED         // 已删除
    }

    /**
     * 检查文件是否可播放
     */
    public boolean isPlayable() {
        return this.enabled && 
               (this.status == MediaStatus.PROCESSED || this.status == MediaStatus.UPLOADED) &&
               (this.fileType == FileType.VIDEO || this.fileType == FileType.AUDIO);
    }

    /**
     * 检查文件是否可转码
     */
    public boolean isTranscodable() {
        return this.enabled && 
               this.status == MediaStatus.UPLOADED &&
               (this.fileType == FileType.VIDEO || this.fileType == FileType.AUDIO);
    }

    /**
     * 获取文件扩展名
     */
    public String getFileExtension() {
        if (this.originalFilename != null && this.originalFilename.contains(".")) {
            return this.originalFilename.substring(this.originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }
        return this.format;
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

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFileUuid() { return fileUuid; }
    public void setFileUuid(String fileUuid) { this.fileUuid = fileUuid; }
    
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public FileType getFileType() { return fileType; }
    public void setFileType(FileType fileType) { this.fileType = fileType; }
    
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public String getBucketName() { return bucketName; }
    public void setBucketName(String bucketName) { this.bucketName = bucketName; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public MediaStatus getStatus() { return status; }
    public void setStatus(MediaStatus status) { this.status = status; }
    
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    
    public Integer getUploadProgress() { return uploadProgress; }
    public void setUploadProgress(Integer uploadProgress) { this.uploadProgress = uploadProgress; }
    
    public Integer getTotalChunks() { return totalChunks; }
    public void setTotalChunks(Integer totalChunks) { this.totalChunks = totalChunks; }
    
    public LocalDateTime getUploadCompletedAt() { return uploadCompletedAt; }
    public void setUploadCompletedAt(LocalDateTime uploadCompletedAt) { this.uploadCompletedAt = uploadCompletedAt; }
    
    public LocalDateTime getProcessingCompletedAt() { return processingCompletedAt; }
    public void setProcessingCompletedAt(LocalDateTime processingCompletedAt) { this.processingCompletedAt = processingCompletedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public List<MediaTranscode> getTranscodes() { return transcodes; }
    public void setTranscodes(List<MediaTranscode> transcodes) { this.transcodes = transcodes; }
    
    public MediaMetadata getMetadata() { return metadata; }
    public void setMetadata(MediaMetadata metadata) { this.metadata = metadata; }
    
    public MediaThumbnail getThumbnail() { return thumbnail; }
    public void setThumbnail(MediaThumbnail thumbnail) { this.thumbnail = thumbnail; }
    
    // Builder pattern implementation
    public static MediaFileBuilder builder() {
        return new MediaFileBuilder();
    }
    
    public static class MediaFileBuilder {
        private MediaFile mediaFile;
        
        public MediaFileBuilder() {
            this.mediaFile = new MediaFile();
        }
        
        public MediaFileBuilder fileUuid(String fileUuid) {
            mediaFile.setFileUuid(fileUuid);
            return this;
        }
        
        public MediaFileBuilder filename(String filename) {
            mediaFile.setFilename(filename);
            return this;
        }
        
        public MediaFileBuilder fileSize(Long fileSize) {
            mediaFile.setFileSize(fileSize);
            return this;
        }
        
        public MediaFileBuilder fileType(FileType fileType) {
            mediaFile.setFileType(fileType);
            return this;
        }
        
        public MediaFileBuilder userId(Long userId) {
            mediaFile.setUserId(userId);
            return this;
        }
        
        public MediaFileBuilder totalChunks(Integer totalChunks) {
            mediaFile.setTotalChunks(totalChunks);
            return this;
        }
        
        public MediaFileBuilder description(String description) {
            mediaFile.setDescription(description);
            return this;
        }
        
        public MediaFileBuilder isPublic(Boolean isPublic) {
            mediaFile.setIsPublic(isPublic);
            return this;
        }
        
        public MediaFileBuilder status(MediaStatus status) {
            mediaFile.setStatus(status);
            return this;
        }
        
        public MediaFileBuilder uploadProgress(Integer uploadProgress) {
            mediaFile.setUploadProgress(uploadProgress);
            return this;
        }
        
        public MediaFileBuilder enabled(Boolean enabled) {
            mediaFile.setEnabled(enabled);
            return this;
        }
        
        public MediaFileBuilder createdAt(LocalDateTime createdAt) {
            mediaFile.setCreatedAt(createdAt);
            return this;
        }
        
        public MediaFileBuilder updatedAt(LocalDateTime updatedAt) {
            mediaFile.setUpdatedAt(updatedAt);
            return this;
        }
        
        public MediaFile build() {
            return mediaFile;
        }
    }
}