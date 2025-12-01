package com.avstream.monitor.service;

import com.avstream.monitor.entity.AlertRecord;
import com.avstream.monitor.entity.AlertRule;
import com.avstream.monitor.enums.AlertStatus;
import com.avstream.monitor.repository.AlertRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 告警记录服务
 */
@Service
@RequiredArgsConstructor
public class AlertRecordService {
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AlertRecordService.class);
    
    private final AlertRecordRepository alertRecordRepository;
    
    /**
     * 创建告警记录
     */
    public void createAlertRecord(AlertRule rule, Double metricValue, String serviceName, String message) {
        AlertRecord record = new AlertRecord();
        record.setRuleId(rule.getId());
        record.setRuleName(rule.getRuleName());
        record.setMetricName(rule.getMetricName());
        record.setMetricValue(metricValue);
        record.setThreshold(rule.getThreshold());
        record.setSeverity(rule.getSeverity());
        record.setServiceName(serviceName);
        record.setMessage(message);
        record.setStatus(AlertStatus.TRIGGERED.name());
        
        alertRecordRepository.save(record);
        log.warn("创建告警记录: {} - {}", rule.getRuleName(), message);
    }

    /**
     * 获取活跃告警数量
     */
    public long getActiveAlertCount() {
        return alertRecordRepository.countByStatus(AlertStatus.TRIGGERED.name());
    }

    /**
     * 获取活跃告警列表
     */
    public List<java.util.Map<String, Object>> getActiveAlerts() {
        List<AlertRecord> activeAlerts = alertRecordRepository.findByStatus(AlertStatus.TRIGGERED.name());
        return activeAlerts.stream()
            .map(alert -> {
                java.util.Map<String, Object> alertMap = new java.util.HashMap<>();
                alertMap.put("id", alert.getId());
                alertMap.put("metricName", alert.getMetricName());
                alertMap.put("metricValue", alert.getMetricValue());
                alertMap.put("message", alert.getMessage());
                alertMap.put("triggeredAt", alert.getTriggeredAt());
                alertMap.put("ruleName", alert.getRuleName());
                alertMap.put("severity", alert.getSeverity());
                alertMap.put("serviceName", alert.getServiceName());
                alertMap.put("threshold", alert.getThreshold());
                return alertMap;
            })
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 解决告警
     */
    public void resolveAlert(Long alertId) {
        Optional<AlertRecord> optionalRecord = alertRecordRepository.findById(alertId);
        if (optionalRecord.isEmpty()) {
            throw new RuntimeException("告警记录不存在: " + alertId);
        }
        
        AlertRecord record = optionalRecord.get();
        record.setStatus(AlertStatus.RESOLVED.name());
        record.setResolvedAt(LocalDateTime.now());
        
        alertRecordRepository.save(record);
        log.info("解决告警记录: {}", alertId);
    }
    
    /**
     * 确认告警
     */
    public void acknowledgeAlert(Long alertId) {
        Optional<AlertRecord> optionalRecord = alertRecordRepository.findById(alertId);
        if (optionalRecord.isEmpty()) {
            throw new RuntimeException("告警记录不存在: " + alertId);
        }
        
        AlertRecord record = optionalRecord.get();
        record.setStatus(AlertStatus.ACKNOWLEDGED.name());
        record.setAcknowledgedAt(LocalDateTime.now());
        
        alertRecordRepository.save(record);
        log.info("确认告警记录: {}", alertId);
    }

    /**
     * 确认告警记录
     */
    public void confirmAlertRecord(Long id) {
        AlertRecord alertRecord = alertRecordRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("告警记录不存在: " + id));
        
        alertRecord.setStatus(AlertStatus.CONFIRMED.name());
        alertRecord.setConfirmedAt(LocalDateTime.now());
        alertRecordRepository.save(alertRecord);
        
        log.info("告警记录已确认: {}", id);
    }

    /**
     * 检查是否存在活跃的告警
     */
    public boolean hasActiveAlert(Long alertRuleId) {
        return alertRecordRepository.existsByRuleIdAndStatus(alertRuleId, AlertStatus.ACTIVE.name());
    }

    /**
     * 解决活跃的告警
     */
    public void resolveActiveAlerts(Long alertRuleId) {
        List<AlertRecord> activeAlerts = alertRecordRepository.findByRuleIdAndStatus(alertRuleId, AlertStatus.ACTIVE.name());
        
        for (AlertRecord alert : activeAlerts) {
            alert.setStatus(AlertStatus.RESOLVED.name());
            alert.setResolvedAt(LocalDateTime.now());
            alertRecordRepository.save(alert);
            log.info("告警已自动解决: {}", alert.getId());
        }
    }

    /**
     * 查询告警记录
     */
    public Page<AlertRecord> getAlertRecords(String status, String serviceName, String severity, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "triggeredAt"));
        
        if (status != null) {
            return alertRecordRepository.findByStatus(status, pageable);
        } else if (serviceName != null) {
            return alertRecordRepository.findByServiceName(serviceName, pageable);
        } else if (severity != null) {
            return alertRecordRepository.findBySeverity(severity, pageable);
        } else {
            return alertRecordRepository.findAll(pageable);
        }
    }
    
    /**
     * 获取未解决的告警记录
     */
    public List<AlertRecord> getUnresolvedAlerts() {
        return alertRecordRepository.findByStatusNot(AlertStatus.RESOLVED.name());
    }
    
    /**
     * 统计未解决的告警数量
     */
    public Long countUnresolvedAlerts() {
        return alertRecordRepository.countUnresolvedAlerts();
    }
    
    /**
     * 检查是否存在未解决的告警
     */
    public boolean hasUnresolvedAlert(Long ruleId) {
        List<AlertRecord> unresolvedAlerts = alertRecordRepository.findUnresolvedByRuleId(ruleId);
        return !unresolvedAlerts.isEmpty();
    }
    
    /**
     * 清理过期告警记录
     */
    public void cleanupExpiredAlerts(LocalDateTime expireTime) {
        try {
            alertRecordRepository.deleteByTriggeredAtBefore(expireTime);
            log.info("清理过期告警记录完成");
        } catch (Exception e) {
            log.error("清理过期告警记录失败", e);
        }
    }
}