package com.avstream.monitor.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 监控指标实体类
 */
@Entity
@Table(name = "monitor_metrics")
@Data
public class MonitorMetric {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;
    
    @Column(name = "service_name", nullable = false, length = 50)
    private String serviceName;
    
    @Column(name = "metric_value", nullable = false)
    private Double metricValue;
    
    @Column(name = "metric_unit", length = 20)
    private String metricUnit;
    
    @Column(name = "tags", length = 500)
    private String tags;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    // 手动添加缺失的setter方法
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }

    public void setMetricUnit(String metricUnit) {
        this.metricUnit = metricUnit;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    // 手动添加缺失的getter方法
    public Long getId() {
        return id;
    }

    public String getMetricName() {
        return metricName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Double getMetricValue() {
        return metricValue;
    }

    public String getMetricUnit() {
        return metricUnit;
    }

    public String getTags() {
        return tags;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}