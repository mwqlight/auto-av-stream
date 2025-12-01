package com.avstream.ai.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 语音识别请求DTO
 */
@Data
public class SpeechRecognitionRequest {
    
    /**
     * 音频文件
     */
    @NotNull(message = "音频文件不能为空")
    private MultipartFile audioFile;
    
    /**
     * 语言代码（可选，默认自动检测）
     */
    private String language = "auto";
    
    /**
     * 是否输出时间戳
     */
    private Boolean withTimestamps = false;
    
    /**
     * 是否输出单词级别的时间戳
     */
    private Boolean wordTimestamps = false;
    
    /**
     * 初始提示文本
     */
    private String initialPrompt = "";
    
    /**
     * 温度参数
     */
    private Double temperature = 0.0;
}