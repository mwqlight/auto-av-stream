package com.avstream.auth.controller;

import com.avstream.auth.entity.Permission;
import com.avstream.auth.entity.Role;
import com.avstream.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 角色管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 获取所有角色
     */
    @GetMapping
    @PreAuthorize("hasPermission('role', 'read')")
    public ResponseEntity<List<Role>> getAllRoles() {
        log.info("获取所有角色列表");
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    /**
     * 根据ID获取角色
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('role', 'read')")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        log.info("根据ID获取角色: {}", id);
        Role role = roleService.getRoleById(id)
            .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + id));
        return ResponseEntity.ok(role);
    }

    /**
     * 根据角色名获取角色
     */
    @GetMapping("/name/{name}")
    @PreAuthorize("hasPermission('role', 'read')")
    public ResponseEntity<Role> getRoleByName(@PathVariable String name) {
        log.info("根据角色名获取角色: {}", name);
        Role role = roleService.getRoleByName(name)
            .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + name));
        return ResponseEntity.ok(role);
    }

    /**
     * 创建角色
     */
    @PostMapping
    @PreAuthorize("hasPermission('role', 'create')")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) {
        log.info("创建角色: {}", role.getName());
        Role createdRole = roleService.createRole(role);
        return ResponseEntity.ok(createdRole);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('role', 'update')")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, 
                                          @Valid @RequestBody Role role) {
        log.info("更新角色: {}", id);
        Role updatedRole = roleService.updateRole(id, role);
        return ResponseEntity.ok(updatedRole);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('role', 'delete')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        log.info("删除角色: {}", id);
        roleService.deleteRole(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 为角色分配权限
     */
    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasPermission('role', 'update')")
    public ResponseEntity<Role> assignPermissions(@PathVariable Long id, 
                                                 @RequestBody List<Long> permissionIds) {
        log.info("为角色分配权限: {}，权限数量: {}", id, permissionIds.size());
        Role role = roleService.assignPermissions(id, permissionIds);
        return ResponseEntity.ok(role);
    }

    /**
     * 移除角色的权限
     */
    @DeleteMapping("/{id}/permissions")
    @PreAuthorize("hasPermission('role', 'update')")
    public ResponseEntity<Role> removePermissions(@PathVariable Long id, 
                                                 @RequestBody List<Long> permissionIds) {
        log.info("从角色移除权限: {}，权限数量: {}", id, permissionIds.size());
        Role role = roleService.removePermissions(id, permissionIds);
        return ResponseEntity.ok(role);
    }

    /**
     * 根据用户ID获取角色
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasPermission('role', 'read')")
    public ResponseEntity<List<Role>> getRolesByUserId(@PathVariable Long userId) {
        log.info("根据用户ID获取角色: {}", userId);
        List<Role> roles = roleService.getRolesByUserId(userId);
        return ResponseEntity.ok(roles);
    }

    /**
     * 获取默认角色
     */
    @GetMapping("/default")
    @PreAuthorize("hasPermission('role', 'read')")
    public ResponseEntity<List<Role>> getDefaultRoles() {
        log.info("获取默认角色列表");
        List<Role> roles = roleService.getDefaultRoles();
        return ResponseEntity.ok(roles);
    }
}