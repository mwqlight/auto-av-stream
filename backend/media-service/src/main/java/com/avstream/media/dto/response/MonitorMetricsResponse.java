package com.avstream.media.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 监控指标响应DTO
 * 
 * @author AV Stream Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorMetricsResponse {

    /** 服务名称 */
    private String serviceName;

    /** 实例ID */
    private String instanceId;

    /** 服务状态 */
    private ServiceStatus status;

    /** 健康检查时间 */
    private LocalDateTime healthCheckTime;

    /** 系统指标 */
    private SystemMetrics systemMetrics;

    /** 业务指标 */
    private BusinessMetrics businessMetrics;

    /** 性能指标 */
    private PerformanceMetrics performanceMetrics;

    /** 历史数据（用于图表展示） */
    private List<HistoricalData> historicalData;

    /** 告警信息 */
    private List<AlertInfo> alerts;

    /**
     * 服务状态枚举
     */
    public enum ServiceStatus {
        HEALTHY,    // 健康
        WARNING,    // 警告
        ERROR,      // 错误
        UNKNOWN     // 未知
    }

    /**
     * 系统指标
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemMetrics {
        /** CPU使用率 */
        private Double cpuUsage;
        
        /** 内存使用率 */
        private Double memoryUsage;
        
        /** 磁盘使用率 */
        private Double diskUsage;
        
        /** 系统负载 */
        private Double systemLoad;
        
        /** 线程数量 */
        private Integer threadCount;
        
        /** 活跃连接数 */
        private Integer activeConnections;
        
        /** 网络IO（MB/s） */
        private Double networkIo;
    }

    /**
     * 业务指标
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessMetrics {
        /** 上传次数 */
        private Long uploadCount;
        
        /** 下载次数 */
        private Long downloadCount;
        
        /** 转码次数 */
        private Long transcodeCount;
        
        /** 流媒体次数 */
        private Long streamingCount;
        
        /** 错误次数 */
        private Long errorCount;
        
        /** 请求总数 */
        private Long totalRequests;
        
        /** 成功请求数 */
        private Long successRequests;
        
        /** 失败请求数 */
        private Long failedRequests;
    }

    /**
     * 性能指标
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceMetrics {
        /** 平均响应时间（ms） */
        private Double avgResponseTime;
        
        /** 吞吐量（req/s） */
        private Double throughput;
        
        /** 错误率（%） */
        private Double errorRate;
        
        /** 队列长度 */
        private Integer queueSize;
        
        /** 活跃任务数 */
        private Integer activeTasks;
        
        /** 延迟（ms） */
        private Double latency;
    }

    /**
     * 历史数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoricalData {
        /** 时间戳 */
        private LocalDateTime timestamp;
        
        /** 指标名称 */
        private String metricName;
        
        /** 指标值 */
        private Double metricValue;
        
        /** 指标单位 */
        private String metricUnit;
    }

    /**
     * 告警信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertInfo {
        /** 告警级别 */
        private AlertLevel level;
        
        /** 告警标题 */
        private String title;
        
        /** 告警描述 */
        private String description;
        
        /** 告警时间 */
        private LocalDateTime alertTime;
        
        /** 告警详情 */
        private Map<String, Object> details;
        
        /** 是否已确认 */
        private Boolean acknowledged;
        
        /** 确认时间 */
        private LocalDateTime acknowledgedTime;
        
        /** 确认人 */
        private String acknowledgedBy;
    }

    /**
     * 告警级别枚举
     */
    public enum AlertLevel {
        INFO,       // 信息
        WARNING,    // 警告
        ERROR,      // 错误
        CRITICAL    // 严重
    }

    /**
     * 创建健康状态的响应
     */
    public static MonitorMetricsResponse createHealthyResponse(String serviceName, String instanceId) {
        return MonitorMetricsResponse.builder()
                .serviceName(serviceName)
                .instanceId(instanceId)
                .status(ServiceStatus.HEALTHY)
                .healthCheckTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建警告状态的响应
     */
    public static MonitorMetricsResponse createWarningResponse(String serviceName, String instanceId, String message) {
        MonitorMetricsResponse response = createHealthyResponse(serviceName, instanceId);
        response.setStatus(ServiceStatus.WARNING);
        
        AlertInfo alert = AlertInfo.builder()
                .level(AlertLevel.WARNING)
                .title("服务警告")
                .description(message)
                .alertTime(LocalDateTime.now())
                .acknowledged(false)
                .build();
        
        response.setAlerts(List.of(alert));
        return response;
    }

    /**
     * 创建错误状态的响应
     */
    public static MonitorMetricsResponse createErrorResponse(String serviceName, String instanceId, String message) {
        MonitorMetricsResponse response = createHealthyResponse(serviceName, instanceId);
        response.setStatus(ServiceStatus.ERROR);
        
        AlertInfo alert = AlertInfo.builder()
                .level(AlertLevel.ERROR)
                .title("服务错误")
                .description(message)
                .alertTime(LocalDateTime.now())
                .acknowledged(false)
                .build();
        
        response.setAlerts(List.of(alert));
        return response;
    }
}