package com.avstream.live.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MediaMTX流媒体服务器配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "live.media-server.mediamtx")
public class MediaMtxProperties {
    
    /**
     * MediaMTX API地址
     */
    private String apiUrl = "http://localhost:9997";
    
    /**
     * RTMP端口
     */
    private Integer rtmpPort = 1935;
    
    /**
     * HLS端口
     */
    private Integer hlsPort = 8080;
    
    /**
     * WebRTC端口
     */
    private Integer webrtcPort = 8889;
    
    /**
     * API超时时间（毫秒）
     */
    private Integer timeout = 5000;
    
    /**
     * 重试次数
     */
    private Integer retryCount = 3;
    
    /**
     * 重试间隔（毫秒）
     */
    private Integer retryInterval = 1000;
}