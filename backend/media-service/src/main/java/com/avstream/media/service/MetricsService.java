package com.avstream.media.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 指标监控服务
 */
@Service
public class MetricsService {
    
    private static final Logger log = LoggerFactory.getLogger(MetricsService.class);
    
    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<String, AtomicLong> gauges = new ConcurrentHashMap<>();
    
    // 计数器
    private Counter uploadCounter;
    private Counter downloadCounter;
    private Counter transcodeCounter;
    private Counter streamCounter;
    private Counter errorCounter;
    
    // 计时器
    private Timer uploadTimer;
    private Timer downloadTimer;
    private Timer transcodeTimer;
    private Timer streamTimer;
    
    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        initializeMetrics();
    }
    
    /**
     * 初始化指标
     */
    private void initializeMetrics() {
        // 初始化计数器
        uploadCounter = Counter.builder("media.upload.count")
                .description("文件上传次数")
                .register(meterRegistry);
                
        downloadCounter = Counter.builder("media.download.count")
                .description("文件下载次数")
                .register(meterRegistry);
                
        transcodeCounter = Counter.builder("media.transcode.count")
                .description("转码任务次数")
                .register(meterRegistry);
                
        streamCounter = Counter.builder("media.stream.count")
                .description("流媒体操作次数")
                .register(meterRegistry);
                
        errorCounter = Counter.builder("media.error.count")
                .description("错误次数")
                .register(meterRegistry);
        
        // 初始化计时器
        uploadTimer = Timer.builder("media.upload.duration")
                .description("文件上传耗时")
                .register(meterRegistry);
                
        downloadTimer = Timer.builder("media.download.duration")
                .description("文件下载耗时")
                .register(meterRegistry);
                
        transcodeTimer = Timer.builder("media.transcode.duration")
                .description("转码任务耗时")
                .register(meterRegistry);
                
        streamTimer = Timer.builder("media.stream.duration")
                .description("流媒体操作耗时")
                .register(meterRegistry);
        
        // 初始化Gauge
        initializeGauges();
        
        log.info("媒体服务指标监控初始化完成");
    }
    
    /**
     * 初始化Gauge指标
     */
    private void initializeGauges() {
        // 活跃上传任务数
        AtomicLong activeUploads = new AtomicLong(0);
        Gauge.builder("media.upload.active", activeUploads, AtomicLong::get)
                .description("活跃上传任务数")
                .register(meterRegistry);
        gauges.put("activeUploads", activeUploads);
        
        // 活跃转码任务数
        AtomicLong activeTranscodes = new AtomicLong(0);
        Gauge.builder("media.transcode.active", activeTranscodes, AtomicLong::get)
                .description("活跃转码任务数")
                .register(meterRegistry);
        gauges.put("activeTranscodes", activeTranscodes);
        
        // 活跃流数量
        AtomicLong activeStreams = new AtomicLong(0);
        Gauge.builder("media.stream.active", activeStreams, AtomicLong::get)
                .description("活跃流数量")
                .register(meterRegistry);
        gauges.put("activeStreams", activeStreams);
        
        // 存储使用量
        AtomicLong storageUsage = new AtomicLong(0);
        Gauge.builder("media.storage.usage", storageUsage, AtomicLong::get)
                .description("存储使用量（字节）")
                .register(meterRegistry);
        gauges.put("storageUsage", storageUsage);
        
        // 队列长度
        AtomicLong queueLength = new AtomicLong(0);
        Gauge.builder("media.queue.length", queueLength, AtomicLong::get)
                .description("任务队列长度")
                .register(meterRegistry);
        gauges.put("queueLength", queueLength);
    }
    
    /**
     * 记录上传操作
     */
    public void recordUpload(long duration, long fileSize, boolean success) {
        uploadCounter.increment();
        uploadTimer.record(duration, TimeUnit.MILLISECONDS);
        
        if (success) {
            // 记录成功上传的文件大小
            meterRegistry.counter("media.upload.size", "status", "success")
                    .increment(fileSize);
        } else {
            errorCounter.increment();
            meterRegistry.counter("media.upload.size", "status", "failed")
                    .increment(fileSize);
        }
    }
    
    /**
     * 记录下载操作
     */
    public void recordDownload(long duration, long fileSize, boolean success) {
        downloadCounter.increment();
        downloadTimer.record(duration, TimeUnit.MILLISECONDS);
        
        if (!success) {
            errorCounter.increment();
        }
    }
    
    /**
     * 记录转码操作
     */
    public void recordTranscode(long duration, String format, boolean success) {
        transcodeCounter.increment();
        transcodeTimer.record(duration, TimeUnit.MILLISECONDS);
        
        // 按格式记录转码
        meterRegistry.counter("media.transcode.format", "format", format)
                .increment();
        
        if (!success) {
            errorCounter.increment();
            meterRegistry.counter("media.transcode.error", "format", format)
                    .increment();
        }
    }
    
    /**
     * 记录流媒体操作
     */
    public void recordStreamOperation(long duration, String operation, String protocol, boolean success) {
        streamCounter.increment();
        streamTimer.record(duration, TimeUnit.MILLISECONDS);
        
        // 按操作类型和协议记录
        meterRegistry.counter("media.stream.operation", 
                "operation", operation, 
                "protocol", protocol)
                .increment();
        
        if (!success) {
            errorCounter.increment();
            meterRegistry.counter("media.stream.error", 
                    "operation", operation, 
                    "protocol", protocol)
                    .increment();
        }
    }
    
    /**
     * 记录错误
     */
    public void recordError(String errorType, String operation) {
        errorCounter.increment();
        meterRegistry.counter("media.error.detail", 
                "type", errorType, 
                "operation", operation)
                .increment();
    }
    
    /**
     * 更新Gauge值
     */
    public void updateGauge(String gaugeName, long value) {
        AtomicLong gauge = gauges.get(gaugeName);
        if (gauge != null) {
            gauge.set(value);
        } else {
            log.warn("未知的Gauge指标: {}", gaugeName);
        }
    }
    
    /**
     * 增加Gauge值
     */
    public void incrementGauge(String gaugeName) {
        AtomicLong gauge = gauges.get(gaugeName);
        if (gauge != null) {
            gauge.incrementAndGet();
        } else {
            log.warn("未知的Gauge指标: {}", gaugeName);
        }
    }
    
    /**
     * 减少Gauge值
     */
    public void decrementGauge(String gaugeName) {
        AtomicLong gauge = gauges.get(gaugeName);
        if (gauge != null) {
            gauge.decrementAndGet();
        } else {
            log.warn("未知的Gauge指标: {}", gaugeName);
        }
    }
    
    /**
     * 获取平均响应时间
     */
    public double getAverageResponseTime() {
        try {
            // 计算所有计时器的平均响应时间
            double totalMean = uploadTimer.mean(TimeUnit.MILLISECONDS) +
                              downloadTimer.mean(TimeUnit.MILLISECONDS) +
                              transcodeTimer.mean(TimeUnit.MILLISECONDS) +
                              streamTimer.mean(TimeUnit.MILLISECONDS);
            
            // 计算有效计时器数量
            int validCount = 0;
            if (uploadTimer.mean(TimeUnit.MILLISECONDS) > 0) validCount++;
            if (downloadTimer.mean(TimeUnit.MILLISECONDS) > 0) validCount++;
            if (transcodeTimer.mean(TimeUnit.MILLISECONDS) > 0) validCount++;
            if (streamTimer.mean(TimeUnit.MILLISECONDS) > 0) validCount++;
            
            return validCount > 0 ? totalMean / validCount : 0.0;
        } catch (Exception e) {
            log.warn("获取平均响应时间失败: {}", e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * 获取吞吐量（请求/秒）
     */
    public double getThroughput() {
        try {
            // 计算总请求数
            double totalCount = uploadCounter.count() + 
                               downloadCounter.count() + 
                               transcodeCounter.count() + 
                               streamCounter.count();
            
            // 获取应用启动时间（以秒为单位）
            long uptimeSeconds = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
            
            // 计算吞吐量（请求/秒）
            return uptimeSeconds > 0 ? totalCount / uptimeSeconds : 0.0;
        } catch (Exception e) {
            log.warn("获取吞吐量失败: {}", e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * 获取错误率
     */
    public double getErrorRate() {
        try {
            double totalOperations = uploadCounter.count() + downloadCounter.count() + 
                                   transcodeCounter.count() + streamCounter.count();
            return totalOperations > 0 ? errorCounter.count() / totalOperations : 0.0;
        } catch (Exception e) {
            log.warn("获取错误率失败: {}", e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * 获取指标统计信息
     */
    public MetricsSummary getMetricsSummary() {
        return MetricsSummary.builder()
                .uploadCount(uploadCounter.count())
                .downloadCount(downloadCounter.count())
                .transcodeCount(transcodeCounter.count())
                .streamCount(streamCounter.count())
                .errorCount(errorCounter.count())
                .activeUploads(gauges.get("activeUploads").get())
                .activeTranscodes(gauges.get("activeTranscodes").get())
                .activeStreams(gauges.get("activeStreams").get())
                .storageUsage(gauges.get("storageUsage").get())
                .queueLength(gauges.get("queueLength").get())
                .build();
    }
    
    /**
     * 指标统计摘要
     */
    public static class MetricsSummary {
        private double uploadCount;
        private double downloadCount;
        private double transcodeCount;
        private double streamCount;
        private double errorCount;
        private long activeUploads;
        private long activeTranscodes;
        private long activeStreams;
        private long storageUsage;
        private long queueLength;
        
        /**
         * 获取错误率（已移至MetricsService类中）
         */
        // public double getErrorRate() {
        //     double totalOperations = uploadCount + downloadCount + transcodeCount + streamCount;
        //     return totalOperations > 0 ? errorCount / totalOperations : 0;
        // }
        
        /**
         * 获取存储使用量（格式化）
         */
        public String getFormattedStorageUsage() {
            return formatFileSize(storageUsage);
        }
        
        private String formatFileSize(long size) {
            if (size < 1024) {
                return size + " B";
            } else if (size < 1024 * 1024) {
                return String.format("%.2f KB", size / 1024.0);
            } else if (size < 1024 * 1024 * 1024) {
                return String.format("%.2f MB", size / (1024.0 * 1024.0));
            } else {
                return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
            }
        }

        // Getter and Setter methods
        public double getUploadCount() { return uploadCount; }
        public void setUploadCount(double uploadCount) { this.uploadCount = uploadCount; }
        
        public double getDownloadCount() { return downloadCount; }
        public void setDownloadCount(double downloadCount) { this.downloadCount = downloadCount; }
        
        public double getTranscodeCount() { return transcodeCount; }
        public void setTranscodeCount(double transcodeCount) { this.transcodeCount = transcodeCount; }
        
        public double getStreamCount() { return streamCount; }
        public void setStreamCount(double streamCount) { this.streamCount = streamCount; }
        
        public double getErrorCount() { return errorCount; }
        public void setErrorCount(double errorCount) { this.errorCount = errorCount; }
        
        public long getActiveUploads() { return activeUploads; }
        public void setActiveUploads(long activeUploads) { this.activeUploads = activeUploads; }
        
        public long getActiveTranscodes() { return activeTranscodes; }
        public void setActiveTranscodes(long activeTranscodes) { this.activeTranscodes = activeTranscodes; }
        
        public long getActiveStreams() { return activeStreams; }
        public void setActiveStreams(long activeStreams) { this.activeStreams = activeStreams; }
        
        public long getStorageUsage() { return storageUsage; }
        public void setStorageUsage(long storageUsage) { this.storageUsage = storageUsage; }
        
        public long getQueueLength() { return queueLength; }
        public void setQueueLength(long queueLength) { this.queueLength = queueLength; }

        /**
         * 创建Builder实例
         */
        public static MetricsSummaryBuilder builder() {
            return new MetricsSummaryBuilder();
        }

        /**
         * Builder类
         */
        public static class MetricsSummaryBuilder {
            private MetricsSummary metricsSummary;

            public MetricsSummaryBuilder() {
                this.metricsSummary = new MetricsSummary();
            }

            public MetricsSummaryBuilder uploadCount(double uploadCount) {
                metricsSummary.setUploadCount(uploadCount);
                return this;
            }

            public MetricsSummaryBuilder downloadCount(double downloadCount) {
                metricsSummary.setDownloadCount(downloadCount);
                return this;
            }

            public MetricsSummaryBuilder transcodeCount(double transcodeCount) {
                metricsSummary.setTranscodeCount(transcodeCount);
                return this;
            }

            public MetricsSummaryBuilder streamCount(double streamCount) {
                metricsSummary.setStreamCount(streamCount);
                return this;
            }

            public MetricsSummaryBuilder errorCount(double errorCount) {
                metricsSummary.setErrorCount(errorCount);
                return this;
            }

            public MetricsSummaryBuilder activeUploads(long activeUploads) {
                metricsSummary.setActiveUploads(activeUploads);
                return this;
            }

            public MetricsSummaryBuilder activeTranscodes(long activeTranscodes) {
                metricsSummary.setActiveTranscodes(activeTranscodes);
                return this;
            }

            public MetricsSummaryBuilder activeStreams(long activeStreams) {
                metricsSummary.setActiveStreams(activeStreams);
                return this;
            }

            public MetricsSummaryBuilder storageUsage(long storageUsage) {
                metricsSummary.setStorageUsage(storageUsage);
                return this;
            }

            public MetricsSummaryBuilder queueLength(long queueLength) {
                metricsSummary.setQueueLength(queueLength);
                return this;
            }

            public MetricsSummary build() {
                return metricsSummary;
            }
        }
    }
}