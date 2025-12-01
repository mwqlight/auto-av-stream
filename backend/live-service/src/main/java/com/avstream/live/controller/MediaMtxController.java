package com.avstream.live.controller;

import com.avstream.live.dto.MediaMtxConfigDTO;
import com.avstream.live.dto.MediaMtxStreamDTO;
import com.avstream.live.service.MediaMtxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * MediaMTX流媒体服务器控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/mediamtx")
public class MediaMtxController {
    
    private final MediaMtxService mediaMtxService;
    
    @Autowired
    public MediaMtxController(MediaMtxService mediaMtxService) {
        this.mediaMtxService = mediaMtxService;
    }
    
    /**
     * 获取所有流列表
     */
    @GetMapping("/streams")
    @PreAuthorize("hasPermission('live:view')")
    public ResponseEntity<List<MediaMtxStreamDTO>> getAllStreams() {
        try {
            List<MediaMtxStreamDTO> streams = mediaMtxService.getAllStreams();
            return ResponseEntity.ok(streams);
        } catch (Exception e) {
            log.error("获取流列表失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取特定流信息
     */
    @GetMapping("/streams/{streamKey}")
    @PreAuthorize("hasPermission('live:view')")
    public ResponseEntity<MediaMtxStreamDTO> getStream(@PathVariable String streamKey) {
        try {
            MediaMtxStreamDTO stream = mediaMtxService.getStream(streamKey);
            if (stream != null) {
                return ResponseEntity.ok(stream);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取流信息失败 - {}: {}", streamKey, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 检查流是否存在
     */
    @GetMapping("/streams/{streamKey}/exists")
    @PreAuthorize("hasPermission('live:view')")
    public ResponseEntity<Boolean> streamExists(@PathVariable String streamKey) {
        try {
            boolean exists = mediaMtxService.streamExists(streamKey);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("检查流是否存在失败 - {}: {}", streamKey, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 检查流是否活跃
     */
    @GetMapping("/streams/{streamKey}/active")
    @PreAuthorize("hasPermission('live:view')")
    public ResponseEntity<Boolean> isStreamActive(@PathVariable String streamKey) {
        try {
            boolean active = mediaMtxService.isStreamActive(streamKey);
            return ResponseEntity.ok(active);
        } catch (Exception e) {
            log.error("检查流活跃状态失败 - {}: {}", streamKey, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取流状态
     */
    @GetMapping("/streams/{streamKey}/state")
    @PreAuthorize("hasPermission('live:view')")
    public ResponseEntity<String> getStreamState(@PathVariable String streamKey) {
        try {
            String state = mediaMtxService.getStreamState(streamKey);
            return ResponseEntity.ok(state);
        } catch (Exception e) {
            log.error("获取流状态失败 - {}: {}", streamKey, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取观看人数
     */
    @GetMapping("/streams/{streamKey}/viewers")
    @PreAuthorize("hasPermission('live:view')")
    public ResponseEntity<Integer> getViewerCount(@PathVariable String streamKey) {
        try {
            Integer viewerCount = mediaMtxService.getViewerCount(streamKey);
            return ResponseEntity.ok(viewerCount);
        } catch (Exception e) {
            log.error("获取观看人数失败 - {}: {}", streamKey, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 开始录制
     */
    @PostMapping("/streams/{streamKey}/record/start")
    @PreAuthorize("hasPermission('live:record')")
    public ResponseEntity<Boolean> startRecording(@PathVariable String streamKey) {
        try {
            boolean success = mediaMtxService.startRecording(streamKey);
            if (success) {
                log.info("开始录制流: {}", streamKey);
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.badRequest().body(false);
            }
        } catch (Exception e) {
            log.error("开始录制失败 - {}: {}", streamKey, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 停止录制
     */
    @PostMapping("/streams/{streamKey}/record/stop")
    @PreAuthorize("hasPermission('live:record')")
    public ResponseEntity<Boolean> stopRecording(@PathVariable String streamKey) {
        try {
            boolean success = mediaMtxService.stopRecording(streamKey);
            if (success) {
                log.info("停止录制流: {}", streamKey);
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.badRequest().body(false);
            }
        } catch (Exception e) {
            log.error("停止录制失败 - {}: {}", streamKey, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取录制状态
     */
    @GetMapping("/streams/{streamKey}/recording")
    @PreAuthorize("hasPermission('live:view')")
    public ResponseEntity<Boolean> isRecording(@PathVariable String streamKey) {
        try {
            boolean recording = mediaMtxService.isRecording(streamKey);
            return ResponseEntity.ok(recording);
        } catch (Exception e) {
            log.error("获取录制状态失败 - {}: {}", streamKey, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取录制时长
     */
    @GetMapping("/streams/{streamKey}/recording/duration")
    @PreAuthorize("hasPermission('live:view')")
    public ResponseEntity<Long> getRecordingDuration(@PathVariable String streamKey) {
        try {
            Long duration = mediaMtxService.getRecordingDuration(streamKey);
            return ResponseEntity.ok(duration);
        } catch (Exception e) {
            log.error("获取录制时长失败 - {}: {}", streamKey, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取服务器配置
     */
    @GetMapping("/config")
    @PreAuthorize("hasPermission('live:config')")
    public ResponseEntity<MediaMtxConfigDTO> getServerConfig() {
        try {
            MediaMtxConfigDTO config = mediaMtxService.getServerConfig();
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            log.error("获取服务器配置失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取服务器状态
     */
    @GetMapping("/status")
    @PreAuthorize("hasPermission('live:view')")
    public ResponseEntity<Map<String, Object>> getServerStatus() {
        try {
            Map<String, Object> status = mediaMtxService.getServerStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("获取服务器状态失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 检查服务器连接状态
     */
    @GetMapping("/health")
    @PreAuthorize("hasPermission('live:view')")
    public ResponseEntity<Boolean> isServerConnected() {
        try {
            boolean connected = mediaMtxService.isServerConnected();
            return ResponseEntity.ok(connected);
        } catch (Exception e) {
            log.error("检查服务器连接状态失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}