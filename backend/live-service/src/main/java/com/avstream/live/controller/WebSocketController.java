package com.avstream.live.controller;

import com.avstream.live.dto.response.LiveStreamResponse;
import com.avstream.live.service.LiveStreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * WebSocket控制器
 * 
 * @author AV Stream Team
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final LiveStreamService liveStreamService;
    
    /**
     * 订阅直播流信息
     */
    @SubscribeMapping("/topic/live/{streamKey}")
    public void subscribeToLiveStream(@Payload Map<String, Object> payload) {
        String streamKey = (String) payload.get("streamKey");
        log.info("用户订阅直播流，流密钥: {}", streamKey);
        
        try {
            LiveStreamResponse liveStream = liveStreamService.getLiveStreamByKey(streamKey);
            
            // 发送当前直播信息
            messagingTemplate.convertAndSend("/topic/live/" + streamKey, Map.of(
                "type", "STREAM_INFO",
                "data", liveStream
            ));
            
            // 增加观看人数
            liveStreamService.incrementViewerCount(streamKey);
            
        } catch (Exception e) {
            log.error("订阅直播流失败，流密钥: {}", streamKey, e);
            messagingTemplate.convertAndSend("/topic/live/" + streamKey, Map.of(
                "type", "ERROR",
                "message", "订阅失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 取消订阅直播流
     */
    @MessageMapping("/live/unsubscribe")
    public void unsubscribeFromLiveStream(@Payload Map<String, Object> payload) {
        String streamKey = (String) payload.get("streamKey");
        log.info("用户取消订阅直播流，流密钥: {}", streamKey);
        
        // 减少观看人数
        liveStreamService.decrementViewerCount(streamKey);
    }
    
    /**
     * 发送聊天消息
     */
    @MessageMapping("/live/chat")
    public void sendChatMessage(@Payload Map<String, Object> payload) {
        String streamKey = (String) payload.get("streamKey");
        String message = (String) payload.get("message");
        String username = (String) payload.get("username");
        
        log.info("用户发送聊天消息，流密钥: {}, 用户名: {}, 消息: {}", streamKey, username, message);
        
        // 广播聊天消息
        messagingTemplate.convertAndSend("/topic/live/" + streamKey + "/chat", Map.of(
            "type", "CHAT_MESSAGE",
            "username", username,
            "message", message,
            "timestamp", System.currentTimeMillis()
        ));
    }
    
    /**
     * 发送直播状态更新
     */
    public void broadcastLiveStatus(String streamKey, String status, LiveStreamResponse liveStream) {
        log.info("广播直播状态更新，流密钥: {}, 状态: {}", streamKey, status);
        
        messagingTemplate.convertAndSend("/topic/live/" + streamKey, Map.of(
            "type", "STATUS_UPDATE",
            "status", status,
            "data", liveStream,
            "timestamp", System.currentTimeMillis()
        ));
    }
    
    /**
     * 发送观看人数更新
     */
    public void broadcastViewerCount(String streamKey, int viewerCount) {
        messagingTemplate.convertAndSend("/topic/live/" + streamKey, Map.of(
            "type", "VIEWER_COUNT",
            "viewerCount", viewerCount,
            "timestamp", System.currentTimeMillis()
        ));
    }
}