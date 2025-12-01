package com.avstream.monitor.service;

import com.avstream.monitor.entity.MonitorMetric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 应用监控服务
 */
@Service
@RequiredArgsConstructor
public class ApplicationMonitorService {
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ApplicationMonitorService.class);
    
    private final MonitorMetricService monitorMetricService;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${monitor.application.services}")
    private List<String> services;
    
    /**
     * 监控应用服务健康状态
     */
    @Scheduled(fixedRate = 30000) // 每30秒执行一次
    public void monitorApplicationHealth() {
        try {
            List<MonitorMetric> metrics = new ArrayList<>();
            
            for (String service : services) {
                try {
                    String healthUrl = getHealthUrl(service);
                    Map<String, Object> healthInfo = restTemplate.getForObject(healthUrl, Map.class);
                    
                    if (healthInfo != null) {
                        metrics.add(createHealthMetric(service, healthInfo));
                        log.debug("服务健康检查成功: {}", service);
                    }
                } catch (Exception e) {
                    log.warn("服务健康检查失败: {}", service);
                    metrics.add(createErrorMetric(service));
                }
            }
            
            // 保存指标数据
            monitorMetricService.saveMetrics(metrics);
            
            log.debug("应用健康监控完成，数量: {}", metrics.size());
        } catch (Exception e) {
            log.error("应用健康监控失败", e);
        }
    }
    
    /**
     * 获取服务健康检查URL
     */
    private String getHealthUrl(String service) {
        switch (service) {
            case "auth-service":
                return "http://localhost:8081/actuator/health";
            case "media-service":
                return "http://localhost:8082/actuator/health";
            case "ai-service":
                return "http://localhost:8083/actuator/health";
            case "live-service":
                return "http://localhost:8084/actuator/health";
            case "gateway":
                return "http://localhost:8080/actuator/health";
            default:
                return "http://localhost:8085/actuator/health";
        }
    }
    
    /**
     * 创建健康指标
     */
    private MonitorMetric createHealthMetric(String service, Map<String, Object> healthInfo) {
        String status = getHealthStatus(healthInfo);
        double statusValue = "UP".equals(status) ? 1.0 : 0.0;
        
        MonitorMetric metric = new MonitorMetric();
        metric.setMetricName("application.health");
        metric.setServiceName(service);
        metric.setMetricValue(statusValue);
        metric.setMetricUnit("status");
        metric.setTags("type=health,status=" + status);
        metric.setTimestamp(LocalDateTime.now());
        
        return metric;
    }
    
    /**
     * 创建错误指标
     */
    private MonitorMetric createErrorMetric(String service) {
        MonitorMetric metric = new MonitorMetric();
        metric.setMetricName("application.health");
        metric.setServiceName(service);
        metric.setMetricValue(0.0);
        metric.setMetricUnit("status");
        metric.setTags("type=health,status=DOWN");
        metric.setTimestamp(LocalDateTime.now());
        
        return metric;
    }
    
    /**
     * 获取健康状态
     */
    private String getHealthStatus(Map<String, Object> healthInfo) {
        if (healthInfo.containsKey("status")) {
            return healthInfo.get("status").toString();
        }
        return "UNKNOWN";
    }
    
    /**
     * 获取应用服务健康状态
     */
    public String getApplicationHealth(String service) {
        try {
            String healthUrl = getHealthUrl(service);
            Map<String, Object> healthInfo = restTemplate.getForObject(healthUrl, Map.class);
            
            if (healthInfo != null) {
                return getHealthStatus(healthInfo);
            }
        } catch (Exception e) {
            log.warn("获取应用服务健康状态失败: {}", service, e);
        }
        
        return "DOWN";
    }
    
    /**
     * 获取所有应用服务健康状态
     */
    public Map<String, String> getAllApplicationHealth() {
        return services.stream()
            .collect(java.util.stream.Collectors.toMap(
                service -> service,
                this::getApplicationHealth
            ));
    }
}