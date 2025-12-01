package com.avstream.media.service;

import lombok.Data;

/**
 * 视频信息类
 */
@Data
public class VideoInfo {
    private String filename;
    private String format;
    private long duration; // 秒
    private long size; // 字节
    private int width;
    private int height;
    private double frameRate;
    private String videoCodec;
    private String audioCodec;
    private int audioChannels;
    private int audioSampleRate;
    private String bitrate;
    
    public VideoInfo() {}
    
    public VideoInfo(String filename, String format, long duration, long size, 
                    int width, int height, double frameRate, String videoCodec, 
                    String audioCodec, int audioChannels, int audioSampleRate, String bitrate) {
        this.filename = filename;
        this.format = format;
        this.duration = duration;
        this.size = size;
        this.width = width;
        this.height = height;
        this.frameRate = frameRate;
        this.videoCodec = videoCodec;
        this.audioCodec = audioCodec;
        this.audioChannels = audioChannels;
        this.audioSampleRate = audioSampleRate;
        this.bitrate = bitrate;
    }
    
    /**
     * 获取视频分辨率
     */
    public String getResolution() {
        return width + "x" + height;
    }
    
    /**
     * 获取视频时长（格式化）
     */
    public String getFormattedDuration() {
        long hours = duration / 3600;
        long minutes = (duration % 3600) / 60;
        long seconds = duration % 60;
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
    
    /**
     * 获取文件大小（格式化）
     */
    public String getFormattedSize() {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * 检查是否为高清视频
     */
    public boolean isHD() {
        return width >= 1280 && height >= 720;
    }
    
    /**
     * 检查是否为全高清视频
     */
    public boolean isFullHD() {
        return width >= 1920 && height >= 1080;
    }
    
    /**
     * 检查是否为4K视频
     */
    public boolean is4K() {
        return width >= 3840 && height >= 2160;
    }
    
    /**
     * 获取视频宽高比
     */
    public String getAspectRatio() {
        int gcd = gcd(width, height);
        int aspectWidth = width / gcd;
        int aspectHeight = height / gcd;
        return aspectWidth + ":" + aspectHeight;
    }
    
    /**
     * 计算最大公约数
     */
    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}