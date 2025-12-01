package com.avstream.monitor.repository;

import com.avstream.monitor.entity.MonitorMetric;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 监控指标Repository
 */
@Repository
public interface MonitorMetricRepository extends JpaRepository<MonitorMetric, Long> {
    
    /**
     * 根据指标名称和服务名称查询指标
     */
    Page<MonitorMetric> findByMetricNameAndServiceName(String metricName, String serviceName, Pageable pageable);
    
    /**
     * 根据服务名称查询指标
     */
    Page<MonitorMetric> findByServiceName(String serviceName, Pageable pageable);
    
    /**
     * 根据时间范围查询指标
     */
    List<MonitorMetric> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据指标名称、服务名称和时间范围查询指标
     */
    List<MonitorMetric> findByMetricNameAndServiceNameAndTimestampBetween(String metricName, String serviceName, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查询最新的指标数据
     */
    @Query("SELECT m FROM MonitorMetric m WHERE m.metricName = :metricName AND m.serviceName = :serviceName ORDER BY m.timestamp DESC")
    List<MonitorMetric> findLatestMetrics(@Param("metricName") String metricName, 
                                         @Param("serviceName") String serviceName, 
                                         Pageable pageable);
    
    /**
     * 统计指标数量
     */
    @Query("SELECT COUNT(m) FROM MonitorMetric m WHERE m.serviceName = :serviceName AND m.timestamp >= :startTime")
    Long countByServiceNameAndTimeRange(@Param("serviceName") String serviceName, 
                                       @Param("startTime") LocalDateTime startTime);
    
    /**
     * 删除过期数据
     */
    void deleteByTimestampBefore(LocalDateTime expireTime);
}