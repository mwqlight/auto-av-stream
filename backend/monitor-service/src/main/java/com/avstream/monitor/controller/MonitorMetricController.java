package com.avstream.monitor.controller;

import com.avstream.monitor.dto.request.MetricQueryRequest;
import com.avstream.monitor.dto.response.MetricResponse;
import com.avstream.monitor.service.MonitorMetricService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 监控指标控制器
 */
@RestController
@RequestMapping("/api/v1/metrics")
@Tag(name = "监控指标管理", description = "监控指标的查询和管理接口")
@RequiredArgsConstructor
public class MonitorMetricController {
    
    private final MonitorMetricService monitorMetricService;
    
    @GetMapping
    @Operation(summary = "查询监控指标", description = "根据条件分页查询监控指标")
    public ResponseEntity<Page<MetricResponse>> queryMetrics(MetricQueryRequest request) {
        Page<MetricResponse> metrics = monitorMetricService.queryMetrics(request);
        return ResponseEntity.ok(metrics);
    }
    
    @GetMapping("/latest")
    @Operation(summary = "获取最新指标", description = "获取指定指标的最新数据")
    public ResponseEntity<List<MetricResponse>> getLatestMetrics(
            @RequestParam String metricName,
            @RequestParam(required = false) String serviceName,
            @RequestParam(defaultValue = "10") int limit) {
        
        if (serviceName == null) {
            serviceName = "monitor-service";
        }
        
        List<MetricResponse> metrics = monitorMetricService.getLatestMetrics(
            metricName, serviceName, limit);
        return ResponseEntity.ok(metrics);
    }
    
    @GetMapping("/count")
    @Operation(summary = "统计指标数量", description = "统计指定服务的指标数量")
    public ResponseEntity<Long> countMetrics(
            @RequestParam String serviceName,
            @RequestParam(required = false) String hours) {
        
        java.time.LocalDateTime startTime = java.time.LocalDateTime.now()
            .minusHours(hours != null ? Long.parseLong(hours) : 24);
        
        Long count = monitorMetricService.countMetrics(serviceName, startTime);
        return ResponseEntity.ok(count);
    }
    
    @DeleteMapping("/cleanup")
    @Operation(summary = "清理过期数据", description = "清理指定时间之前的监控数据")
    public ResponseEntity<Void> cleanupExpiredData(@RequestParam String days) {
        java.time.LocalDateTime expireTime = java.time.LocalDateTime.now()
            .minusDays(Long.parseLong(days));
        
        monitorMetricService.cleanupExpiredData(expireTime);
        return ResponseEntity.ok().build();
    }
}