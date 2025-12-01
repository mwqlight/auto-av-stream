package com.avstream.monitor.controller;

import com.avstream.monitor.service.AlertRecordService;
import com.avstream.monitor.service.ApplicationMonitorService;
import com.avstream.monitor.service.SystemMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "健康检查", description = "系统健康状态检查接口")
@RequiredArgsConstructor
public class HealthController {
    
    private final SystemMonitorService systemMonitorService;
    private final ApplicationMonitorService applicationMonitorService;
    private final AlertRecordService alertRecordService;
    
    @GetMapping("/system")
    @Operation(summary = "系统健康状态", description = "获取当前系统的健康状态")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Map<String, Object> healthInfo = new HashMap<>();
        
        String systemHealth = systemMonitorService.getSystemHealth();
        Long unresolvedAlerts = alertRecordService.countUnresolvedAlerts();
        
        healthInfo.put("status", systemHealth);
        healthInfo.put("unresolvedAlerts", unresolvedAlerts);
        healthInfo.put("timestamp", java.time.LocalDateTime.now());
        
        return ResponseEntity.ok(healthInfo);
    }
    
    @GetMapping("/applications")
    @Operation(summary = "应用服务健康状态", description = "获取所有应用服务的健康状态")
    public ResponseEntity<Map<String, String>> getApplicationsHealth() {
        Map<String, String> healthStatus = applicationMonitorService.getAllApplicationHealth();
        return ResponseEntity.ok(healthStatus);
    }
    
    @GetMapping("/application/{serviceName}")
    @Operation(summary = "单个应用服务健康状态", description = "获取指定应用服务的健康状态")
    public ResponseEntity<Map<String, String>> getApplicationHealth(@PathVariable String serviceName) {
        String healthStatus = applicationMonitorService.getApplicationHealth(serviceName);
        
        Map<String, String> response = new HashMap<>();
        response.put("serviceName", serviceName);
        response.put("status", healthStatus);
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/summary")
    @Operation(summary = "健康状态汇总", description = "获取系统整体健康状态汇总")
    public ResponseEntity<Map<String, Object>> getHealthSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        // 系统健康状态
        String systemHealth = systemMonitorService.getSystemHealth();
        summary.put("system", systemHealth);
        
        // 应用服务健康状态
        Map<String, String> appHealth = applicationMonitorService.getAllApplicationHealth();
        summary.put("applications", appHealth);
        
        // 告警统计
        Long unresolvedAlerts = alertRecordService.countUnresolvedAlerts();
        summary.put("unresolvedAlerts", unresolvedAlerts);
        
        // 总体状态
        String overallStatus = calculateOverallStatus(systemHealth, appHealth, unresolvedAlerts);
        summary.put("overall", overallStatus);
        summary.put("timestamp", java.time.LocalDateTime.now());
        
        return ResponseEntity.ok(summary);
    }
    
    /**
     * 计算总体健康状态
     */
    private String calculateOverallStatus(String systemHealth, Map<String, String> appHealth, Long unresolvedAlerts) {
        // 如果系统状态为CRITICAL，则总体状态为CRITICAL
        if ("CRITICAL".equals(systemHealth)) {
            return "CRITICAL";
        }
        
        // 检查应用服务状态
        boolean hasCriticalApp = appHealth.values().stream()
            .anyMatch(status -> "DOWN".equals(status));
        
        if (hasCriticalApp) {
            return "CRITICAL";
        }
        
        // 检查未解决的告警数量
        if (unresolvedAlerts > 10) {
            return "WARNING";
        }
        
        // 如果系统状态为WARNING，则总体状态为WARNING
        if ("WARNING".equals(systemHealth)) {
            return "WARNING";
        }
        
        return "HEALTHY";
    }
}