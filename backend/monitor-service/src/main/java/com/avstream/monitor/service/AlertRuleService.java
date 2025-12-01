package com.avstream.monitor.service;

import com.avstream.monitor.dto.request.AlertRuleRequest;
import com.avstream.monitor.dto.response.AlertRuleResponse;
import com.avstream.monitor.entity.AlertRule;
import com.avstream.monitor.repository.AlertRuleRepository;
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
import java.util.stream.Collectors;

/**
 * 告警规则服务
 */
@Service
@RequiredArgsConstructor
public class AlertRuleService {
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AlertRuleService.class);
    
    private final AlertRuleRepository alertRuleRepository;
    
    /**
     * 创建告警规则
     */
    public AlertRuleResponse createAlertRule(AlertRuleRequest request) {
        if (alertRuleRepository.existsByRuleName(request.getRuleName())) {
            throw new RuntimeException("告警规则名称已存在: " + request.getRuleName());
        }
        
        AlertRule rule = new AlertRule();
        rule.setRuleName(request.getRuleName());
        rule.setMetricName(request.getMetricName());
        rule.setThreshold(request.getThreshold());
        rule.setOperator(request.getOperator());
        rule.setDuration(request.getDuration());
        rule.setSeverity(request.getSeverity());
        rule.setEnabled(request.getEnabled());
        rule.setDescription(request.getDescription());
        
        AlertRule savedRule = alertRuleRepository.save(rule);
        log.info("创建告警规则成功: {}", savedRule.getRuleName());
        
        return convertToResponse(savedRule);
    }
    
    /**
     * 更新告警规则
     */
    public AlertRuleResponse updateAlertRule(Long id, AlertRuleRequest request) {
        Optional<AlertRule> optionalRule = alertRuleRepository.findById(id);
        if (optionalRule.isEmpty()) {
            throw new RuntimeException("告警规则不存在: " + id);
        }
        
        AlertRule rule = optionalRule.get();
        rule.setRuleName(request.getRuleName());
        rule.setMetricName(request.getMetricName());
        rule.setThreshold(request.getThreshold());
        rule.setOperator(request.getOperator());
        rule.setDuration(request.getDuration());
        rule.setSeverity(request.getSeverity());
        rule.setEnabled(request.getEnabled());
        rule.setDescription(request.getDescription());
        
        AlertRule updatedRule = alertRuleRepository.save(rule);
        log.info("更新告警规则成功: {}", updatedRule.getRuleName());
        
        return convertToResponse(updatedRule);
    }
    
    /**
     * 删除告警规则
     */
    public void deleteAlertRule(Long id) {
        if (!alertRuleRepository.existsById(id)) {
            throw new RuntimeException("告警规则不存在: " + id);
        }
        
        alertRuleRepository.deleteById(id);
        log.info("删除告警规则成功: {}", id);
    }
    
    /**
     * 查询告警规则列表
     */
    public Page<AlertRuleResponse> getAlertRules(String ruleName, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        
        Page<AlertRule> rules;
        if (ruleName != null && !ruleName.trim().isEmpty()) {
            rules = alertRuleRepository.findByRuleNameContaining(ruleName, pageable);
        } else {
            rules = alertRuleRepository.findAll(pageable);
        }
        
        return rules.map(this::convertToResponse);
    }
    
    /**
     * 获取告警规则详情
     */
    public AlertRuleResponse getAlertRuleById(Long id) {
        Optional<AlertRule> optionalRule = alertRuleRepository.findById(id);
        if (optionalRule.isEmpty()) {
            throw new RuntimeException("告警规则不存在: " + id);
        }
        
        return convertToResponse(optionalRule.get());
    }
    
    /**
     * 获取启用的告警规则
     */
    public List<AlertRuleResponse> getEnabledAlertRules() {
        List<AlertRule> rules = alertRuleRepository.findByEnabledTrue();
        return rules.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取启用的告警规则实体
     */
    public List<AlertRule> getEnabledAlertRuleEntities() {
        return alertRuleRepository.findByEnabledTrue();
    }
    
    /**
     * 切换告警规则状态
     */
    public AlertRuleResponse toggleAlertRule(Long id, boolean enabled) {
        Optional<AlertRule> optionalRule = alertRuleRepository.findById(id);
        if (optionalRule.isEmpty()) {
            throw new RuntimeException("告警规则不存在: " + id);
        }
        
        AlertRule rule = optionalRule.get();
        rule.setEnabled(enabled);
        
        AlertRule updatedRule = alertRuleRepository.save(rule);
        log.info("切换告警规则状态: {} -> {}", updatedRule.getRuleName(), enabled);
        
        return convertToResponse(updatedRule);
    }
    
    /**
     * 根据指标名称获取告警规则
     */
    public List<AlertRuleResponse> getAlertRulesByMetric(String metricName) {
        List<AlertRule> rules = alertRuleRepository.findByMetricName(metricName);
        return rules.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * 实体转换为响应DTO
     */
    private AlertRuleResponse convertToResponse(AlertRule rule) {
        AlertRuleResponse response = new AlertRuleResponse();
        response.setId(rule.getId());
        response.setRuleName(rule.getRuleName());
        response.setMetricName(rule.getMetricName());
        response.setThreshold(rule.getThreshold());
        response.setOperator(rule.getOperator());
        response.setDuration(rule.getDuration());
        response.setSeverity(rule.getSeverity());
        response.setEnabled(rule.getEnabled());
        response.setDescription(rule.getDescription());
        response.setCreatedAt(rule.getCreatedAt());
        response.setUpdatedAt(rule.getUpdatedAt());
        return response;
    }
}