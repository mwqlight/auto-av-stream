package com.avstream.media.exception;

/**
 * 转码异常
 */
public class TranscodeException extends MediaServiceException {
    
    public TranscodeException(int code, String message) {
        super(code, message);
    }
    
    public TranscodeException(int code, String message, String details) {
        super(code, message, details);
    }
    
    public TranscodeException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
    
    public TranscodeException(int code, String message, String details, Throwable cause) {
        super(code, message, details, cause);
    }
    
    /**
     * 转码失败异常
     */
    public static TranscodeException transcodeFailed(String filename) {
        return new TranscodeException(2001, "视频转码失败", 
            String.format("文件名: %s", filename));
    }
    
    /**
     * 转码超时异常
     */
    public static TranscodeException transcodeTimeout(String filename) {
        return new TranscodeException(2002, "转码超时", 
            String.format("文件名: %s", filename));
    }
    
    /**
     * 转码模板不存在异常
     */
    public static TranscodeException templateNotFound(String template) {
        return new TranscodeException(2003, "转码模板不存在", 
            String.format("模板名称: %s", template));
    }
    
    /**
     * 转码任务不存在异常
     */
    public static TranscodeException taskNotFound(String taskId) {
        return new TranscodeException(2004, "转码任务不存在", 
            String.format("任务ID: %s", taskId));
    }
    
    /**
     * 转码任务已存在异常
     */
    public static TranscodeException taskAlreadyExists(String taskId) {
        return new TranscodeException(2005, "转码任务已存在", 
            String.format("任务ID: %s", taskId));
    }
    
    /**
     * 转码任务队列已满异常
     */
    public static TranscodeException queueFull() {
        return new TranscodeException(2006, "转码任务队列已满");
    }
    
    /**
     * 转码格式不支持异常
     */
    public static TranscodeException formatNotSupported(String format) {
        return new TranscodeException(2007, "转码格式不支持", 
            String.format("格式: %s", format));
    }
    
    /**
     * FFmpeg执行异常
     */
    public static TranscodeException ffmpegExecutionFailed(String command) {
        return new TranscodeException(2008, "FFmpeg执行失败", 
            String.format("命令: %s", command));
    }
    
    /**
     * 缩略图生成失败异常
     */
    public static TranscodeException thumbnailGenerationFailed(String filename) {
        return new TranscodeException(2009, "缩略图生成失败", 
            String.format("文件名: %s", filename));
    }
    
    /**
     * 音频提取失败异常
     */
    public static TranscodeException audioExtractionFailed(String filename) {
        return new TranscodeException(2010, "音频提取失败", 
            String.format("文件名: %s", filename));
    }
}