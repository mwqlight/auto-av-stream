package com.avstream.media.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * FFmpeg音视频处理服务
 */
@Service
public class FFmpegService {
    
    private static final Logger log = LoggerFactory.getLogger(FFmpegService.class);

    @Value("${ffmpeg.path:/usr/bin/ffmpeg}")
    private String ffmpegPath;

    @Value("${ffmpeg.output-dir:/tmp/av-stream/output}")
    private String outputDir;

    @Value("${ffmpeg.threads:4}")
    private int threads;

    /**
     * 视频转码
     */
    public CompletableFuture<String> transcodeVideo(String inputPath, String outputPath, 
                                                   String codec, String preset, String quality) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ensureOutputDirectory(outputPath);
                
                List<String> command = new ArrayList<>();
                command.add(ffmpegPath);
                command.add("-i");
                command.add(inputPath);
                command.add("-c:v");
                command.add(codec);
                command.add("-preset");
                command.add(preset);
                command.add("-crf");
                command.add(quality);
                command.add("-threads");
                command.add(String.valueOf(threads));
                command.add("-y"); // 覆盖输出文件
                command.add(outputPath);

                ProcessBuilder processBuilder = new ProcessBuilder(command);
                processBuilder.redirectErrorStream(true);
                
                Process process = processBuilder.start();
                
