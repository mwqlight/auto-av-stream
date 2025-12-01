package com.avstream.media.service;

import lombok.Data;

/**
 * 存储健康信息
 * 
 * @author AV Stream Team
 */
@Data
public class StorageHealthInfo {
    
    /** 存储服务是否可用 */
    private boolean available;
    
    /** 总存储容量（字节） */
    private long totalCapacity;
    
    /** 已使用存储容量（字节） */
    private long usedCapacity;
    
    /** 可用存储容量（字节） */
    private long freeCapacity;
    
    /** 存储使用率（百分比） */
    private double usagePercentage;
    
    /** 文件数量 */
    private long fileCount;
    
    /** 存储桶数量 */
    private int bucketCount;
    
    /** 健康检查时间戳 */
    private long healthCheckTimestamp;
    
    /** 错误信息（如果有） */
    private String errorMessage;
    
    /**
     * 创建健康的存储信息
     */
    public static StorageHealthInfo healthy(long totalCapacity, long usedCapacity, long fileCount, int bucketCount) {
        StorageHealthInfo info = new StorageHealthInfo();
        info.setAvailable(true);
        info.setTotalCapacity(totalCapacity);
        info.setUsedCapacity(usedCapacity);
        info.setFreeCapacity(totalCapacity - usedCapacity);
        info.setUsagePercentage(totalCapacity > 0 ? (usedCapacity * 100.0 / totalCapacity) : 0);
        info.setFileCount(fileCount);
        info.setBucketCount(bucketCount);
        info.setHealthCheckTimestamp(System.currentTimeMillis());
        return info;
    }
    
    /**
     * 创建不健康的存储信息
     */
    public static StorageHealthInfo unhealthy(String errorMessage) {
        StorageHealthInfo info = new StorageHealthInfo();
        info.setAvailable(false);
        info.setErrorMessage(errorMessage);
        info.setHealthCheckTimestamp(System.currentTimeMillis());
        return info;
    }
}