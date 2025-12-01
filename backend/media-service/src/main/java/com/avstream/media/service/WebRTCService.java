package com.avstream.media.service;

import com.avstream.media.config.WebRTCProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * WebRTC流媒体服务
 */
@Service
@RequiredArgsConstructor
public class WebRTCService {
    
    private static final Logger log = LoggerFactory.getLogger(WebRTCService.class);

    private final WebRTCProperties webRTCProperties;

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, StreamSession> streamSessions = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    /**
     * 注册WebSocket会话
     */
    public void registerSession(String sessionId, WebSocketSession session) {
        sessions.put(sessionId, session);
        log.info("WebSocket会话注册成功: {}", sessionId);
    }

    /**
     * 移除WebSocket会话
     */
    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
        StreamSession streamSession = streamSessions.remove(sessionId);
        if (streamSession != null) {
            streamSession.close();
        }
        log.info("WebSocket会话移除: {}", sessionId);
    }

    /**
     * 处理WebRTC信令消息
     */
    public void handleSignalingMessage(String sessionId, String message) {
        try {
            WebSocketSession session = sessions.get(sessionId);
            if (session == null) {
                log.warn("会话不存在: {}", sessionId);
                return;
            }

            // 解析信令消息
            SignalingMessage signalingMessage = parseSignalingMessage(message);
            
            switch (signalingMessage.getType()) {
                case "offer":
                    handleOffer(sessionId, signalingMessage);
                    break;
                case "answer":
                    handleAnswer(sessionId, signalingMessage);
                    break;
                case "ice-candidate":
                    handleIceCandidate(sessionId, signalingMessage);
                    break;
                case "start-stream":
                    handleStartStream(sessionId, signalingMessage);
                    break;
                case "stop-stream":
                    handleStopStream(sessionId);
                    break;
                default:
                    log.warn("未知的信令消息类型: {}", signalingMessage.getType());
            }
            
        } catch (Exception e) {
            log.error("处理信令消息失败: {}", e.getMessage(), e);
            sendError(sessionId, "处理信令消息失败: " + e.getMessage());
        }
    }

    /**
     * 处理Offer消息
     */
    private void handleOffer(String sessionId, SignalingMessage message) {
        StreamSession streamSession = streamSessions.computeIfAbsent(sessionId, 
            id -> new StreamSession(id, this));
        
        streamSession.handleOffer(message.getSdp());
        
        // 模拟生成Answer
        String answerSdp = generateAnswerSdp();
        sendSignalingMessage(sessionId, new SignalingMessage("answer", answerSdp, null));
        
        log.info("处理Offer消息完成: {}", sessionId);
    }

    /**
     * 处理Answer消息
     */
    private void handleAnswer(String sessionId, SignalingMessage message) {
        StreamSession streamSession = streamSessions.get(sessionId);
        if (streamSession != null) {
            streamSession.handleAnswer(message.getSdp());
            log.info("处理Answer消息完成: {}", sessionId);
        }
    }

    /**
     * 处理ICE候选
     */
    private void handleIceCandidate(String sessionId, SignalingMessage message) {
        StreamSession streamSession = streamSessions.get(sessionId);
        if (streamSession != null) {
            streamSession.handleIceCandidate(message.getCandidate());
            log.info("处理ICE候选完成: {}", sessionId);
        }
    }

    /**
     * 开始流媒体传输
     */
    private void handleStartStream(String sessionId, SignalingMessage message) {
        StreamSession streamSession = streamSessions.get(sessionId);
        if (streamSession != null) {
            streamSession.startStreaming();
            log.info("开始流媒体传输: {}", sessionId);
            
            // 发送流媒体状态更新
            sendStreamStatus(sessionId, "streaming", "流媒体传输已开始");
        }
    }

    /**
     * 停止流媒体传输
     */
    private void handleStopStream(String sessionId) {
        StreamSession streamSession = streamSessions.get(sessionId);
        if (streamSession != null) {
            streamSession.stopStreaming();
            log.info("停止流媒体传输: {}", sessionId);
            
            // 发送流媒体状态更新
            sendStreamStatus(sessionId, "stopped", "流媒体传输已停止");
        }
    }

    /**
     * 发送信令消息
     */
    public void sendSignalingMessage(String sessionId, SignalingMessage message) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                String jsonMessage = message.toJson();
                session.sendMessage(new TextMessage(jsonMessage));
                log.debug("发送信令消息到会话: {}", sessionId);
            } catch (IOException e) {
                log.error("发送信令消息失败: {}", e.getMessage(), e);
                removeSession(sessionId);
            }
        }
    }

    /**
     * 发送错误消息
     */
    private void sendError(String sessionId, String errorMessage) {
        SignalingMessage errorMsg = new SignalingMessage("error", null, null);
        errorMsg.setErrorMessage(errorMessage);
        sendSignalingMessage(sessionId, errorMsg);
    }

    /**
     * 发送流媒体状态
     */
    private void sendStreamStatus(String sessionId, String status, String message) {
        SignalingMessage statusMsg = new SignalingMessage("stream-status", null, null);
        statusMsg.setStatus(status);
        statusMsg.setStatusMessage(message);
        sendSignalingMessage(sessionId, statusMsg);
    }

    /**
     * 生成Answer SDP（简化版）
     */
    private String generateAnswerSdp() {
        return "v=0\n" +
               "o=- 123456789 2 IN IP4 127.0.0.1\n" +
               "s=-\n" +
               "t=0 0\n" +
               "a=group:BUNDLE 0 1\n" +
               "m=video 9 UDP/TLS/RTP/SAVPF 96 97 98\n" +
               "c=IN IP4 0.0.0.0\n" +
               "a=rtcp:9 IN IP4 0.0.0.0\n" +
               "a=ice-ufrag:abc123\n" +
               "a=ice-pwd:def456\n" +
               "a=fingerprint:sha-256 00:11:22:33:44:55:66:77:88:99:AA:BB:CC:DD:EE:FF\n" +
               "a=setup:active\n" +
               "a=mid:0\n" +
               "a=sendrecv\n" +
               "a=rtpmap:96 VP8/90000\n" +
               "a=rtpmap:97 H264/90000\n" +
               "a=rtpmap:98 VP9/90000\n";
    }

    /**
     * 解析信令消息
     */
    private SignalingMessage parseSignalingMessage(String message) {
        // 简化版JSON解析
        try {
            String type = extractJsonValue(message, "type");
            String sdp = extractJsonValue(message, "sdp");
            String candidate = extractJsonValue(message, "candidate");
            
            return new SignalingMessage(type, sdp, candidate);
        } catch (Exception e) {
            throw new RuntimeException("解析信令消息失败", e);
        }
    }

    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return null;
        
        startIndex += searchKey.length();
        int endIndex = json.indexOf(",", startIndex);
        if (endIndex == -1) endIndex = json.indexOf("}", startIndex);
        
        if (endIndex == -1) return null;
        
        String value = json.substring(startIndex, endIndex).trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        
        return value;
    }

    /**
     * 流媒体会话类
     */
    private static class StreamSession {
        private final String sessionId;
        private final WebRTCService webRTCService;
        private volatile boolean streaming = false;
        
        public StreamSession(String sessionId, WebRTCService webRTCService) {
            this.sessionId = sessionId;
            this.webRTCService = webRTCService;
        }
        
        public void handleOffer(String sdp) {
            log.info("处理Offer SDP: {}", sessionId);
            // 实际实现中会解析SDP并设置媒体配置
        }
        
        public void handleAnswer(String sdp) {
            log.info("处理Answer SDP: {}", sessionId);
            // 实际实现中会解析SDP并建立连接
        }
        
        public void handleIceCandidate(String candidate) {
            log.info("处理ICE候选: {}", candidate);
            // 实际实现中会添加ICE候选
        }
        
        public void startStreaming() {
            if (!streaming) {
                streaming = true;
                log.info("开始流媒体传输: {}", sessionId);
                
                // 模拟流媒体传输
                webRTCService.scheduler.scheduleAtFixedRate(() -> {
                    if (streaming) {
                        // 发送流媒体数据包
                        sendMediaPacket();
                    }
                }, 0, 33, TimeUnit.MILLISECONDS); // 30fps
            }
        }
        
        public void stopStreaming() {
            streaming = false;
            log.info("停止流媒体传输: {}", sessionId);
        }
        
        public void close() {
            stopStreaming();
        }
        
        private void sendMediaPacket() {
            // 模拟发送媒体数据包
            // 实际实现中会编码和发送音视频数据
            SignalingMessage packet = new SignalingMessage("media-packet", null, null);
            packet.setTimestamp(System.currentTimeMillis());
            webRTCService.sendSignalingMessage(sessionId, packet);
        }
    }

    /**
     * 信令消息类
     */
    public static class SignalingMessage {
        private String type;
        private String sdp;
        private String candidate;
        private String errorMessage;
        private String status;
        private String statusMessage;
        private Long timestamp;
        
        public SignalingMessage(String type, String sdp, String candidate) {
            this.type = type;
            this.sdp = sdp;
            this.candidate = candidate;
        }
        
        public String toJson() {
            StringBuilder json = new StringBuilder();
            json.append("{");
            
            if (type != null) {
                json.append("\"type\":\"").append(type).append("\"");
            }
            
            if (sdp != null) {
                if (json.length() > 1) json.append(",");
                json.append("\"sdp\":\"").append(sdp.replace("\"", "\\\"")).append("\"");
            }
            
            if (candidate != null) {
                if (json.length() > 1) json.append(",");
                json.append("\"candidate\":\"").append(candidate).append("\"");
            }
            
            if (errorMessage != null) {
                if (json.length() > 1) json.append(",");
                json.append("\"error\":\"").append(errorMessage).append("\"");
            }
            
            if (status != null) {
                if (json.length() > 1) json.append(",");
                json.append("\"status\":\"").append(status).append("\"");
            }
            
            if (statusMessage != null) {
                if (json.length() > 1) json.append(",");
                json.append("\"statusMessage\":\"").append(statusMessage).append("\"");
            }
            
            if (timestamp != null) {
                if (json.length() > 1) json.append(",");
                json.append("\"timestamp\":").append(timestamp);
            }
            
            json.append("}");
            return json.toString();
        }
        
        // getter和setter方法
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getSdp() { return sdp; }
        public void setSdp(String sdp) { this.sdp = sdp; }
        
        public String getCandidate() { return candidate; }
        public void setCandidate(String candidate) { this.candidate = candidate; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getStatusMessage() { return statusMessage; }
        public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
        
        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    }

    @PreDestroy
    public void cleanup() {
        log.info("清理WebRTC服务资源");
        
        // 关闭所有会话
        sessions.keySet().forEach(this::removeSession);
        
        // 关闭调度器
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}