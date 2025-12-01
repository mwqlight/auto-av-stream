package com.avstream.media.exception;

import lombok.Getter;

/**
 * 媒体服务异常基类
 */
@Getter
public class MediaServiceException extends RuntimeException {
    
    /** 错误码 */
    private final int code;
    
    /** 错误信息 */
    private final String message;
    
    /** 错误详情 */
    private final String details;
    
    public MediaServiceException(int code, String message) {
        this(code, message, (String) null);
    }
    
    public MediaServiceException(int code, String message, String details) {
        super(message);
        this.code = code;
        this.message = message;
        this.details = details;
    }
    
    public MediaServiceException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.details = cause != null ? cause.getMessage() : null;
    }
    
    public MediaServiceException(int code, String message, String details, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.details = details;
    }
    
    /**
     * 获取完整的错误信息
     */
    public String getFullMessage() {
        if (details != null && !details.isEmpty()) {
            return String.format("[%d] %s - %s", code, message, details);
        } else {
            return String.format("[%d] %s", code, message);
        }
    }
}