package com.avstream.media.service;

import com.avstream.media.entity.MonitorMetrics;
import com.avstream.media.repository.MonitorMetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 监控指标收集服务
 * 负责定期收集和存储媒体服务的各项监控指标
 * 
 * @author AV Stream Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MonitorMetricsService {

    private final MonitorMetricsRepository monitorMetricsRepository;
    private final MetricsService metricsService;
    
    // 业务指标计数器
    private final AtomicLong uploadCounter = new AtomicLong(0);
    private final AtomicLong downloadCounter = new AtomicLong(0);
    private final AtomicLong transcodeCounter = new AtomicLong(0);
    private final AtomicLong streamingCounter = new AtomicLong(0);
    private final AtomicLong errorCounter = new AtomicLong(0);

    /**
     * 每30秒收集一次系统级监控指标
     */
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void collectSystemMetrics() {
        try {
            log.debug("开始收集系统监控指标");
            
            // 收集CPU使用率
            collectCpuUsage();
            
            // 收集内存使用情况
            collectMemoryUsage();
            
            // 收集线程信息
            collectThreadMetrics();
            
            // 收集系统负载
            collectSystemLoad();
            
            log.debug("系统监控指标收集完成");
        } catch (Exception e) {
            log.error("收集系统监控指标失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 每60秒收集一次服务级监控指标
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void collectServiceMetrics() {
        try {
            log.debug("开始收集服务监控指标");
            
            // 收集业务指标
            collectBusinessMetrics();
            
            // 收集性能指标
            collectPerformanceMetrics();
            
            // 收集存储指标
            collectStorageMetrics();
            
            // 收集网络指标
            collectNetworkMetrics();
            
            log.debug("服务监控指标收集完成");
        } catch (Exception e) {
            log.error("收集服务监控指标失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 收集CPU使用率
     */
    private void collectCpuUsage() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean sunOsBean = 
                    (com.sun.management.OperatingSystemMXBean) osBean;
                double cpuUsage = sunOsBean.getProcessCpuLoad() * 100;
                
                saveMetric(MonitorMetrics.MetricNames.CPU_USAGE, 
                          cpuUsage, "%", 
                          "SYSTEM", 
                          "media-service");
            }
        } catch (Exception e) {
            log.warn("收集CPU使用率失败: {}", e.getMessage());
        }
    }

    /**
     * 收集内存使用情况
     */
    private void collectMemoryUsage() {
        try {
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
            MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
            
            // 堆内存使用率
            double heapUsagePercent = (double) heapUsage.getUsed() / heapUsage.getMax() * 100;
            saveMetric("heap_memory_usage", heapUsagePercent, "%", 
                      "SYSTEM", 
                      "media-service");
            
            // 非堆内存使用率
            double nonHeapUsagePercent = (double) nonHeapUsage.getUsed() / nonHeapUsage.getMax() * 100;
            saveMetric("non_heap_memory_usage", nonHeapUsagePercent, "%", 
                      "SYSTEM", 
                      "media-service");
            
            // 总内存使用率
            double totalMemoryUsage = (double) (heapUsage.getUsed() + nonHeapUsage.getUsed()) / 
                                     (heapUsage.getMax() + nonHeapUsage.getMax()) * 100;
            saveMetric(MonitorMetrics.MetricNames.MEMORY_USAGE, totalMemoryUsage, "%", 
                      "SYSTEM", 
                      "media-service");
        } catch (Exception e) {
            log.warn("收集内存使用情况失败: {}", e.getMessage());
        }
    }

    /**
     * 收集线程指标
     */
    private void collectThreadMetrics() {
        try {
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            int threadCount = threadBean.getThreadCount();
            int peakThreadCount = threadBean.getPeakThreadCount();
            int daemonThreadCount = threadBean.getDaemonThreadCount();
            
            saveMetric("thread_count", (double) threadCount, "个", 
                      "SYSTEM", 
                      "media-service");
            saveMetric("peak_thread_count", (double) peakThreadCount, "个", 
                      "SYSTEM", 
                      "media-service");
            saveMetric("daemon_thread_count", (double) daemonThreadCount, "个", 
                      "SYSTEM", 
                      "media-service");
        } catch (Exception e) {
            log.warn("收集线程指标失败: {}", e.getMessage());
        }
    }

    /**
     * 收集系统负载
     */
    private void collectSystemLoad() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            double systemLoad = osBean.getSystemLoadAverage();
            
            if (systemLoad >= 0) {
                saveMetric("system_load", systemLoad, "", 
                          "SYSTEM", 
                          "media-service");
            }
        } catch (Exception e) {
            log.warn("收集系统负载失败: {}", e.getMessage());
        }
    }

    /**
     * 收集业务指标
     */
    private void collectBusinessMetrics() {
        try {
            // 上传次数
            long uploadCount = uploadCounter.getAndSet(0);
            saveMetric(MonitorMetrics.MetricNames.UPLOAD_COUNT, (double) uploadCount, "次", 
                      "BUSINESS", 
                      "media-service");
            
            // 下载次数
            long downloadCount = downloadCounter.getAndSet(0);
            saveMetric(MonitorMetrics.MetricNames.DOWNLOAD_COUNT, (double) downloadCount, "次", 
                      "BUSINESS", 
                      "media-service");
            
            // 转码次数
            long transcodeCount = transcodeCounter.getAndSet(0);
            saveMetric(MonitorMetrics.MetricNames.TRANSCODE_COUNT, (double) transcodeCount, "次", 
                      "BUSINESS", 
                      "media-service");
            
            // 流媒体次数
            long streamingCount = streamingCounter.getAndSet(0);
            saveMetric(MonitorMetrics.MetricNames.STREAMING_COUNT, (double) streamingCount, "次", 
                      "BUSINESS", 
                      "media-service");
            
            // 错误次数
            long errorCount = errorCounter.getAndSet(0);
            saveMetric(MonitorMetrics.MetricNames.ERROR_COUNT, (double) errorCount, "次", 
                      "BUSINESS", 
                      "media-service");
        } catch (Exception e) {
            log.warn("收集业务指标失败: {}", e.getMessage());
        }
    }

    /**
     * 收集性能指标
     */
    private void collectPerformanceMetrics() {
        try {
            // 从MetricsService获取性能指标
            double avgResponseTime = metricsService.getAverageResponseTime();
            double throughput = metricsService.getThroughput();
            double errorRate = metricsService.getErrorRate();
            
            saveMetric("avg_response_time", avgResponseTime, "ms", 
                      "PERFORMANCE", 
                      "media-service");
            saveMetric("throughput", throughput, "req/s", 
                      "PERFORMANCE", 
                      "media-service");
            saveMetric("error_rate", errorRate, "%", 
                      "PERFORMANCE", 
                      "media-service");
        } catch (Exception e) {
            log.warn("收集性能指标失败: {}", e.getMessage());
        }
    }

    /**
     * 收集存储指标
     */
    private void collectStorageMetrics() {
        try {
            // 这里可以添加存储相关的指标收集逻辑
            // 例如：磁盘使用率、文件数量等
            
            // 示例：模拟磁盘使用率
            double diskUsage = 75.5; // 实际应该从系统获取
            saveMetric(MonitorMetrics.MetricNames.DISK_USAGE, diskUsage, "%", 
                      "SYSTEM", 
                      "media-service");
        } catch (Exception e) {
            log.warn("收集存储指标失败: {}", e.getMessage());
        }
    }

    /**
     * 收集网络指标
     */
    private void collectNetworkMetrics() {
        try {
            // 这里可以添加网络相关的指标收集逻辑
            // 例如：网络IO、连接数等
            
            // 示例：模拟活跃连接数
            double activeConnections = 150.0; // 实际应该从系统获取
            saveMetric(MonitorMetrics.MetricNames.ACTIVE_CONNECTIONS, activeConnections, "个", 
                      "SERVICE", 
                      "media-service");
        } catch (Exception e) {
            log.warn("收集网络指标失败: {}", e.getMessage());
        }
    }

    /**
     * 保存监控指标
     */
    private void saveMetric(String metricName, double metricValue, String unit, 
                           String metricType, String serviceName) {
        MonitorMetrics metric = new MonitorMetrics();
        metric.setMetricName(metricName);
        metric.setMetricValue(metricValue);
        metric.setMetricUnit(unit);
        metric.setMetricType(metricType);
        metric.setServiceName(serviceName);
        metric.setInstanceId(getInstanceId());
        metric.setCollectTime(LocalDateTime.now());
        
        monitorMetricsRepository.save(metric);
        log.debug("保存监控指标: {} = {} {}", metricName, metricValue, unit);
    }

    /**
     * 获取实例ID
     */
    private String getInstanceId() {
        // 这里可以返回实际的实例ID，例如：主机名+进程ID
        try {
            String hostname = java.net.InetAddress.getLocalHost().getHostName();
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            return hostname + "-" + pid;
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * 增加上传计数
     */
    public void incrementUploadCount() {
        uploadCounter.incrementAndGet();
    }

    /**
     * 增加下载计数
     */
    public void incrementDownloadCount() {
        downloadCounter.incrementAndGet();
    }

    /**
     * 增加转码计数
     */
    public void incrementTranscodeCount() {
        transcodeCounter.incrementAndGet();
    }

    /**
     * 增加流媒体计数
     */
    public void incrementStreamingCount() {
        streamingCounter.incrementAndGet();
    }

    /**
     * 增加错误计数
     */
    public void incrementErrorCount() {
        errorCounter.incrementAndGet();
    }

    /**
     * 清理过期监控数据（每天凌晨执行）
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupExpiredMetrics() {
        try {
            LocalDateTime expireTime = LocalDateTime.now().minusDays(30); // 保留30天数据
            monitorMetricsRepository.deleteByCollectTimeBefore(expireTime);
            log.info("清理过期监控数据完成");
        } catch (Exception e) {
            log.error("清理过期监控数据失败: {}", e.getMessage(), e);
        }
    }
}