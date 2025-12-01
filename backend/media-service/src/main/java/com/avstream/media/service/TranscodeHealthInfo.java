package com.avstream.media.service;

import lombok.Data;

/**
 * 转码服务健康信息
 * 
 * @author AV Stream Team
 */
@Data
public class TranscodeHealthInfo {
    
    /** 转码服务是否可用 */
    private boolean available;
    
    /** FFmpeg是否可用 */
    private boolean ffmpegAvailable;
    
    /** 数据库连接状态 */
    private boolean databaseConnected;
    
    /** 总转码任务数量 */
    private long totalTranscodeTasks;
    
    /** 正在处理的转码任务数量 */
    private long processingTranscodeTasks;
    
    /** 成功的转码任务数量 */
    private long successfulTranscodeTasks;
    
    /** 失败的转码任务数量 */
    private long failedTranscodeTasks;
    
    /** 平均转码时间（毫秒） */
    private long averageTranscodeTime;
    
    /** 健康检查时间戳 */
    private long healthCheckTimestamp;
    
    /** 错误信息（如果有） */
    private String errorMessage;
    
    /**
     * 创建健康的转码服务信息
     */
    public static TranscodeHealthInfo healthy(boolean ffmpegAvailable, boolean databaseConnected, 
                                            long totalTranscodeTasks, long processingTranscodeTasks,
                                            long successfulTranscodeTasks, long failedTranscodeTasks,
                                            long averageTranscodeTime) {
        TranscodeHealthInfo info = new TranscodeHealthInfo();
        info.setAvailable(true);
        info.setFfmpegAvailable(ffmpegAvailable);
        info.setDatabaseConnected(databaseConnected);
        info.setTotalTranscodeTasks(totalTranscodeTasks);
        info.setProcessingTranscodeTasks(processingTranscodeTasks);
        info.setSuccessfulTranscodeTasks(successfulTranscodeTasks);
        info.setFailedTranscodeTasks(failedTranscodeTasks);
        info.setAverageTranscodeTime(averageTranscodeTime);
        info.setHealthCheckTimestamp(System.currentTimeMillis());
        return info;
    }
    
    /**
     * 创建不健康的转码服务信息
     */
    public static TranscodeHealthInfo unhealthy(String errorMessage) {
        TranscodeHealthInfo info = new TranscodeHealthInfo();
        info.setAvailable(false);
        info.setErrorMessage(errorMessage);
        info.setHealthCheckTimestamp(System.currentTimeMillis());
        return info;
    }
}