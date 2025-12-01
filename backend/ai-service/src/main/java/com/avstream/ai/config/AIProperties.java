package com.avstream.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI服务配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AIProperties {
    
    /**
     * Whisper语音识别配置
     */
    private WhisperProperties whisper = new WhisperProperties();
    
    /**
     * Stable Diffusion图像生成配置
     */
    private StableDiffusionProperties stableDiffusion = new StableDiffusionProperties();
    
    /**
     * TTS文本转语音配置
     */
    private TTSProperties tts = new TTSProperties();
    
    /**
     * 其他AI模型配置
     */
    private ModelProperties models = new ModelProperties();
    
    /**
     * 通用配置
     */
    private CacheProperties cache = new CacheProperties();
    
    /**
     * 并发配置
     */
    private ThreadPoolProperties threadPool = new ThreadPoolProperties();
    
    /**
     * Whisper语音识别配置类
     */
    @Data
    public static class WhisperProperties {
        private Boolean enabled = true;
        private String modelPath;
        private String modelUrl;
        private String modelName;
        private String modelSize;
        private String language;
        private Double temperature;
        private Integer bestOf;
        private Integer beamSize;
        private Double patience;
        private Double lengthPenalty;
        private String suppressTokens;
        private String initialPrompt;
        private Boolean conditionOnPreviousText;
        private Double compressionRatioThreshold;
        private Double logprobThreshold;
        private Double noSpeechThreshold;
        private Boolean wordTimestamps;
        private String prependPunctuations;
        private String appendPunctuations;
        
        public boolean isEnabled() {
            return enabled != null ? enabled : true;
        }
        
        public String getModelUrl() {
            return modelUrl != null ? modelUrl : modelPath;
        }
        
        public String getModelName() {
            return modelName != null ? modelName : "whisper";
        }
    }
    
    /**
     * Stable Diffusion图像生成配置类
     */
    @Data
    public static class StableDiffusionProperties {
        private Boolean enabled = true;
        private String modelPath;
        private String modelUrl;
        private String modelName;
        private Integer steps;
        private Double guidanceScale;
        private Integer width;
        private Integer height;
        private Long seed;
        private String sampler;
        private Integer clipSkip;
        private Boolean hiresFix;
        private Double hiresUpscale;
        private Integer hiresSteps;
        private String hiresUpscaler;
        
        public boolean isEnabled() {
            return enabled != null ? enabled : true;
        }
        
        public String getModelUrl() {
            return modelUrl != null ? modelUrl : modelPath;
        }
        
        public String getModelName() {
            return modelName != null ? modelName : "stable_diffusion";
        }
    }
    
    /**
     * TTS文本转语音配置类
     */
    @Data
    public static class TTSProperties {
        private Boolean enabled = true;
        private String modelPath;
        private String modelUrl;
        private String modelName;
        private String language;
        private String speaker;
        private Double speed;
        private Double pitch;
        private Double energy;
        private Integer sampleRate;
        private String format;
        
        public boolean isEnabled() {
            return enabled != null ? enabled : true;
        }
        
        public String getModelUrl() {
            return modelUrl != null ? modelUrl : modelPath;
        }
        
        public String getModelName() {
            return modelName != null ? modelName : "tts";
        }
    }
    
    /**
     * 其他AI模型配置类
     */
    @Data
    public static class ModelProperties {
        private StyleTransferProperties styleTransfer = new StyleTransferProperties();
        private SuperResolutionProperties superResolution = new SuperResolutionProperties();
        private InpaintingProperties inpainting = new InpaintingProperties();
        
        @Data
        public static class StyleTransferProperties {
            private String modelPath;
            private String modelName;
        }
        
        @Data
        public static class SuperResolutionProperties {
            private String modelPath;
            private String modelName;
            private Integer scale;
        }
        
        @Data
        public static class InpaintingProperties {
            private String modelPath;
            private String modelName;
        }
    }
    
    /**
     * 缓存配置类
     */
    @Data
    public static class CacheProperties {
        private Boolean enabled;
        private Integer ttl;
    }
    
    /**
     * 线程池配置类
     */
    @Data
    public static class ThreadPoolProperties {
        private Integer coreSize;
        private Integer maxSize;
        private Integer queueCapacity;
        private Integer keepAliveSeconds;
    }
}