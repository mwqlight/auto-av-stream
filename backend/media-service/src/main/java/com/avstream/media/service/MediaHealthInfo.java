package com.avstream.media.service;

import lombok.Data;

/**
 * 媒体服务健康信息
 * 
 * @author AV Stream Team
 */
@Data
public class MediaHealthInfo {
    
    /** 媒体服务是否可用 */
    private boolean available;
    
    /** 数据库连接状态 */
    private boolean databaseConnected;
    
    /** 存储服务状态 */
    private boolean storageServiceAvailable;
    
    /** 转码服务状态 */
    private boolean transcodeServiceAvailable;
    
    /** 总媒体文件数量 */
    private long totalMediaFiles;
    
    /** 活跃媒体文件数量 */
    private long activeMediaFiles;
    
    /** 正在处理的转码任务数量 */
    private long processingTranscodeTasks;
    
    /** 失败的转码任务数量 */
    private long failedTranscodeTasks;
    
    /** 健康检查时间戳 */
    private long healthCheckTimestamp;
    
    /** 错误信息（如果有） */
    private String errorMessage;
    
    /**
     * 创建健康的媒体服务信息
     */
    public static MediaHealthInfo healthy(boolean databaseConnected, boolean storageServiceAvailable, 
                                        boolean transcodeServiceAvailable, long totalMediaFiles, 
                                        long activeMediaFiles, long processingTranscodeTasks, 
                                        long failedTranscodeTasks) {
        MediaHealthInfo info = new MediaHealthInfo();
        info.setAvailable(true);
        info.setDatabaseConnected(databaseConnected);
        info.setStorageServiceAvailable(storageServiceAvailable);
        info.setTranscodeServiceAvailable(transcodeServiceAvailable);
        info.setTotalMediaFiles(totalMediaFiles);
        info.setActiveMediaFiles(activeMediaFiles);
        info.setProcessingTranscodeTasks(processingTranscodeTasks);
        info.setFailedTranscodeTasks(failedTranscodeTasks);
        info.setHealthCheckTimestamp(System.currentTimeMillis());
        return info;
    }
    
    /**
     * 创建不健康的媒体服务信息
     */
    public static MediaHealthInfo unhealthy(String errorMessage) {
        MediaHealthInfo info = new MediaHealthInfo();
        info.setAvailable(false);
        info.setErrorMessage(errorMessage);
        info.setHealthCheckTimestamp(System.currentTimeMillis());
        return info;
    }
}