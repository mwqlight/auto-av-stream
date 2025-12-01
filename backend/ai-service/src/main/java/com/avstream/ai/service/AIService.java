package com.avstream.ai.service;

import com.avstream.ai.dto.request.ImageGenerationRequest;
import com.avstream.ai.dto.request.SpeechRecognitionRequest;
import com.avstream.ai.dto.request.TextToSpeechRequest;
import com.avstream.ai.dto.response.ImageGenerationResponse;
import com.avstream.ai.dto.response.SpeechRecognitionResponse;

import java.util.Map;

/**
 * AI服务接口
 */
public interface AIService {

    /**
     * 语音识别
     */
    SpeechRecognitionResponse speechRecognition(SpeechRecognitionRequest request);

    /**
     * 图像生成
     */
    ImageGenerationResponse imageGeneration(ImageGenerationRequest request);

    /**
     * 文本转语音
     */
    byte[] textToSpeech(TextToSpeechRequest request);

    /**
     * 图像风格转换
     */
    ImageGenerationResponse styleTransfer(String style, String contentImageUrl);

    /**
     * 图像超分辨率
     */
    ImageGenerationResponse superResolution(String imageUrl, Integer scale);

    /**
     * 图像修复
     */
    ImageGenerationResponse inpainting(String imageUrl, String maskUrl);

    /**
     * 获取AI服务状态
     */
    Map<String, Object> getServiceStatus();

    /**
     * 获取支持的模型列表
     */
    Map<String, Object> getSupportedModels();
}