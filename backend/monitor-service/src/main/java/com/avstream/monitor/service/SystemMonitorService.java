package com.avstream.monitor.service;

import com.avstream.monitor.entity.MonitorMetric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统监控服务
 */
@Service
@RequiredArgsConstructor
public class SystemMonitorService {
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SystemMonitorService.class);
    
    private final MonitorMetricService monitorMetricService;
    
    /**
     * 收集系统指标
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void collectSystemMetrics() {
        try {
            List<MonitorMetric> metrics = new ArrayList<>();
            
            // 收集CPU使用率
            metrics.add(collectCpuUsage());
            
            // 收集内存使用情况
            metrics.addAll(collectMemoryUsage());
            
            // 收集线程信息
            metrics.add(collectThreadInfo());
            
            // 保存指标数据
            monitorMetricService.saveMetrics(metrics);
            
            log.debug("系统指标收集完成，数量: {}", metrics.size());
        } catch (Exception e) {
            log.error("系统指标收集失败", e);
        }
    }
    
    /**
     * 收集CPU使用率
     */
    private MonitorMetric collectCpuUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double cpuUsage = 0.0;
        
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = 
                (com.sun.management.OperatingSystemMXBean) osBean;
            cpuUsage = sunOsBean.getSystemCpuLoad() * 100;
        }
        
        MonitorMetric metric = new MonitorMetric();
        metric.setMetricName("system.cpu.usage");
        metric.setServiceName("monitor-service");
        metric.setMetricValue(cpuUsage);
        metric.setMetricUnit("%");
        metric.setTags("type=system");
        metric.setTimestamp(LocalDateTime.now());
        
        return metric;
    }
    
    /**
     * 收集内存使用情况
     */
    private List<MonitorMetric> collectMemoryUsage() {
        List<MonitorMetric> metrics = new ArrayList<>();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        // 堆内存使用情况
        MemoryUsage heapMemory = memoryBean.getHeapMemoryUsage();
        MonitorMetric heapMetric = new MonitorMetric();
        heapMetric.setMetricName("jvm.memory.heap.used");
        heapMetric.setServiceName("monitor-service");
        heapMetric.setMetricValue((double) heapMemory.getUsed() / 1024 / 1024); // MB
        heapMetric.setMetricUnit("MB");
        heapMetric.setTags("type=heap");
        heapMetric.setTimestamp(LocalDateTime.now());
        metrics.add(heapMetric);
        
        // 非堆内存使用情况
        MemoryUsage nonHeapMemory = memoryBean.getNonHeapMemoryUsage();
        MonitorMetric nonHeapMetric = new MonitorMetric();
        nonHeapMetric.setMetricName("jvm.memory.nonheap.used");
        nonHeapMetric.setServiceName("monitor-service");
        nonHeapMetric.setMetricValue((double) nonHeapMemory.getUsed() / 1024 / 1024); // MB
        nonHeapMetric.setMetricUnit("MB");
        nonHeapMetric.setTags("type=nonheap");
        nonHeapMetric.setTimestamp(LocalDateTime.now());
        metrics.add(nonHeapMetric);
        
        return metrics;
    }
    
    /**
     * 收集线程信息
     */
    private MonitorMetric collectThreadInfo() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        int threadCount = threadBean.getThreadCount();
        
        MonitorMetric metric = new MonitorMetric();
        metric.setMetricName("jvm.thread.count");
        metric.setServiceName("monitor-service");
        metric.setMetricValue((double) threadCount);
        metric.setMetricUnit("count");
        metric.setTags("type=thread");
        metric.setTimestamp(LocalDateTime.now());
        
        return metric;
    }
    
    /**
     * 获取系统健康状态
     */
    public String getSystemHealth() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            
            double cpuUsage = 0.0;
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean sunOsBean = 
                    (com.sun.management.OperatingSystemMXBean) osBean;
                cpuUsage = sunOsBean.getSystemCpuLoad() * 100;
            }
            
            MemoryUsage heapMemory = memoryBean.getHeapMemoryUsage();
            double heapUsage = (double) heapMemory.getUsed() / heapMemory.getMax() * 100;
            
            if (cpuUsage > 90 || heapUsage > 90) {
                return "CRITICAL";
            } else if (cpuUsage > 80 || heapUsage > 80) {
                return "WARNING";
            } else {
                return "HEALTHY";
            }
        } catch (Exception e) {
            log.error("获取系统健康状态失败", e);
            return "UNKNOWN";
        }
    }
}