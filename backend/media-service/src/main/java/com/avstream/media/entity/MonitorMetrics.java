package com.avstream.media.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 监控指标实体类
 * 用于存储媒体服务的监控指标数据
 * 
 * @author AV Stream Team
 */
@Entity
@Table(name = "monitor_metrics")
@Data
public class MonitorMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 指标类型 */
    @Column(name = "metric_type", nullable = false, length = 50)
    private String metricType;

    /** 指标名称 */
    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;

    /** 指标值 */
    @Column(name = "metric_value", nullable = false)
    private Double metricValue;

    /** 指标单位 */
    @Column(name = "metric_unit", length = 20)
    private String metricUnit;

    /** 服务名称 */
    @Column(name = "service_name", nullable = false, length = 50)
    private String serviceName;

    /** 实例ID */
    @Column(name = "instance_id", length = 100)
    private String instanceId;

    /** 标签（JSON格式） */
    @Column(name = "labels", length = 500)
    private String labels;

    /** 采集时间 */
    @Column(name = "collect_time", nullable = false)
    private LocalDateTime collectTime;

    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 指标类型枚举
     */
    public enum MetricType {
        SYSTEM,     // 系统指标
        SERVICE,    // 服务指标
        BUSINESS,   // 业务指标
        PERFORMANCE // 性能指标
    }

    /**
     * 常用指标名称
     */
    public static class MetricNames {
        // 系统指标
        public static final String CPU_USAGE = "cpu_usage";
        public static final String MEMORY_USAGE = "memory_usage";
        public static final String DISK_USAGE = "disk_usage";
        public static final String NETWORK_IO = "network_io";
        
        // 服务指标
        public static final String ACTIVE_CONNECTIONS = "active_connections";
        public static final String REQUEST_COUNT = "request_count";
        public static final String ERROR_COUNT = "error_count";
        public static final String RESPONSE_TIME = "response_time";
        
        // 业务指标
        public static final String UPLOAD_COUNT = "upload_count";
        public static final String DOWNLOAD_COUNT = "download_count";
        public static final String TRANSCODE_COUNT = "transcode_count";
        public static final String STREAMING_COUNT = "streaming_count";
        
        // 性能指标
        public static final String THROUGHPUT = "throughput";
        public static final String LATENCY = "latency";
        public static final String QUEUE_SIZE = "queue_size";
        public static final String ACTIVE_TASKS = "active_tasks";
    }

    /**
     * 常用服务名称
     */
    public static class ServiceNames {
        public static final String MEDIA_SERVICE = "media-service";
        public static final String STORAGE_SERVICE = "storage-service";
        public static final String TRANSCODE_SERVICE = "transcode-service";
        public static final String STREAMING_SERVICE = "streaming-service";
        public static final String MONITOR_SERVICE = "monitor-service";
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.collectTime == null) {
            this.collectTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}