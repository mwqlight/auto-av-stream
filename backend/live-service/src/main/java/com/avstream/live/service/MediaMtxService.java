package com.avstream.live.service;

import com.avstream.live.dto.MediaMtxConfigDTO;
import com.avstream.live.dto.MediaMtxStreamDTO;

import java.util.List;
import java.util.Map;

/**
 * MediaMTX流媒体服务器服务接口
 */
public interface MediaMtxService {
    
    /**
     * 获取所有流列表
     */
    List<MediaMtxStreamDTO> getAllStreams();
    
    /**
     * 获取特定流信息
     */
    MediaMtxStreamDTO getStream(String streamKey);
    
    /**
     * 检查流是否存在
     */
    boolean streamExists(String streamKey);
    
    /**
     * 检查流是否正在推流
     */
    boolean isStreamActive(String streamKey);
    
    /**
     * 获取流状态
     */
    String getStreamState(String streamKey);
    
    /**
     * 获取当前观看人数
     */
    Integer getViewerCount(String streamKey);
    
    /**
     * 开始录制流
     */
    boolean startRecording(String streamKey);
    
    /**
     * 停止录制流
     */
    boolean stopRecording(String streamKey);
    
    /**
     * 获取录制状态
     */
    boolean isRecording(String streamKey);
    
    /**
     * 获取录制时长（秒）
     */
    Long getRecordingDuration(String streamKey);
    
    /**
     * 获取服务器配置
     */
    MediaMtxConfigDTO getServerConfig();
    
    /**
     * 获取服务器状态
     */
    Map<String, Object> getServerStatus();
    
    /**
     * 检查服务器连接状态
     */
    boolean isServerConnected();
}