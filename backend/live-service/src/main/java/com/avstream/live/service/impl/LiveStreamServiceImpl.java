package com.avstream.live.service.impl;

import com.avstream.live.dto.request.CreateLiveRequest;
import com.avstream.live.dto.request.UpdateLiveRequest;
import com.avstream.live.dto.response.LiveStreamResponse;
import com.avstream.live.entity.LiveStream;
import com.avstream.live.repository.LiveStreamRepository;
import com.avstream.live.service.LiveStreamService;
import com.avstream.live.service.MediaMtxService;
import com.avstream.live.dto.MediaMtxStreamDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 直播流服务实现类
 * 
 * @author AV Stream Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LiveStreamServiceImpl implements LiveStreamService {
    
    private final LiveStreamRepository liveStreamRepository;
    private final MediaMtxService mediaMtxService;
    
    @Value("${live.push.rtmp-url-template:rtmp://localhost:1935/live/{streamKey}}")
    private String rtmpPushUrlTemplate;
    
    @Value("${live.push.hls-url-template:http://localhost:8080/hls/{streamKey}/index.m3u8}")
    private String hlsPushUrlTemplate;
    
    @Value("${live.push.webrtc-url-template:ws://localhost:8889/ws/{streamKey}}")
    private String webrtcPushUrlTemplate;
    
    @Value("${live.pull.rtmp-url-template:rtmp://localhost:1935/live/{streamKey}}")
    private String rtmpPullUrlTemplate;
    
    @Value("${live.pull.hls-url-template:http://localhost:8080/hls/{streamKey}/index.m3u8}")
    private String hlsPullUrlTemplate;
    
    @Value("${live.pull.webrtc-url-template:ws://localhost:8889/ws/{streamKey}}")
    private String webrtcPullUrlTemplate;
    
    @Override
    @Transactional
    public LiveStreamResponse createLiveStream(CreateLiveRequest request, Long userId, String username) {
        log.info("创建直播流，用户ID: {}, 用户名: {}", userId, username);
        
        // 生成唯一的流密钥
        String streamKey = generateStreamKey();
        
        // 检查MediaMTX服务器连接状态
        if (!mediaMtxService.isServerConnected()) {
            log.warn("MediaMTX服务器连接失败，但继续创建直播流记录");
        }
        
        LiveStream liveStream = new LiveStream();
        liveStream.setTitle(request.getTitle());
        liveStream.setDescription(request.getDescription());
        liveStream.setStreamKey(streamKey);
        liveStream.setUserId(userId);
        liveStream.setUsername(username);
        liveStream.setStatus(LiveStream.StreamStatus.CREATED);
        liveStream.setType(request.getType());
        liveStream.setRecordEnabled(request.getRecordEnabled());
        liveStream.setViewerCount(0);
        liveStream.setMaxViewerCount(0);
        
        // 设置推流和拉流地址
        liveStream.setRtmpPushUrl(rtmpPushUrlTemplate.replace("{streamKey}", streamKey));
        liveStream.setHlsPushUrl(hlsPushUrlTemplate.replace("{streamKey}", streamKey));
        liveStream.setWebrtcPushUrl(webrtcPushUrlTemplate.replace("{streamKey}", streamKey));
        liveStream.setRtmpPullUrl(rtmpPullUrlTemplate.replace("{streamKey}", streamKey));
        liveStream.setHlsPullUrl(hlsPullUrlTemplate.replace("{streamKey}", streamKey));
        liveStream.setWebrtcPullUrl(webrtcPullUrlTemplate.replace("{streamKey}", streamKey));
        
        LiveStream savedStream = liveStreamRepository.save(liveStream);
        log.info("直播流创建成功，流ID: {}, 流密钥: {}", savedStream.getId(), streamKey);
        
        return LiveStreamResponse.fromEntity(savedStream);
    }
    
    @Override
    @Transactional
    public LiveStreamResponse updateLiveStream(Long streamId, UpdateLiveRequest request, Long userId) {
        log.info("更新直播流，流ID: {}, 用户ID: {}", streamId, userId);
        
        LiveStream liveStream = getLiveStreamByIdAndUserId(streamId, userId);
        
        if (request.getTitle() != null) {
            liveStream.setTitle(request.getTitle());
        }
        
        if (request.getDescription() != null) {
            liveStream.setDescription(request.getDescription());
        }
        
        if (request.getRecordEnabled() != null) {
            liveStream.setRecordEnabled(request.getRecordEnabled());
        }
        
        liveStream.setUpdateTime(LocalDateTime.now());
        LiveStream updatedStream = liveStreamRepository.save(liveStream);
        
        log.info("直播流更新成功，流ID: {}", streamId);
        return LiveStreamResponse.fromEntity(updatedStream);
    }
    
    @Override
    @Transactional
    public LiveStreamResponse startLiveStream(Long streamId, Long userId) {
        log.info("开始直播，流ID: {}, 用户ID: {}", streamId, userId);
        
        LiveStream liveStream = getLiveStreamByIdAndUserId(streamId, userId);
        
        if (liveStream.getStatus() != LiveStream.StreamStatus.CREATED) {
            throw new IllegalStateException("只有已创建的直播才能开始");
        }
        
        // 检查MediaMTX服务器连接状态
        if (!mediaMtxService.isServerConnected()) {
            throw new IllegalStateException("流媒体服务器连接失败，无法开始直播");
        }
        
        // 检查流是否已经在MediaMTX中存在
        if (mediaMtxService.streamExists(liveStream.getStreamKey())) {
            log.warn("流密钥已存在于MediaMTX服务器中: {}", liveStream.getStreamKey());
        }
        
        liveStream.setStatus(LiveStream.StreamStatus.LIVE);
        liveStream.setStartTime(LocalDateTime.now());
        liveStream.setViewerCount(0);
        liveStream.setMaxViewerCount(0);
        
        LiveStream startedStream = liveStreamRepository.save(liveStream);
        log.info("直播开始成功，流ID: {}", streamId);
        
        return LiveStreamResponse.fromEntity(startedStream);
    }
    
    @Override
    @Transactional
    public LiveStreamResponse endLiveStream(Long streamId, Long userId) {
        log.info("结束直播，流ID: {}, 用户ID: {}", streamId, userId);
        
        LiveStream liveStream = getLiveStreamByIdAndUserId(streamId, userId);
        
        if (liveStream.getStatus() != LiveStream.StreamStatus.LIVE) {
            throw new IllegalStateException("只有正在直播的流才能结束");
        }
        
        // 停止录制（如果正在录制）
        if (liveStream.getRecordEnabled() && mediaMtxService.isRecording(liveStream.getStreamKey())) {
            try {
                mediaMtxService.stopRecording(liveStream.getStreamKey());
                log.info("已停止录制流: {}", liveStream.getStreamKey());
            } catch (Exception e) {
                log.error("停止录制失败 - {}: {}", liveStream.getStreamKey(), e.getMessage());
            }
        }
        
        liveStream.setStatus(LiveStream.StreamStatus.ENDED);
        liveStream.setEndTime(LocalDateTime.now());
        
        LiveStream endedStream = liveStreamRepository.save(liveStream);
        log.info("直播结束成功，流ID: {}", streamId);
        
        return LiveStreamResponse.fromEntity(endedStream);
    }
    
    @Override
    @Transactional
    public void deleteLiveStream(Long streamId, Long userId) {
        log.info("删除直播流，流ID: {}, 用户ID: {}", streamId, userId);
        
        LiveStream liveStream = getLiveStreamByIdAndUserId(streamId, userId);
        
        if (liveStream.getStatus() == LiveStream.StreamStatus.LIVE) {
            throw new IllegalStateException("正在直播的流不能删除");
        }
        
        liveStreamRepository.delete(liveStream);
        log.info("直播流删除成功，流ID: {}", streamId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public LiveStreamResponse getLiveStreamById(Long streamId) {
        log.info("根据ID获取直播流，流ID: {}", streamId);
        
        LiveStream liveStream = liveStreamRepository.findById(streamId)
                .orElseThrow(() -> new IllegalArgumentException("直播流不存在"));
        
        return LiveStreamResponse.fromEntity(liveStream);
    }
    
    @Override
    @Transactional(readOnly = true)
    public LiveStreamResponse getLiveStreamByKey(String streamKey) {
        log.info("根据流密钥获取直播流，流密钥: {}", streamKey);
        
        LiveStream liveStream = liveStreamRepository.findByStreamKey(streamKey)
                .orElseThrow(() -> new IllegalArgumentException("直播流不存在"));
        
        return LiveStreamResponse.fromEntity(liveStream);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LiveStreamResponse> getUserLiveStreams(Long userId) {
        log.info("获取用户直播流列表，用户ID: {}", userId);
        
        List<LiveStream> liveStreams = liveStreamRepository.findByUserId(userId);
        return liveStreams.stream()
                .map(LiveStreamResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LiveStreamResponse> getUserLiveStreams(Long userId, Pageable pageable) {
        log.info("分页获取用户直播流列表，用户ID: {}, 分页: {}", userId, pageable);
        
        Page<LiveStream> liveStreams = liveStreamRepository.findByUserId(userId, pageable);
        return liveStreams.map(LiveStreamResponse::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LiveStreamResponse> getActiveLiveStreams() {
        log.info("获取正在直播的流列表");
        
        List<LiveStream> liveStreams = liveStreamRepository.findByStatus(LiveStream.StreamStatus.LIVE);
        return liveStreams.stream()
                .map(LiveStreamResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LiveStreamResponse> getAllLiveStreams(Pageable pageable) {
        log.info("分页获取所有直播流列表，分页: {}", pageable);
        
        Page<LiveStream> liveStreams = liveStreamRepository.findAll(pageable);
        return liveStreams.map(LiveStreamResponse::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LiveStreamResponse> searchLiveStreams(String keyword, LiveStream.StreamStatus status, Pageable pageable) {
        log.info("搜索直播流，关键词: {}, 状态: {}, 分页: {}", keyword, status, pageable);
        
        Page<LiveStream> liveStreams = liveStreamRepository.findByTitleContainingAndStatus(keyword, status, pageable);
        return liveStreams.map(LiveStreamResponse::fromEntity);
    }
    
    @Override
    @Transactional
    public void incrementViewerCount(String streamKey) {
        log.info("增加观看人数，流密钥: {}", streamKey);
        
        Optional<LiveStream> liveStreamOpt = liveStreamRepository.findByStreamKey(streamKey);
        if (liveStreamOpt.isPresent()) {
            LiveStream liveStream = liveStreamOpt.get();
            if (liveStream.getStatus() == LiveStream.StreamStatus.LIVE) {
                int currentViewerCount = liveStream.getViewerCount() != null ? liveStream.getViewerCount() : 0;
                liveStream.setViewerCount(currentViewerCount + 1);
                
                if (liveStream.getMaxViewerCount() == null || 
                    liveStream.getViewerCount() > liveStream.getMaxViewerCount()) {
                    liveStream.setMaxViewerCount(liveStream.getViewerCount());
                }
                
                liveStreamRepository.save(liveStream);
            }
        }
    }
    
    @Override
    @Transactional
    public void decrementViewerCount(String streamKey) {
        log.info("减少观看人数，流密钥: {}", streamKey);
        
        Optional<LiveStream> liveStreamOpt = liveStreamRepository.findByStreamKey(streamKey);
        if (liveStreamOpt.isPresent()) {
            LiveStream liveStream = liveStreamOpt.get();
            if (liveStream.getStatus() == LiveStream.StreamStatus.LIVE && 
                liveStream.getViewerCount() != null && liveStream.getViewerCount() > 0) {
                liveStream.setViewerCount(liveStream.getViewerCount() - 1);
                liveStreamRepository.save(liveStream);
            }
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public LiveStreamStats getLiveStreamStats(Long userId) {
        log.info("获取直播统计信息，用户ID: {}", userId);
        
        Long totalLiveCount = liveStreamRepository.countByUserIdAndStatus(userId, LiveStream.StreamStatus.ENDED);
        Long totalLiveDuration = liveStreamRepository.sumLiveDurationByUserId(userId);
        
        // 计算总观看人数（这里简化处理，实际应该统计所有直播的总观看人数）
        Long totalViewerCount = 0L;
        List<LiveStream> userStreams = liveStreamRepository.findByUserId(userId);
        for (LiveStream stream : userStreams) {
            if (stream.getMaxViewerCount() != null) {
                totalViewerCount += stream.getMaxViewerCount();
            }
        }
        
        return new LiveStreamStats(totalLiveCount, totalLiveDuration, totalViewerCount);
    }
    
    /**
     * 开始录制直播流
     */
    @Override
    @Transactional
    public boolean startRecording(Long streamId, Long userId) {
        log.info("开始录制直播，流ID: {}, 用户ID: {}", streamId, userId);
        
        LiveStream liveStream = getLiveStreamByIdAndUserId(streamId, userId);
        
        if (liveStream.getStatus() != LiveStream.StreamStatus.LIVE) {
            throw new IllegalStateException("只有正在直播的流才能开始录制");
        }
        
        if (!liveStream.getRecordEnabled()) {
            throw new IllegalStateException("该直播流未启用录制功能");
        }
        
        boolean success = mediaMtxService.startRecording(liveStream.getStreamKey());
        if (success) {
            log.info("录制开始成功，流密钥: {}", liveStream.getStreamKey());
        } else {
            log.error("录制开始失败，流密钥: {}", liveStream.getStreamKey());
        }
        
        return success;
    }
    
    /**
     * 停止录制直播流
     */
    @Override
    @Transactional
    public boolean stopRecording(Long streamId, Long userId) {
        log.info("停止录制直播，流ID: {}, 用户ID: {}", streamId, userId);
        
        LiveStream liveStream = getLiveStreamByIdAndUserId(streamId, userId);
        
        if (liveStream.getStatus() != LiveStream.StreamStatus.LIVE) {
            throw new IllegalStateException("只有正在直播的流才能停止录制");
        }
        
        boolean success = mediaMtxService.stopRecording(liveStream.getStreamKey());
        if (success) {
            log.info("录制停止成功，流密钥: {}", liveStream.getStreamKey());
        } else {
            log.error("录制停止失败，流密钥: {}", liveStream.getStreamKey());
        }
        
        return success;
    }
    
    /**
     * 获取直播流实时状态
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getLiveStreamRealTimeStatus(Long streamId) {
        log.info("获取直播流实时状态，流ID: {}", streamId);
        
        LiveStream liveStream = liveStreamRepository.findById(streamId)
                .orElseThrow(() -> new IllegalArgumentException("直播流不存在"));
        
        Map<String, Object> status = new HashMap<>();
        status.put("streamId", liveStream.getId());
        status.put("streamKey", liveStream.getStreamKey());
        status.put("status", liveStream.getStatus().name());
        status.put("viewerCount", liveStream.getViewerCount());
        status.put("maxViewerCount", liveStream.getMaxViewerCount());
        
        // 从MediaMTX获取实时状态
        if (liveStream.getStatus() == LiveStream.StreamStatus.LIVE) {
            try {
                MediaMtxStreamDTO mediaMtxStream = mediaMtxService.getStream(liveStream.getStreamKey());
                if (mediaMtxStream != null) {
                    status.put("mediaMtxState", mediaMtxStream.getState());
                    status.put("mediaMtxViewerCount", mediaMtxStream.getReadersCount());
                    status.put("isRecording", mediaMtxStream.getIsRecording());
                    status.put("recordingDuration", mediaMtxStream.getRecordingDuration());
                    status.put("sourceType", mediaMtxStream.getSourceType());
                    status.put("sourceAddress", mediaMtxStream.getSourceAddress());
                }
            } catch (Exception e) {
                log.warn("获取MediaMTX流状态失败 - {}: {}", liveStream.getStreamKey(), e.getMessage());
                status.put("mediaMtxState", "unknown");
            }
        }
        
        return status;
    }
    
    /**
     * 获取流媒体服务器状态
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMediaServerStatus() {
        log.info("获取流媒体服务器状态");
        
        Map<String, Object> status = new HashMap<>();
        status.put("serverConnected", mediaMtxService.isServerConnected());
        
        if (mediaMtxService.isServerConnected()) {
            try {
                Map<String, Object> serverStatus = mediaMtxService.getServerStatus();
                status.putAll(serverStatus);
                
                // 获取活跃流数量
                List<MediaMtxStreamDTO> activeStreams = mediaMtxService.getAllStreams().stream()
                        .filter(stream -> "running".equals(stream.getState()))
                        .collect(Collectors.toList());
                status.put("activeStreamCount", activeStreams.size());
                
            } catch (Exception e) {
                log.error("获取流媒体服务器详细状态失败: {}", e.getMessage());
            }
        }
        
        return status;
    }
    
    /**
     * 生成唯一的流密钥
     */
    private String generateStreamKey() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    /**
     * 根据ID和用户ID获取直播流
     */
    private LiveStream getLiveStreamByIdAndUserId(Long streamId, Long userId) {
        LiveStream liveStream = liveStreamRepository.findById(streamId)
                .orElseThrow(() -> new IllegalArgumentException("直播流不存在"));
        
        if (!liveStream.getUserId().equals(userId)) {
            throw new SecurityException("无权操作该直播流");
        }
        
        return liveStream;
    }
}