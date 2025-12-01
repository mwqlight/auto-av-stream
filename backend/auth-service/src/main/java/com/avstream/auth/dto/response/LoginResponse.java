package com.avstream.auth.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录响应DTO
 * 
 * @author AV Stream Team
 */
@Data
public class LoginResponse {
    
    private String accessToken;
    
    private String refreshToken;
    
    private String tokenType = "Bearer";
    
    private Long expiresIn;
    
    private UserInfo user;
    
    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String displayName;
        private String avatarUrl;
        private LocalDateTime lastLoginAt;
        private Boolean mfaEnabled;
    }
}