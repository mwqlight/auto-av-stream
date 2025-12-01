package com.avstream.monitor.repository;

import com.avstream.monitor.entity.AlertRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 告警规则Repository
 */
@Repository
public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {
    
    /**
     * 根据规则名称查询
     */
    Page<AlertRule> findByRuleNameContaining(String ruleName, Pageable pageable);
    
    /**
     * 根据指标名称查询
     */
    List<AlertRule> findByMetricName(String metricName);
    
    /**
     * 查询启用的告警规则
     */
    List<AlertRule> findByEnabledTrue();
    
    /**
     * 根据严重程度查询
     */
    List<AlertRule> findBySeverity(String severity);
    
    /**
     * 检查规则名称是否存在
     */
    boolean existsByRuleName(String ruleName);
    
    /**
     * 根据指标名称和严重程度查询
     */
    @Query("SELECT ar FROM AlertRule ar WHERE ar.metricName = :metricName AND ar.severity = :severity AND ar.enabled = true")
    List<AlertRule> findByMetricNameAndSeverity(@Param("metricName") String metricName, 
                                               @Param("severity") String severity);
}