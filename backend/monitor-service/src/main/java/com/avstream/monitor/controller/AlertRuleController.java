package com.avstream.monitor.controller;

import com.avstream.monitor.dto.request.AlertRuleRequest;
import com.avstream.monitor.dto.response.AlertRuleResponse;
import com.avstream.monitor.service.AlertRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 告警规则控制器
 */
@RestController
@RequestMapping("/api/v1/alert-rules")
@Tag(name = "告警规则管理", description = "告警规则的增删改查接口")
@RequiredArgsConstructor
public class AlertRuleController {
    
    private final AlertRuleService alertRuleService;
    
    @PostMapping
    @Operation(summary = "创建告警规则", description = "创建新的告警规则")
    public ResponseEntity<AlertRuleResponse> createAlertRule(@RequestBody AlertRuleRequest request) {
        AlertRuleResponse response = alertRuleService.createAlertRule(request);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新告警规则", description = "更新指定ID的告警规则")
    public ResponseEntity<AlertRuleResponse> updateAlertRule(
            @PathVariable Long id, 
            @RequestBody AlertRuleRequest request) {
        AlertRuleResponse response = alertRuleService.updateAlertRule(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除告警规则", description = "删除指定ID的告警规则")
    public ResponseEntity<Void> deleteAlertRule(@PathVariable Long id) {
        alertRuleService.deleteAlertRule(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping
    @Operation(summary = "查询告警规则", description = "分页查询告警规则列表")
    public ResponseEntity<Page<AlertRuleResponse>> getAlertRules(
            @RequestParam(required = false) String ruleName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<AlertRuleResponse> rules = alertRuleService.getAlertRules(ruleName, page, size);
        return ResponseEntity.ok(rules);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取告警规则详情", description = "获取指定ID的告警规则详情")
    public ResponseEntity<AlertRuleResponse> getAlertRuleById(@PathVariable Long id) {
        AlertRuleResponse response = alertRuleService.getAlertRuleById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/enabled")
    @Operation(summary = "获取启用的告警规则", description = "获取所有启用的告警规则")
    public ResponseEntity<List<AlertRuleResponse>> getEnabledAlertRules() {
        List<AlertRuleResponse> rules = alertRuleService.getEnabledAlertRules();
        return ResponseEntity.ok(rules);
    }
    
    @PatchMapping("/{id}/toggle")
    @Operation(summary = "切换告警规则状态", description = "启用或禁用告警规则")
    public ResponseEntity<AlertRuleResponse> toggleAlertRule(
            @PathVariable Long id, 
            @RequestParam boolean enabled) {
        
        AlertRuleResponse response = alertRuleService.toggleAlertRule(id, enabled);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/by-metric")
    @Operation(summary = "根据指标查询告警规则", description = "根据指标名称查询相关告警规则")
    public ResponseEntity<List<AlertRuleResponse>> getAlertRulesByMetric(
            @RequestParam String metricName) {
        
        List<AlertRuleResponse> rules = alertRuleService.getAlertRulesByMetric(metricName);
        return ResponseEntity.ok(rules);
    }
}