package com.avstream.ai.constant;

/**
 * AI服务常量定义
 */
public class AIConstants {
    
    private AIConstants() {
        // 常量类，禁止实例化
    }
    
    /**
     * 请求类型常量
     */
    public static final String REQUEST_TYPE_SPEECH_RECOGNITION = "SPEECH_RECOGNITION";
    public static final String REQUEST_TYPE_IMAGE_GENERATION = "IMAGE_GENERATION";
    public static final String REQUEST_TYPE_TEXT_TO_SPEECH = "TEXT_TO_SPEECH";
    public static final String REQUEST_TYPE_IMAGE_STYLE_TRANSFER = "IMAGE_STYLE_TRANSFER";
    public static final String REQUEST_TYPE_SUPER_RESOLUTION = "SUPER_RESOLUTION";
    public static final String REQUEST_TYPE_IMAGE_INPAINTING = "IMAGE_INPAINTING";
    
    /**
     * 状态常量
     */
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
    
    /**
     * 文件路径常量
     */
    public static final String UPLOAD_DIR = "uploads";
    public static final String AUDIO_UPLOAD_DIR = "uploads/audio";
    public static final String IMAGE_UPLOAD_DIR = "uploads/images";
    public static final String OUTPUT_DIR = "outputs";
    
    /**
     * 模型名称常量
     */
    public static final String MODEL_WHISPER = "openai/whisper-base";
    public static final String MODEL_STABLE_DIFFUSION = "runwayml/stable-diffusion-v1-5";
    public static final String MODEL_TACOTRON2 = "tacotron2";
    
    /**
     * 默认配置常量
     */
    public static final int DEFAULT_IMAGE_WIDTH = 512;
    public static final int DEFAULT_IMAGE_HEIGHT = 512;
    public static final int DEFAULT_GUIDANCE_SCALE = 7;
    public static final int DEFAULT_NUM_INFERENCE_STEPS = 20;
    public static final int DEFAULT_SAMPLE_RATE = 22050;
    
    /**
     * 错误码常量
     */
    public static final String ERROR_CODE_FILE_UPLOAD_FAILED = "AI_1001";
    public static final String ERROR_CODE_FILE_TYPE_INVALID = "AI_1002";
    public static final String ERROR_CODE_MODEL_LOAD_FAILED = "AI_1003";
    public static final String ERROR_CODE_INFERENCE_FAILED = "AI_1004";
    public static final String ERROR_CODE_SERVICE_UNAVAILABLE = "AI_1005";
    public static final String ERROR_CODE_PARAMETER_INVALID = "AI_1006";
    
    /**
     * 缓存键常量
     */
    public static final String CACHE_KEY_MODEL_STATUS = "ai:model:status";
    public static final String CACHE_KEY_REQUEST_STATS = "ai:request:stats";
    public static final String CACHE_KEY_MODEL_LIST = "ai:model:list";
}