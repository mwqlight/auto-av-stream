package com.avstream.monitor.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 告警规则实体类
 */
@Entity
@Table(name = "alert_rules")
@Data
public class AlertRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;
    
    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;
    
    @Column(name = "threshold", nullable = false)
    private Double threshold;
    
    @Column(name = "operator", nullable = false, length = 10)
    private String operator = "gt"; // >, <, >=, <=, ==, !=
    
    @Column(name = "duration", nullable = false)
    private Long duration; // 持续时间（毫秒）
    
    @Column(name = "severity", nullable = false, length = 20)
    private String severity; // INFO, WARNING, CRITICAL
    
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 手动添加缺失的getter方法
    public Long getId() {
        return id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getMetricName() {
        return metricName;
    }

    public Double getThreshold() {
        return threshold;
    }

    public String getOperator() {
        return operator;
    }

    public Long getDuration() {
        return duration;
    }

    public String getSeverity() {
        return severity;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}