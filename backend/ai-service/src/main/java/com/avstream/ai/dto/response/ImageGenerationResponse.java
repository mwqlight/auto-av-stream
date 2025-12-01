package com.avstream.ai.dto.response;

import lombok.Data;

/**
 * 图像生成响应DTO
 */
@Data
public class ImageGenerationResponse {
    
    /**
     * 生成的图像URL或Base64编码
     */
    private String imageUrl;
    
    /**
     * 图像宽度
     */
    private Integer width;
    
    /**
     * 图像高度
     */
    private Integer height;
    
    /**
     * 使用的随机种子
     */
    private Long seed;
    
    /**
     * 生成步数
     */
    private Integer steps;
    
    /**
     * 引导系数
     */
    private Double guidanceScale;
    
    /**
     * 处理时间（毫秒）
     */
    private Long processingTime;
    
    /**
     * 图像格式
     */
    private String format = "png";
}