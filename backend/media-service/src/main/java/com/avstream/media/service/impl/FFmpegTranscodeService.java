package com.avstream.media.service.impl;

import com.avstream.media.entity.MediaTranscode;
import com.avstream.media.repository.MediaTranscodeRepository;
import com.avstream.media.service.TranscodeHealthInfo;
import com.avstream.media.service.TranscodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * FFmpeg转码服务实现
 * 
 * @author AV Stream Team
 */
@Service
public class FFmpegTranscodeService implements TranscodeService {

    private static final Logger log = LoggerFactory.getLogger(FFmpegTranscodeService.class);

    @Autowired
    private MediaTranscodeRepository transcodeRepository;

    private static final List<String> SUPPORTED_OUTPUT_FORMATS = Arrays.asList(
            "mp4", "avi", "mkv", "mov", "webm", "flv"
    );

    private static final List<String> TRANSCODE_TEMPLATES = Arrays.asList(
            "1080p_h264", "720p_h264", "480p_h264",
            "1080p_h265", "720p_h265", "480p_h265",
            "mobile_optimized", "web_optimized"
    );

    @Override
    public MediaTranscode createTranscodeTask(String fileUuid, String templateName, Long userId) {
        MediaTranscode transcode = new MediaTranscode();
        transcode.setTranscodeUuid(UUID.randomUUID().toString());
        transcode.setFileUuid(fileUuid);
        transcode.setTemplateName(templateName);
        transcode.setUserId(userId);
        transcode.setStatus(MediaTranscode.TranscodeStatus.PENDING);
        transcode.setProgress(0);
        
        // 设置输出格式和参数
        setTranscodeParameters(transcode, templateName);
        
        return transcodeRepository.save(transcode);
    }

    @Override
    @Async("transcodeExecutor")
    public void startTranscodeTask(String transcodeUuid) {
        try {
            MediaTranscode transcode = transcodeRepository.findByTranscodeUuid(transcodeUuid)
                    .orElseThrow(() -> new RuntimeException("转码任务不存在: " + transcodeUuid));
            
            transcode.setStatus(MediaTranscode.TranscodeStatus.PROCESSING);
            transcodeRepository.save(transcode);
            
            log.info("开始转码任务: {}", transcodeUuid);
            
            // 执行FFmpeg转码命令
            boolean success = executeFFmpegTranscode(transcode);
            
            if (success) {
                transcode.setStatus(MediaTranscode.TranscodeStatus.COMPLETED);
                transcode.setProgress(100);
                log.info("转码任务完成: {}", transcodeUuid);
            } else {
                transcode.setStatus(MediaTranscode.TranscodeStatus.FAILED);
                log.error("转码任务失败: {}", transcodeUuid);
            }
            
            transcodeRepository.save(transcode);
            
        } catch (Exception e) {
            log.error("转码任务执行异常: {}", transcodeUuid, e);
            
            // 更新任务状态为失败
            transcodeRepository.findByTranscodeUuid(transcodeUuid).ifPresent(transcode -> {
                transcode.setStatus(MediaTranscode.TranscodeStatus.FAILED);
                transcodeRepository.save(transcode);
            });
        }
    }

    @Override
    public void cancelTranscodeTask(String transcodeUuid) {
        transcodeRepository.findByTranscodeUuid(transcodeUuid).ifPresent(transcode -> {
            if (transcode.getStatus() == MediaTranscode.TranscodeStatus.PROCESSING) {
                // 这里需要实现取消FFmpeg进程的逻辑
                // 实际项目中可以使用进程管理来终止正在运行的FFmpeg进程
                transcode.setStatus(MediaTranscode.TranscodeStatus.CANCELLED);
                transcodeRepository.save(transcode);
                log.info("取消转码任务: {}", transcodeUuid);
            }
        });
    }

    @Override
    public Integer getTranscodeProgress(String transcodeUuid) {
        return transcodeRepository.findByTranscodeUuid(transcodeUuid)
                .map(MediaTranscode::getProgress)
                .orElse(0);
    }

    @Override
    public MediaTranscode.TranscodeStatus getTranscodeStatus(String transcodeUuid) {
        return transcodeRepository.findByTranscodeUuid(transcodeUuid)
                .map(MediaTranscode::getStatus)
                .orElse(MediaTranscode.TranscodeStatus.FAILED);
    }

    @Override
    public void retryTranscodeTask(String transcodeUuid) {
        transcodeRepository.findByTranscodeUuid(transcodeUuid).ifPresent(transcode -> {
            if (transcode.getStatus() == MediaTranscode.TranscodeStatus.FAILED) {
                transcode.setStatus(MediaTranscode.TranscodeStatus.PENDING);
                transcode.setProgress(0);
                transcode.setRetryCount(transcode.getRetryCount() + 1);
                transcodeRepository.save(transcode);
                
                // 异步重新执行转码任务
                CompletableFuture.runAsync(() -> startTranscodeTask(transcodeUuid));
            }
        });
    }

