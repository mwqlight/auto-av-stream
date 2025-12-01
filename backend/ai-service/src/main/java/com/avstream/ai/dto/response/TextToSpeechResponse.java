package com.avstream.ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文本转语音响应DTO
 * 
 * @author AV Stream Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextToSpeechResponse {
    
    private String taskId;
    
    private String audioUrl;
    
    private Integer duration;
    
    private Integer sampleRate;
    
    private String format;
    
    private Long fileSize;
    
    private String status;
    
    private String errorMessage;
}