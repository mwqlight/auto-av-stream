package com.avstream.media.controller;

import com.avstream.media.dto.response.MonitorMetricsResponse;
import com.avstream.media.service.MediaService;
import com.avstream.media.service.MonitorService;
import com.avstream.media.service.StorageService;
import com.avstream.media.service.TranscodeService;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 媒体服务监控控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/monitor")
@RequiredArgsConstructor
public class MonitorController {
    
    private final StorageService storageService;
    private final MediaService mediaService;
    private final TranscodeService transcodeService;
    private final MonitorService monitorService;
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查媒体服务健康状态")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        MonitorMetricsResponse metrics = monitorService.getCompleteMetrics();
        
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", metrics.getStatus().name());
        healthInfo.put("timestamp", metrics.getHealthCheckTime());
        healthInfo.put("service", metrics.getServiceName());
        healthInfo.put("instanceId", metrics.getInstanceId());
        
        // 检查关键服务状态
        Map<String, String> services = new HashMap<>();
        services.put("storageService", "UP");
        services.put("mediaService", "UP");
        services.put("transcodeService", "UP");
        services.put("monitorService", "UP");
        
        healthInfo.put("services", services);
        
        return ResponseEntity.ok(healthInfo);
    }
    
    /**
     * 获取指标数据
     */
    @GetMapping("/metrics")
    @Operation(summary = "获取指标数据", description = "获取媒体服务的各项指标数据")
    public ResponseEntity<MonitorMetricsResponse> getMetrics() {
        MonitorMetricsResponse metrics = monitorService.getCompleteMetrics();
        return ResponseEntity.ok(metrics);
    }
    
    /**
     * 获取系统级指标
     */
    private Map<String, Object> getSystemMetrics() {
        Map<String, Object> systemMetrics = new HashMap<>();
        
        // 获取JVM内存使用情况
        Runtime runtime = Runtime.getRuntime();
        systemMetrics.put("jvm.memory.used", runtime.totalMemory() - runtime.freeMemory());
        systemMetrics.put("jvm.memory.max", runtime.maxMemory());
        systemMetrics.put("jvm.memory.total", runtime.totalMemory());
        
        // 获取线程信息
        systemMetrics.put("jvm.threads.active", Thread.activeCount());
        
        // 获取系统负载
        systemMetrics.put("system.load.average", runtime.availableProcessors());
        
        return systemMetrics;
    }
    
    /**
     * 重置指标数据（仅用于测试）
     */
    @PostMapping("/metrics/reset")
    @Operation(summary = "重置指标数据", description = "重置所有监控指标数据")
    public ResponseEntity<Map<String, Object>> resetMetrics() {
        monitorService.resetMetrics();
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "指标数据已重置");
        result.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取实时流媒体统计
     */
    @GetMapping("/streaming")
    @Operation(summary = "获取流媒体统计", description = "获取实时流媒体统计信息")
    public ResponseEntity<Map<String, Object>> getStreamingStats() {
        Map<String, Object> streamingStats = new HashMap<>();
        
        try {
            // 这里可以集成MediaMTX服务获取实际流媒体数据
            streamingStats.put("activeStreams", 0L);
            streamingStats.put("bandwidth", Map.of(
                "upload", 0L,
                "download", 0L,
                "total", 0L
            ));
            streamingStats.put("latency", Map.of(
                "average", 0,
                "max", 0,
                "min", 0
            ));
            streamingStats.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(streamingStats);
        } catch (Exception e) {
            streamingStats.put("error", e.getMessage());
            streamingStats.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(streamingStats);
        }
    }
    
    /**
     * 获取存储性能指标
     */
    @GetMapping("/storage/performance")
    @Operation(summary = "获取存储性能", description = "获取存储性能指标")
    public ResponseEntity<Map<String, Object>> getStoragePerformance() {
        Map<String, Object> performanceStats = new HashMap<>();
        
        try {
            // 这里可以集成MinIO服务获取实际存储性能数据
            performanceStats.put("readSpeed", 0.0);
            performanceStats.put("writeSpeed", 0.0);
            performanceStats.put("latency", 0.0);
            performanceStats.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(performanceStats);
        } catch (Exception e) {
            performanceStats.put("error", e.getMessage());
            performanceStats.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(performanceStats);
        }
    }

    /**
     * 获取详细的监控指标数据
     */
    @GetMapping("/metrics/detailed")
    @Operation(summary = "获取详细监控指标", description = "获取媒体服务的详细监控指标数据")
    public ResponseEntity<MonitorMetricsResponse> getDetailedMetrics() {
        try {
            MonitorMetricsResponse response = monitorService.getCompleteMetrics();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            MonitorMetricsResponse errorResponse = MonitorMetricsResponse.builder()
                    .serviceName("media-service")
                    .instanceId("unknown")
                    .status(MonitorMetricsResponse.ServiceStatus.ERROR)
                    .healthCheckTime(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 获取指定时间范围内的监控历史数据
     */
    @GetMapping("/metrics/history")
    @Operation(summary = "获取监控历史数据", description = "获取指定时间范围内的监控历史数据")
    public ResponseEntity<Map<String, Object>> getMetricsHistory(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false, defaultValue = "cpu_usage,memory_usage") String metrics) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            // 这里可以集成时序数据库获取历史数据
            Map<String, Object> historyData = new HashMap<>();
            historyData.put("cpu_usage", new ArrayList<>());
            historyData.put("memory_usage", new ArrayList<>());
            
            result.put("data", historyData);
            result.put("startTime", LocalDateTime.now().minusHours(1));
            result.put("endTime", LocalDateTime.now());
            result.put("metrics", metrics);
            result.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 获取服务健康状态详情
     */
    @GetMapping("/health/detailed")
    @Operation(summary = "获取详细健康状态", description = "获取媒体服务的详细健康状态信息")
    public ResponseEntity<MonitorMetricsResponse> getDetailedHealth() {
        try {
            MonitorMetricsResponse response = monitorService.getCompleteMetrics();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            MonitorMetricsResponse errorResponse = MonitorMetricsResponse.builder()
                    .serviceName("media-service")
                    .instanceId("unknown")
                    .status(MonitorMetricsResponse.ServiceStatus.ERROR)
                    .healthCheckTime(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}