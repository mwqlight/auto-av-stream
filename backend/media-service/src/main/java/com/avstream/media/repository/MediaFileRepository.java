package com.avstream.media.repository;

import com.avstream.media.entity.MediaFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 媒体文件数据访问接口
 * 
 * @author AV Stream Team
 */
@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

    /**
     * 根据文件UUID查找媒体文件
     */
    Optional<MediaFile> findByFileUuid(String fileUuid);

    /**
     * 根据用户ID查找媒体文件
     */
    List<MediaFile> findByUserId(Long userId);

    /**
     * 根据用户ID分页查找媒体文件
     */
    Page<MediaFile> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据文件类型查找媒体文件
     */
    List<MediaFile> findByFileType(MediaFile.FileType fileType);

    /**
     * 根据状态查找媒体文件
     */
    List<MediaFile> findByStatus(MediaFile.MediaStatus status);

    /**
     * 根据是否公开查找媒体文件
     */
    List<MediaFile> findByIsPublic(Boolean isPublic);

    /**
     * 根据是否启用查找媒体文件
     */
    List<MediaFile> findByEnabled(Boolean enabled);

    /**
     * 根据文件名模糊查找媒体文件
     */
    List<MediaFile> findByFilenameContainingIgnoreCase(String filename);

    /**
     * 根据用户ID和文件类型查找媒体文件
     */
    List<MediaFile> findByUserIdAndFileType(Long userId, MediaFile.FileType fileType);

    /**
     * 根据用户ID和状态查找媒体文件
     */
    List<MediaFile> findByUserIdAndStatus(Long userId, MediaFile.MediaStatus status);

    /**
     * 根据用户ID和是否公开查找媒体文件
     */
    List<MediaFile> findByUserIdAndIsPublic(Long userId, Boolean isPublic);

    /**
     * 查找需要处理的媒体文件
     */
    @Query("SELECT m FROM MediaFile m WHERE m.status = 'UPLOADED' AND m.enabled = true")
    List<MediaFile> findFilesNeedProcessing();

    /**
     * 查找处理失败的媒体文件
     */
    @Query("SELECT m FROM MediaFile m WHERE m.status = 'FAILED' AND m.enabled = true")
    List<MediaFile> findFailedFiles();

    /**
     * 查找上传中的媒体文件
     */
    @Query("SELECT m FROM MediaFile m WHERE m.status = 'UPLOADING' AND m.updatedAt < :threshold")
    List<MediaFile> findStuckUploads(@Param("threshold") LocalDateTime threshold);

    /**
     * 更新媒体文件状态
     */
    @Modifying
    @Query("UPDATE MediaFile m SET m.status = :status, m.updatedAt = CURRENT_TIMESTAMP WHERE m.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") MediaFile.MediaStatus status);

    /**
     * 更新上传进度
     */
    @Modifying
    @Query("UPDATE MediaFile m SET m.uploadProgress = :progress, m.updatedAt = CURRENT_TIMESTAMP WHERE m.id = :id")
    void updateUploadProgress(@Param("id") Long id, @Param("progress") Integer progress);

    /**
     * 标记上传完成
     */
    @Modifying
    @Query("UPDATE MediaFile m SET m.status = 'UPLOADED', m.uploadProgress = 100, m.uploadCompletedAt = CURRENT_TIMESTAMP, m.updatedAt = CURRENT_TIMESTAMP WHERE m.id = :id")
    void markUploadCompleted(@Param("id") Long id);

    /**
     * 标记处理完成
     */
    @Modifying
    @Query("UPDATE MediaFile m SET m.status = 'PROCESSED', m.processingCompletedAt = CURRENT_TIMESTAMP, m.updatedAt = CURRENT_TIMESTAMP WHERE m.id = :id")
    void markProcessingCompleted(@Param("id") Long id);

    /**
     * 标记处理失败
     */
    @Modifying
    @Query("UPDATE MediaFile m SET m.status = 'FAILED', m.updatedAt = CURRENT_TIMESTAMP WHERE m.id = :id")
    void markProcessingFailed(@Param("id") Long id);

    /**
     * 软删除媒体文件
     */
    @Modifying
    @Query("UPDATE MediaFile m SET m.enabled = false, m.deletedAt = CURRENT_TIMESTAMP, m.updatedAt = CURRENT_TIMESTAMP WHERE m.id = :id")
    void softDelete(@Param("id") Long id);

    /**
     * 恢复已删除的媒体文件
     */
    @Modifying
    @Query("UPDATE MediaFile m SET m.enabled = true, m.deletedAt = null, m.updatedAt = CURRENT_TIMESTAMP WHERE m.id = :id")
    void restore(@Param("id") Long id);

    /**
     * 统计用户文件数量
     */
    @Query("SELECT COUNT(m) FROM MediaFile m WHERE m.userId = :userId AND m.enabled = true")
    Long countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户文件总大小
     */
    @Query("SELECT COALESCE(SUM(m.fileSize), 0) FROM MediaFile m WHERE m.userId = :userId AND m.enabled = true")
    Long sumFileSizeByUserId(@Param("userId") Long userId);

    /**
     * 根据文件类型统计用户文件数量
     */
    @Query("SELECT m.fileType, COUNT(m) FROM MediaFile m WHERE m.userId = :userId AND m.enabled = true GROUP BY m.fileType")
    List<Object[]> countByFileTypeAndUserId(@Param("userId") Long userId);

    /**
     * 查找最近上传的文件
     */
    @Query("SELECT m FROM MediaFile m WHERE m.userId = :userId AND m.enabled = true ORDER BY m.createdAt DESC")
    List<MediaFile> findRecentFiles(@Param("userId") Long userId, Pageable pageable);

    /**
     * 查找热门文件（按播放次数排序）
     */
    @Query("SELECT m FROM MediaFile m WHERE m.isPublic = true AND m.enabled = true ORDER BY m.createdAt DESC")
    List<MediaFile> findPopularFiles(Pageable pageable);

    /**
     * 检查文件UUID是否存在
     */
    boolean existsByFileUuid(String fileUuid);

    /**
     * 检查文件名是否存在（同一用户）
     */
    boolean existsByUserIdAndFilename(Long userId, String filename);

    /**
     * 查找超过指定时间的临时文件
     */
    @Query("SELECT m FROM MediaFile m WHERE m.isTemporary = true AND m.createdAt < :threshold AND m.enabled = true")
    List<MediaFile> findTemporaryFilesOlderThan(@Param("threshold") LocalDateTime threshold);

    /**
     * 计算总存储使用量
     */
    @Query("SELECT COALESCE(SUM(m.fileSize), 0) FROM MediaFile m WHERE m.enabled = true")
    Long calculateTotalStorageUsage();

    /**
     * 根据文件类型统计文件数量
     */
    @Query("SELECT COUNT(m) FROM MediaFile m WHERE m.fileType = :fileType AND m.enabled = true")
    Long countByFileType(@Param("fileType") String fileType);
}