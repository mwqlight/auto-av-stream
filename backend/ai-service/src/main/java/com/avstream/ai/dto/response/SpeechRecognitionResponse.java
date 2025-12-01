package com.avstream.ai.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 语音识别响应DTO
 */
@Data
public class SpeechRecognitionResponse {
    
    /**
     * 识别的文本
     */
    private String text;
    
    /**
     * 语言代码
     */
    private String language;
    
    /**
     * 置信度
     */
    private Double confidence;
    
    /**
     * 处理时间（毫秒）
     */
    private Long processingTime;
    
    /**
     * 时间戳信息（如果有）
     */
    private List<WordTimestamp> timestamps;
    
    /**
     * 单词时间戳
     */
    @Data
    public static class WordTimestamp {
        private String word;
        private Double start;
        private Double end;
        private Double confidence;
    }
}