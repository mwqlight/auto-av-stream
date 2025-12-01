package com.avstream.media.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 上传请求DTO
 * 
 * @author AV Stream Team
 */
@Data
public class UploadRequest {

    /** 文件名 */
    @NotBlank(message = "文件名不能为空")
    @Size(max = 255, message = "文件名长度不能超过255个字符")
    private String filename;

    /** 文件大小（字节） */
    @NotNull(message = "文件大小不能为空")
    private Long fileSize;

    /** 文件类型 */
    @NotBlank(message = "文件类型不能为空")
    private String fileType;

    /** MIME类型 */
    @NotBlank(message = "MIME类型不能为空")
    private String mimeType;

    /** 是否公开 */
    private Boolean isPublic = false;

    /** 是否启用自动转码 */
    private Boolean autoTranscode = true;

    /** 转码预设列表 */
    private String[] transcodePresets = {"1080p", "720p", "480p"};

    /** 自定义标签 */
    private String[] tags;

    /** 文件描述 */
    @Size(max = 1000, message = "文件描述长度不能超过1000个字符")
    private String description;

    /** 分类ID */
    private Long categoryId;

    /** 存储桶名称 */
    private String bucketName;

    /** 是否启用水印 */
    private Boolean enableWatermark = false;

    /** 水印配置 */
    private WatermarkConfig watermarkConfig;

    /**
     * 水印配置类
     */
    @Data
    public static class WatermarkConfig {
        
        /** 水印文本 */
        @Size(max = 100, message = "水印文本长度不能超过100个字符")
        private String text;

        /** 水印图片路径 */
        private String imagePath;

        /** 水印位置 */
        private WatermarkPosition position = WatermarkPosition.BOTTOM_RIGHT;

        /** 水印透明度（0-100） */
        private Integer opacity = 50;

        /** 水印大小（相对于视频宽度的百分比） */
        private Integer size = 10;
    }

    /**
     * 水印位置枚举
     */
    public enum WatermarkPosition {
        TOP_LEFT,       // 左上角
        TOP_RIGHT,      // 右上角
        BOTTOM_LEFT,    // 左下角
        BOTTOM_RIGHT,   // 右下角
        CENTER          // 中心
    }

    /**
     * 获取文件扩展名
     */
    public String getFileExtension() {
        if (this.filename != null && this.filename.contains(".")) {
            return this.filename.substring(this.filename.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }

    /**
     * 检查是否为视频文件
     */
    public boolean isVideoFile() {
        return this.mimeType != null && this.mimeType.startsWith("video/");
    }

    /**
     * 检查是否为音频文件
     */
    public boolean isAudioFile() {
        return this.mimeType != null && this.mimeType.startsWith("audio/");
    }

    /**
     * 检查是否为图片文件
     */
    public boolean isImageFile() {
        return this.mimeType != null && this.mimeType.startsWith("image/");
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
    
    // Manual getter methods for fields not handled by @Data
    public Boolean getIsPublic() {
        return isPublic;
    }
    
    public String getDescription() {
        return description;
    }
}