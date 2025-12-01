package com.avstream.live.exception;

/**
 * 资源未找到异常
 * 
 * @author AV Stream Team
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}