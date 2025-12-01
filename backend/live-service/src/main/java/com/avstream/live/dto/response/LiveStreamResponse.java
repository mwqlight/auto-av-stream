package com.avstream.live.dto.response;

import com.avstream.live.entity.LiveStream;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 直播流响应DTO
 * 
 * @author AV Stream Team
 */
@Data
public class LiveStreamResponse {
    
    private Long id;
    private String title;
    private String description;
    private String streamKey;
    private Long userId;
    private String username;
    private LiveStream.StreamStatus status;
    private LiveStream.StreamType type;
    private Boolean recordEnabled;
    private String recordPath;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer viewerCount;
    private Integer maxViewerCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 推流地址
    private String rtmpPushUrl;
    private String hlsPushUrl;
    private String webrtcPushUrl;
    
    // 拉流地址
    private String rtmpPullUrl;
    private String hlsPullUrl;
    private String webrtcPullUrl;
    
    /**
     * 从实体类转换为响应DTO
     */
    public static LiveStreamResponse fromEntity(LiveStream liveStream) {
        LiveStreamResponse response = new LiveStreamResponse();
        response.setId(liveStream.getId());
        response.setTitle(liveStream.getTitle());
        response.setDescription(liveStream.getDescription());
        response.setStreamKey(liveStream.getStreamKey());
        response.setUserId(liveStream.getUserId());
        response.setUsername(liveStream.getUsername());
        response.setStatus(liveStream.getStatus());
        response.setType(liveStream.getType());
        response.setRecordEnabled(liveStream.getRecordEnabled());
        response.setRecordPath(liveStream.getRecordPath());
        response.setStartTime(liveStream.getStartTime());
        response.setEndTime(liveStream.getEndTime());
        response.setViewerCount(liveStream.getViewerCount());
        response.setMaxViewerCount(liveStream.getMaxViewerCount());
        response.setCreateTime(liveStream.getCreateTime());
        response.setUpdateTime(liveStream.getUpdateTime());
        
        // 生成推流地址
        response.setRtmpPushUrl(generateRtmpPushUrl(liveStream.getStreamKey()));
        response.setHlsPushUrl(generateHlsPushUrl(liveStream.getStreamKey()));
        response.setWebrtcPushUrl(generateWebrtcPushUrl(liveStream.getStreamKey()));
        
        // 生成拉流地址
        response.setRtmpPullUrl(generateRtmpPullUrl(liveStream.getStreamKey()));
        response.setHlsPullUrl(generateHlsPullUrl(liveStream.getStreamKey()));
        response.setWebrtcPullUrl(generateWebrtcPullUrl(liveStream.getStreamKey()));
        
        return response;
    }
    
    /**
     * 生成RTMP推流地址
     */
    private static String generateRtmpPushUrl(String streamKey) {
        return String.format("rtmp://localhost:1935/live/%s", streamKey);
    }
    
    /**
     * 生成HLS推流地址
     */
    private static String generateHlsPushUrl(String streamKey) {
        return String.format("http://localhost:8080/hls/%s/index.m3u8", streamKey);
    }
    
    /**
     * 生成WebRTC推流地址
     */
    private static String generateWebrtcPushUrl(String streamKey) {
        return String.format("ws://localhost:8889/ws/%s", streamKey);
    }
    
    /**
     * 生成RTMP拉流地址
     */
    private static String generateRtmpPullUrl(String streamKey) {
        return String.format("rtmp://localhost:1935/live/%s", streamKey);
    }
    
    /**
     * 生成HLS拉流地址
     */
    private static String generateHlsPullUrl(String streamKey) {
        return String.format("http://localhost:8080/hls/%s/index.m3u8", streamKey);
    }
    
    /**
     * 生成WebRTC拉流地址
     */
    private static String generateWebrtcPullUrl(String streamKey) {
        return String.format("ws://localhost:8889/ws/%s", streamKey);
    }
}