    @Override
    public void deleteTranscodeTask(String transcodeUuid) {
        transcodeRepository.findByTranscodeUuid(transcodeUuid).ifPresent(transcode -> {
            // 删除转码后的文件（如果存在）
            // 实际项目中需要删除对应的存储文件
            transcodeRepository.delete(transcode);
            log.info("删除转码任务: {}", transcodeUuid);
        });
    }

    @Override
    public List<String> getTranscodeTemplates() {
        return TRANSCODE_TEMPLATES;
    }

    @Override
    public List<String> getSupportedOutputFormats() {
        return SUPPORTED_OUTPUT_FORMATS;
    }

    @Override
    public boolean isTranscodeProcessing(String fileUuid) {
        return transcodeRepository.findByFileUuidAndStatus(fileUuid, 
                MediaTranscode.TranscodeStatus.PROCESSING).isPresent();
    }

    @Override
    public List<MediaTranscode> getTranscodeHistory(String fileUuid) {
        return transcodeRepository.findByFileUuidOrderByCreatedAtDesc(fileUuid);
    }

    @Override
    public List<MediaTranscode> batchCreateTranscodeTasks(List<String> fileUuids, String templateName, Long userId) {
        return fileUuids.stream()
                .map(fileUuid -> createTranscodeTask(fileUuid, templateName, userId))
                .toList();
    }

    @Override
    public void batchCancelTranscodeTasks(List<String> transcodeUuids) {
        transcodeUuids.forEach(this::cancelTranscodeTask);
    }

    @Override
    public void cleanupExpiredTranscodeTasks() {
        // 清理过期的转码任务（例如30天前的失败任务）
        // 实际项目中需要根据业务需求实现
        log.info("执行转码任务清理");
    }

    /**
     * 设置转码参数
     */
    private void setTranscodeParameters(MediaTranscode transcode, String templateName) {
        switch (templateName) {
            case "1080p_h264":
                transcode.setOutputFormat("mp4");
                transcode.setVideoCodec("libx264");
                transcode.setVideoBitrate(4000000L); // 4000k = 4,000,000 bps
                transcode.setResolution("1920x1080");
                transcode.setFrameRateStr("30");
                transcode.setAudioCodec("aac");
                transcode.setAudioBitrate(128000L); // 128k = 128,000 bps
                break;
            case "720p_h264":
                transcode.setOutputFormat("mp4");
                transcode.setVideoCodec("libx264");
                transcode.setVideoBitrate(2000000L); // 2000k = 2,000,000 bps
                transcode.setResolution("1280x720");
                transcode.setFrameRateStr("30");
                transcode.setAudioCodec("aac");
                transcode.setAudioBitrate(128000L); // 128k = 128,000 bps
                break;
            case "480p_h264":
                transcode.setOutputFormat("mp4");
                transcode.setVideoCodec("libx264");
                transcode.setVideoBitrate(1000000L); // 1000k = 1,000,000 bps
                transcode.setResolution("854x480");
                transcode.setFrameRateStr("30");
                transcode.setAudioCodec("aac");
                transcode.setAudioBitrate(96000L); // 96k = 96,000 bps
                break;
            case "mobile_optimized":
                transcode.setOutputFormat("mp4");
                transcode.setVideoCodec("libx264");
                transcode.setVideoBitrate(1500000L); // 1500k = 1,500,000 bps
                transcode.setResolution("720x1280");
                transcode.setFrameRateStr("25");
                transcode.setAudioCodec("aac");
                transcode.setAudioBitrate(96000L); // 96k = 96,000 bps
                break;
            default:
                // 默认配置
                transcode.setOutputFormat("mp4");
                transcode.setVideoCodec("libx264");
                transcode.setVideoBitrate(2000000L); // 2000k = 2,000,000 bps
                transcode.setResolution("1280x720");
                transcode.setFrameRateStr("30");
                transcode.setAudioCodec("aac");
                transcode.setAudioBitrate(128000L); // 128k = 128,000 bps
        }
    }

