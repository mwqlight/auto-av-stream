package com.avstream.media.repository;

import com.avstream.media.entity.MediaTranscode;
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
 * 媒体转码数据访问接口
 * 
 * @author AV Stream Team
 */
@Repository
public interface MediaTranscodeRepository extends JpaRepository<MediaTranscode, Long> {

    /**
     * 根据转码任务UUID查找转码记录
     */
    Optional<MediaTranscode> findByTranscodeUuid(String transcodeUuid);

    /**
     * 根据文件UUID查找转码记录
     */
    List<MediaTranscode> findByFileUuid(String fileUuid);

    /**
     * 根据文件UUID和状态查找转码记录
     */
    Optional<MediaTranscode> findByFileUuidAndStatus(String fileUuid, MediaTranscode.TranscodeStatus status);

    /**
     * 根据文件UUID按创建时间降序查找转码记录
     */
    List<MediaTranscode> findByFileUuidOrderByCreatedAtDesc(String fileUuid);

    /**
     * 根据媒体文件ID查找转码记录
     */
    List<MediaTranscode> findByMediaFileId(Long mediaFileId);

    /**
     * 根据媒体文件ID和状态查找转码记录
     */
    List<MediaTranscode> findByMediaFileIdAndStatus(Long mediaFileId, MediaTranscode.TranscodeStatus status);

    /**
     * 根据状态查找转码记录
     */
    List<MediaTranscode> findByStatus(MediaTranscode.TranscodeStatus status);

    /**
     * 根据用户ID查找转码记录
     */
    List<MediaTranscode> findByUserId(Long userId);

    /**
     * 根据用户ID和状态查找转码记录
     */
    List<MediaTranscode> findByUserIdAndStatus(Long userId, MediaTranscode.TranscodeStatus status);

    /**
     * 根据转码模板查找转码记录
     */
    List<MediaTranscode> findByTemplateName(String templateName);

    /**
     * 根据输出格式查找转码记录
     */
    List<MediaTranscode> findByOutputFormat(String outputFormat);

    /**
     * 根据分辨率查找转码记录
     */
    List<MediaTranscode> findByOutputWidthAndOutputHeight(Integer width, Integer height);

    /**
     * 查找待处理的转码任务
     */
    @Query("SELECT t FROM MediaTranscode t WHERE t.status = 'PENDING' AND t.enabled = true ORDER BY t.priority DESC, t.createdAt ASC")
    List<MediaTranscode> findPendingTranscodes();

    /**
     * 查找处理中的转码任务
     */
    @Query("SELECT t FROM MediaTranscode t WHERE t.status = 'PROCESSING' AND t.enabled = true")
    List<MediaTranscode> findProcessingTranscodes();

    /**
     * 查找失败的转码任务
     */
    @Query("SELECT t FROM MediaTranscode t WHERE t.status = 'FAILED' AND t.enabled = true AND t.retryCount < :maxRetryCount")
    List<MediaTranscode> findFailedTranscodesWithRetry(@Param("maxRetryCount") Integer maxRetryCount);

    /**
     * 查找超时的转码任务
     */
    @Query("SELECT t FROM MediaTranscode t WHERE t.status = 'PROCESSING' AND t.updatedAt < :timeoutThreshold")
    List<MediaTranscode> findTimeoutTranscodes(@Param("timeoutThreshold") LocalDateTime timeoutThreshold);

    /**
     * 查找高优先级的转码任务
     */
    @Query("SELECT t FROM MediaTranscode t WHERE t.priority >= :minPriority AND t.status = 'PENDING' AND t.enabled = true ORDER BY t.priority DESC, t.createdAt ASC")
    List<MediaTranscode> findHighPriorityTranscodes(@Param("minPriority") Integer minPriority);

