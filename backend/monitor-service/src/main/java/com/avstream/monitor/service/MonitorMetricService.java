package com.avstream.monitor.service;

import com.avstream.monitor.dto.request.MetricQueryRequest;
import com.avstream.monitor.dto.response.MetricResponse;
import com.avstream.monitor.entity.MonitorMetric;
import com.avstream.monitor.repository.MonitorMetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 监控指标服务
 */
@Service
@RequiredArgsConstructor
public class MonitorMetricService {
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MonitorMetricService.class);
    
    private final MonitorMetricRepository monitorMetricRepository;
    
    /**
     * 保存监控指标
     */
    public void saveMetric(MonitorMetric metric) {
        try {
            monitorMetricRepository.save(metric);
            log.debug("保存监控指标成功: {} - {}", metric.getServiceName(), metric.getMetricName());
        } catch (Exception e) {
            log.error("保存监控指标失败: {} - {}", metric.getServiceName(), metric.getMetricName(), e);
        }
    }
    
    /**
     * 批量保存监控指标
     */
    public void saveMetrics(List<MonitorMetric> metrics) {
        try {
            monitorMetricRepository.saveAll(metrics);
            log.debug("批量保存监控指标成功，数量: {}", metrics.size());
        } catch (Exception e) {
            log.error("批量保存监控指标失败", e);
        }
    }
    
    /**
     * 查询监控指标
     */
    public Page<MetricResponse> queryMetrics(MetricQueryRequest request) {
        Pageable pageable = PageRequest.of(
            request.getPage() - 1, 
            request.getSize(),
            Sort.by(Sort.Direction.fromString(request.getSortOrder()), request.getSortBy())
        );
        
        Page<MonitorMetric> metrics;
        if (request.getMetricName() != null && request.getServiceName() != null) {
            metrics = monitorMetricRepository.findByMetricNameAndServiceName(
                request.getMetricName(), request.getServiceName(), pageable);
        } else if (request.getServiceName() != null) {
            metrics = monitorMetricRepository.findByServiceName(request.getServiceName(), pageable);
        } else {
            metrics = monitorMetricRepository.findAll(pageable);
        }
        
        return metrics.map(this::convertToResponse);
    }
    
    /**
     * 获取最新指标数据
     */
    public List<MetricResponse> getLatestMetrics(String metricName, String serviceName, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
        List<MonitorMetric> metrics = monitorMetricRepository.findLatestMetrics(
            metricName, serviceName, pageable);
        
        return metrics.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取最新指标数据实体
     */
    public List<MonitorMetric> getLatestMetricEntities(String metricName, String serviceName, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
        return monitorMetricRepository.findLatestMetrics(metricName, serviceName, pageable);
    }

    /**
     * 获取指标数据
     */
    public List<MetricResponse> getMetrics(String metricName, String serviceName, 
                                          LocalDateTime startTime, LocalDateTime endTime, int limit) {
        List<MonitorMetric> metrics = monitorMetricRepository.findByMetricNameAndServiceNameAndTimestampBetween(
            metricName, serviceName, startTime, endTime);
        
        // 手动限制结果数量
        if (limit > 0 && metrics.size() > limit) {
            metrics = metrics.subList(0, limit);
        }
        
        return metrics.stream()
            .map(this::convertToResponse)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取指标趋势
     */
    public Map<String, List<MetricResponse>> getMetricsTrend(List<String> metricNames, int hours) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(hours);
        
        Map<String, List<MetricResponse>> trends = new java.util.HashMap<>();
        
        for (String metricName : metricNames) {
            List<MetricResponse> metrics = getMetrics(metricName, null, startTime, endTime, 100);
            trends.put(metricName, metrics);
        }
        
        return trends;
    }

    /**
     * 获取总指标数量
     */
    public long getTotalMetricsCount() {
        return monitorMetricRepository.count();
    }
    
    /**
     * 统计指标数据
     */
    public Long countMetrics(String serviceName, LocalDateTime startTime) {
        return monitorMetricRepository.countByServiceNameAndTimeRange(serviceName, startTime);
    }
    
    /**
     * 清理过期数据
     */
    public void cleanupExpiredData(LocalDateTime expireTime) {
        try {
            monitorMetricRepository.deleteByTimestampBefore(expireTime);
            log.info("清理过期监控指标数据完成");
        } catch (Exception e) {
            log.error("清理过期监控指标数据失败", e);
        }
    }
    
    /**
     * 实体转换为响应DTO
     */
    private MetricResponse convertToResponse(MonitorMetric metric) {
        MetricResponse response = new MetricResponse();
        response.setId(metric.getId());
        response.setMetricName(metric.getMetricName());
        response.setServiceName(metric.getServiceName());
        response.setMetricValue(metric.getMetricValue());
        response.setMetricUnit(metric.getMetricUnit());
        response.setTags(metric.getTags());
        response.setTimestamp(metric.getTimestamp());
        response.setCreatedAt(metric.getCreatedAt());
        return response;
    }
}