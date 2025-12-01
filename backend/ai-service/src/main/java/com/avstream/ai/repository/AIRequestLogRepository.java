package com.avstream.ai.repository;

import com.avstream.ai.entity.AIRequestLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI请求日志Repository
 */
public interface AIRequestLogRepository extends JpaRepository<AIRequestLog, Long> {
    
    /**
     * 根据用户ID和请求类型查询日志
     */
    Page<AIRequestLog> findByUserIdAndRequestType(Long userId, String requestType, Pageable pageable);
    
    /**
     * 根据状态查询日志
     */
    Page<AIRequestLog> findByStatus(String status, Pageable pageable);
    
    /**
     * 根据时间范围查询日志
     */
    List<AIRequestLog> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计各请求类型的数量
     */
    @Query("SELECT l.requestType, COUNT(l) FROM AIRequestLog l WHERE l.createdAt >= :startTime GROUP BY l.requestType")
    List<Object[]> countByRequestTypeSince(@Param("startTime") LocalDateTime startTime);
    
    /**
     * 统计成功率
     */
    @Query("SELECT COUNT(l) FROM AIRequestLog l WHERE l.status = 'SUCCESS' AND l.createdAt >= :startTime")
    Long countSuccessSince(@Param("startTime") LocalDateTime startTime);
    
    /**
     * 统计总请求数
     */
    @Query("SELECT COUNT(l) FROM AIRequestLog l WHERE l.createdAt >= :startTime")
    Long countTotalSince(@Param("startTime") LocalDateTime startTime);
}