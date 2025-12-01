package com.avstream.monitor.controller;

import com.avstream.monitor.dto.response.MetricResponse;
import com.avstream.monitor.service.AlertRecordService;
import com.avstream.monitor.service.ApplicationMonitorService;
import com.avstream.monitor.service.MonitorMetricService;
import com.avstream.monitor.service.SystemMonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 监控仪表板控制器
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final MonitorMetricService monitorMetricService;
    private final AlertRecordService alertRecordService;
    private final SystemMonitorService systemMonitorService;
    private final ApplicationMonitorService applicationMonitorService;

    /**
     * 获取系统概览信息
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        Map<String, Object> overview = Map.of(
            "systemHealth", systemMonitorService.getSystemHealth(),
            "applicationHealth", applicationMonitorService.getAllApplicationHealth(),
            "activeAlerts", alertRecordService.getActiveAlertCount(),
            "totalMetrics", monitorMetricService.getTotalMetricsCount(),
            "timestamp", LocalDateTime.now()
        );
        return ResponseEntity.ok(overview);
    }

    /**
     * 获取指标数据
     */
    @GetMapping("/metrics")
    public ResponseEntity<List<MetricResponse>> getMetrics(
            @RequestParam String metricName,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(defaultValue = "100") int limit) {
        
        List<MetricResponse> metrics = monitorMetricService.getMetrics(
            metricName, serviceName, startTime, endTime, limit);
        
        return ResponseEntity.ok(metrics);
    }

    /**
     * 获取系统健康状态
     */
    @GetMapping("/system/health")
    public ResponseEntity<Map<String, String>> getSystemHealth() {
        String health = systemMonitorService.getSystemHealth();
        return ResponseEntity.ok(Map.of("status", health));
    }

    /**
     * 获取应用服务健康状态
     */
    @GetMapping("/application/health")
    public ResponseEntity<Map<String, String>> getApplicationHealth() {
        Map<String, String> healthStatus = applicationMonitorService.getAllApplicationHealth();
        return ResponseEntity.ok(healthStatus);
    }

    /**
     * 获取活跃告警列表
     */
    @GetMapping("/alerts/active")
    public ResponseEntity<List<Map<String, Object>>> getActiveAlerts() {
        List<Map<String, Object>> activeAlerts = alertRecordService.getActiveAlerts();
        return ResponseEntity.ok(activeAlerts);
    }

    /**
     * 获取最近指标趋势
     */
    @GetMapping("/metrics/trend")
    public ResponseEntity<Map<String, List<MetricResponse>>> getMetricsTrend(
            @RequestParam List<String> metricNames,
            @RequestParam(defaultValue = "1") int hours) {
        
        Map<String, List<MetricResponse>> trends = monitorMetricService.getMetricsTrend(
            metricNames, hours);
        
        return ResponseEntity.ok(trends);
    }

    /**
     * 获取系统性能指标
     */
    @GetMapping("/system/metrics")
    public ResponseEntity<Map<String, Object>> getSystemMetrics() {
        // 获取最近1小时的系统指标
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(1);
        
        List<MetricResponse> cpuMetrics = monitorMetricService.getMetrics(
            "system.cpu.usage", "monitor-service", startTime, endTime, 60);
        
        List<MetricResponse> memoryMetrics = monitorMetricService.getMetrics(
            "jvm.memory.heap.used", "monitor-service", startTime, endTime, 60);
        
        List<MetricResponse> threadMetrics = monitorMetricService.getMetrics(
            "jvm.thread.count", "monitor-service", startTime, endTime, 60);
        
        Map<String, Object> systemMetrics = Map.of(
            "cpu", cpuMetrics,
            "memory", memoryMetrics,
            "threads", threadMetrics,
            "timestamp", LocalDateTime.now()
        );
        
        return ResponseEntity.ok(systemMetrics);
    }
}