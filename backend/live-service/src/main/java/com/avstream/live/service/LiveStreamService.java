package com.avstream.live.service;

import com.avstream.live.dto.request.CreateLiveRequest;
import com.avstream.live.dto.request.UpdateLiveRequest;
import com.avstream.live.dto.response.LiveStreamResponse;
import com.avstream.live.entity.LiveStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 直播流服务接口
 * 
 * @author AV Stream Team
 */
public interface LiveStreamService {
    
    /**
     * 创建直播流
     */
    LiveStreamResponse createLiveStream(CreateLiveRequest request, Long userId, String username);
    
    /**
     * 更新直播流
     */
    LiveStreamResponse updateLiveStream(Long streamId, UpdateLiveRequest request, Long userId);
    
    /**
     * 开始直播
     */
    LiveStreamResponse startLiveStream(Long streamId, Long userId);
    
    /**
     * 结束直播
     */
    LiveStreamResponse endLiveStream(Long streamId, Long userId);
    
    /**
     * 删除直播流
     */
    void deleteLiveStream(Long streamId, Long userId);
    
    /**
     * 根据ID获取直播流
     */
    LiveStreamResponse getLiveStreamById(Long streamId);
    
    /**
     * 根据流密钥获取直播流
     */
    LiveStreamResponse getLiveStreamByKey(String streamKey);
    
    /**
     * 获取用户的所有直播流
     */
    List<LiveStreamResponse> getUserLiveStreams(Long userId);
    
    /**
     * 分页获取用户直播流
     */
    Page<LiveStreamResponse> getUserLiveStreams(Long userId, Pageable pageable);
    
    /**
     * 获取正在直播的流列表
     */
    List<LiveStreamResponse> getActiveLiveStreams();
    
    /**
     * 分页获取所有直播流
     */
    Page<LiveStreamResponse> getAllLiveStreams(Pageable pageable);
    
    /**
     * 搜索直播流
     */
    Page<LiveStreamResponse> searchLiveStreams(String keyword, LiveStream.StreamStatus status, Pageable pageable);
    
    /**
     * 增加观看人数
     */
    void incrementViewerCount(String streamKey);
    
    /**
     * 减少观看人数
     */
    void decrementViewerCount(String streamKey);
    
    /**
     * 获取直播统计信息
     */
    LiveStreamStats getLiveStreamStats(Long userId);
    
    /**
     * 开始录制直播流
     */
    boolean startRecording(Long streamId, Long userId);
    
    /**
     * 停止录制直播流
     */
    boolean stopRecording(Long streamId, Long userId);
    
    /**
     * 获取直播流实时状态
     */
    Map<String, Object> getLiveStreamRealTimeStatus(Long streamId);
    
    /**
     * 获取流媒体服务器状态
     */
    Map<String, Object> getMediaServerStatus();
    
    /**
     * 直播统计信息
     */
    class LiveStreamStats {
        private Long totalLiveCount;
        private Long totalLiveDuration;
        private Long totalViewerCount;
        
        public LiveStreamStats(Long totalLiveCount, Long totalLiveDuration, Long totalViewerCount) {
            this.totalLiveCount = totalLiveCount;
            this.totalLiveDuration = totalLiveDuration;
            this.totalViewerCount = totalViewerCount;
        }
        
        // getters and setters
        public Long getTotalLiveCount() { return totalLiveCount; }
        public void setTotalLiveCount(Long totalLiveCount) { this.totalLiveCount = totalLiveCount; }
        
        public Long getTotalLiveDuration() { return totalLiveDuration; }
        public void setTotalLiveDuration(Long totalLiveDuration) { this.totalLiveDuration = totalLiveDuration; }
        
        public Long getTotalViewerCount() { return totalViewerCount; }
        public void setTotalViewerCount(Long totalViewerCount) { this.totalViewerCount = totalViewerCount; }
    }
}