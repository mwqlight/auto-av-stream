package com.avstream.monitor.repository;

import com.avstream.monitor.entity.AlertRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 告警记录Repository
 */
@Repository
public interface AlertRecordRepository extends JpaRepository<AlertRecord, Long> {
    
    /**
     * 根据状态查询告警记录
     */
    Page<AlertRecord> findByStatus(String status, Pageable pageable);
    
    /**
     * 根据服务名称查询告警记录
     */
    Page<AlertRecord> findByServiceName(String serviceName, Pageable pageable);
    
    /**
     * 根据严重程度查询告警记录
     */
    Page<AlertRecord> findBySeverity(String severity, Pageable pageable);
    
    /**
     * 查询未解决的告警记录
     */
    List<AlertRecord> findByStatusNot(String status);
    
    /**
     * 根据时间范围查询告警记录
     */
    List<AlertRecord> findByTriggeredAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计未解决的告警数量
     */
    @Query("SELECT COUNT(ar) FROM AlertRecord ar WHERE ar.status != 'RESOLVED'")
    Long countUnresolvedAlerts();
    
    /**
     * 根据规则ID查询未解决的告警
     */
    @Query("SELECT ar FROM AlertRecord ar WHERE ar.ruleId = :ruleId AND ar.status != 'RESOLVED'")
    List<AlertRecord> findUnresolvedByRuleId(@Param("ruleId") Long ruleId);
    
    /**
     * 删除过期告警记录
     */
    void deleteByTriggeredAtBefore(LocalDateTime expireTime);

    /**
     * 根据告警规则ID和状态查询告警记录
     */
    List<AlertRecord> findByRuleIdAndStatus(Long ruleId, String status);

    /**
     * 根据状态查询告警记录
     */
    List<AlertRecord> findByStatus(String status);

    /**
     * 检查是否存在指定告警规则和状态的告警记录
     */
    boolean existsByRuleIdAndStatus(Long ruleId, String status);

    /**
     * 检查是否存在指定告警规则和多个状态的告警记录
     */
    boolean existsByRuleIdAndStatusIn(Long ruleId, List<String> statuses);

    /**
     * 根据状态统计告警记录数量
     */
    long countByStatus(String status);
}