    /**
     * 执行FFmpeg转码命令
     */
    private boolean executeFFmpegTranscode(MediaTranscode transcode) {
        try {
            // 构建FFmpeg命令
            String inputPath = "/path/to/input/" + transcode.getFileUuid(); // 实际路径需要根据存储配置获取
            String outputPath = "/path/to/output/" + transcode.getTranscodeUuid() + "." + transcode.getOutputFormat();
            
            List<String> command = Arrays.asList(
                    "ffmpeg",
                    "-i", inputPath,
                    "-c:v", transcode.getVideoCodec(),
                    "-b:v", String.valueOf(transcode.getVideoBitrate()),
                    "-s", transcode.getResolution(),
                    "-r", String.valueOf(transcode.getFrameRate()),
                    "-c:a", transcode.getAudioCodec(),
                    "-b:a", String.valueOf(transcode.getAudioBitrate()),
                    "-y", // 覆盖输出文件
                    outputPath
            );
            
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            
            // 读取输出流（用于进度监控）
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // 解析FFmpeg输出，更新转码进度
                updateProgressFromFFmpegOutput(line, transcode);
            }
            
            int exitCode = process.waitFor();
            return exitCode == 0;
            
        } catch (IOException | InterruptedException e) {
            log.error("FFmpeg转码执行失败: {}", transcode.getTranscodeUuid(), e);
            return false;
        }
    }

    /**
     * 从FFmpeg输出解析进度
     */
    private void updateProgressFromFFmpegOutput(String output, MediaTranscode transcode) {
        // 解析FFmpeg输出中的时间信息来估算进度
        // 实际项目中需要更精确的进度计算
        if (output.contains("time=")) {
            // 简单模拟进度更新
            int currentProgress = transcode.getProgress();
            if (currentProgress < 90) {
                transcode.setProgress(currentProgress + 5);
                transcodeRepository.save(transcode);
            }
        }
    }

    @Override
    public boolean healthCheck() {
        try {
            // 检查数据库连接
            boolean dbConnected = transcodeRepository.count() >= 0;
            
            // 检查FFmpeg是否可用
            boolean ffmpegAvailable = checkFFmpegAvailability();
            
            // 所有依赖都可用才认为服务健康
            return dbConnected && ffmpegAvailable;
        } catch (Exception e) {
            log.error("转码服务健康检查失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public TranscodeHealthInfo getTranscodeHealthInfo() {
        try {
            // 检查数据库连接
            boolean dbConnected = true;
            try {
                transcodeRepository.count();
            } catch (Exception e) {
                dbConnected = false;
                log.warn("数据库连接检查失败: {}", e.getMessage());
            }
            
            // 检查FFmpeg是否可用
            boolean ffmpegAvailable = checkFFmpegAvailability();
            
            // 统计转码任务信息
            long totalTranscodeTasks = transcodeRepository.count();
            
            // 使用countByStatus方法获取不同状态的转码任务数量
            List<Object[]> statusCounts = transcodeRepository.countByStatus();
            long processingTranscodeTasks = 0;
            long successfulTranscodeTasks = 0;
            long failedTranscodeTasks = 0;
            
            for (Object[] statusCount : statusCounts) {
                MediaTranscode.TranscodeStatus status = (MediaTranscode.TranscodeStatus) statusCount[0];
                Long count = (Long) statusCount[1];
                
                switch (status) {
                    case PROCESSING:
                        processingTranscodeTasks = count;
                        break;
                    case COMPLETED:
                        successfulTranscodeTasks = count;
                        break;
                    case FAILED:
                        failedTranscodeTasks = count;
                        break;
                    default:
                        // 其他状态不统计
                        break;
                }
            }
            
            // 计算平均转码时间（这里简化处理，实际项目中需要从数据库统计）
            long averageTranscodeTime = 0;
            if (successfulTranscodeTasks > 0) {
                // 模拟平均转码时间，实际项目中需要从数据库计算
                averageTranscodeTime = 120000L; // 2分钟
            }
            
            // 如果所有核心依赖都可用，则服务健康
            boolean serviceAvailable = dbConnected && ffmpegAvailable;
            
            if (serviceAvailable) {
                return TranscodeHealthInfo.healthy(
                    ffmpegAvailable, dbConnected,
                    totalTranscodeTasks, processingTranscodeTasks,
                    successfulTranscodeTasks, failedTranscodeTasks,
                    averageTranscodeTime
                );
            } else {
                String errorMessage = "转码服务不可用: ";
                if (!dbConnected) errorMessage += "数据库连接失败; ";
                if (!ffmpegAvailable) errorMessage += "FFmpeg不可用; ";
                return TranscodeHealthInfo.unhealthy(errorMessage.trim());
            }
        } catch (Exception e) {
            log.error("获取转码服务健康信息失败: {}", e.getMessage(), e);
            return TranscodeHealthInfo.unhealthy("获取健康信息时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 检查FFmpeg是否可用
     */
    private boolean checkFFmpegAvailability() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-version");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            log.warn("FFmpeg检查失败: {}", e.getMessage());
            return false;
        }
    }
}