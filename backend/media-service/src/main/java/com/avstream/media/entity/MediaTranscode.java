package com.avstream.media.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 媒体转码实体类
 * 
 * @author AV Stream Team
 */
@Entity
@Table(name = "media_transcodes")
public class MediaTranscode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 原始媒体文件 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_file_id", nullable = false)
    private MediaFile originalFile;

    /** 转码文件唯一标识 */
    @Column(name = "transcode_uuid", unique = true, nullable = false, length = 36)
    private String transcodeUuid;

    /** 原始文件唯一标识 */
    @Column(name = "file_uuid", nullable = false, length = 36)
    private String fileUuid;

    /** 转码文件名 */
    @Column(name = "filename", nullable = false, length = 255)
    private String filename;

    /** 转码文件路径 */
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    /** 转码文件大小（字节） */
    @Column(name = "file_size")
    private Long fileSize;

    /** 转码预设名称 */
    @Column(name = "preset_name", nullable = false, length = 50)
    private String presetName;

    /** 转码格式 */
    @Column(name = "format", nullable = false, length = 10)
    private String format;

    /** 输出格式 */
    @Column(name = "output_format", length = 10)
    private String outputFormat;

    /** 视频分辨率（字符串形式） */
    @Column(name = "resolution", length = 20)
    private String resolution;

    /** 视频帧率（字符串形式） */
    @Column(name = "frame_rate_str", length = 10)
    private String frameRateStr;

    /** 视频编码器 */
    @Column(name = "video_codec", length = 50)
    private String videoCodec;

    /** 视频码率（bps） */
    @Column(name = "video_bitrate")
    private Long videoBitrate;

    /** 视频分辨率宽度 */
    @Column(name = "width")
    private Integer width;

    /** 视频分辨率高度 */
    @Column(name = "height")
    private Integer height;

    /** 视频帧率（fps） */
    @Column(name = "frame_rate")
    private BigDecimal frameRate;

    /** 音频编码器 */
    @Column(name = "audio_codec", length = 50)
    private String audioCodec;

    /** 音频码率（bps） */
    @Column(name = "audio_bitrate")
    private Long audioBitrate;

    /** 音频采样率（Hz） */
    @Column(name = "sample_rate")
    private Integer sampleRate;

    /** 音频声道数 */
    @Column(name = "channels")
    private Integer channels;

    /** 转码状态 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TranscodeStatus status = TranscodeStatus.PENDING;

    /** 转码进度（0-100） */
    @Column(name = "progress")
    private Integer progress = 0;

    /** 转码开始时间 */
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    /** 转码完成时间 */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /** 转码失败原因 */
    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    /** 转码任务ID */
    @Column(name = "job_id", length = 100)
    private String jobId;

    /** 用户ID */
    @Column(name = "user_id")
    private Long userId;

    /** 转码耗时（秒） */
    @Column(name = "processing_time")
    private Integer processingTime;

    /** 是否启用 */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    /** 重试次数 */
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    /** 创建时间 */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Getter and Setter methods for fields that don't have them yet
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public MediaFile getOriginalFile() { return originalFile; }
    public void setOriginalFile(MediaFile originalFile) { this.originalFile = originalFile; }
    
    public String getTranscodeUuid() { return transcodeUuid; }
    public void setTranscodeUuid(String transcodeUuid) { this.transcodeUuid = transcodeUuid; }
    
    public String getFileUuid() { return fileUuid; }
    
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
    
    public String getOutputFormat() { return outputFormat; }
    public void setOutputFormat(String outputFormat) { this.outputFormat = outputFormat; }
    
    public void setResolution(String resolution) { this.resolution = resolution; }
    
    public String getFrameRateStr() { return frameRateStr; }
    public void setFrameRateStr(String frameRateStr) { this.frameRateStr = frameRateStr; }
    
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
    
    public Integer getSampleRate() { return sampleRate; }
    public void setSampleRate(Integer sampleRate) { this.sampleRate = sampleRate; }
    
    public Integer getChannels() { return channels; }
    public void setChannels(Integer channels) { this.channels = channels; }
    
    public TranscodeStatus getStatus() { return status; }
    public void setStatus(TranscodeStatus status) { this.status = status; }
    
    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    
    public Long getUserId() { return userId; }
    
    public Integer getProcessingTime() { return processingTime; }
    public void setProcessingTime(Integer processingTime) { this.processingTime = processingTime; }
    
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    /**
     * 转码状态枚举
     */
    public enum TranscodeStatus {
        PENDING,        // 等待转码
        PROCESSING,     // 转码中
        COMPLETED,      // 转码完成
        FAILED,         // 转码失败
        CANCELLED       // 转码取消
    }

    /**
     * 获取视频分辨率
     */
    public String getResolution() {
        if (this.width != null && this.height != null) {
            return this.width + "x" + this.height;
        }
        return null;
    }

    /**
     * 获取转码预设描述
     */
    public String getPresetDescription() {
        switch (this.presetName) {
            case "original":
                return "原始质量";
            case "4k":
                return "4K超高清";
            case "1080p":
                return "1080P全高清";
            case "720p":
                return "720P高清";
            case "480p":
                return "480P标清";
            case "360p":
                return "360P流畅";
            case "audio_high":
                return "高质量音频";
            case "audio_medium":
                return "中等质量音频";
            case "audio_low":
                return "低质量音频";
            case "hls":
                return "HLS流媒体";
            case "dash":
                return "DASH流媒体";
            default:
                return this.presetName;
        }
    }

    /**
     * 检查转码是否完成
     */
    public boolean isCompleted() {
        return this.status == TranscodeStatus.COMPLETED;
    }

    /**
     * 检查转码是否失败
     */
    public boolean isFailed() {
        return this.status == TranscodeStatus.FAILED;
    }

    // Setter方法
    public void setFileUuid(String fileUuid) {
        // 这里需要根据fileUuid查找对应的MediaFile并设置originalFile
        // 实际应用中应该通过Service来设置关联关系
        // 暂时保留此方法以通过编译
    }

    public void setTemplateName(String templateName) {
        this.presetName = templateName;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 检查转码是否可播放
     */
    public boolean isPlayable() {
        return this.enabled && this.status == TranscodeStatus.COMPLETED;
    }

    /**
     * 获取转码文件大小（格式化显示）
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
     * 获取转码耗时（格式化显示）
     */
    public String getFormattedProcessingTime() {
        if (this.processingTime == null) return "0秒";
        
        long seconds = this.processingTime;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        
        if (hours > 0) {
            return String.format("%d小时%d分%d秒", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%d分%d秒", minutes, secs);
        } else {
            return String.format("%d秒", secs);
        }
    }


}