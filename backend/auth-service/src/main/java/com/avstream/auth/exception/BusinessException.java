package com.avstream.auth.exception;

/**
 * 业务异常类
 * 
 * @author AV Stream Team
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /**
     * 用户相关错误码 (10000-10099)
     */
    public static class UserError {
        public static final int USER_NOT_FOUND = 10001;
        public static final int USER_ALREADY_EXISTS = 10002;
        public static final int USER_DISABLED = 10003;
        public static final int USER_LOCKED = 10004;
        public static final int INVALID_PASSWORD = 10005;
        public static final int MFA_REQUIRED = 10006;
        public static final int MFA_INVALID = 10007;
    }

    /**
     * 角色相关错误码 (10100-10199)
     */
    public static class RoleError {
        public static final int ROLE_NOT_FOUND = 10101;
        public static final int ROLE_ALREADY_EXISTS = 10102;
        public static final int ROLE_IN_USE = 10103;
        public static final int PERMISSION_DENIED = 10104;
    }

    /**
     * 认证相关错误码 (10200-10299)
     */
    public static class AuthError {
        public static final int TOKEN_INVALID = 10201;
        public static final int TOKEN_EXPIRED = 10202;
        public static final int REFRESH_TOKEN_INVALID = 10203;
        public static final int INVALID_CREDENTIALS = 10204;
    }

    /**
     * 创建用户相关异常
     */
    public static BusinessException userNotFound() {
        return new BusinessException(UserError.USER_NOT_FOUND, "用户不存在");
    }

    public static BusinessException userAlreadyExists(String username) {
        return new BusinessException(UserError.USER_ALREADY_EXISTS, "用户已存在: " + username);
    }

    public static BusinessException userDisabled() {
        return new BusinessException(UserError.USER_DISABLED, "用户已被禁用");
    }

    public static BusinessException userLocked() {
        return new BusinessException(UserError.USER_LOCKED, "用户已被锁定");
    }

    public static BusinessException invalidPassword() {
        return new BusinessException(UserError.INVALID_PASSWORD, "密码错误");
    }

    public static BusinessException mfaRequired() {
        return new BusinessException(UserError.MFA_REQUIRED, "需要MFA验证码");
    }

    public static BusinessException mfaInvalid() {
        return new BusinessException(UserError.MFA_INVALID, "MFA验证码无效");
    }

    /**
     * 创建角色相关异常
     */
    public static BusinessException roleNotFound() {
        return new BusinessException(RoleError.ROLE_NOT_FOUND, "角色不存在");
    }

    public static BusinessException roleAlreadyExists(String roleName) {
        return new BusinessException(RoleError.ROLE_ALREADY_EXISTS, "角色已存在: " + roleName);
    }

    public static BusinessException roleInUse() {
        return new BusinessException(RoleError.ROLE_IN_USE, "角色正在使用中，无法删除");
    }

    public static BusinessException permissionDenied() {
        return new BusinessException(RoleError.PERMISSION_DENIED, "权限不足");
    }

    /**
     * 创建认证相关异常
     */
    public static BusinessException tokenInvalid() {
        return new BusinessException(AuthError.TOKEN_INVALID, "令牌无效");
    }

    public static BusinessException tokenExpired() {
        return new BusinessException(AuthError.TOKEN_EXPIRED, "令牌已过期");
    }

    public static BusinessException refreshTokenInvalid() {
        return new BusinessException(AuthError.REFRESH_TOKEN_INVALID, "刷新令牌无效");
    }

    public static BusinessException invalidCredentials() {
        return new BusinessException(AuthError.INVALID_CREDENTIALS, "用户名或密码错误");
    }
}