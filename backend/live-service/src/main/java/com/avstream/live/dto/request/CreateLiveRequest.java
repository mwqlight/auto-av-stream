package com.avstream.live.dto.request;

import com.avstream.live.entity.LiveStream;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 创建直播请求DTO
 * 
 * @author AV Stream Team
 */
@Data
public class CreateLiveRequest {
    
    /** 直播标题 */
    @NotBlank(message = "直播标题不能为空")
    @Size(max = 200, message = "直播标题长度不能超过200个字符")
    private String title;
    
    /** 直播描述 */
    @Size(max = 1000, message = "直播描述长度不能超过1000个字符")
    private String description;
    
    /** 直播类型 */
    @NotNull(message = "直播类型不能为空")
    private LiveStream.StreamType type;
    
    /** 是否录制 */
    private Boolean recordEnabled = false;
}