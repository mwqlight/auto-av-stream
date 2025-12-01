package com.avstream.media.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * WebRTC配置类
 */
@Slf4j
@Configuration
public class WebRTCConfig {

    @Value("${webrtc.stun-servers:stun:stun.l.google.com:19302}")
    private String stunServers;

    @Value("${webrtc.turn-servers:}")
    private String turnServers;

    @Value("${webrtc.ice-candidate-pool-size:5}")
    private int iceCandidatePoolSize;

    @Value("${webrtc.max-bandwidth:1000000}")
    private int maxBandwidth;

    @Value("${webrtc.video-codec-priority:VP8,VP9,H264}")
    private String videoCodecPriority;

    @Value("${webrtc.audio-codec-priority:opus,pcmu,pcma}")
    private String audioCodecPriority;

    @Value("${webrtc.enable-dtls:true}")
    private boolean enableDtls;

    @Value("${webrtc.enable-srtp:true}")
    private boolean enableSrtp;

    @Value("${webrtc.ice-transport-policy:all}")
    private String iceTransportPolicy;

    @Value("${webrtc.bundle-policy:balanced}")
    private String bundlePolicy;

    @Value("${webrtc.rtcp-mux-policy:require}")
    private String rtcpMuxPolicy;

    // Getters
    public String getStunServers() {
        return stunServers;
    }

    public String getTurnServers() {
        return turnServers;
    }

    public int getIceCandidatePoolSize() {
        return iceCandidatePoolSize;
    }

    public int getMaxBandwidth() {
        return maxBandwidth;
    }

    public String getVideoCodecPriority() {
        return videoCodecPriority;
    }

    public String getAudioCodecPriority() {
        return audioCodecPriority;
    }

    public boolean isEnableDtls() {
        return enableDtls;
    }

    public boolean isEnableSrtp() {
        return enableSrtp;
    }

    public String getIceTransportPolicy() {
        return iceTransportPolicy;
    }

    public String getBundlePolicy() {
        return bundlePolicy;
    }

    public String getRtcpMuxPolicy() {
        return rtcpMuxPolicy;
    }
}