package com.avstream.ai.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 文本转语音请求DTO
 */
@Data
public class TextToSpeechRequest {
    
    /**
     * 要转换的文本
     */
    @NotBlank(message = "文本内容不能为空")
    private String text;
    
    /**
     * 语言代码
     */
    @NotBlank(message = "语言代码不能为空")
    private String language = "zh";
    
    /**
     * 说话人标识
     */
    private String speaker = "default";
    
    /**
     * 语速（0.5-2.0）
     */
    @NotNull(message = "语速不能为空")
    @Positive(message = "语速必须为正数")
    private Double speed = 1.0;
    
    /**
     * 音调（-1.0到1.0）
     */
    private Double pitch = 0.0;
    
    /**
     * 能量（0.0到2.0）
     */
    private Double energy = 0.0;
    
    /**
     * 采样率
     */
    @NotNull(message = "采样率不能为空")
    @Positive(message = "采样率必须为正数")
    private Integer sampleRate = 22050;
    
    /**
     * 输出格式
     */
    private String format = "wav";
}