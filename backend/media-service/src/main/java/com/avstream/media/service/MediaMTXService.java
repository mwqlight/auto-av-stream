package com.avstream.media.service;

import com.avstream.media.config.MediaMTXProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * MediaMTX流媒体服务器集成服务
 */
@Service
@RequiredArgsConstructor
public class MediaMTXService {

    private static final Logger log = LoggerFactory.getLogger(MediaMTXService.class);

    private final MediaMTXProperties mediaMTXProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 创建RTMP推流路径
     */
    public String createRTMPStream(String streamId, String sourceUrl) {
        try {
            String path = mediaMTXProperties.getApiPath() + "/paths/add/" + streamId;
            
            Map<String, Object> config = new HashMap<>();
            config.put("source", mediaMTXProperties.getRtmpUrl() + "/" + streamId);
            config.put("sourceOnDemand", true);
            config.put("sourceRedirect", sourceUrl);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(config, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                mediaMTXProperties.getApiUrl() + path, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("RTMP流路径创建成功: {}", streamId);
                return mediaMTXProperties.getRtmpUrl() + "/" + streamId;
            } else {
                log.error("RTMP流路径创建失败: {}", response.getBody());
                throw new RuntimeException("创建RTMP流失败: " + response.getBody());
            }
            
        } catch (Exception e) {
            log.error("创建RTMP流路径异常: {}", e.getMessage(), e);
            throw new RuntimeException("创建RTMP流路径异常", e);
        }
    }

    /**
     * 创建HLS流路径
     */
    public String createHLSStream(String streamId) {
        try {
            String path = mediaMTXProperties.getApiPath() + "/paths/add/" + streamId + "_hls";
            
            Map<String, Object> config = new HashMap<>();
            config.put("source", mediaMTXProperties.getRtmpUrl() + "/" + streamId);
            config.put("sourceOnDemand", true);
            config.put("hls", true);
            config.put("hlsSegmentDuration", "2s");
            config.put("hlsSegmentCount", 5);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(config, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                mediaMTXProperties.getApiUrl() + path, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("HLS流路径创建成功: {}", streamId);
                return mediaMTXProperties.getHlsUrl() + "/" + streamId + "_hls/index.m3u8";
            } else {
                log.error("HLS流路径创建失败: {}", response.getBody());
                throw new RuntimeException("创建HLS流失败: " + response.getBody());
            }
            
        } catch (Exception e) {
            log.error("创建HLS流路径异常: {}", e.getMessage(), e);
            throw new RuntimeException("创建HLS流路径异常", e);
        }
    }

    /**
     * 创建WebRTC流路径
     */
    public String createWebRTCStream(String streamId) {
        try {
            String path = mediaMTXProperties.getApiPath() + "/paths/add/" + streamId + "_webrtc";
            
            Map<String, Object> config = new HashMap<>();
            config.put("source", mediaMTXProperties.getRtmpUrl() + "/" + streamId);
            config.put("sourceOnDemand", true);
            config.put("webrtc", true);
            config.put("webrtcIceServers", "[{\"urls\":\"stun:stun.l.google.com:19302\"}]");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(config, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                mediaMTXProperties.getApiUrl() + path, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("WebRTC流路径创建成功: {}", streamId);
                return mediaMTXProperties.getWebRTCUrl() + "/" + streamId + "_webrtc/whep";
            } else {
                log.error("WebRTC流路径创建失败: {}", response.getBody());
                throw new RuntimeException("创建WebRTC流失败: " + response.getBody());
            }
            
        } catch (Exception e) {
            log.error("创建WebRTC流路径异常: {}", e.getMessage(), e);
            throw new RuntimeException("创建WebRTC流路径异常", e);
        }
    }

    /**
     * 删除流路径
     */
    public boolean deleteStream(String streamId) {
        try {
            String[] paths = {
                streamId,
                streamId + "_hls",
                streamId + "_webrtc"
            };
            
            boolean allDeleted = true;
            
            for (String path : paths) {
                try {
                    restTemplate.delete(mediaMTXProperties.getApiUrl() + mediaMTXProperties.getApiPath() + "/paths/delete/" + path);
                    log.info("流路径删除成功: {}", path);
                } catch (Exception e) {
                    log.warn("删除流路径失败: {} - {}", path, e.getMessage());
                    allDeleted = false;
                }
            }
            
            return allDeleted;
            
        } catch (Exception e) {
            log.error("删除流路径异常: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取流状态
     */
    public Map<String, Object> getStreamStatus(String streamId) {
        try {
            String path = mediaMTXProperties.getApiPath() + "/paths/get/" + streamId;
            
            ResponseEntity<Map> response = restTemplate.getForEntity(
                mediaMTXProperties.getApiUrl() + path, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                log.warn("获取流状态失败: {}", streamId);
                return null;
            }
            
        } catch (Exception e) {
            log.error("获取流状态异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取所有流路径
     */
    public Map<String, Object> getAllStreams() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                mediaMTXProperties.getApiUrl() + mediaMTXProperties.getApiPath() + "/paths/list", Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                log.error("获取所有流路径失败");
                return null;
            }
            
        } catch (Exception e) {
            log.error("获取所有流路径异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 检查MediaMTX服务状态
     */
    public boolean checkServiceStatus() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                mediaMTXProperties.getApiUrl() + mediaMTXProperties.getApiPath() + "/config/global/get", String.class);
            
            return response.getStatusCode() == HttpStatus.OK;
            
        } catch (Exception e) {
            log.warn("MediaMTX服务不可用: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 创建完整的流媒体配置（RTMP + HLS + WebRTC）
     */
    public Map<String, String> createCompleteStream(String streamId, String sourceUrl) {
        Map<String, String> result = new HashMap<>();
        
        try {
            // 创建RTMP推流路径
            String rtmpUrl = createRTMPStream(streamId, sourceUrl);
            result.put("rtmp", rtmpUrl);
            
            // 创建HLS播放路径
            String hlsUrl = createHLSStream(streamId);
            result.put("hls", hlsUrl);
            
            // 创建WebRTC播放路径
            String webrtcUrl = createWebRTCStream(streamId);
            result.put("webrtc", webrtcUrl);
            
            log.info("完整流媒体配置创建成功: {}", streamId);
            return result;
            
        } catch (Exception e) {
            // 清理已创建的资源
            deleteStream(streamId);
            throw new RuntimeException("创建完整流媒体配置失败", e);
        }
    }
}