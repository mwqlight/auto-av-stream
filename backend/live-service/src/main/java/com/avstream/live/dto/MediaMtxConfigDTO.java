package com.avstream.live.dto;

import lombok.Data;

/**
 * MediaMTX配置信息DTO
 */
@Data
public class MediaMtxConfigDTO {
    
    /**
     * API地址
     */
    private String apiUrl;
    
    /**
     * RTMP端口
     */
    private Integer rtmpPort;
    
    /**
     * HLS端口
     */
    private Integer hlsPort;
    
    /**
     * WebRTC端口
     */
    private Integer webrtcPort;
    
    /**
     * 录制路径
     */
    private String recordPath;
    
    /**
     * 是否启用录制
     */
    private Boolean recordEnabled;
    
    /**
     * 最大录制时长（分钟）
     */
    private Integer maxRecordDuration;
}