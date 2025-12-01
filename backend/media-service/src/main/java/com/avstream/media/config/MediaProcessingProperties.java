package com.avstream.media.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "media.processing")
public class MediaProcessingProperties {
    
    private Video video = new Video();
    private Audio audio = new Audio();
    private Image image = new Image();
    private Transcoding transcoding = new Transcoding();
    
    @Data
    public static class Video {
        private String maxFileSize = "2GB";
        private List<String> allowedFormats = List.of("mp4", "avi", "mov", "mkv", "webm");
        private String maxResolution = "3840x2160";
        private String defaultBitrate = "8M";
        private Thumbnail thumbnail = new Thumbnail();
        
        @Data
        public static class Thumbnail {
            private int width = 320;
            private int height = 180;
            private String format = "jpeg";
        }
    }
    
    @Data
    public static class Audio {
        private String maxFileSize = "500MB";
        private List<String> allowedFormats = List.of("mp3", "wav", "flac", "aac", "ogg");
        private String defaultBitrate = "320k";
    }
    
    @Data
    public static class Image {
        private String maxFileSize = "100MB";
        private List<String> allowedFormats = List.of("jpg", "jpeg", "png", "gif", "webp");
        private String maxResolution = "8192x8192";
    }
    
    @Data
    public static class Transcoding {
        private boolean enabled = true;
        private int parallelJobs = 3;
        private String qualityPreset = "medium";
        private String hwAcceleration = "auto";
    }
}