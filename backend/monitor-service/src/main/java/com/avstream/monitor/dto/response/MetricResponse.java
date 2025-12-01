package com.avstream.monitor.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 指标响应DTO
 */
public class MetricResponse {
    
    private Long id;
    
    private String metricName;
    
    private String serviceName;
    
    private Double metricValue;
    
    private String metricUnit;
    
    private String tags;
    
    private LocalDateTime timestamp;
    
    private LocalDateTime createdAt;

    // 手动添加setter和getter方法
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }

    public Double getMetricValue() {
        return metricValue;
    }

    public void setMetricUnit(String metricUnit) {
        this.metricUnit = metricUnit;
    }

    public String getMetricUnit() {
        return metricUnit;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTags() {
        return tags;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}