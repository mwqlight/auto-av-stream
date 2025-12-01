package com.avstream.media.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 媒体缩略图实体类
 * 
 * @author AV Stream Team
 */
@Data
@Entity
@Table(name = "media_thumbnails")
@EqualsAndHashCode(of = "id")
@ToString(exclude = "mediaFile")
public class MediaThumbnail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联的媒体文件 */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_file_id", nullable = false)
    private MediaFile mediaFile;

    /** 缩略图文件路径 */
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    /** 缩略图文件名 */
    @Column(name = "filename", nullable = false, length = 255)
    private String filename;

    /** 缩略图文件大小（字节） */
    @Column(name = "file_size")
    private Long fileSize;

    /** 缩略图宽度（像素） */
    @Column(name = "width", nullable = false)
    private Integer width;

    /** 缩略图高度（像素） */
    @Column(name = "height", nullable = false)
    private Integer height;

    /** 缩略图格式 */
    @Column(name = "format", nullable = false, length = 10)
    private String format;

    /** 缩略图质量（0-100） */
    @Column(name = "quality")
    private Integer quality = 85;

    /** 缩略图类型 */
    @Enumerated(EnumType.STRING)
    @Column(name = "thumbnail_type", nullable = false, length = 20)
    private ThumbnailType thumbnailType;

    /** 截图时间点（秒，仅视频） */
    @Column(name = "capture_time")
    private Double captureTime;

    /** 是否启用 */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    /** 创建时间 */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 缩略图类型枚举
     */
    public enum ThumbnailType {
        SMALL,          // 小图（用于列表）
        MEDIUM,         // 中图（用于预览）
        LARGE,          // 大图（用于详情）
        COVER,          // 封面图
        POSTER,         // 海报图
        SCREENSHOT      // 截图
    }

    /**
     * 获取缩略图尺寸
     */
    public String getSize() {
        return this.width + "x" + this.height;
    }

    /**
     * 获取缩略图文件大小（格式化显示）
     */
    public String getFormattedFileSize() {
        if (this.fileSize == null) return "0 B";
        
        String[] units = {"B", "KB", "MB", "GB"};
        double size = this.fileSize;
        int unitIndex = 0;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", size, units[unitIndex]);
    }

    /**
     * 获取缩略图类型描述
     */
    public String getThumbnailTypeDescription() {
        switch (this.thumbnailType) {
            case SMALL:
                return "小图";
            case MEDIUM:
                return "中图";
            case LARGE:
                return "大图";
            case COVER:
                return "封面图";
            case POSTER:
                return "海报图";
            case SCREENSHOT:
                return "截图";
            default:
                return this.thumbnailType.name();
        }
    }

    /**
     * 检查是否为视频截图
     */
    public boolean isVideoScreenshot() {
        return this.thumbnailType == ThumbnailType.SCREENSHOT && this.captureTime != null;
    }

    /**
     * 获取截图时间点（格式化显示）
     */
    public String getFormattedCaptureTime() {
        if (this.captureTime == null) return "00:00";
        
        int totalSeconds = this.captureTime.intValue();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        
        return String.format("%02d:%02d", minutes, seconds);
    }
}