package com.avstream.media.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 存储服务接口
 * 
 * @author AV Stream Team
 */
public interface StorageService {

    /**
     * 上传文件
     */
    String uploadFile(MultipartFile file, String fileUuid) throws IOException;

    /**
     * 上传分片文件
     */
    void uploadChunk(MultipartFile chunk, String fileUuid, Integer chunkIndex) throws IOException;

    /**
     * 合并分片文件
     */
    String mergeChunks(String fileUuid, Integer totalChunks) throws IOException;

    /**
     * 下载文件
     */
    byte[] downloadFile(String filePath) throws IOException;

    /**
     * 删除文件
     */
    void deleteFile(String filePath) throws IOException;

    /**
     * 取消上传
     */
    void cancelUpload(String fileUuid) throws IOException;

    /**
     * 生成预览URL
     */
    String generatePreviewUrl(String fileUuid);

    /**
     * 生成播放URL
     */
    String generatePlayUrl(String fileUuid);

    /**
     * 生成下载URL
     */
    String generateDownloadUrl(String fileUuid);

    /**
     * 检查文件是否存在
     */
    boolean fileExists(String filePath);

    /**
     * 获取文件大小
     */
    long getFileSize(String filePath) throws IOException;

    /**
     * 获取文件类型
     */
    String getFileType(String filePath) throws IOException;

    /**
     * 清理过期文件
     */
    void cleanupExpiredFiles();

    /**
     * 健康检查 - 检查存储服务是否可用
     */
    boolean healthCheck();

    /**
     * 获取存储使用情况
     */
    StorageHealthInfo getStorageHealthInfo();
}