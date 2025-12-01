package com.avstream.media.repository;

import com.avstream.media.entity.MediaThumbnail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 媒体缩略图数据访问接口
 * 
 * @author AV Stream Team
 */
@Repository
public interface MediaThumbnailRepository extends JpaRepository<MediaThumbnail, Long> {

    /**
     * 根据缩略图UUID查找缩略图
     */
    Optional<MediaThumbnail> findByThumbnailUuid(String thumbnailUuid);

    /**
     * 根据媒体文件ID查找缩略图
     */
    List<MediaThumbnail> findByMediaFileId(Long mediaFileId);

    /**
     * 根据媒体文件ID和缩略图类型查找缩略图
     */
    List<MediaThumbnail> findByMediaFileIdAndThumbnailType(Long mediaFileId, MediaThumbnail.ThumbnailType thumbnailType);

    /**
     * 根据缩略图类型查找缩略图
     */
    List<MediaThumbnail> findByThumbnailType(MediaThumbnail.ThumbnailType thumbnailType);

    /**
     * 根据尺寸查找缩略图
     */
    List<MediaThumbnail> findByWidthAndHeight(Integer width, Integer height);

    /**
     * 根据媒体文件ID查找默认缩略图
     */
    @Query("SELECT t FROM MediaThumbnail t WHERE t.mediaFile.id = :mediaFileId AND t.isDefault = true")
    Optional<MediaThumbnail> findDefaultThumbnailByMediaFileId(@Param("mediaFileId") Long mediaFileId);

    /**
     * 查找指定尺寸的缩略图
     */
    @Query("SELECT t FROM MediaThumbnail t WHERE t.mediaFile.id = :mediaFileId AND t.width = :width AND t.height = :height")
    Optional<MediaThumbnail> findByMediaFileIdAndSize(
        @Param("mediaFileId") Long mediaFileId,
        @Param("width") Integer width,
        @Param("height") Integer height
    );

    /**
     * 查找最接近指定尺寸的缩略图
     */
    @Query("SELECT t FROM MediaThumbnail t WHERE t.mediaFile.id = :mediaFileId ORDER BY ABS(t.width - :targetWidth) + ABS(t.height - :targetHeight) ASC")
    List<MediaThumbnail> findClosestThumbnailBySize(
        @Param("mediaFileId") Long mediaFileId,
        @Param("targetWidth") Integer targetWidth,
        @Param("targetHeight") Integer targetHeight
    );

    /**
     * 查找大于等于指定尺寸的缩略图
     */
    @Query("SELECT t FROM MediaThumbnail t WHERE t.mediaFile.id = :mediaFileId AND t.width >= :minWidth AND t.height >= :minHeight ORDER BY t.width ASC, t.height ASC")
    List<MediaThumbnail> findThumbnailsLargerThan(
        @Param("mediaFileId") Long mediaFileId,
        @Param("minWidth") Integer minWidth,
        @Param("minHeight") Integer minHeight
    );

    /**
     * 查找小于等于指定尺寸的缩略图
     */
    @Query("SELECT t FROM MediaThumbnail t WHERE t.mediaFile.id = :mediaFileId AND t.width <= :maxWidth AND t.height <= :maxHeight ORDER BY t.width DESC, t.height DESC")
    List<MediaThumbnail> findThumbnailsSmallerThan(
        @Param("mediaFileId") Long mediaFileId,
        @Param("maxWidth") Integer maxWidth,
        @Param("maxHeight") Integer maxHeight
    );

    /**
     * 查找高质量缩略图（大于等于1080p）
     */
    @Query("SELECT t FROM MediaThumbnail t WHERE t.width >= 1920 AND t.height >= 1080")
    List<MediaThumbnail> findHighQualityThumbnails();

    /**
     * 查找标准质量缩略图（720p）
     */
    @Query("SELECT t FROM MediaThumbnail t WHERE t.width = 1280 AND t.height = 720")
    List<MediaThumbnail> findStandardQualityThumbnails();

    /**
     * 查找低质量缩略图（小于480p）
     */
    @Query("SELECT t FROM MediaThumbnail t WHERE t.width < 854 AND t.height < 480")
    List<MediaThumbnail> findLowQualityThumbnails();

    /**
     * 查找正方形缩略图
     */
    @Query("SELECT t FROM MediaThumbnail t WHERE t.width = t.height")
    List<MediaThumbnail> findSquareThumbnails();

    /**
     * 查找宽屏缩略图（宽高比大于1.5）
     */
    @Query("SELECT t FROM MediaThumbnail t WHERE t.width / t.height > 1.5")
    List<MediaThumbnail> findWidescreenThumbnails();

    /**
     * 查找竖屏缩略图（宽高比小于0.8）
     */
    @Query("SELECT t FROM MediaThumbnail t WHERE t.width / t.height < 0.8")
    List<MediaThumbnail> findPortraitThumbnails();

    /**
     * 根据文件大小范围查找缩略图
     */
    @Query("SELECT t FROM MediaThumbnail t WHERE t.fileSize >= :minSize AND t.fileSize <= :maxSize")
    List<MediaThumbnail> findByFileSizeRange(
        @Param("minSize") Long minSize,
        @Param("maxSize") Long maxSize
    );

