package com.avstream.media.task;

import com.avstream.media.entity.MediaFile;
import com.avstream.media.entity.MediaTranscode;
import com.avstream.media.repository.MediaFileRepository;
import com.avstream.media.repository.MediaTranscodeRepository;
import com.avstream.media.service.TranscodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类
 * 
 * @author AV Stream Team
 */
@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private MediaTranscodeRepository transcodeRepository;

    @Autowired
    private TranscodeService transcodeService;

    /**
     * 清理临时文件（每小时执行一次）
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanupTemporaryFiles() {
        log.info("开始清理临时文件");
        
        try {
            // 清理24小时前创建的临时文件
            LocalDateTime threshold = LocalDateTime.now().minusHours(24);
            List<MediaFile> temporaryFiles = mediaFileRepository.findTemporaryFilesOlderThan(threshold);
            
            int deletedCount = 0;
            for (MediaFile file : temporaryFiles) {
                try {
                    // 删除文件记录和存储文件
                    // 实际项目中需要调用存储服务删除物理文件
                    mediaFileRepository.delete(file);
                    deletedCount++;
                } catch (Exception e) {
                    log.error("删除临时文件失败: {}", file.getFileUuid(), e);
                }
            }
            
            log.info("临时文件清理完成，共删除 {} 个文件", deletedCount);
            
        } catch (Exception e) {
            log.error("临时文件清理任务执行失败", e);
        }
    }

    /**
     * 重试失败的转码任务（每30分钟执行一次）
     */
    @Scheduled(cron = "0 */30 * * * ?")
    public void retryFailedTranscodeTasks() {
        log.info("开始重试失败的转码任务");
        
        try {
            // 查找2小时内失败的转码任务，且重试次数小于3次
            LocalDateTime threshold = LocalDateTime.now().minusHours(2);
            List<MediaTranscode> failedTasks = transcodeRepository.findFailedTasksForRetry(threshold, 3);
            
            int retriedCount = 0;
            for (MediaTranscode task : failedTasks) {
                try {
                    transcodeService.retryTranscodeTask(task.getTranscodeUuid());
                    retriedCount++;
                } catch (Exception e) {
                    log.error("重试转码任务失败: {}", task.getTranscodeUuid(), e);
                }
            }
            
            log.info("转码任务重试完成，共重试 {} 个任务", retriedCount);
            
        } catch (Exception e) {
            log.error("转码任务重试任务执行失败", e);
        }
    }

    /**
     * 清理过期的转码任务（每天凌晨2点执行）
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredTranscodeTasks() {
        log.info("开始清理过期的转码任务");
        
        try {
            // 清理30天前完成的转码任务
            LocalDateTime threshold = LocalDateTime.now().minusDays(30);
            List<MediaTranscode> expiredTasks = transcodeRepository.findExpiredTasks(threshold);
            
            int deletedCount = 0;
            for (MediaTranscode task : expiredTasks) {
                try {
                    transcodeService.deleteTranscodeTask(task.getTranscodeUuid());
                    deletedCount++;
                } catch (Exception e) {
                    log.error("删除过期转码任务失败: {}", task.getTranscodeUuid(), e);
                }
            }
            
            log.info("过期转码任务清理完成，共删除 {} 个任务", deletedCount);
            
        } catch (Exception e) {
            log.error("转码任务清理任务执行失败", e);
        }
    }

    /**
     * 统计存储使用情况（每小时执行一次）
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void calculateStorageUsage() {
        log.info("开始统计存储使用情况");
        
        try {
            // 统计总文件数量
            long totalFiles = mediaFileRepository.count();
            
            // 统计总存储空间使用量
            Long totalStorage = mediaFileRepository.calculateTotalStorageUsage();
            
            // 统计各类型文件数量
            long imageCount = mediaFileRepository.countByFileType("IMAGE");
            long videoCount = mediaFileRepository.countByFileType("VIDEO");
            long audioCount = mediaFileRepository.countByFileType("AUDIO");
            long otherCount = mediaFileRepository.countByFileType("OTHER");
            
            log.info("存储使用统计完成:");
            log.info("总文件数: {}", totalFiles);
            log.info("总存储空间: {} bytes", totalStorage);
            log.info("图片文件: {}", imageCount);
            log.info("视频文件: {}", videoCount);
            log.info("音频文件: {}", audioCount);
            log.info("其他文件: {}", otherCount);
            
        } catch (Exception e) {
            log.error("存储使用统计任务执行失败", e);
        }
    }

    /**
     * 检查转码任务状态（每5分钟执行一次）
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void checkTranscodeTaskStatus() {
        log.debug("开始检查转码任务状态");
        
        try {
            // 查找处理时间超过2小时的转码任务
            LocalDateTime threshold = LocalDateTime.now().minusHours(2);
            List<MediaTranscode> longRunningTasks = transcodeRepository.findLongRunningTasks(threshold);
            
            for (MediaTranscode task : longRunningTasks) {
                log.warn("转码任务处理时间过长: {} (开始时间: {})", 
                        task.getTranscodeUuid(), task.getCreatedAt());
                
                // 可以发送告警通知或自动重启任务
                // 实际项目中根据业务需求实现
            }
            
            log.debug("转码任务状态检查完成，发现 {} 个长时间运行的任务", longRunningTasks.size());
            
        } catch (Exception e) {
            log.error("转码任务状态检查任务执行失败", e);
        }
    }
}