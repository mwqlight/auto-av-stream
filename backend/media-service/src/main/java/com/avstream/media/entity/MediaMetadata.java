package com.avstream.media.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * 媒体元数据实体类
 * 
 * @author AV Stream Team
 */
@Data
@Entity
@Table(name = "media_metadata")
@EqualsAndHashCode(of = "id")
@ToString(exclude = "mediaFile")
public class MediaMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联的媒体文件 */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_file_id", nullable = false)
    private MediaFile mediaFile;

    /** 视频相关元数据 */
    
    /** 视频时长（秒） */
    @Column(name = "duration")
    private BigDecimal duration;

    /** 视频宽度（像素） */
    @Column(name = "width")
    private Integer width;

    /** 视频高度（像素） */
    @Column(name = "height")
    private Integer height;

    /** 视频编码器 */
    @Column(name = "video_codec", length = 50)
    private String videoCodec;

    /** 视频码率（bps） */
    @Column(name = "video_bitrate")
    private Long videoBitrate;

    /** 视频帧率（fps） */
    @Column(name = "frame_rate")
    private BigDecimal frameRate;

    /** 视频像素格式 */
    @Column(name = "pixel_format", length = 20)
    private String pixelFormat;

    /** 视频宽高比 */
    @Column(name = "aspect_ratio", length = 10)
    private String aspectRatio;

    /** 音频相关元数据 */
    
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

    /** 音频位深度 */
    @Column(name = "bit_depth")
    private Integer bitDepth;

    /** 通用元数据 */
    
    /** 文件编码格式 */
    @Column(name = "container_format", length = 20)
    private String containerFormat;

    /** 文件创建时间 */
    @Column(name = "file_creation_time", length = 100)
    private String fileCreationTime;

    /** 文件修改时间 */
    @Column(name = "file_modification_time", length = 100)
    private String fileModificationTime;

    /** 文件编码器 */
    @Column(name = "encoder", length = 100)
    private String encoder;

    /** 文件大小（字节） */
    @Column(name = "file_size")
    private Long fileSize;

    /** 文件CRC校验值 */
    @Column(name = "crc_checksum", length = 32)
    private String crcChecksum;

    /** 元数据提取时间 */
    @Column(name = "metadata_extracted_at", length = 100)
    private String metadataExtractedAt;

    /** 元数据版本 */
    @Column(name = "metadata_version", length = 10)
    private String metadataVersion = "1.0";

    /** 自定义元数据（JSON格式） */
    @Column(name = "custom_metadata", columnDefinition = "TEXT")
    private String customMetadata;

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
     * 获取视频时长（格式化显示）
     */
    public String getFormattedDuration() {
        if (this.duration == null) return "00:00";
        
        long totalSeconds = this.duration.longValue();
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
     * 获取视频码率（格式化显示）
     */
    public String getFormattedVideoBitrate() {
        if (this.videoBitrate == null) return "0 bps";
        
        String[] units = {"bps", "Kbps", "Mbps", "Gbps"};
        double bitrate = this.videoBitrate;
        int unitIndex = 0;
        
        while (bitrate >= 1000 && unitIndex < units.length - 1) {
            bitrate /= 1000;
            unitIndex++;
        }
        
        return String.format("%.2f %s", bitrate, units[unitIndex]);
    }

    /**
     * 获取音频码率（格式化显示）
     */
    public String getFormattedAudioBitrate() {
        if (this.audioBitrate == null) return "0 bps";
        
        String[] units = {"bps", "Kbps", "Mbps"};
        double bitrate = this.audioBitrate;
        int unitIndex = 0;
        
        while (bitrate >= 1000 && unitIndex < units.length - 1) {
            bitrate /= 1000;
            unitIndex++;
        }
        
        return String.format("%.2f %s", bitrate, units[unitIndex]);
    }

    /**
     * 检查是否为高清视频
     */
    public boolean isHdVideo() {
        if (this.width == null || this.height == null) return false;
        return this.width >= 1280 && this.height >= 720;
    }

    /**
     * 检查是否为4K视频
     */
    public boolean is4kVideo() {
        if (this.width == null || this.height == null) return false;
        return this.width >= 3840 && this.height >= 2160;
    }

    /**
     * 检查是否为高质量音频
     */
    public boolean isHighQualityAudio() {
        if (this.audioBitrate == null) return false;
        return this.audioBitrate >= 256000; // 256 Kbps
    }
}