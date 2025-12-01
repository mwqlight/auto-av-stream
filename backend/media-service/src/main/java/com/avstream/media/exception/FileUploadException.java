package com.avstream.media.exception;

/**
 * 文件上传异常
 */
public class FileUploadException extends MediaServiceException {
    
    public FileUploadException(int code, String message) {
        super(code, message);
    }
    
    public FileUploadException(int code, String message, String details) {
        super(code, message, details);
    }
    
    public FileUploadException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
    
    public FileUploadException(int code, String message, String details, Throwable cause) {
        super(code, message, details, cause);
    }
    
    /**
     * 文件大小超过限制异常
     */
    public static FileUploadException fileSizeExceeded(long maxSize) {
        return new FileUploadException(1001, "文件大小超过限制", 
            String.format("最大允许大小: %d bytes", maxSize));
    }
    
    /**
     * 文件类型不支持异常
     */
    public static FileUploadException fileTypeNotSupported(String fileType) {
        return new FileUploadException(1002, "文件类型不支持", 
            String.format("不支持的文件类型: %s", fileType));
    }
    
    /**
     * 上传失败异常
     */
    public static FileUploadException uploadFailed(String filename) {
        return new FileUploadException(1003, "文件上传失败", 
            String.format("文件名: %s", filename));
    }
    
    /**
     * 分片上传异常
     */
    public static FileUploadException chunkUploadFailed(String filename, int chunkIndex) {
        return new FileUploadException(1004, "分片上传失败", 
            String.format("文件名: %s, 分片索引: %d", filename, chunkIndex));
    }
    
    /**
     * 分片合并异常
     */
    public static FileUploadException chunkMergeFailed(String filename) {
        return new FileUploadException(1005, "分片合并失败", 
            String.format("文件名: %s", filename));
    }
    
    /**
     * 存储空间不足异常
     */
    public static FileUploadException storageSpaceInsufficient() {
        return new FileUploadException(1006, "存储空间不足");
    }
    
    /**
     * 上传超时异常
     */
    public static FileUploadException uploadTimeout(String filename) {
        return new FileUploadException(1007, "上传超时", 
            String.format("文件名: %s", filename));
    }
}