    /**
     * 查找大文件缩略图（大于1MB）
     */
    @Query("SELECT t FROM MediaThumbnail t WHERE t.fileSize > 1048576")
    List<MediaThumbnail> findLargeThumbnails();

    /**
     * 查找小文件缩略图（小于100KB）
     */
    @Query("SELECT t FROM MediaThumbnail t WHERE t.fileSize < 102400")
    List<MediaThumbnail> findSmallThumbnails();

    /**
     * 更新缩略图为默认
     */
    @Modifying
    @Query("UPDATE MediaThumbnail t SET t.isDefault = true, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    void setAsDefault(@Param("id") Long id);

    /**
     * 取消其他缩略图的默认状态
     */
    @Modifying
    @Query("UPDATE MediaThumbnail t SET t.isDefault = false, t.updatedAt = CURRENT_TIMESTAMP WHERE t.mediaFile.id = :mediaFileId AND t.id != :currentId")
    void unsetOtherDefaults(
        @Param("mediaFileId") Long mediaFileId,
        @Param("currentId") Long currentId
    );

    /**
     * 软删除缩略图
     */
    @Modifying
    @Query("UPDATE MediaThumbnail t SET t.enabled = false, t.deletedAt = CURRENT_TIMESTAMP, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    void softDelete(@Param("id") Long id);

    /**
     * 恢复已删除的缩略图
     */
    @Modifying
    @Query("UPDATE MediaThumbnail t SET t.enabled = true, t.deletedAt = null, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    void restore(@Param("id") Long id);

    /**
     * 软删除指定媒体文件的所有缩略图
     */
    @Query("UPDATE MediaThumbnail t SET t.enabled = false, t.deletedAt = CURRENT_TIMESTAMP, t.updatedAt = CURRENT_TIMESTAMP WHERE t.mediaFile.id = :mediaFileId")
    @Modifying
    @Transactional
    void softDeleteByMediaFileId(@Param("mediaFileId") Long mediaFileId);

    /**
     * 恢复指定媒体文件的所有缩略图
     */
    @Query("UPDATE MediaThumbnail t SET t.enabled = true, t.deletedAt = null, t.updatedAt = CURRENT_TIMESTAMP WHERE t.mediaFile.id = :mediaFileId")
    @Modifying
    @Transactional
    void restoreByMediaFileId(@Param("mediaFileId") Long mediaFileId);

    /**
     * 统计指定媒体文件的缩略图数量
     */
    @Query("SELECT COUNT(t) FROM MediaThumbnail t WHERE t.mediaFile.id = :mediaFileId AND t.enabled = true")
    Long countByMediaFileId(@Param("mediaFileId") Long mediaFileId);

    /**
     * 统计不同尺寸的缩略图数量
     */
    @Query("SELECT CONCAT(t.width, 'x', t.height), COUNT(t) FROM MediaThumbnail t WHERE t.enabled = true GROUP BY t.width, t.height")
    List<Object[]> countBySize();

    /**
     * 统计不同缩略图类型的数量
     */
    @Query("SELECT t.thumbnailType, COUNT(t) FROM MediaThumbnail t WHERE t.enabled = true GROUP BY t.thumbnailType")
    List<Object[]> countByThumbnailType();

    /**
     * 统计缩略图总文件大小
     */
    @Query("SELECT SUM(t.fileSize) FROM MediaThumbnail t WHERE t.enabled = true")
    Long findTotalFileSize();

    /**
     * 统计平均缩略图文件大小
     */
    @Query("SELECT AVG(t.fileSize) FROM MediaThumbnail t WHERE t.enabled = true")
    Double findAverageFileSize();

    /**
     * 检查缩略图UUID是否存在
     */
    boolean existsByThumbnailUuid(String thumbnailUuid);

    /**
     * 检查是否存在默认缩略图
     */
    @Query("SELECT COUNT(t) > 0 FROM MediaThumbnail t WHERE t.mediaFile.id = :mediaFileId AND t.isDefault = true AND t.enabled = true")
    boolean existsDefaultThumbnailByMediaFileId(@Param("mediaFileId") Long mediaFileId);

    /**
     * 检查指定尺寸的缩略图是否存在
     */
    @Query("SELECT COUNT(t) > 0 FROM MediaThumbnail t WHERE t.mediaFile.id = :mediaFileId AND t.width = :width AND t.height = :height AND t.enabled = true")
    boolean existsByMediaFileIdAndSize(
        @Param("mediaFileId") Long mediaFileId,
        @Param("width") Integer width,
        @Param("height") Integer height
    );

    /**
     * 查找指定媒体文件的所有缩略图尺寸
     */
    @Query("SELECT DISTINCT CONCAT(t.width, 'x', t.height) FROM MediaThumbnail t WHERE t.mediaFile.id = :mediaFileId AND t.enabled = true")
    List<String> findThumbnailSizesByMediaFileId(@Param("mediaFileId") Long mediaFileId);

    /**
     * 查找指定媒体文件的所有缩略图类型
     */
    @Query("SELECT DISTINCT t.thumbnailType FROM MediaThumbnail t WHERE t.mediaFile.id = :mediaFileId AND t.enabled = true")
    List<MediaThumbnail.ThumbnailType> findThumbnailTypesByMediaFileId(@Param("mediaFileId") Long mediaFileId);
}