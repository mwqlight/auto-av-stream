package com.avstream.ai.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 图像生成请求DTO
 */
@Data
public class ImageGenerationRequest {
    
    /**
     * 提示词
     */
    @NotBlank(message = "提示词不能为空")
    private String prompt;
    
    /**
     * 负面提示词
     */
    private String negativePrompt = "";
    
    /**
     * 图像宽度
     */
    @NotNull(message = "图像宽度不能为空")
    @Positive(message = "图像宽度必须为正数")
    private Integer width = 512;
    
    /**
     * 图像高度
     */
    @NotNull(message = "图像高度不能为空")
    @Positive(message = "图像高度必须为正数")
    private Integer height = 512;
    
    /**
     * 生成步数
     */
    @NotNull(message = "生成步数不能为空")
    @Positive(message = "生成步数必须为正数")
    private Integer steps = 20;
    
    /**
     * 引导系数
     */
    @NotNull(message = "引导系数不能为空")
    @Positive(message = "引导系数必须为正数")
    private Double guidanceScale = 7.5;
    
    /**
     * 随机种子
     */
    private Long seed = -1L;
    
    /**
     * 采样器
     */
    private String sampler = "dpm++_2m";
    
    /**
     * 是否启用高分辨率修复
     */
    private Boolean hiresFix = false;
    
    /**
     * 高分辨率放大倍数
     */
    private Double hiresUpscale = 2.0;
    
    /**
     * 高分辨率步数
     */
    private Integer hiresSteps = 10;
}