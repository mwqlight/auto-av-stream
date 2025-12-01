package com.avstream.media.service;

import com.avstream.media.dto.request.UploadRequest;
import com.avstream.media.dto.response.MediaInfoResponse;
import com.avstream.media.dto.response.UploadResponse;
import com.avstream.media.entity.MediaFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 媒体文件服务接口
 * 
 * @author AV Stream Team
 */
public interface MediaService {

    /**
     * 上传媒体文件
     */
    UploadResponse uploadFile(MultipartFile file, UploadRequest request, Long userId);

    /**
     * 分片上传媒体文件
     */
    UploadResponse uploadChunk(MultipartFile chunk, String fileUuid, Integer chunkIndex, Integer totalChunks, Long userId);

    /**
     * 合并分片文件
     */
    UploadResponse mergeChunks(String fileUuid, Long userId);

    /**
     * 获取媒体文件信息
     */
    MediaInfoResponse getMediaInfo(String fileUuid, Long userId);

    /**
     * 获取媒体文件信息（公开访问）
     */
    MediaInfoResponse getPublicMediaInfo(String fileUuid);

    /**
     * 获取用户媒体文件列表
     */
    Page<MediaInfoResponse> getUserMediaFiles(Long userId, Pageable pageable);

    /**
     * 搜索媒体文件
     */
    Page<MediaInfoResponse> searchMediaFiles(String keyword, Long userId, Pageable pageable);

    /**
     * 按文件类型筛选媒体文件
     */
    Page<MediaInfoResponse> filterMediaFilesByType(MediaFile.FileType fileType, Long userId, Pageable pageable);

    /**
     * 按状态筛选媒体文件
     */
    Page<MediaInfoResponse> filterMediaFilesByStatus(MediaFile.MediaStatus status, Long userId, Pageable pageable);

    /**
     * 更新媒体文件信息
     */
    MediaInfoResponse updateMediaInfo(String fileUuid, String filename, String description, Boolean isPublic, Long userId);

    /**
     * 删除媒体文件
     */
    void deleteMediaFile(String fileUuid, Long userId);

    /**
     * 批量删除媒体文件
     */
    void batchDeleteMediaFiles(List<String> fileUuids, Long userId);

    /**
     * 恢复已删除的媒体文件
     */
    void restoreMediaFile(String fileUuid, Long userId);

    /**
     * 永久删除媒体文件
     */
    void permanentDeleteMediaFile(String fileUuid, Long userId);

    /**
     * 下载媒体文件
     */
    MediaFile downloadMediaFile(String fileUuid, Long userId);

    /**
     * 下载媒体文件（公开访问）
     */
    MediaFile downloadPublicMediaFile(String fileUuid);

    /**
     * 获取媒体文件预览URL
     */
    String getPreviewUrl(String fileUuid, Long userId);

    /**
     * 获取媒体文件播放URL
     */
    String getPlayUrl(String fileUuid, Long userId);

    /**
     * 获取媒体文件下载URL
     */
    String getDownloadUrl(String fileUuid, Long userId);

    /**
     * 检查文件上传权限
     */
    boolean checkUploadPermission(Long userId);

    /**
     * 检查文件大小限制
     */
    boolean checkFileSizeLimit(Long fileSize, Long userId);

    /**
     * 检查文件类型支持
     */
    boolean checkFileTypeSupport(String filename);

    /**
     * 获取用户存储使用情况
     */
    StorageUsage getUserStorageUsage(Long userId);

    /**
     * 清理过期临时文件
     */
    void cleanupExpiredTemporaryFiles();

    /**
     * 清理过期媒体文件
     */
    void cleanupExpiredMediaFiles();

    /**
     * 健康检查 - 检查媒体服务是否可用
     */
    boolean healthCheck();

    /**
     * 获取媒体服务健康信息
     */
    MediaHealthInfo getMediaHealthInfo();

    /**
     * 处理上传完成的文件
     */
    void processUploadedFile(String fileUuid);

    /**
     * 处理上传失败的文件
     */
    void handleUploadFailed(String fileUuid, String errorMessage);

    /**
     * 获取上传进度
     */
    Integer getUploadProgress(String fileUuid, Long userId);

    /**
     * 取消上传
     */
    void cancelUpload(String fileUuid, Long userId);

    /**
     * 重新处理上传失败的文件
     */
    void retryFailedUpload(String fileUuid, Long userId);

    /**
     * 统计用户文件数量
     */
    Long countUserFiles(Long userId);

    /**
     * 统计用户文件总大小
     */
    Long sumUserFileSize(Long userId);

    /**
     * 获取热门媒体文件
     */
    List<MediaInfoResponse> getPopularMediaFiles(int limit);

    /**
     * 获取最近上传的媒体文件
     */
    List<MediaInfoResponse> getRecentMediaFiles(int limit);

    /**
     * 获取媒体文件路径
     */
    String getFilePath(String fileUuid, Long userId);

    /**
     * 存储使用情况类
     */
    class StorageUsage {
        private Long usedStorage;
        private Long totalStorage;
        private Double usagePercentage;

        public StorageUsage(Long usedStorage, Long totalStorage) {
            this.usedStorage = usedStorage;
            this.totalStorage = totalStorage;
            this.usagePercentage = totalStorage > 0 ? (usedStorage.doubleValue() / totalStorage.doubleValue()) * 100 : 0.0;
        }

        // Getters and Setters
        public Long getUsedStorage() { return usedStorage; }
        public void setUsedStorage(Long usedStorage) { this.usedStorage = usedStorage; }

        public Long getTotalStorage() { return totalStorage; }
        public void setTotalStorage(Long totalStorage) { this.totalStorage = totalStorage; }

        public Double getUsagePercentage() { return usagePercentage; }
        public void setUsagePercentage(Double usagePercentage) { this.usagePercentage = usagePercentage; }
    }
}