    /**
     * 更新转码任务状态
     */
    @Modifying
    @Query("UPDATE MediaTranscode t SET t.status = :status, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") MediaTranscode.TranscodeStatus status);

    /**
     * 更新转码进度
     */
    @Modifying
    @Query("UPDATE MediaTranscode t SET t.progress = :progress, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    void updateProgress(@Param("id") Long id, @Param("progress") Integer progress);

    /**
     * 标记转码开始处理
     */
    @Modifying
    @Query("UPDATE MediaTranscode t SET t.status = 'PROCESSING', t.startedAt = CURRENT_TIMESTAMP, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    void markProcessingStarted(@Param("id") Long id);

    /**
     * 标记转码完成
     */
    @Modifying
    @Query("UPDATE MediaTranscode t SET t.status = 'COMPLETED', t.progress = 100, t.completedAt = CURRENT_TIMESTAMP, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    void markCompleted(@Param("id") Long id);

    /**
     * 标记转码失败
     */
    @Modifying
    @Query("UPDATE MediaTranscode t SET t.status = 'FAILED', t.retryCount = t.retryCount + 1, t.errorMessage = :errorMessage, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    void markFailed(@Param("id") Long id, @Param("errorMessage") String errorMessage);

    /**
     * 重置转码任务（用于重试）
     */
    @Modifying
    @Query("UPDATE MediaTranscode t SET t.status = 'PENDING', t.progress = 0, t.startedAt = null, t.completedAt = null, t.errorMessage = null, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    void resetForRetry(@Param("id") Long id);

    /**
     * 软删除转码记录
     */
    @Modifying
    @Query("UPDATE MediaTranscode t SET t.enabled = false, t.deletedAt = CURRENT_TIMESTAMP, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    void softDelete(@Param("id") Long id);

    /**
     * 恢复已删除的转码记录
     */
    @Modifying
    @Query("UPDATE MediaTranscode t SET t.enabled = true, t.deletedAt = null, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    void restore(@Param("id") Long id);

    /**
     * 统计用户转码任务数量
     */
    @Query("SELECT COUNT(t) FROM MediaTranscode t WHERE t.userId = :userId AND t.enabled = true")
    Long countByUserId(@Param("userId") Long userId);

    /**
     * 统计不同状态的转码任务数量
     */
    @Query("SELECT t.status, COUNT(t) FROM MediaTranscode t WHERE t.enabled = true GROUP BY t.status")
    List<Object[]> countByStatus();

    /**
     * 统计用户不同状态的转码任务数量
     */
    @Query("SELECT t.status, COUNT(t) FROM MediaTranscode t WHERE t.userId = :userId AND t.enabled = true GROUP BY t.status")
    List<Object[]> countByStatusAndUserId(@Param("userId") Long userId);

    /**
     * 统计转码任务的平均处理时间
     */
    @Query("SELECT AVG(TIMESTAMPDIFF(SECOND, t.startedAt, t.completedAt)) FROM MediaTranscode t WHERE t.status = 'COMPLETED' AND t.startedAt IS NOT NULL AND t.completedAt IS NOT NULL")
    Double findAverageProcessingTime();

    /**
     * 统计转码任务的成功率
     */
    @Query("SELECT COUNT(t) FROM MediaTranscode t WHERE t.status = 'COMPLETED' AND t.enabled = true")
    Long countCompletedTranscodes();

    @Query("SELECT COUNT(t) FROM MediaTranscode t WHERE t.enabled = true")
    Long countTotalTranscodes();

    /**
     * 查找最近转码的任务
     */
    @Query("SELECT t FROM MediaTranscode t WHERE t.userId = :userId AND t.enabled = true ORDER BY t.createdAt DESC")
    List<MediaTranscode> findRecentTranscodes(@Param("userId") Long userId, Pageable pageable);

    /**
     * 查找耗时最长的转码任务
     */
    @Query("SELECT t FROM MediaTranscode t WHERE t.status = 'COMPLETED' AND t.startedAt IS NOT NULL AND t.completedAt IS NOT NULL ORDER BY TIMESTAMPDIFF(SECOND, t.startedAt, t.completedAt) DESC")
    List<MediaTranscode> findLongestTranscodes(Pageable pageable);

    /**
     * 检查转码任务UUID是否存在
     */
    boolean existsByTranscodeUuid(String transcodeUuid);

    /**
     * 检查是否存在正在处理的转码任务
     */
    @Query("SELECT COUNT(t) > 0 FROM MediaTranscode t WHERE t.originalFile.id = :mediaFileId AND t.status IN ('PENDING', 'PROCESSING') AND t.enabled = true")
    boolean existsProcessingTranscodes(@Param("mediaFileId") Long mediaFileId);

    /**
     * 查找指定媒体文件的转码格式列表
     */
    @Query("SELECT DISTINCT t.outputFormat FROM MediaTranscode t WHERE t.originalFile.id = :mediaFileId AND t.status = 'COMPLETED' AND t.enabled = true")
    List<String> findTranscodedFormatsByMediaFileId(@Param("mediaFileId") Long mediaFileId);

    /**
     * 查找指定媒体文件的转码分辨率列表
     */
    @Query("SELECT DISTINCT CONCAT(t.outputWidth, 'x', t.outputHeight) FROM MediaTranscode t WHERE t.originalFile.id = :mediaFileId AND t.status = 'COMPLETED' AND t.enabled = true")
    List<String> findTranscodedResolutionsByMediaFileId(@Param("mediaFileId") Long mediaFileId);

    /**
     * 查找失败的转码任务用于重试
     */
    @Query("SELECT t FROM MediaTranscode t WHERE t.status = 'FAILED' AND t.enabled = true AND t.retryCount < :maxRetryCount AND t.updatedAt >= :threshold")
    List<MediaTranscode> findFailedTasksForRetry(@Param("threshold") LocalDateTime threshold, @Param("maxRetryCount") Integer maxRetryCount);

    /**
     * 查找过期的转码任务
     */
    @Query("SELECT t FROM MediaTranscode t WHERE t.status = 'COMPLETED' AND t.enabled = true AND t.completedAt < :threshold")
    List<MediaTranscode> findExpiredTasks(@Param("threshold") LocalDateTime threshold);

    /**
     * 查找长时间运行的转码任务
     */
    @Query("SELECT t FROM MediaTranscode t WHERE t.status = 'PROCESSING' AND t.enabled = true AND t.startedAt < :threshold")
    List<MediaTranscode> findLongRunningTasks(@Param("threshold") LocalDateTime threshold);
}