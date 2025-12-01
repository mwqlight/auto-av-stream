package com.avstream.live.dto.request;

import lombok.Data;

import jakarta.validation.constraints.Size;

/**
 * 更新直播请求DTO
 * 
 * @author AV Stream Team
 */
@Data
public class UpdateLiveRequest {
    
    /** 直播标题 */
    @Size(max = 200, message = "直播标题长度不能超过200个字符")
    private String title;
    
    /** 直播描述 */
    @Size(max = 1000, message = "直播描述长度不能超过1000个字符")
    private String description;
    
    /** 是否录制 */
    private Boolean recordEnabled;
}