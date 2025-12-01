package com.avstream.live.service.impl;

import com.avstream.live.config.MediaMtxProperties;
import com.avstream.live.dto.MediaMtxConfigDTO;
import com.avstream.live.dto.MediaMtxStreamDTO;
import com.avstream.live.service.MediaMtxService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * MediaMTX流媒体服务器服务实现
 */
@Slf4j
@Service
public class MediaMtxServiceImpl implements MediaMtxService {
    
    private final RestTemplate restTemplate;
    private final MediaMtxProperties mediaMtxProperties;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public MediaMtxServiceImpl(RestTemplate restTemplate, MediaMtxProperties mediaMtxProperties, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.mediaMtxProperties = mediaMtxProperties;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public List<MediaMtxStreamDTO> getAllStreams() {
        try {
            String url = mediaMtxProperties.getApiUrl() + "/v3/config/global/get";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode paths = root.path("paths");
                
                List<MediaMtxStreamDTO> streams = new ArrayList<>();
                
                if (paths.isObject()) {
                    Iterator<Map.Entry<String, JsonNode>> fields = paths.fields();
                    while (fields.hasNext()) {
                        Map.Entry<String, JsonNode> entry = fields.next();
                        String streamKey = entry.getKey();
                        
                        // 获取流详细信息
                        MediaMtxStreamDTO streamInfo = getStream(streamKey);
                        if (streamInfo != null) {
                            streams.add(streamInfo);
                        }
                    }
                }
                
                return streams;
            }
        } catch (Exception e) {
            log.error("获取流列表失败: {}", e.getMessage());
        }
        
        return Collections.emptyList();
    }
    
    @Override
    public MediaMtxStreamDTO getStream(String streamKey) {
        try {
            String url = mediaMtxProperties.getApiUrl() + "/v3/paths/get/" + streamKey;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                
                MediaMtxStreamDTO stream = new MediaMtxStreamDTO();
                stream.setName(streamKey);
                stream.setState(root.path("state").asText("unknown"));
                stream.setReadersCount(root.path("readers").size());
                stream.setSourceType(root.path("source").path("type").asText(""));
                stream.setSourceAddress(root.path("source").path("address").asText(""));
                stream.setCreationTime(root.path("created").asText(""));
                stream.setIsRecording(root.path("recording").asBoolean(false));
                stream.setRecordingPath(root.path("recordingPath").asText(""));
                stream.setRecordingDuration(root.path("recordingDuration").asLong(0L));
                
                return stream;
            }
        } catch (Exception e) {
            log.error("获取流信息失败 - {}: {}", streamKey, e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public boolean streamExists(String streamKey) {
        try {
            String url = mediaMtxProperties.getApiUrl() + "/v3/paths/get/" + streamKey;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isStreamActive(String streamKey) {
        MediaMtxStreamDTO stream = getStream(streamKey);
        return stream != null && "running".equals(stream.getState());
    }
    
    @Override
    public String getStreamState(String streamKey) {
        MediaMtxStreamDTO stream = getStream(streamKey);
        return stream != null ? stream.getState() : "not_found";
    }
    
    @Override
    public Integer getViewerCount(String streamKey) {
        MediaMtxStreamDTO stream = getStream(streamKey);
        return stream != null ? stream.getReadersCount() : 0;
    }
    
    @Override
    public boolean startRecording(String streamKey) {
        try {
            String url = mediaMtxProperties.getApiUrl() + "/v3/paths/edit/" + streamKey;
            
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> recordConfig = new HashMap<>();
            recordConfig.put("record", true);
            requestBody.put("recordConfig", recordConfig);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.error("开始录制失败 - {}: {}", streamKey, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean stopRecording(String streamKey) {
        try {
            String url = mediaMtxProperties.getApiUrl() + "/v3/paths/edit/" + streamKey;
            
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> recordConfig = new HashMap<>();
            recordConfig.put("record", false);
            requestBody.put("recordConfig", recordConfig);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.error("停止录制失败 - {}: {}", streamKey, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean isRecording(String streamKey) {
        MediaMtxStreamDTO stream = getStream(streamKey);
        return stream != null && Boolean.TRUE.equals(stream.getIsRecording());
    }
    
    @Override
    public Long getRecordingDuration(String streamKey) {
        MediaMtxStreamDTO stream = getStream(streamKey);
        return stream != null ? stream.getRecordingDuration() : 0L;
    }
    
    @Override
    public MediaMtxConfigDTO getServerConfig() {
        MediaMtxConfigDTO config = new MediaMtxConfigDTO();
        config.setApiUrl(mediaMtxProperties.getApiUrl());
        config.setRtmpPort(mediaMtxProperties.getRtmpPort());
        config.setHlsPort(mediaMtxProperties.getHlsPort());
        config.setWebrtcPort(mediaMtxProperties.getWebrtcPort());
        config.setRecordPath("/tmp/records"); // 默认录制路径
        config.setRecordEnabled(true);
        config.setMaxRecordDuration(120);
        
        return config;
    }
    
    @Override
    public Map<String, Object> getServerStatus() {
        try {
            String url = mediaMtxProperties.getApiUrl() + "/v3/status";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                
                Map<String, Object> status = new HashMap<>();
                status.put("version", root.path("rtspServer").path("version").asText(""));
                status.put("uptime", root.path("rtspServer").path("uptime").asLong(0L));
                status.put("pathCount", root.path("paths").size());
                status.put("readerCount", root.path("readers").size());
                
                return status;
            }
        } catch (Exception e) {
            log.error("获取服务器状态失败: {}", e.getMessage());
        }
        
        return Collections.emptyMap();
    }
    
    @Override
    public boolean isServerConnected() {
        try {
            String url = mediaMtxProperties.getApiUrl() + "/v3/status";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }
}