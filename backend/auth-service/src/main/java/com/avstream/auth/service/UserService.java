package com.avstream.auth.service;

import com.avstream.auth.entity.Permission;
import com.avstream.auth.entity.Role;
import com.avstream.auth.entity.User;
import com.avstream.auth.repository.PermissionRepository;
import com.avstream.auth.repository.RoleRepository;
import com.avstream.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 用户管理服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 获取所有用户
     */
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    /**
     * 根据ID获取用户
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * 根据用户名获取用户
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * 根据邮箱获取用户
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * 创建用户
     */
    @Transactional
    public User createUser(User user) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("用户名已存在: " + user.getUsername());
        }
        
        // 检查邮箱是否已存在
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("邮箱已被注册: " + user.getEmail());
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置默认值
        if (user.getDisplayName() == null) {
            user.setDisplayName(user.getUsername());
        }
        
        User savedUser = userRepository.save(user);
        log.info("创建用户成功: {}", savedUser.getUsername());
        return savedUser;
    }
    
    /**
     * 更新用户
     */
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + id));
        
        // 检查用户名是否与其他用户冲突
        if (!user.getUsername().equals(userDetails.getUsername()) && 
            userRepository.findByUsername(userDetails.getUsername()).isPresent()) {
            throw new IllegalArgumentException("用户名已存在: " + userDetails.getUsername());
        }
        
        // 检查邮箱是否与其他用户冲突
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
            throw new IllegalArgumentException("邮箱已被注册: " + userDetails.getEmail());
        }
        
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setDisplayName(userDetails.getDisplayName());
        user.setPhone(userDetails.getPhone());
        user.setEmailVerified(userDetails.isEmailVerified());
        user.setMfaEnabled(userDetails.isMfaEnabled());
        
        User updatedUser = userRepository.save(user);
        log.info("更新用户成功: {}", updatedUser.getUsername());
        return updatedUser;
    }
    
    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + id));
        
        userRepository.delete(user);
        log.info("删除用户成功: {}", user.getUsername());
    }
    
    /**
     * 为用户分配角色
     */
    @Transactional
    public User assignRoles(Long userId, List<Long> roleIds) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
        
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        
        // 检查角色是否存在
        if (roles.size() != roleIds.size()) {
            throw new IllegalArgumentException("部分角色不存在");
        }
        
        user.setRoles(roles);
        User updatedUser = userRepository.save(user);
        
        log.info("为用户分配角色成功: {}，角色数量: {}", user.getUsername(), roles.size());
        return updatedUser;
    }
    
    /**
     * 移除用户的角色
     */
    @Transactional
    public User removeRoles(Long userId, List<Long> roleIds) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
        
        Set<Role> rolesToRemove = new HashSet<>(roleRepository.findAllById(roleIds));
        user.getRoles().removeAll(rolesToRemove);
        
        User updatedUser = userRepository.save(user);
        log.info("从用户移除角色成功: {}，移除角色数量: {}", user.getUsername(), rolesToRemove.size());
        return updatedUser;
    }
    
    /**
     * 重置用户密码
     */
    @Transactional
    public User resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLastPasswordResetAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        log.info("重置用户密码成功: {}", user.getUsername());
        return updatedUser;
    }
    
    /**
     * 锁定用户
     */
    @Transactional
    public User lockUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
        
        user.setLocked(true);
        user.setLockedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        log.info("锁定用户成功: {}", user.getUsername());
        return updatedUser;
    }
    
    /**
     * 解锁用户
     */
    @Transactional
    public User unlockUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
        
        user.setLocked(false);
        user.setLockedAt(null);
        user.setLoginAttempts(0);
        
        User updatedUser = userRepository.save(user);
        log.info("解锁用户成功: {}", user.getUsername());
        return updatedUser;
    }
    
    /**
     * 获取用户的权限列表
     */
    public List<Permission> getUserPermissions(Long userId) {
        return permissionRepository.findByUserId(userId);
    }
    
    /**
     * 检查用户是否有特定权限
     */
    public boolean hasPermission(Long userId, String permissionCode) {
        List<Permission> userPermissions = getUserPermissions(userId);
        return userPermissions.stream()
            .anyMatch(permission -> permission.getCode().equals(permissionCode));
    }
    
    /**
     * 根据状态筛选用户
     */
    public Page<User> getUsersByStatus(User.UserStatus status, Pageable pageable) {
        return userRepository.findByStatus(status, pageable);
    }
    
    /**
     * 搜索用户
     */
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        return userRepository.searchUsers(keyword, pageable);
    }
}