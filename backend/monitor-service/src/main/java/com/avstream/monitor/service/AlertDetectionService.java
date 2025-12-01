package com.avstream.monitor.service;

import com.avstream.monitor.entity.AlertRule;
import com.avstream.monitor.entity.MonitorMetric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 告警检测服务
 */
@Service
@RequiredArgsConstructor
public class AlertDetectionService {
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AlertDetectionService.class);
    
    private final AlertRuleService alertRuleService;
    private final AlertRecordService alertRecordService;
    private final MonitorMetricService monitorMetricService;
    
    /**
     * 检测告警
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void detectAlerts() {
        try {
            List<AlertRule> enabledRules = alertRuleService.getEnabledAlertRuleEntities();
            
            for (AlertRule rule : enabledRules) {
                checkAlertRule(rule);
            }
            
            log.debug("告警检测完成，检查规则数量: {}", enabledRules.size());
        } catch (Exception e) {
            log.error("告警检测失败", e);
        }
    }
    
    /**
     * 检查告警规则
     */
    private void checkAlertRule(AlertRule rule) {
        try {
            // 获取相关指标数据
            List<MonitorMetric> metrics = monitorMetricService.getLatestMetricEntities(rule.getMetricName(), "monitor-service", 10);
            
            if (metrics.isEmpty()) {
                return;
            }
            
            // 检查是否满足告警条件
            boolean shouldAlert = metrics.stream()
                .anyMatch(metric -> {
                    double currentValue = metric.getMetricValue();
                    double threshold = rule.getThreshold();
                    
                    // 根据比较运算符判断是否触发告警
                    switch (rule.getOperator()) {
                        case "gt":
                            return currentValue > threshold;
                        case "gte":
                            return currentValue >= threshold;
                        case "lt":
                            return currentValue < threshold;
                        case "lte":
                            return currentValue <= threshold;
                        case "eq":
                            return Math.abs(currentValue - threshold) < 0.001;
                        default:
                            return currentValue > threshold;
                    }
                });
            
            if (shouldAlert) {
                // 检查是否已经存在相同规则的告警
                boolean existingAlert = alertRecordService.hasUnresolvedAlert(rule.getId());
                
                if (!existingAlert) {
                    // 创建告警记录
                    String message = generateAlertMessage(rule, metrics.get(0));
                    alertRecordService.createAlertRecord(
                        rule, 
                        metrics.get(0).getMetricValue(), 
                        "monitor-service", 
                        message
                    );
                    
                    // 发送告警通知
                    sendAlertNotification(rule, message);
                    
                    log.warn("告警触发: {} - 当前值: {}, 阈值: {}, 操作符: {}", 
                        rule.getRuleName(), metrics.get(0).getMetricValue(), 
                        rule.getThreshold(), rule.getOperator());
                }
            } else {
                // 如果不满足告警条件，自动解决已存在的告警
                resolveAlertsIfNeeded(rule);
            }
        } catch (Exception e) {
            log.error("检查告警规则失败: {}", rule.getRuleName(), e);
        }
    }
    
    /**
     * 检查指标阈值
     */
    private boolean checkMetricThreshold(AlertRule rule, List<MonitorMetric> metrics) {
        // 检查最近几个指标是否都满足阈值条件
        int validCount = 0;
        for (MonitorMetric metric : metrics) {
            if (checkSingleMetric(rule, metric)) {
                validCount++;
            }
        }
        
        // 超过一半的指标满足条件则认为需要告警
        return validCount >= metrics.size() / 2;
    }
    
    /**
     * 检查单个指标
     */
    private boolean checkSingleMetric(AlertRule rule, MonitorMetric metric) {
        double metricValue = metric.getMetricValue();
        double threshold = rule.getThreshold();
        
        switch (rule.getOperator()) {
            case ">":
                return metricValue > threshold;
            case ">=":
                return metricValue >= threshold;
            case "<":
                return metricValue < threshold;
            case "<=":
                return metricValue <= threshold;
            case "==":
                return metricValue == threshold;
            case "!=":
                return metricValue != threshold;
            default:
                log.warn("未知的操作符: {}", rule.getOperator());
                return false;
        }
    }
    
    /**
     * 生成告警消息
     */
    private String generateAlertMessage(AlertRule rule, MonitorMetric metric) {
        return String.format("告警规则[%s]触发: 指标[%s]当前值[%.2f]超过阈值[%.2f]", 
            rule.getRuleName(), rule.getMetricName(), metric.getMetricValue(), rule.getThreshold());
    }

    /**
     * 发送告警通知
     */
    private void sendAlertNotification(AlertRule rule, String message) {
        // 这里可以实现邮件、短信、钉钉等通知方式
        log.warn("告警通知: {}", message);
        
        // TODO: 实现具体的通知逻辑
        // - 邮件通知
        // - 短信通知  
        // - 钉钉/企业微信通知
        // - Webhook通知
    }

    /**
     * 如果需要，解决告警
     */
    private void resolveAlertsIfNeeded(AlertRule rule) {
        // 如果指标恢复正常，自动解决相关告警
        alertRecordService.resolveActiveAlerts(rule.getId());
    }
    
    /**
     * 手动触发告警检测
     */
    public void triggerManualDetection() {
        log.info("手动触发告警检测");
        detectAlerts();
    }
}