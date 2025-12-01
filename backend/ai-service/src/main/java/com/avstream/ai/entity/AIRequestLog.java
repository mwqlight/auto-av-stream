package com.avstream.ai.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * AI请求日志实体
 */
@Data
@Entity
@Table(name = "ai_request_log")
public class AIRequestLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 请求类型
     */
    @Column(nullable = false, length = 50)
    private String requestType;
    
    /**
     * 用户ID
     */
    @Column(nullable = false)
    private Long userId;
    
    /**
     * 请求参数
     */
    @Column(columnDefinition = "TEXT")
    private String requestParams;
    
    /**
     * 响应结果
     */
    @Column(columnDefinition = "TEXT")
    private String responseResult;
    
    /**
     * 处理时间（毫秒）
     */
    @Column
    private Long processingTime;
    
    /**
     * 状态：SUCCESS/FAILED
     */
    @Column(nullable = false, length = 20)
    private String status;
    
    /**
     * 错误信息
     */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}