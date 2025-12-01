package com.avstream.media.websocket;

import com.avstream.media.service.WebRTCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebRTC WebSocket处理器
 */
@Component
public class WebRTCWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(WebRTCWebSocketHandler.class);

    private final WebRTCService webRTCService;
    private final Map<String, String> sessionIdMap = new ConcurrentHashMap<>();

    public WebRTCWebSocketHandler(WebRTCService webRTCService) {
        this.webRTCService = webRTCService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = generateSessionId(session);
        sessionIdMap.put(session.getId(), sessionId);
        
        webRTCService.registerSession(sessionId, session);
        
        log.info("WebSocket连接建立: {} -> {}", session.getId(), sessionId);
        
        // 发送连接成功消息
        sendConnectionSuccess(session, sessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String sessionId = sessionIdMap.get(session.getId());
        if (sessionId == null) {
            log.warn("未知的WebSocket会话: {}", session.getId());
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        String payload = message.getPayload();
        log.debug("收到WebSocket消息: {} -> {}", sessionId, payload);

        try {
            webRTCService.handleSignalingMessage(sessionId, payload);
        } catch (Exception e) {
            log.error("处理WebSocket消息失败: {}", e.getMessage(), e);
            sendErrorMessage(session, "处理消息失败: " + e.getMessage());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String sessionId = sessionIdMap.get(session.getId());
        log.error("WebSocket传输错误: {} -> {}", sessionId, exception.getMessage(), exception);
        
        if (sessionId != null) {
            webRTCService.removeSession(sessionId);
            sessionIdMap.remove(session.getId());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String sessionId = sessionIdMap.get(session.getId());
        log.info("WebSocket连接关闭: {} -> {}, 状态: {}", session.getId(), sessionId, closeStatus);
        
        if (sessionId != null) {
            webRTCService.removeSession(sessionId);
            sessionIdMap.remove(session.getId());
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 生成会话ID
     */
    private String generateSessionId(WebSocketSession session) {
        String remoteAddress = session.getRemoteAddress() != null ? 
            session.getRemoteAddress().toString() : "unknown";
        return "webrtc_" + System.currentTimeMillis() + "_" + 
               Math.abs(remoteAddress.hashCode()) % 10000;
    }

    /**
     * 发送连接成功消息
     */
    private void sendConnectionSuccess(WebSocketSession session, String sessionId) {
        try {
            String successMessage = "{\"type\":\"connected\",\"sessionId\":\"" + sessionId + "\"}";
            session.sendMessage(new TextMessage(successMessage));
            log.info("发送连接成功消息: {}", sessionId);
        } catch (IOException e) {
            log.error("发送连接成功消息失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 发送错误消息
     */
    private void sendErrorMessage(WebSocketSession session, String errorMessage) {
        try {
            String errorMsg = "{\"type\":\"error\",\"message\":\"" + errorMessage + "\"}";
            session.sendMessage(new TextMessage(errorMsg));
        } catch (IOException e) {
            log.error("发送错误消息失败: {}", e.getMessage(), e);
        }
    }
}