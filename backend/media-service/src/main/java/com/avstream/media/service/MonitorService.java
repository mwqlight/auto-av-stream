package com.avstream.media.service;

import com.avstream.media.dto.response.MonitorMetricsResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 监控服务类
 */
@Slf4j
@Service
public class MonitorService {

    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<String, AtomicLong> businessMetrics = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Timer> performanceTimers = new ConcurrentHashMap<>();

    // 业务指标计数器
    private Counter uploadCounter;
    private Counter downloadCounter;
    private Counter transcodeCounter;
    private Counter streamingCounter;
    private Counter errorCounter;
    private Counter totalRequestsCounter;
    private Counter successRequestsCounter;
    private Counter failedRequestsCounter;

    @Autowired
    public MonitorService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void init() {
        // 初始化业务指标计数器
        uploadCounter = Counter.builder("media.upload.count")
                .description("媒体文件上传次数")
                .register(meterRegistry);

        downloadCounter = Counter.builder("media.download.count")
                .description("媒体文件下载次数")
                .register(meterRegistry);

        transcodeCounter = Counter.builder("media.transcode.count")
                .description("媒体文件转码次数")
                .register(meterRegistry);

        streamingCounter = Counter.builder("media.streaming.count")
                .description("流媒体播放次数")
                .register(meterRegistry);

        errorCounter = Counter.builder("media.error.count")
                .description("媒体服务错误次数")
                .register(meterRegistry);

        totalRequestsCounter = Counter.builder("media.requests.total")
                .description("总请求数")
                .register(meterRegistry);

        successRequestsCounter = Counter.builder("media.requests.success")
                .description("成功请求数")
                .register(meterRegistry);

        failedRequestsCounter = Counter.builder("media.requests.failed")
                .description("失败请求数")
                .register(meterRegistry);

        // 初始化性能指标
        Timer.builder("media.request.duration")
                .description("请求处理时间")
                .register(meterRegistry);

        // 初始化系统指标Gauge
        Gauge.builder("media.system.cpu.usage", this, MonitorService::getCpuUsage)
                .description("CPU使用率")
                .register(meterRegistry);

        Gauge.builder("media.system.memory.usage", this, MonitorService::getMemoryUsage)
                .description("内存使用率")
                .register(meterRegistry);

        Gauge.builder("media.system.thread.count", this, MonitorService::getThreadCount)
                .description("线程数量")
                .register(meterRegistry);

        log.info("监控服务初始化完成");
    }

