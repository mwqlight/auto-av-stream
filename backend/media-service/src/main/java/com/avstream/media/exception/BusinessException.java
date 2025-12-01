package com.avstream.media.exception;

/**
 * 业务异常类
 * 
 * @author AV Stream Team
 */
public class BusinessException extends RuntimeException {
    
    private final String code;
    private final String message;

    public BusinessException(String message) {
        super(message);
        this.code = "MEDIA_ERROR";
        this.message = message;
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = "MEDIA_ERROR";
        this.message = message;
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 媒体文件相关错误码
     */
    public static class ErrorCodes {
        public static final String FILE_NOT_FOUND = "MEDIA_FILE_NOT_FOUND";
        public static final String FILE_UPLOAD_FAILED = "MEDIA_UPLOAD_FAILED";
        public static final String FILE_DELETE_FAILED = "MEDIA_DELETE_FAILED";
        public static final String FILE_ACCESS_DENIED = "MEDIA_ACCESS_DENIED";
        public static final String FILE_SIZE_EXCEEDED = "MEDIA_SIZE_EXCEEDED";
        public static final String FILE_TYPE_UNSUPPORTED = "MEDIA_TYPE_UNSUPPORTED";
        public static final String STORAGE_LIMIT_EXCEEDED = "STORAGE_LIMIT_EXCEEDED";
        public static final String UPLOAD_PERMISSION_DENIED = "UPLOAD_PERMISSION_DENIED";
        public static final String TRANSCODE_FAILED = "TRANSCODE_FAILED";
        public static final String METADATA_EXTRACTION_FAILED = "METADATA_EXTRACTION_FAILED";
        public static final String THUMBNAIL_GENERATION_FAILED = "THUMBNAIL_GENERATION_FAILED";
        
        /**
         * 创建文件不存在异常
         */
        public static BusinessException fileNotFound(String fileUuid) {
            return new BusinessException(FILE_NOT_FOUND, "文件不存在: " + fileUuid);
        }
        
        /**
         * 创建文件上传失败异常
         */
        public static BusinessException fileUploadFailed(String filename) {
            return new BusinessException(FILE_UPLOAD_FAILED, "文件上传失败: " + filename);
        }
        
        /**
         * 创建文件删除失败异常
         */
        public static BusinessException fileDeleteFailed(String fileUuid) {
            return new BusinessException(FILE_DELETE_FAILED, "文件删除失败: " + fileUuid);
        }
        
        /**
         * 创建文件访问拒绝异常
         */
        public static BusinessException fileAccessDenied(String fileUuid) {
            return new BusinessException(FILE_ACCESS_DENIED, "无权访问文件: " + fileUuid);
        }
        
        /**
         * 创建文件大小超出限制异常
         */
        public static BusinessException fileSizeExceeded(Long fileSize, Long maxSize) {
            return new BusinessException(FILE_SIZE_EXCEEDED, 
                String.format("文件大小超出限制: %d > %d", fileSize, maxSize));
        }
        
        /**
         * 创建文件类型不支持异常
         */
        public static BusinessException fileTypeUnsupported(String fileType) {
            return new BusinessException(FILE_TYPE_UNSUPPORTED, "不支持的文件类型: " + fileType);
        }
        
        /**
         * 创建存储空间不足异常
         */
        public static BusinessException storageLimitExceeded(Long used, Long total) {
            return new BusinessException(STORAGE_LIMIT_EXCEEDED, 
                String.format("存储空间不足: 已使用 %d/%d", used, total));
        }
        
        /**
         * 创建上传权限拒绝异常
         */
        public static BusinessException uploadPermissionDenied() {
            return new BusinessException(UPLOAD_PERMISSION_DENIED, "上传权限不足");
        }
        
        /**
         * 创建转码失败异常
         */
        public static BusinessException transcodeFailed(String transcodeUuid) {
            return new BusinessException(TRANSCODE_FAILED, "转码失败: " + transcodeUuid);
        }
        
        /**
         * 创建元数据提取失败异常
         */
        public static BusinessException metadataExtractionFailed(String fileUuid) {
            return new BusinessException(METADATA_EXTRACTION_FAILED, "元数据提取失败: " + fileUuid);
        }
        
        /**
         * 创建缩略图生成失败异常
         */
        public static BusinessException thumbnailGenerationFailed(String fileUuid) {
            return new BusinessException(THUMBNAIL_GENERATION_FAILED, "缩略图生成失败: " + fileUuid);
        }
    }
}