                // 读取输出
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("FFmpeg输出: {}", line);
                    }
                }
                
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    log.info("视频转码成功: {} -> {}", inputPath, outputPath);
                    return outputPath;
                } else {
                    throw new RuntimeException("FFmpeg转码失败，退出码: " + exitCode);
                }
                
            } catch (IOException | InterruptedException e) {
                log.error("视频转码失败: {}", e.getMessage(), e);
                throw new RuntimeException("视频转码失败", e);
            }
        });
    }

    /**
     * 提取视频缩略图
     */
    public CompletableFuture<String> extractThumbnail(String videoPath, String outputPath, 
                                                      int width, int height, String timestamp) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ensureOutputDirectory(outputPath);
                
                List<String> command = new ArrayList<>();
                command.add(ffmpegPath);
                command.add("-i");
                command.add(videoPath);
                command.add("-ss");
                command.add(timestamp);
                command.add("-vframes");
                command.add("1");
                command.add("-s");
                command.add(width + "x" + height);
                command.add("-y");
                command.add(outputPath);

                ProcessBuilder processBuilder = new ProcessBuilder(command);
                Process process = processBuilder.start();
                
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    log.info("缩略图提取成功: {} -> {}", videoPath, outputPath);
                    return outputPath;
                } else {
                    throw new RuntimeException("缩略图提取失败");
                }
                
            } catch (IOException | InterruptedException e) {
                log.error("缩略图提取失败: {}", e.getMessage(), e);
                throw new RuntimeException("缩略图提取失败", e);
            }
        });
    }

    /**
     * 获取视频信息
     */
    public CompletableFuture<VideoInfo> getVideoInfo(String videoPath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<String> command = new ArrayList<>();
                command.add(ffmpegPath);
                command.add("-i");
                command.add(videoPath);

                ProcessBuilder processBuilder = new ProcessBuilder(command);
                processBuilder.redirectErrorStream(true);
                
                Process process = processBuilder.start();
                
                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }
                
                process.waitFor();
                
                return parseVideoInfo(output.toString(), videoPath);
                
            } catch (IOException | InterruptedException e) {
                log.error("获取视频信息失败: {}", e.getMessage(), e);
                throw new RuntimeException("获取视频信息失败", e);
            }
        });
    }

    /**
     * 音频提取
     */
    public CompletableFuture<String> extractAudio(String videoPath, String outputPath, 
                                                  String audioCodec, String bitrate) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ensureOutputDirectory(outputPath);
                
                List<String> command = new ArrayList<>();
                command.add(ffmpegPath);
                command.add("-i");
                command.add(videoPath);
                command.add("-vn"); // 禁用视频
                command.add("-c:a");
                command.add(audioCodec);
                command.add("-b:a");
                command.add(bitrate);
                command.add("-y");
                command.add(outputPath);

                ProcessBuilder processBuilder = new ProcessBuilder(command);
                Process process = processBuilder.start();
                
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    log.info("音频提取成功: {} -> {}", videoPath, outputPath);
                    return outputPath;
                } else {
                    throw new RuntimeException("音频提取失败");
                }
                
            } catch (IOException | InterruptedException e) {
                log.error("音频提取失败: {}", e.getMessage(), e);
                throw new RuntimeException("音频提取失败", e);
            }
        });
    }

    /**
     * 视频剪辑
     */
    public CompletableFuture<String> clipVideo(String inputPath, String outputPath, 
                                              String startTime, String duration) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ensureOutputDirectory(outputPath);
                
                List<String> command = new ArrayList<>();
                command.add(ffmpegPath);
                command.add("-i");
                command.add(inputPath);
                command.add("-ss");
                command.add(startTime);
                command.add("-t");
                command.add(duration);
                command.add("-c");
                command.add("copy"); // 直接复制流
                command.add("-y");
                command.add(outputPath);

                ProcessBuilder processBuilder = new ProcessBuilder(command);
                Process process = processBuilder.start();
                
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    log.info("视频剪辑成功: {} -> {}", inputPath, outputPath);
                    return outputPath;
                } else {
                    throw new RuntimeException("视频剪辑失败");
                }
                
            } catch (IOException | InterruptedException e) {
                log.error("视频剪辑失败: {}", e.getMessage(), e);
                throw new RuntimeException("视频剪辑失败", e);
            }
        });
    }

    private void ensureOutputDirectory(String outputPath) throws IOException {
        Path path = Paths.get(outputPath);
        Path parentDir = path.getParent();
        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
    }

    private VideoInfo parseVideoInfo(String ffmpegOutput, String videoPath) {
        VideoInfo info = new VideoInfo();
        info.setFilePath(videoPath);
        
        // 解析视频信息（简化版）
        String[] lines = ffmpegOutput.split("\n");
        for (String line : lines) {
            if (line.contains("Duration:")) {
                // 解析时长
                String duration = line.split("Duration: ")[1].split(",")[0].trim();
                info.setDuration(duration);
            } else if (line.contains("Video:")) {
                // 解析视频信息
                String[] parts = line.split(",");
                for (String part : parts) {
                    part = part.trim();
                    if (part.contains("x")) {
                        // 解析分辨率
                        String[] res = part.split("x");
                        if (res.length == 2) {
                            try {
                                info.setWidth(Integer.parseInt(res[0].trim()));
                                info.setHeight(Integer.parseInt(res[1].split(" ")[0].trim()));
                            } catch (NumberFormatException e) {
                                log.warn("解析分辨率失败: {}", part);
                            }
                        }
                    } else if (part.contains("fps")) {
                        // 解析帧率
                        try {
                            String fps = part.split("fps")[0].trim();
                            info.setFrameRate(Double.parseDouble(fps));
                        } catch (NumberFormatException e) {
                            log.warn("解析帧率失败: {}", part);
                        }
                    }
                }
            }
        }
        
        // 获取文件大小
        File file = new File(videoPath);
        if (file.exists()) {
            info.setFileSize(file.length());
        }
        
        return info;
    }

    /**
     * 视频信息类
     */
    public static class VideoInfo {
        private String filePath;
        private String duration;
        private int width;
        private int height;
        private double frameRate;
        private long fileSize;
        
        // getter和setter方法
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        
        public int getWidth() { return width; }
        public void setWidth(int width) { this.width = width; }
        
        public int getHeight() { return height; }
        public void setHeight(int height) { this.height = height; }
        
        public double getFrameRate() { return frameRate; }
        public void setFrameRate(double frameRate) { this.frameRate = frameRate; }
        
        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    }
}