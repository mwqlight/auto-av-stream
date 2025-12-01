package com.avstream.media.config;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.io.File;

/**
 * FFmpeg配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ffmpeg")
public class FFmpegConfig {
    
    private static final Logger log = LoggerFactory.getLogger(FFmpegConfig.class);
    
    /** FFmpeg可执行文件路径 */
    private String executablePath = "ffmpeg";
    
    /** FFprobe可执行文件路径 */
    private String ffprobePath = "ffprobe";
    
    /** 默认转码线程数 */
    private int threadCount = 2;
    
    /** 默认转码质量（0-51，越小质量越好） */
    private int defaultQuality = 23;
    
    /** 默认视频编码器 */
    private String defaultVideoCodec = "libx264";
    
    /** 默认音频编码器 */
    private String defaultAudioCodec = "aac";
    
    /** 默认输出格式 */
    private String defaultOutputFormat = "mp4";
    
    /** 临时文件目录 */
    private String tempDir = "/tmp/ffmpeg";
    
    /** 最大转码时间（分钟） */
    private int maxTranscodeTime = 60;
    
    /** 是否启用硬件加速 */
    private boolean hardwareAcceleration = false;
    
    /** 硬件加速类型（cuda, vaapi, qsv等） */
    private String hardwareAccelerationType = "cuda";
    
    /** 最大并发转码任务数 */
    private int maxConcurrentTasks = 4;
    
    /** 转码超时时间（秒） */
    private int timeoutSeconds = 3600;
    
    /** 是否启用转码进度监控 */
    private boolean progressMonitoring = true;
    
    /** 转码进度更新间隔（毫秒） */
    private int progressUpdateInterval = 1000;
    
    /**
     * 初始化配置
     */
    @PostConstruct
    public void init() {
        log.info("FFmpeg配置初始化...");
        log.info("FFmpeg路径: {}", executablePath);
        log.info("FFprobe路径: {}", ffprobePath);
        log.info("默认线程数: {}", threadCount);
        log.info("最大并发任务数: {}", maxConcurrentTasks);
        
        // 创建临时目录
        createTempDir();
        
        // 检查FFmpeg是否可用
        checkFFmpegAvailability();
    }
    
    /**
     * 创建临时目录
     */
    private void createTempDir() {
        File tempDirFile = new File(tempDir);
        if (!tempDirFile.exists()) {
            if (tempDirFile.mkdirs()) {
                log.info("创建FFmpeg临时目录: {}", tempDir);
            } else {
                log.warn("创建FFmpeg临时目录失败: {}", tempDir);
            }
        }
    }
    
    /**
     * 检查FFmpeg是否可用
     */
    private void checkFFmpegAvailability() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(executablePath, "-version");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                log.info("FFmpeg可用: {}", executablePath);
            } else {
                log.warn("FFmpeg不可用或版本检查失败: {}", executablePath);
            }
        } catch (Exception e) {
            log.warn("检查FFmpeg可用性失败: {}", e.getMessage());
        }
        
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(ffprobePath, "-version");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                log.info("FFprobe可用: {}", ffprobePath);
            } else {
                log.warn("FFprobe不可用或版本检查失败: {}", ffprobePath);
            }
        } catch (Exception e) {
            log.warn("检查FFprobe可用性失败: {}", e.getMessage());
        }
    }
    
    /**
     * 获取转码模板参数
     */
    public String[] getTranscodeParams(String template) {
        switch (template) {
            case "1080p_h264":
                return new String[]{
                    "-c:v", "libx264",
                    "-preset", "medium",
                    "-crf", "23",
                    "-maxrate", "4M",
                    "-bufsize", "8M",
                    "-vf", "scale=1920:1080",
                    "-c:a", "aac",
                    "-b:a", "128k"
                };
            case "720p_h264":
                return new String[]{
                    "-c:v", "libx264",
                    "-preset", "medium",
                    "-crf", "23",
                    "-maxrate", "2M",
                    "-bufsize", "4M",
                    "-vf", "scale=1280:720",
                    "-c:a", "aac",
                    "-b:a", "128k"
                };
            case "480p_h264":
                return new String[]{
                    "-c:v", "libx264",
                    "-preset", "medium",
                    "-crf", "23",
                    "-maxrate", "1M",
                    "-bufsize", "2M",
                    "-vf", "scale=854:480",
                    "-c:a", "aac",
                    "-b:a", "96k"
                };
            case "1080p_h265":
                return new String[]{
                    "-c:v", "libx265",
                    "-preset", "medium",
                    "-crf", "28",
                    "-maxrate", "3M",
                    "-bufsize", "6M",
                    "-vf", "scale=1920:1080",
                    "-c:a", "aac",
                    "-b:a", "128k"
                };
            default:
                return new String[]{
                    "-c:v", "copy",
                    "-c:a", "copy"
                };
        }
    }
    
    /**
     * 获取缩略图参数
     */
    public String[] getThumbnailParams() {
        return new String[]{
            "-vf", "thumbnail",
            "-frames:v", "1",
            "-ss", "00:00:01"
        };
    }
    
    /**
     * 获取音频提取参数
     */
    public String[] getAudioExtractParams() {
        return new String[]{
            "-vn",
            "-c:a", "aac",
            "-b:a", "128k"
        };
    }
}