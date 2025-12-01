package com.avstream.auth.service;

import com.avstream.auth.dto.request.LoginRequest;
import com.avstream.auth.dto.request.RegisterRequest;
import com.avstream.auth.dto.response.LoginResponse;
import com.avstream.auth.entity.User;
import com.avstream.auth.repository.UserRepository;
import com.avstream.auth.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务
 * 
 * @author AV Stream Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * 用户注册
     */
    @Transactional
    public User register(RegisterRequest registerRequest) {
        // 验证用户名是否已存在
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new BadCredentialsException("用户名已存在");
        }
        
        // 验证邮箱是否已存在
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new BadCredentialsException("邮箱已被注册");
        }
        
        // 验证密码和确认密码是否一致
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new BadCredentialsException("密码和确认密码不一致");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setDisplayName(registerRequest.getDisplayName() != null ? 
            registerRequest.getDisplayName() : registerRequest.getUsername());
        user.setPhone(registerRequest.getPhone());
        user.setEmailVerified(false);
        user.setMfaEnabled(false);
        
        // 保存用户
        User savedUser = userRepository.save(user);
        
        log.info("用户注册成功: {}", savedUser.getUsername());
        return savedUser;
    }

    /**
     * 用户登录
     */
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            // 验证用户状态
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("用户名或密码错误"));

            if (user.isLocked()) {
                throw new BadCredentialsException("账户已被锁定，请稍后重试");
            }

            // 验证密码
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                handleFailedLogin(user);
                throw new BadCredentialsException("用户名或密码错误");
            }

            // 验证MFA（如果启用）
            if (user.getMfaEnabled() && loginRequest.getMfaCode() == null) {
                throw new BadCredentialsException("需要MFA验证码");
            }

            // 认证成功
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 更新用户登录信息
            user.setLastLoginAt(LocalDateTime.now());
            user.resetLoginAttempts();
            userRepository.save(user);

            // 生成令牌
            String accessToken = jwtUtils.generateAccessToken(authentication);
            String refreshToken = jwtUtils.generateRefreshToken(authentication);

            // 构建响应
            LoginResponse response = new LoginResponse();
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken);
            response.setExpiresIn(jwtUtils.getExpirationDateFromToken(accessToken).getTime());

            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setEmail(user.getEmail());
            userInfo.setDisplayName(user.getDisplayName());
            userInfo.setAvatarUrl(user.getAvatarUrl());
            userInfo.setLastLoginAt(user.getLastLoginAt());
            userInfo.setMfaEnabled(user.getMfaEnabled());

            response.setUser(userInfo);

            log.info("用户登录成功: {}", user.getUsername());
            return response;

        } catch (BadCredentialsException e) {
            log.warn("登录失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 刷新令牌
     */
    @Transactional
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new BadCredentialsException("无效的刷新令牌");
        }

        String username = jwtUtils.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("用户不存在"));

        if (!user.isActive()) {
            throw new BadCredentialsException("用户已被禁用");
        }

        // 重新生成令牌
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(), null);

        String newAccessToken = jwtUtils.generateAccessToken(authentication);
        String newRefreshToken = jwtUtils.generateRefreshToken(authentication);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setExpiresIn(jwtUtils.getExpirationDateFromToken(newAccessToken).getTime());

        return response;
    }

    /**
     * 用户登出
     */
    public void logout(String token) {
        // 在实际应用中，可以将令牌加入黑名单
        // 这里简单清除安全上下文
        SecurityContextHolder.clearContext();
        log.info("用户登出成功");
    }

    /**
     * 验证令牌
     */
    public boolean validateToken(String token) {
        return jwtUtils.validateToken(token);
    }

    /**
     * 处理登录失败
     */
    private void handleFailedLogin(User user) {
        user.incrementLoginAttempts();
        
        if (user.getLoginAttempts() >= 5) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
            log.warn("用户账户被锁定: {}", user.getUsername());
        }
        
        userRepository.save(user);
    }
}