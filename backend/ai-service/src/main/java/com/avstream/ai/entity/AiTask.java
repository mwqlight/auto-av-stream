package com.avstream.ai.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * AI任务实体类
 * 
 * @author AV Stream Team
 */
@Entity
@Table(name = "ai_tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiTask {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String taskId;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskType taskType;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    
    @Column(columnDefinition = "TEXT")
    private String inputData;
    
    @Column(columnDefinition = "TEXT")
    private String outputData;
    
    @Column
    private String modelName;
    
    @Column
    private String parameters;
    
    @Column
    private Double progress;
    
    @Column
    private String errorMessage;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime startedAt;
    
    @Column
    private LocalDateTime completedAt;
    
    @Column
    private Integer estimatedDuration;
    
    @Column
    private Integer actualDuration;
    
    @Column
    private Long inputFileSize;
    
    @Column
    private Long outputFileSize;
    
    @Column
    private String inputFileUrl;
    
    @Column
    private String outputFileUrl;
    
    @Column
    private String callbackUrl;
    
    @Column
    private Integer priority;
    
    /**
     * AI任务类型枚举
     */
    public enum TaskType {
        SPEECH_TO_TEXT,    // 语音转文本
        TEXT_TO_SPEECH,    // 文本转语音
        IMAGE_GENERATION,  // 图像生成
        STYLE_TRANSFER,    // 风格转换
        SUPER_RESOLUTION,  // 超分辨率
        INPAINTING,        // 图像修复
        VIDEO_ANALYSIS,    // 视频分析
        AUDIO_ENHANCEMENT, // 音频增强
        TEXT_SUMMARY,      // 文本摘要
        TRANSLATION        // 翻译
    }
    
    /**
     * AI任务状态枚举
     */
    public enum TaskStatus {
        PENDING,      // 等待中
        PROCESSING,   // 处理中
        COMPLETED,    // 已完成
        FAILED,       // 失败
        CANCELLED     // 已取消
    }
}