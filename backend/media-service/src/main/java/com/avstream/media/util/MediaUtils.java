package com.avstream.media.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 媒体工具类
 */
public class MediaUtils {

    private static final Logger log = LoggerFactory.getLogger(MediaUtils.class);

    private MediaUtils() {
        // 工具类，防止实例化
    }

    /**
     * 验证流ID格式
     */
    public static boolean isValidStreamId(String streamId) {
        if (!StringUtils.hasText(streamId)) {
            return false;
        }
        
        // 流ID只能包含字母、数字、下划线和连字符，长度3-50
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]{3,50}$");
        return pattern.matcher(streamId).matches();
    }

    /**
     * 生成安全的流ID
     */
    public static String generateSafeStreamId(String input) {
        if (!StringUtils.hasText(input)) {
            return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        }
        
        // 移除特殊字符，只保留字母、数字、下划线和连字符
        String safeId = input.replaceAll("[^a-zA-Z0-9_-]", "-");
        
        // 限制长度
        if (safeId.length() > 50) {
            safeId = safeId.substring(0, 50);
        }
        
        // 确保不以连字符或下划线开头
        safeId = safeId.replaceAll("^[-_]+", "");
        
        // 如果为空，生成随机ID
        if (!StringUtils.hasText(safeId)) {
            safeId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        }
        
        return safeId.toLowerCase();
    }

    /**
     * 计算视频比特率
     */
    public static int calculateVideoBitrate(int width, int height, double frameRate) {
        // 简单的比特率计算算法
        int baseBitrate = 500; // kbps
        int resolutionFactor = (width * height) / (640 * 480); // 相对于640x480的倍数
        int frameRateFactor = (int) (frameRate / 30.0); // 相对于30fps的倍数
        
        return baseBitrate * resolutionFactor * frameRateFactor;
    }

    /**
     * 获取视频分辨率等级
     */
    public static String getResolutionLevel(int width, int height) {
        if (width >= 3840 && height >= 2160) {
            return "4K";
        } else if (width >= 1920 && height >= 1080) {
            return "1080p";
        } else if (width >= 1280 && height >= 720) {
            return "720p";
        } else if (width >= 854 && height >= 480) {
            return "480p";
        } else if (width >= 640 && height >= 360) {
            return "360p";
        } else {
            return "SD";
        }
    }

    /**
     * 格式化时长（秒转时分秒）
     */
    public static String formatDuration(long seconds) {
        if (seconds <= 0) {
            return "00:00";
        }
        
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%02d:%02d", minutes, secs);
        }
    }

    /**
     * 解析时长（时分秒转秒）
     */
    public static long parseDuration(String duration) {
        if (!StringUtils.hasText(duration)) {
            return 0;
        }
        
        try {
            String[] parts = duration.split(":");
            if (parts.length == 3) {
                // 时:分:秒
                long hours = Long.parseLong(parts[0]);
                long minutes = Long.parseLong(parts[1]);
                long seconds = Long.parseLong(parts[2]);
                return hours * 3600 + minutes * 60 + seconds;
            } else if (parts.length == 2) {
                // 分:秒
                long minutes = Long.parseLong(parts[0]);
                long seconds = Long.parseLong(parts[1]);
                return minutes * 60 + seconds;
            } else {
                // 只有秒
                return Long.parseLong(duration);
            }
        } catch (NumberFormatException e) {
            log.warn("解析时长失败: {}", duration);
            return 0;
        }
    }

    /**
     * 计算文件上传预估时间
     */
    public static String estimateUploadTime(long fileSize, double uploadSpeedMbps) {
        if (fileSize <= 0 || uploadSpeedMbps <= 0) {
            return "未知";
        }
        
        // 转换为MB
        double fileSizeMB = fileSize / (1024.0 * 1024.0);
        
        // 计算时间（秒）
        double timeSeconds = (fileSizeMB * 8) / uploadSpeedMbps;
        
        if (timeSeconds < 60) {
            return String.format("%.0f秒", timeSeconds);
        } else if (timeSeconds < 3600) {
            return String.format("%.1f分钟", timeSeconds / 60);
        } else {
            return String.format("%.1f小时", timeSeconds / 3600);
        }
    }

    /**
     * 检查是否为直播流URL
     */
    public static boolean isLiveStreamUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return false;
        }
        
        String lowerUrl = url.toLowerCase();
        return lowerUrl.startsWith("rtmp://") || 
               lowerUrl.startsWith("rtsp://") ||
               lowerUrl.contains(".m3u8") ||
               lowerUrl.contains("/webrtc");
    }

    /**
     * 获取流协议类型
     */
    public static String getStreamProtocol(String url) {
        if (!StringUtils.hasText(url)) {
            return "unknown";
        }
        
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.startsWith("rtmp://")) {
            return "rtmp";
        } else if (lowerUrl.startsWith("rtsp://")) {
            return "rtsp";
        } else if (lowerUrl.contains(".m3u8")) {
            return "hls";
        } else if (lowerUrl.contains("/webrtc")) {
            return "webrtc";
        } else if (lowerUrl.startsWith("http://") || lowerUrl.startsWith("https://")) {
            return "http";
        } else {
            return "unknown";
        }
    }

    /**
     * 生成播放令牌（用于安全访问）
     */
    public static String generatePlayToken(String streamId, long expireTime) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String data = streamId + "|" + timestamp + "|" + expireTime;
        
        // 简单的哈希生成（实际项目中应使用更安全的算法）
        String hash = Integer.toHexString(data.hashCode());
        
        return streamId + "_" + timestamp + "_" + hash;
    }

    /**
     * 验证播放令牌
     */
    public static boolean validatePlayToken(String token, String streamId) {
        if (!StringUtils.hasText(token) || !StringUtils.hasText(streamId)) {
            return false;
        }
        
        try {
            String[] parts = token.split("_");
            if (parts.length != 3) {
                return false;
            }
            
            String tokenStreamId = parts[0];
            String timestamp = parts[1];
            String hash = parts[2];
            
            // 检查流ID是否匹配
            if (!streamId.equals(tokenStreamId)) {
                return false;
            }
            
            // 检查时间戳是否有效
            long tokenTime = Long.parseLong(timestamp);
            long currentTime = System.currentTimeMillis();
            
            // 假设令牌有效期为1小时
            if (currentTime - tokenTime > 3600000) {
                return false;
            }
            
            // 验证哈希
            String expectedData = streamId + "|" + timestamp + "|" + 3600000;
            String expectedHash = Integer.toHexString(expectedData.hashCode());
            
            return hash.equals(expectedHash);
            
        } catch (Exception e) {
            log.warn("验证播放令牌失败: {}", token);
            return false;
        }
    }

    /**
     * 计算视频质量评分
     */
    public static int calculateVideoQualityScore(int width, int height, double frameRate, String codec) {
        int score = 0;
        
        // 分辨率评分
        if (width >= 3840 && height >= 2160) {
            score += 100; // 4K
        } else if (width >= 1920 && height >= 1080) {
            score += 80; // 1080p
        } else if (width >= 1280 && height >= 720) {
            score += 60; // 720p
        } else if (width >= 854 && height >= 480) {
            score += 40; // 480p
        } else {
            score += 20; // SD
        }
        
        // 帧率评分
        if (frameRate >= 60) {
            score += 20;
        } else if (frameRate >= 30) {
            score += 15;
        } else if (frameRate >= 24) {
            score += 10;
        } else {
            score += 5;
        }
        
        // 编码器评分
        if ("h265".equalsIgnoreCase(codec) || "hevc".equalsIgnoreCase(codec)) {
            score += 15;
        } else if ("h264".equalsIgnoreCase(codec) || "avc".equalsIgnoreCase(codec)) {
            score += 10;
        } else {
            score += 5;
        }
        
        return score;
    }
}