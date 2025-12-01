package com.avstream.media.repository;

import com.avstream.media.entity.MediaMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 媒体元数据数据访问接口
 * 
 * @author AV Stream Team
 */
@Repository
public interface MediaMetadataRepository extends JpaRepository<MediaMetadata, Long> {

    /**
     * 根据媒体文件ID查找元数据
     */
    Optional<MediaMetadata> findByMediaFileId(Long mediaFileId);

    /**
     * 根据视频编码格式查找元数据
     */
    List<MediaMetadata> findByVideoCodec(String videoCodec);

    /**
     * 根据音频编码格式查找元数据
     */
    List<MediaMetadata> findByAudioCodec(String audioCodec);

    /**
     * 根据分辨率查找元数据
     */
    List<MediaMetadata> findByWidthAndHeight(Integer width, Integer height);

    /**
     * 查找指定分辨率范围内的元数据
     */
    @Query("SELECT m FROM MediaMetadata m WHERE m.width >= :minWidth AND m.height >= :minHeight AND m.width <= :maxWidth AND m.height <= :maxHeight")
    List<MediaMetadata> findByResolutionRange(
        @Param("minWidth") Integer minWidth,
        @Param("minHeight") Integer minHeight,
        @Param("maxWidth") Integer maxWidth,
        @Param("maxHeight") Integer maxHeight
    );

    /**
     * 查找高分辨率元数据（大于等于1080p）
     */
    @Query("SELECT m FROM MediaMetadata m WHERE m.width >= 1920 AND m.height >= 1080")
    List<MediaMetadata> findHighResolutionMetadata();

    /**
     * 查找低分辨率元数据（小于720p）
     */
    @Query("SELECT m FROM MediaMetadata m WHERE m.width < 1280 AND m.height < 720")
    List<MediaMetadata> findLowResolutionMetadata();

    /**
     * 根据帧率范围查找元数据
     */
    @Query("SELECT m FROM MediaMetadata m WHERE m.frameRate >= :minFrameRate AND m.frameRate <= :maxFrameRate")
    List<MediaMetadata> findByFrameRateRange(
        @Param("minFrameRate") Double minFrameRate,
        @Param("maxFrameRate") Double maxFrameRate
    );

    /**
     * 根据比特率范围查找元数据
     */
    @Query("SELECT m FROM MediaMetadata m WHERE m.bitrate >= :minBitrate AND m.bitrate <= :maxBitrate")
    List<MediaMetadata> findByBitrateRange(
        @Param("minBitrate") Long minBitrate,
        @Param("maxBitrate") Long maxBitrate
    );

    /**
     * 查找高比特率元数据
     */
    @Query("SELECT m FROM MediaMetadata m WHERE m.bitrate > :threshold")
    List<MediaMetadata> findHighBitrateMetadata(@Param("threshold") Long threshold);

    /**
     * 根据时长范围查找元数据
     */
    @Query("SELECT m FROM MediaMetadata m WHERE m.duration >= :minDuration AND m.duration <= :maxDuration")
    List<MediaMetadata> findByDurationRange(
        @Param("minDuration") Double minDuration,
        @Param("maxDuration") Double maxDuration
    );

    /**
     * 查找长视频元数据（大于10分钟）
     */
    @Query("SELECT m FROM MediaMetadata m WHERE m.duration > 600")
    List<MediaMetadata> findLongVideoMetadata();

    /**
     * 查找短视频元数据（小于3分钟）
     */
    @Query("SELECT m FROM MediaMetadata m WHERE m.duration < 180")
    List<MediaMetadata> findShortVideoMetadata();

    /**
     * 根据文件大小范围查找元数据
     */
    @Query("SELECT m FROM MediaMetadata m WHERE m.fileSize >= :minSize AND m.fileSize <= :maxSize")
    List<MediaMetadata> findByFileSizeRange(
        @Param("minSize") Long minSize,
        @Param("maxSize") Long maxSize
    );

    /**
     * 查找大文件元数据（大于1GB）
     */
    @Query("SELECT m FROM MediaMetadata m WHERE m.fileSize > 1073741824")
    List<MediaMetadata> findLargeFileMetadata();

    /**
     * 根据容器格式查找元数据
     */
    List<MediaMetadata> findByContainerFormat(String containerFormat);

    /**
     * 根据视频编码格式和分辨率查找元数据
     */
    List<MediaMetadata> findByVideoCodecAndWidthAndHeight(String videoCodec, Integer width, Integer height);

    /**
     * 根据音频编码格式和采样率查找元数据
     */
    List<MediaMetadata> findByAudioCodecAndSampleRate(String audioCodec, Integer sampleRate);

    /**
     * 统计不同视频编码格式的数量
     */
    @Query("SELECT m.videoCodec, COUNT(m) FROM MediaMetadata m GROUP BY m.videoCodec")
    List<Object[]> countByVideoCodec();

    /**
     * 统计不同音频编码格式的数量
     */
    @Query("SELECT m.audioCodec, COUNT(m) FROM MediaMetadata m GROUP BY m.audioCodec")
    List<Object[]> countByAudioCodec();

    /**
     * 统计不同分辨率的数量
     */
    @Query("SELECT CONCAT(m.width, 'x', m.height), COUNT(m) FROM MediaMetadata m GROUP BY m.width, m.height")
    List<Object[]> countByResolution();

    /**
     * 统计平均比特率
     */
    @Query("SELECT AVG(m.bitrate) FROM MediaMetadata m")
    Double findAverageBitrate();

    /**
     * 统计平均时长
     */
    @Query("SELECT AVG(m.duration) FROM MediaMetadata m")
    Double findAverageDuration();

    /**
     * 统计总文件大小
     */
    @Query("SELECT SUM(m.fileSize) FROM MediaMetadata m")
    Long findTotalFileSize();

    /**
     * 查找媒体文件ID列表
     */
    @Query("SELECT m.mediaFile.id FROM MediaMetadata m")
    List<Long> findAllMediaFileIds();

    /**
     * 检查媒体文件是否已有元数据
     */
    boolean existsByMediaFileId(Long mediaFileId);

    /**
     * 根据媒体文件ID删除元数据
     */
    void deleteByMediaFileId(Long mediaFileId);
}