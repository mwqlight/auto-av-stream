package com.avstream.ai.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 语音转文本请求DTO
 * 
 * @author AV Stream Team
 */
@Data
public class SpeechToTextRequest {
    
    @NotBlank(message = "音频文件URL不能为空")
    private String audioUrl;
    
    private String language;
    
    private String model;
    
    private Boolean wordTimestamps;
    
    private Double temperature;
    
    private String prompt;
    
    private Boolean translate;
    
    private Integer beamSize;
    
    private Double bestOf;
    
    private Double patience;
    
    private Double lengthPenalty;
    
    private Double compressionRatioThreshold;
    
    private Double logprobThreshold;
    
    private Double noSpeechThreshold;
}