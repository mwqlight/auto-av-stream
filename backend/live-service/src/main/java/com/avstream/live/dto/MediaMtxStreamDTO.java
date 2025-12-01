package com.avstream.live.dto;

import lombok.Data;

/**
 * MediaMTX流信息DTO
 */
@Data
public class MediaMtxStreamDTO {
    
    /**
     * 流名称
     */
    private String name;
    
    /**
     * 流状态
     */
    private String state;
    
    /**
     * 读取者数量
     */
    private Integer readersCount;
    
    /**
     * 源类型
     */
    private String sourceType;
    
    /**
     * 源地址
     */
    private String sourceAddress;
    
    /**
     * 创建时间
     */
    private String creationTime;
    
    /**
     * 是否正在录制
     */
    private Boolean isRecording;
    
    /**
     * 录制路径
     */
    private String recordingPath;
    
    /**
     * 录制时长（秒）
     */
    private Long recordingDuration;
}