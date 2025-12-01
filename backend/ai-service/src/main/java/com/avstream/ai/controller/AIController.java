package com.avstream.ai.controller;

import com.avstream.ai.dto.request.ImageGenerationRequest;
import com.avstream.ai.dto.request.SpeechRecognitionRequest;
import com.avstream.ai.dto.request.TextToSpeechRequest;
import com.avstream.ai.dto.response.ImageGenerationResponse;
import com.avstream.ai.dto.response.SpeechRecognitionResponse;
import com.avstream.ai.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * AI服务控制器
 * 提供语音识别、图像生成、文本转语音等AI能力
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Validated
@Tag(name = "AI服务", description = "提供各种AI能力服务")
public class AIController {

    private final AIService aiService;

    /**
     * 语音识别
     */
    @PostMapping(value = "/speech-recognition", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "语音识别", description = "将音频文件转换为文本")
    public ResponseEntity<SpeechRecognitionResponse> speechRecognition(
            @Valid @ModelAttribute SpeechRecognitionRequest request) {
        
        log.info("语音识别请求: language={}, withTimestamps={}", 
                request.getLanguage(), request.getWithTimestamps());
        
        SpeechRecognitionResponse response = aiService.speechRecognition(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 图像生成
     */
    @PostMapping("/image-generation")
    public ResponseEntity<ImageGenerationResponse> imageGeneration(
            @Valid @RequestBody ImageGenerationRequest request) {
        
        log.info("图像生成请求: prompt={}, width={}, height={}", 
                request.getPrompt(), request.getWidth(), request.getHeight());
        
        ImageGenerationResponse response = aiService.imageGeneration(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 文本转语音
     */
    @PostMapping("/text-to-speech")
    public ResponseEntity<byte[]> textToSpeech(
            @Valid @RequestBody TextToSpeechRequest request) {
        
        log.info("文本转语音请求: text={}, language={}, speaker={}", 
                request.getText(), request.getLanguage(), request.getSpeaker());
        
        byte[] audioData = aiService.textToSpeech(request);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "speech." + request.getFormat());
        headers.setContentLength(audioData.length);
        
        return new ResponseEntity<>(audioData, headers, HttpStatus.OK);
    }

    /**
     * 图像风格转换
     */
    @PostMapping("/style-transfer")
    public ResponseEntity<ImageGenerationResponse> styleTransfer(
            @RequestParam String style,
            @RequestParam String contentImageUrl) {
        
        log.info("图像风格转换请求: style={}", style);
        
        ImageGenerationResponse response = aiService.styleTransfer(style, contentImageUrl);
        return ResponseEntity.ok(response);
    }

    /**
     * 图像超分辨率
     */
    @PostMapping("/super-resolution")
    public ResponseEntity<ImageGenerationResponse> superResolution(
            @RequestParam String imageUrl,
            @RequestParam(defaultValue = "4") Integer scale) {
        
        log.info("图像超分辨率请求: scale={}", scale);
        
        ImageGenerationResponse response = aiService.superResolution(imageUrl, scale);
        return ResponseEntity.ok(response);
    }

    /**
     * 图像修复
     */
    @PostMapping("/inpainting")
    public ResponseEntity<ImageGenerationResponse> inpainting(
            @RequestParam String imageUrl,
            @RequestParam String maskUrl) {
        
        log.info("图像修复请求");
        
        ImageGenerationResponse response = aiService.inpainting(imageUrl, maskUrl);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取AI服务状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getServiceStatus() {
        
        log.info("获取AI服务状态");
        
        Map<String, Object> status = aiService.getServiceStatus();
        return ResponseEntity.ok(status);
    }

    /**
     * 获取支持的模型列表
     */
    @GetMapping("/models")
    public ResponseEntity<Map<String, Object>> getSupportedModels() {
        
        log.info("获取支持的模型列表");
        
        Map<String, Object> models = aiService.getSupportedModels();
        return ResponseEntity.ok(models);
    }
}