    /**
     * 获取CPU使用率
     */
    public double getCpuUsage() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean sunOsBean = 
                    (com.sun.management.OperatingSystemMXBean) osBean;
                return sunOsBean.getSystemCpuLoad() * 100;
            }
        } catch (Exception e) {
            log.warn("获取CPU使用率失败: {}", e.getMessage());
        }
        return 0.0;
    }

    /**
     * 获取内存使用率
     */
    public double getMemoryUsage() {
        try {
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            long used = memoryBean.getHeapMemoryUsage().getUsed() + 
                       memoryBean.getNonHeapMemoryUsage().getUsed();
            long max = memoryBean.getHeapMemoryUsage().getMax() + 
                      memoryBean.getNonHeapMemoryUsage().getMax();
            
            if (max > 0) {
                return (double) used / max * 100;
            }
        } catch (Exception e) {
            log.warn("获取内存使用率失败: {}", e.getMessage());
        }
        return 0.0;
    }

    /**
     * 获取线程数量
     */
    public int getThreadCount() {
        try {
            return ManagementFactory.getThreadMXBean().getThreadCount();
        } catch (Exception e) {
            log.warn("获取线程数量失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 记录上传操作
     */
    public void recordUpload() {
        uploadCounter.increment();
        totalRequestsCounter.increment();
        successRequestsCounter.increment();
    }

    /**
     * 记录下载操作
     */
    public void recordDownload() {
        downloadCounter.increment();
        totalRequestsCounter.increment();
        successRequestsCounter.increment();
    }

    /**
     * 记录转码操作
     */
    public void recordTranscode() {
        transcodeCounter.increment();
        totalRequestsCounter.increment();
        successRequestsCounter.increment();
    }

    /**
     * 记录流媒体操作
     */
    public void recordStreaming() {
        streamingCounter.increment();
        totalRequestsCounter.increment();
        successRequestsCounter.increment();
    }

    /**
     * 记录错误操作
     */
    public void recordError() {
        errorCounter.increment();
        totalRequestsCounter.increment();
        failedRequestsCounter.increment();
    }

    /**
     * 记录请求处理时间
     */
    public Timer.Sample startRequestTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * 停止请求计时器并记录时间
     */
    public void stopRequestTimer(Timer.Sample sample, String operation) {
        if (sample != null) {
            sample.stop(Timer.builder("media.request.duration")
                    .tag("operation", operation)
                    .register(meterRegistry));
        }
    }

    /**
     * 获取完整的监控指标
     */
    public MonitorMetricsResponse getCompleteMetrics() {
        return MonitorMetricsResponse.builder()
                .serviceName("media-service")
                .instanceId("media-service-001")
                .status(getServiceStatus())
                .healthCheckTime(LocalDateTime.now())
                .systemMetrics(getSystemMetrics())
                .businessMetrics(getBusinessMetrics())
                .performanceMetrics(getPerformanceMetrics())
                .build();
    }

    /**
     * 获取服务状态
     */
    private MonitorMetricsResponse.ServiceStatus getServiceStatus() {
        double cpuUsage = getCpuUsage();
        double memoryUsage = getMemoryUsage();
        
        if (cpuUsage > 90 || memoryUsage > 90) {
            return MonitorMetricsResponse.ServiceStatus.ERROR;
        } else if (cpuUsage > 80 || memoryUsage > 80) {
            return MonitorMetricsResponse.ServiceStatus.WARNING;
        } else {
            return MonitorMetricsResponse.ServiceStatus.HEALTHY;
        }
    }

    /**
     * 获取系统指标
     */
    private MonitorMetricsResponse.SystemMetrics getSystemMetrics() {
        double systemLoad = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        return MonitorMetricsResponse.SystemMetrics.builder()
                .cpuUsage(getCpuUsage())
                .memoryUsage(getMemoryUsage())
                .diskUsage(0.0) // 需要实现磁盘使用率获取
                .systemLoad(Double.valueOf(systemLoad))
                .threadCount(Integer.valueOf(getThreadCount()))
                .activeConnections(Integer.valueOf(0)) // 需要实现连接数统计
                .networkIo(0.0) // 需要实现网络IO统计
                .build();
    }

    /**
     * 获取业务指标
     */
    private MonitorMetricsResponse.BusinessMetrics getBusinessMetrics() {
        return MonitorMetricsResponse.BusinessMetrics.builder()
                .uploadCount((long) uploadCounter.count())
                .downloadCount((long) downloadCounter.count())
                .transcodeCount((long) transcodeCounter.count())
                .streamingCount((long) streamingCounter.count())
                .errorCount((long) errorCounter.count())
                .totalRequests((long) totalRequestsCounter.count())
                .successRequests((long) successRequestsCounter.count())
                .failedRequests((long) failedRequestsCounter.count())
                .build();
    }

    /**
     * 获取性能指标
     */
    private MonitorMetricsResponse.PerformanceMetrics getPerformanceMetrics() {
        // 这里可以添加更复杂的性能指标计算
        return MonitorMetricsResponse.PerformanceMetrics.builder()
                .avgResponseTime(0.0) // 需要实现平均响应时间计算
                .throughput(0.0) // 需要实现吞吐量计算
                .errorRate(failedRequestsCounter.count() > 0 ? 
                    (double) failedRequestsCounter.count() / totalRequestsCounter.count() * 100 : 0.0)
                .queueSize(0) // 需要实现队列长度统计
                .activeTasks(0) // 需要实现活跃任务数统计
                .latency(0.0) // 需要实现延迟统计
                .build();
    }

    /**
     * 重置所有指标
     */
    public void resetMetrics() {
        // 重置计数器
        meterRegistry.clear();
        
        // 重新初始化计数器
        init();
        
        log.info("监控指标已重置");
    }
}