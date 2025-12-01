package com.avstream.monitor.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 告警记录实体类
 */
@Entity
@Table(name = "alert_records")
@Data
public class AlertRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "rule_id", nullable = false)
    private Long ruleId;
    
    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;
    
    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;
    
    @Column(name = "metric_value", nullable = false)
    private Double metricValue;
    
    @Column(name = "threshold", nullable = false)
    private Double threshold;
    
    @Column(name = "severity", nullable = false, length = 20)
    private String severity;
    
    @Column(name = "service_name", nullable = false, length = 50)
    private String serviceName;
    
    @Column(name = "message", length = 1000)
    private String message;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status; // TRIGGERED, RESOLVED, ACKNOWLEDGED
    
    @Column(name = "triggered_at", nullable = false)
    private LocalDateTime triggeredAt;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (triggeredAt == null) {
            triggeredAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "TRIGGERED";
        }
    }

    // 手动添加缺失的setter和getter方法
    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }

    public LocalDateTime getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTriggeredAt(LocalDateTime triggeredAt) {
        this.triggeredAt = triggeredAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getMetricName() {
        return metricName;
    }

    public Double getMetricValue() {
        return metricValue;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTriggeredAt() {
        return triggeredAt;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getSeverity() {
        return severity;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Double getThreshold() {
        return threshold;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getRuleId() {
        return ruleId;
    }
}