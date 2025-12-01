package com.avstream.live.controller;

import com.avstream.live.service.LiveStreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 直播流媒体控制器 - 处理MediaMTX相关的API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/live/media")
public class LiveStreamMediaController {
    
    private final LiveStreamService liveStreamService;
    
    @Autowired
    public LiveStreamMediaController(LiveStreamService liveStreamService) {
        this.liveStreamService = liveStreamService;
    }
    
    /**
     * 开始录制直播流
     */
    @PostMapping("/{streamId}/record/start")
    @PreAuthorize("hasPermission('live:record')")
    public ResponseEntity<Map<String, Object>> startRecording(
            @PathVariable Long streamId,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            boolean success = liveStreamService.startRecording(streamId, userId);
            
            Map<String, Object> response = Map.of(
                "success", success,
                "message", success ? "录制开始成功" : "录制开始失败",
                "streamId", streamId
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("开始录制失败 - 流ID: {}, 错误: {}", streamId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage(),
                "streamId", streamId
            ));
        }
    }
    
    /**
     * 停止录制直播流
     */
    @PostMapping("/{streamId}/record/stop")
    @PreAuthorize("hasPermission('live:record')")
    public ResponseEntity<Map<String, Object>> stopRecording(
            @PathVariable Long streamId,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            boolean success = liveStreamService.stopRecording(streamId, userId);
            
            Map<String, Object> response = Map.of(
                "success", success,
                "message", success ? "录制停止成功" : "录制停止失败",
                "streamId", streamId
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("停止录制失败 - 流ID: {}, 错误: {}", streamId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage(),
                "streamId", streamId
            ));
        }
    }
    
    /**
     * 获取直播流实时状态
     */
    @GetMapping("/{streamId}/status")
    @PreAuthorize("hasPermission('live:view')")
    public ResponseEntity<Map<String, Object>> getRealTimeStatus(@PathVariable Long streamId) {
        try {
            Map<String, Object> status = liveStreamService.getLiveStreamRealTimeStatus(streamId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("获取直播流实时状态失败 - 流ID: {}, 错误: {}", streamId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage(),
                "streamId", streamId
            ));
        }
    }
    
    /**
     * 获取流媒体服务器状态
     */
    @GetMapping("/server/status")
    @PreAuthorize("hasPermission('live:config')")
    public ResponseEntity<Map<String, Object>> getMediaServerStatus() {
        try {
            Map<String, Object> status = liveStreamService.getMediaServerStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("获取流媒体服务器状态失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "获取服务器状态失败"
            ));
        }
    }
    
    /**
     * 检查流媒体服务器连接状态
     */
    @GetMapping("/server/health")
    @PreAuthorize("hasPermission('live:view')")
    public ResponseEntity<Map<String, Object>> checkMediaServerHealth() {
        try {
            Map<String, Object> status = liveStreamService.getMediaServerStatus();
            boolean connected = (boolean) status.get("serverConnected");
            
            Map<String, Object> response = Map.of(
                "connected", connected,
                "message", connected ? "流媒体服务器连接正常" : "流媒体服务器连接失败"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("检查流媒体服务器健康状态失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "connected", false,
                "message", "检查服务器健康状态失败"
            ));
        }
    }
    
    /**
     * 从认证信息中获取用户ID
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new SecurityException("用户未认证");
        }
        
        // 这里需要根据实际的认证实现来获取用户ID
        // 假设认证信息中包含用户ID作为principal
        try {
            return Long.parseLong(authentication.getPrincipal().toString());
        } catch (NumberFormatException e) {
            throw new SecurityException("无法获取用户ID");
        }
    }
}