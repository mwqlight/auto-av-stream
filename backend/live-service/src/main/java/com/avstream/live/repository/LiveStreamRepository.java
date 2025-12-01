package com.avstream.live.repository;

import com.avstream.live.entity.LiveStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 直播流数据访问层
 * 
 * @author AV Stream Team
 */
@Repository
public interface LiveStreamRepository extends JpaRepository<LiveStream, Long> {
    
    /**
     * 根据用户ID查询直播流列表
     */
    List<LiveStream> findByUserId(Long userId);
    
    /**
     * 根据用户ID分页查询直播流列表
     */
    Page<LiveStream> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据流密钥查询直播流
     */
    Optional<LiveStream> findByStreamKey(String streamKey);
    
    /**
     * 根据状态查询直播流列表
     */
    List<LiveStream> findByStatus(LiveStream.StreamStatus status);
    
    /**
     * 根据状态分页查询直播流列表
     */
    Page<LiveStream> findByStatus(LiveStream.StreamStatus status, Pageable pageable);
    
    /**
     * 查询正在直播的流
     */
    List<LiveStream> findByStatusAndStartTimeBeforeAndEndTimeAfter(
            LiveStream.StreamStatus status, 
            LocalDateTime startTime, 
            LocalDateTime endTime);
    
    /**
     * 根据标题模糊查询
     */
    @Query("SELECT ls FROM LiveStream ls WHERE ls.title LIKE %:keyword% AND ls.status = :status")
    Page<LiveStream> findByTitleContainingAndStatus(@Param("keyword") String keyword, 
                                                   @Param("status") LiveStream.StreamStatus status, 
                                                   Pageable pageable);
    
    /**
     * 统计用户的总直播时长
     */
    @Query("SELECT SUM(TIMESTAMPDIFF(SECOND, ls.startTime, ls.endTime)) FROM LiveStream ls WHERE ls.userId = :userId AND ls.status = 'ENDED'")
    Long sumLiveDurationByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户的总直播次数
     */
    Long countByUserIdAndStatus(Long userId, LiveStream.StreamStatus status);
    
    /**
     * 查询需要清理的已结束直播流
     */
    @Query("SELECT ls FROM LiveStream ls WHERE ls.status = 'ENDED' AND ls.endTime < :cutoffTime")
    List<LiveStream> findEndedStreamsBefore(@Param("cutoffTime") LocalDateTime cutoffTime);
}