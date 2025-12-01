package com.avstream.media.exception;

/**
 * 流媒体异常
 */
public class StreamException extends MediaServiceException {
    
    public StreamException(int code, String message) {
        super(code, message);
    }
    
    public StreamException(int code, String message, String details) {
        super(code, message, details);
    }
    
    public StreamException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
    
    public StreamException(int code, String message, String details, Throwable cause) {
        super(code, message, details, cause);
    }
    
    /**
     * 流不存在异常
     */
    public static StreamException streamNotFound(String streamId) {
        return new StreamException(3001, "流不存在", 
            String.format("流ID: %s", streamId));
    }
    
    /**
     * 流已存在异常
     */
    public static StreamException streamAlreadyExists(String streamId) {
        return new StreamException(3002, "流已存在", 
            String.format("流ID: %s", streamId));
    }
    
    /**
     * 流创建失败异常
     */
    public static StreamException streamCreationFailed(String streamId) {
        return new StreamException(3003, "流创建失败", 
            String.format("流ID: %s", streamId));
    }
    
    /**
     * 流删除失败异常
     */
    public static StreamException streamDeletionFailed(String streamId) {
        return new StreamException(3004, "流删除失败", 
            String.format("流ID: %s", streamId));
    }
    
    /**
     * 流未激活异常
     */
    public static StreamException streamNotActive(String streamId) {
        return new StreamException(3005, "流未激活", 
            String.format("流ID: %s", streamId));
    }
    
    /**
     * 流权限不足异常
     */
    public static StreamException streamPermissionDenied(String streamId) {
        return new StreamException(3006, "流访问权限不足", 
            String.format("流ID: %s", streamId));
    }
    
    /**
     * 流格式不支持异常
     */
    public static StreamException streamFormatNotSupported(String format) {
        return new StreamException(3007, "流格式不支持", 
            String.format("格式: %s", format));
    }
    
    /**
     * 推流失败异常
     */
    public static StreamException publishFailed(String streamId) {
        return new StreamException(3008, "推流失败", 
            String.format("流ID: %s", streamId));
    }
    
    /**
     * 拉流失败异常
     */
    public static StreamException playFailed(String streamId) {
        return new StreamException(3009, "拉流失败", 
            String.format("流ID: %s", streamId));
    }
    
    /**
     * 流超时异常
     */
    public static StreamException streamTimeout(String streamId) {
        return new StreamException(3010, "流超时", 
            String.format("流ID: %s", streamId));
    }
    
    /**
     * 流带宽不足异常
     */
    public static StreamException bandwidthInsufficient(String streamId) {
        return new StreamException(3011, "流带宽不足", 
            String.format("流ID: %s", streamId));
    }
    
    /**
     * WebRTC会话创建失败异常
     */
    public static StreamException webrtcSessionCreationFailed(String sessionId) {
        return new StreamException(3012, "WebRTC会话创建失败", 
            String.format("会话ID: %s", sessionId));
    }
    
    /**
     * WebRTC会话不存在异常
     */
    public static StreamException webrtcSessionNotFound(String sessionId) {
        return new StreamException(3013, "WebRTC会话不存在", 
            String.format("会话ID: %s", sessionId));
    }
    
    /**
     * MediaMTX服务不可用异常
     */
    public static StreamException mediamtxServiceUnavailable() {
        return new StreamException(3014, "MediaMTX服务不可用");
    }
}