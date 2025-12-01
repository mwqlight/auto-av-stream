package com.avstream.ai.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * AI服务指标监控服务
 */
@Slf4j
@Service
public class AIMetricsService {

    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<String, Counter> requestCounters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Timer> requestTimers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Counter> errorCounters = new ConcurrentHashMap<>();

    public AIMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void init() {
        log.info("初始化AI服务指标监控...");
        
        // 初始化基础指标
        initializeBaseMetrics();
        
        log.info("AI服务指标监控初始化完成");
    }

    /**
     * 初始化基础指标
     */
    private void initializeBaseMetrics() {
        // 请求总数计数器
        Counter.builder("ai.requests.total")
            .description("AI服务总请求数")
            .register(meterRegistry);

        // 错误总数计数器
        Counter.builder("ai.errors.total")
            .description("AI服务总错误数")
            .register(meterRegistry);

        // 平均响应时间计时器
        Timer.builder("ai.response.time")
            .description("AI服务平均响应时间")
            .register(meterRegistry);
    }

    /**
     * 记录请求指标
     */
    public void recordRequest(String requestType, long duration, boolean success) {
        try {
            // 记录总请求数
            Counter totalCounter = requestCounters.computeIfAbsent("total", 
                k -> Counter.builder("ai.requests.total")
                    .tag("type", requestType)
                    .register(meterRegistry));
            totalCounter.increment();

            // 记录特定类型请求数
            Counter typeCounter = requestCounters.computeIfAbsent(requestType,
                k -> Counter.builder("ai.requests.by_type")
                    .tag("type", requestType)
                    .register(meterRegistry));
            typeCounter.increment();

            // 记录响应时间
            Timer timer = requestTimers.computeIfAbsent(requestType,
                k -> Timer.builder("ai.response.time.by_type")
                    .tag("type", requestType)
                    .register(meterRegistry));
            timer.record(duration, TimeUnit.MILLISECONDS);

            // 记录错误
            if (!success) {
                Counter errorCounter = errorCounters.computeIfAbsent(requestType,
                    k -> Counter.builder("ai.errors.by_type")
                        .tag("type", requestType)
                        .register(meterRegistry));
                errorCounter.increment();

                // 总错误数
                Counter.builder("ai.errors.total")
                    .register(meterRegistry)
                    .increment();
            }

            log.debug("记录AI服务指标: type={}, duration={}ms, success={}", 
                     requestType, duration, success);

        } catch (Exception e) {
            log.warn("记录AI服务指标失败: {}", e.getMessage());
        }
    }

    /**
     * 记录模型加载指标
     */
    public void recordModelLoad(String modelName, boolean success, long duration) {
        try {
            Counter.builder("ai.model.load")
                .tag("model", modelName)
                .tag("success", String.valueOf(success))
                .register(meterRegistry)
                .increment();

            Timer.builder("ai.model.load.time")
                .tag("model", modelName)
                .register(meterRegistry)
                .record(duration, TimeUnit.MILLISECONDS);

        } catch (Exception e) {
            log.warn("记录模型加载指标失败: {}", e.getMessage());
        }
    }

    /**
     * 记录内存使用指标
     */
    public void recordMemoryUsage(long usedMemory, long maxMemory) {
        try {
            meterRegistry.gauge("ai.memory.used", usedMemory);
            meterRegistry.gauge("ai.memory.max", maxMemory);
            
            if (maxMemory > 0) {
                double usageRatio = (double) usedMemory / maxMemory;
                meterRegistry.gauge("ai.memory.usage_ratio", usageRatio);
            }

        } catch (Exception e) {
            log.warn("记录内存使用指标失败: {}", e.getMessage());
        }
    }

    /**
     * 记录GPU使用指标（如果可用）
     */
    public void recordGpuUsage(double gpuUsage, double gpuMemoryUsage) {
        try {
            meterRegistry.gauge("ai.gpu.usage", gpuUsage);
            meterRegistry.gauge("ai.gpu.memory.usage", gpuMemoryUsage);

        } catch (Exception e) {
            log.debug("记录GPU使用指标失败（可能不支持GPU）: {}", e.getMessage());
        }
    }

    /**
     * 获取当前指标快照
     */
    public String getMetricsSnapshot() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            long maxMemory = runtime.maxMemory();

            return String.format(
                "AI服务指标快照 - 内存使用: %dMB/%dMB (%.1f%%), 线程数: %d",
                usedMemory / (1024 * 1024),
                maxMemory / (1024 * 1024),
                (double) usedMemory / maxMemory * 100,
                Thread.activeCount()
            );

        } catch (Exception e) {
            return "获取指标快照失败: " + e.getMessage();
        }
    }
}