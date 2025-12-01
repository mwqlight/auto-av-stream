package com.avstream.live.controller;

import com.avstream.live.dto.request.CreateLiveRequest;
import com.avstream.live.dto.request.UpdateLiveRequest;
import com.avstream.live.dto.response.LiveStreamResponse;
import com.avstream.live.entity.LiveStream;
import com.avstream.live.service.LiveStreamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 直播控制器
 * 
 * @author AV Stream Team
 */
@RestController
@RequestMapping("/api/v1/live")
@Tag(name = "直播管理", description = "直播流管理相关接口")
@Slf4j
@Validated
@RequiredArgsConstructor
public class LiveController {

    private final LiveStreamService liveStreamService;

    @PostMapping("/create")
    @Operation(summary = "创建直播", description = "创建新的直播流")
    public ResponseEntity<LiveStreamResponse> createLive(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Username") String username,
            @Valid @RequestBody CreateLiveRequest request) {
        
        log.info("用户创建直播: userId={}, username={}, title={}", userId, username, request.getTitle());
        
        LiveStreamResponse response = liveStreamService.createLiveStream(request, userId, username);
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{streamId}")
    @Operation(summary = "更新直播信息", description = "更新直播流的基本信息")
    public ResponseEntity<LiveStreamResponse> updateLive(
            @PathVariable Long streamId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UpdateLiveRequest request) {
        
        log.info("用户更新直播: userId={}, streamId={}", userId, streamId);
        
        LiveStreamResponse response = liveStreamService.updateLiveStream(streamId, request, userId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{streamId}")
    @Operation(summary = "获取直播详情", description = "根据ID获取直播流详情")
    public ResponseEntity<LiveStreamResponse> getLive(@PathVariable Long streamId) {
        
        log.info("获取直播详情: streamId={}", streamId);
        
        LiveStreamResponse response = liveStreamService.getLiveStreamById(streamId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    @Operation(summary = "获取直播列表", description = "分页获取直播流列表")
    public ResponseEntity<Page<LiveStreamResponse>> getLiveList(Pageable pageable) {
        
        log.info("获取直播列表");
        
        Page<LiveStreamResponse> response = liveStreamService.getAllLiveStreams(pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户直播列表", description = "获取指定用户的直播流列表")
    public ResponseEntity<List<LiveStreamResponse>> getUserLives(@PathVariable Long userId) {
        
        log.info("获取用户直播列表: userId={}", userId);
        
        List<LiveStreamResponse> response = liveStreamService.getUserLiveStreams(userId);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{streamId}/start")
    @Operation(summary = "开始直播", description = "开始指定的直播流")
    public ResponseEntity<LiveStreamResponse> startLive(
            @PathVariable Long streamId,
            @RequestHeader("X-User-Id") Long userId) {
        
        log.info("用户开始直播: userId={}, streamId={}", userId, streamId);
        
        LiveStreamResponse response = liveStreamService.startLiveStream(streamId, userId);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{streamId}/end")
    @Operation(summary = "结束直播", description = "结束指定的直播流")
    public ResponseEntity<LiveStreamResponse> endLive(
            @PathVariable Long streamId,
            @RequestHeader("X-User-Id") Long userId) {
        
        log.info("用户结束直播: userId={}, streamId={}", userId, streamId);
        
        LiveStreamResponse response = liveStreamService.endLiveStream(streamId, userId);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{streamId}")
    @Operation(summary = "删除直播", description = "删除指定的直播流")
    public ResponseEntity<Void> deleteLive(
            @PathVariable Long streamId,
            @RequestHeader("X-User-Id") Long userId) {
        
        log.info("用户删除直播: userId={}, streamId={}", userId, streamId);
        
        liveStreamService.deleteLiveStream(streamId, userId);
        
        return ResponseEntity.ok().build();
    }

    // 观看人数统计功能需要根据流密钥实现，暂时移除该接口
    // 实际实现需要先通过streamId获取streamKey，再调用相应服务

    // 观看人数统计功能需要根据流密钥实现，暂时移除该接口
    // 实际实现需要先通过streamId获取streamKey，再调用相应服务
}