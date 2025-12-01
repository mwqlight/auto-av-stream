package com.avstream.media.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "webrtc")
public class WebRTCProperties {
    
    private List<String> stunServers = List.of(
        "stun:stun.l.google.com:19302",
        "stun:stun1.l.google.com:19302"
    );
    
    private List<TurnServer> turnServers = List.of();
    
    private int iceTimeout = 30000;
    private int maxBandwidth = 1000000;
    
    private Video video = new Video();
    private Audio audio = new Audio();
    
    @Data
    public static class TurnServer {
        private String url;
        private String username;
        private String credential;
    }
    
    @Data
    public static class Video {
        private List<String> codecPriority = List.of("VP8", "H264");
        private int maxFramerate = 30;
    }
    
    @Data
    public static class Audio {
        private List<String> codecPriority = List.of("OPUS", "PCMU");
    }
}