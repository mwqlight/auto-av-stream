package com.avstream.live.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 直播流实体类
 * 
 * @author AV Stream Team
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Entity
@Table(name = "live_stream")
public class LiveStream {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 直播标题 */
    @Column(nullable = false, length = 200)
    private String title;
    
    /** 直播描述 */
    @Column(length = 1000)
    private String description;
    
    /** 流密钥（唯一标识） */
    @Column(nullable = false, unique = true, length = 50)
    private String streamKey;
    
    /** 用户ID */
    @Column(nullable = false)
    private Long userId;
    
    /** 用户名 */
    @Column(nullable = false, length = 100)
    private String username;
    
    /** 直播状态 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StreamStatus status;
    
    /** 直播类型 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StreamType type;
    
    /** 是否录制 */
    @Column(nullable = false)
    private Boolean recordEnabled = false;
    
    /** 录制文件路径 */
    @Column(length = 500)
    private String recordPath;
    
    /** 开始时间 */
    @Column
    private LocalDateTime startTime;
    
    /** 结束时间 */
    @Column
    private LocalDateTime endTime;
    
    /** 观看人数 */
    @Column
    private Integer viewerCount = 0;
    
    /** 最大观看人数 */
    @Column
    private Integer maxViewerCount = 0;
    
    /** RTMP推流URL */
    @Column(length = 500)
    private String rtmpPushUrl;
    
    /** HLS推流URL */
    @Column(length = 500)
    private String hlsPushUrl;
    
    /** WebRTC推流URL */
    @Column(length = 500)
    private String webrtcPushUrl;
    
    /** RTMP拉流URL */
    @Column(length = 500)
    private String rtmpPullUrl;
    
    /** HLS拉流URL */
    @Column(length = 500)
    private String hlsPullUrl;
    
    /** WebRTC拉流URL */
    @Column(length = 500)
    private String webrtcPullUrl;
    
    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @Column(nullable = false)
    private LocalDateTime updateTime;
    
    /**
     * 直播状态枚举
     */
    public enum StreamStatus {
        CREATED,    // 已创建
        LIVE,       // 直播中
        PAUSED,     // 已暂停
        ENDED,      // 已结束
        ERROR       // 错误
    }
    
    /**
     * 直播类型枚举
     */
    public enum StreamType {
        PUBLIC,     // 公开直播
        PRIVATE,    // 私密直播
        PASSWORD    // 密码直播
    }
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}