package com.avstream.auth.controller;

import com.avstream.auth.entity.Permission;
import com.avstream.auth.entity.Role;
import com.avstream.auth.entity.User;
import com.avstream.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取所有用户（分页）
     */
    @GetMapping
    @PreAuthorize("hasPermission('user', 'read')")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? 
            Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        log.info("获取用户列表，页码: {}，大小: {}，排序: {}", page, size, sort);
        Page<User> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('user', 'read')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("根据ID获取用户: {}", id);
        User user = userService.getUserById(id)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + id));
        return ResponseEntity.ok(user);
    }

    /**
     * 根据用户名获取用户
     */
    @GetMapping("/username/{username}")
    @PreAuthorize("hasPermission('user', 'read')")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        log.info("根据用户名获取用户: {}", username);
        User user = userService.getUserByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + username));
        return ResponseEntity.ok(user);
    }

    /**
     * 根据邮箱获取用户
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasPermission('user', 'read')")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        log.info("根据邮箱获取用户: {}", email);
        User user = userService.getUserByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + email));
        return ResponseEntity.ok(user);
    }

    /**
     * 创建用户
     */
    @PostMapping
    @PreAuthorize("hasPermission('user', 'create')")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("创建用户: {}", user.getUsername());
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('user', 'update')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, 
                                          @Valid @RequestBody User user) {
        log.info("更新用户: {}", id);
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('user', 'delete')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("删除用户: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/{id}/roles")
    @PreAuthorize("hasPermission('user', 'update')")
    public ResponseEntity<User> assignRoles(@PathVariable Long id, 
                                           @RequestBody List<Long> roleIds) {
        log.info("为用户分配角色: {}，角色数量: {}", id, roleIds.size());
        User user = userService.assignRoles(id, roleIds);
        return ResponseEntity.ok(user);
    }

    /**
     * 移除用户的角色
     */
    @DeleteMapping("/{id}/roles")
    @PreAuthorize("hasPermission('user', 'update')")
    public ResponseEntity<User> removeRoles(@PathVariable Long id, 
                                           @RequestBody List<Long> roleIds) {
        log.info("从用户移除角色: {}，角色数量: {}", id, roleIds.size());
        User user = userService.removeRoles(id, roleIds);
        return ResponseEntity.ok(user);
    }

    /**
     * 重置用户密码
     */
    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasPermission('user', 'update')")
    public ResponseEntity<User> resetPassword(@PathVariable Long id, 
                                             @RequestBody String newPassword) {
        log.info("重置用户密码: {}", id);
        User user = userService.resetPassword(id, newPassword);
        return ResponseEntity.ok(user);
    }

    /**
     * 锁定用户
     */
    @PostMapping("/{id}/lock")
    @PreAuthorize("hasPermission('user', 'update')")
    public ResponseEntity<User> lockUser(@PathVariable Long id) {
        log.info("锁定用户: {}", id);
        User user = userService.lockUser(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 解锁用户
     */
    @PostMapping("/{id}/unlock")
    @PreAuthorize("hasPermission('user', 'update')")
    public ResponseEntity<User> unlockUser(@PathVariable Long id) {
        log.info("解锁用户: {}", id);
        User user = userService.unlockUser(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 获取用户的权限列表
     */
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasPermission('user', 'read')")
    public ResponseEntity<List<Permission>> getUserPermissions(@PathVariable Long id) {
        log.info("获取用户权限列表: {}", id);
        List<Permission> permissions = userService.getUserPermissions(id);
        return ResponseEntity.ok(permissions);
    }

    /**
     * 检查用户是否有特定权限
     */
    @GetMapping("/{id}/has-permission/{permissionCode}")
    @PreAuthorize("hasPermission('user', 'read')")
    public ResponseEntity<Boolean> hasPermission(@PathVariable Long id, 
                                                @PathVariable String permissionCode) {
        log.info("检查用户权限: {}，权限码: {}", id, permissionCode);
        boolean hasPermission = userService.hasPermission(id, permissionCode);
        return ResponseEntity.ok(hasPermission);
    }

    /**
     * 根据状态筛选用户
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasPermission('user', 'read')")
    public ResponseEntity<Page<User>> getUsersByStatus(
            @PathVariable User.UserStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        log.info("根据状态筛选用户: {}，页码: {}，大小: {}", status, page, size);
        Page<User> users = userService.getUsersByStatus(status, pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * 搜索用户
     */
    @GetMapping("/search")
    @PreAuthorize("hasPermission('user', 'read')")
    public ResponseEntity<Page<User>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        log.info("搜索用户: {}，页码: {}，大小: {}", keyword, page, size);
        Page<User> users = userService.searchUsers(keyword, pageable);
        return ResponseEntity.ok(users);
    }
}