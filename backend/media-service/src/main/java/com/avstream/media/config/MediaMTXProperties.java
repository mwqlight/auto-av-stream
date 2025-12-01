package com.avstream.media.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mediamtx")
public class MediaMTXProperties {
    
    private String baseUrl = "http://localhost:9997";
    private String apiPath = "/v2";
    private Rtmp rtmp = new Rtmp();
    private Hls hls = new Hls();
    private WebRTC webrtc = new WebRTC();
    private Stream stream = new Stream();
    
    // Getter and Setter methods
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    
    public String getApiPath() { return apiPath; }
    public void setApiPath(String apiPath) { this.apiPath = apiPath; }
    
    public Rtmp getRtmp() { return rtmp; }
    public void setRtmp(Rtmp rtmp) { this.rtmp = rtmp; }
    
    public Hls getHls() { return hls; }
    public void setHls(Hls hls) { this.hls = hls; }
    
    public WebRTC getWebRTC() { return webrtc; }
    public void setWebRTC(WebRTC webrtc) { this.webrtc = webrtc; }
    
    public Stream getStream() { return stream; }
    public void setStream(Stream stream) { this.stream = stream; }
    
    public static class Rtmp {
        private int port = 1935;
        private boolean enabled = true;
        
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
    
    public static class Hls {
        private int port = 8888;
        private boolean enabled = true;
        
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
    
    public static class WebRTC {
        private int port = 8889;
        private boolean enabled = true;
        
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
    
    @Data
    public static class Stream {
        private String defaultTimeout = "30s";
        private int maxConnections = 100;
    }
    
    public String getApiUrl() {
        return baseUrl + apiPath;
    }
    
    public String getRtmpUrl() {
        return "rtmp://localhost:" + rtmp.getPort();
    }
    
    public String getHlsUrl() {
        return "http://localhost:" + hls.getPort();
    }
    
    public String getWebRTCUrl() {
        return "http://localhost:" + webrtc.getPort();
    }
}