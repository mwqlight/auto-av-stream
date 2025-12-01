package com.avstream.media.repository;

import com.avstream.media.entity.MonitorMetrics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 监控指标数据访问接口
 * 
 * @author AV Stream Team
 */
@Repository
public interface MonitorMetricsRepository extends JpaRepository<MonitorMetrics, Long> {

    /**
     * 根据服务名称和指标名称查找最新指标
     */
    Optional<MonitorMetrics> findTopByServiceNameAndMetricNameOrderByCollectTimeDesc(
            String serviceName, String metricName);

    /**
     * 根据服务名称和指标类型查找指标列表
     */
    List<MonitorMetrics> findByServiceNameAndMetricTypeOrderByCollectTimeDesc(
            String serviceName, String metricType);

    /**
     * 根据服务名称和指标名称查找指定时间范围内的指标
     */
    List<MonitorMetrics> findByServiceNameAndMetricNameAndCollectTimeBetween(
            String serviceName, String metricName, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据服务名称查找指定时间范围内的指标
     */
    List<MonitorMetrics> findByServiceNameAndCollectTimeBetween(
            String serviceName, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据指标名称查找指定时间范围内的指标
     */
    List<MonitorMetrics> findByMetricNameAndCollectTimeBetween(
            String metricName, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计指定服务在指定时间范围内的指标数量
     */
    Long countByServiceNameAndCollectTimeBetween(
            String serviceName, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取指定服务的最新指标值
     */
    @Query("SELECT m FROM MonitorMetrics m WHERE m.serviceName = :serviceName " +
           "AND m.collectTime = (SELECT MAX(m2.collectTime) FROM MonitorMetrics m2 WHERE m2.serviceName = :serviceName AND m2.metricName = m.metricName)")
    List<MonitorMetrics> findLatestMetricsByServiceName(@Param("serviceName") String serviceName);

    /**
     * 获取指定服务的关键性能指标
     */
    @Query("SELECT m FROM MonitorMetrics m WHERE m.serviceName = :serviceName " +
           "AND m.metricName IN ('cpu_usage', 'memory_usage', 'disk_usage', 'active_connections', 'request_count', 'error_count') " +
           "AND m.collectTime >= :startTime ORDER BY m.collectTime DESC")
    List<MonitorMetrics> findKeyMetricsByServiceNameAndTimeRange(
            @Param("serviceName") String serviceName, 
            @Param("startTime") LocalDateTime startTime);

    /**
     * 获取指定时间范围内的所有服务指标
     */
    Page<MonitorMetrics> findByCollectTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * 删除过期指标数据
     */
    void deleteByCollectTimeBefore(LocalDateTime expireTime);

    /**
     * 获取指定服务的平均指标值
     */
    @Query("SELECT AVG(m.metricValue) FROM MonitorMetrics m WHERE m.serviceName = :serviceName " +
           "AND m.metricName = :metricName AND m.collectTime BETWEEN :startTime AND :endTime")
    Optional<Double> findAverageMetricValue(
            @Param("serviceName") String serviceName,
            @Param("metricName") String metricName,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 获取指定服务的最大指标值
     */
    @Query("SELECT MAX(m.metricValue) FROM MonitorMetrics m WHERE m.serviceName = :serviceName " +
           "AND m.metricName = :metricName AND m.collectTime BETWEEN :startTime AND :endTime")
    Optional<Double> findMaxMetricValue(
            @Param("serviceName") String serviceName,
            @Param("metricName") String metricName,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 获取指定服务的最小指标值
     */
    @Query("SELECT MIN(m.metricValue) FROM MonitorMetrics m WHERE m.serviceName = :serviceName " +
           "AND m.metricName = :metricName AND m.collectTime BETWEEN :startTime AND :endTime")
    Optional<Double> findMinMetricValue(
            @Param("serviceName") String serviceName,
            @Param("metricName") String metricName,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 获取服务健康状态
     */
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM MonitorMetrics m " +
           "WHERE m.serviceName = :serviceName AND m.metricName = 'health_status' " +
           "AND m.metricValue = 1 AND m.collectTime >= :checkTime")
    Boolean isServiceHealthy(
            @Param("serviceName") String serviceName,
            @Param("checkTime") LocalDateTime checkTime);
}