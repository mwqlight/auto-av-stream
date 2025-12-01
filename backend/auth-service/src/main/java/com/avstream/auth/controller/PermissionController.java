package com.avstream.auth.controller;

import com.avstream.auth.entity.Permission;
import com.avstream.auth.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 权限管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 获取所有权限
     */
    @GetMapping
    @PreAuthorize("hasPermission('permission', 'read')")
    public ResponseEntity<List<Permission>> getAllPermissions() {
        log.info("获取所有权限列表");
        List<Permission> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    /**
     * 根据ID获取权限
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('permission', 'read')")
    public ResponseEntity<Permission> getPermissionById(@PathVariable Long id) {
        log.info("根据ID获取权限: {}", id);
        Permission permission = permissionService.getPermissionById(id)
            .orElseThrow(() -> new IllegalArgumentException("权限不存在: " + id));
        return ResponseEntity.ok(permission);
    }

    /**
     * 根据权限码获取权限
     */
    @GetMapping("/code/{code}")
    @PreAuthorize("hasPermission('permission', 'read')")
    public ResponseEntity<Permission> getPermissionByCode(@PathVariable String code) {
        log.info("根据权限码获取权限: {}", code);
        Permission permission = permissionService.getPermissionByCode(code)
            .orElseThrow(() -> new IllegalArgumentException("权限不存在: " + code));
        return ResponseEntity.ok(permission);
    }

    /**
     * 创建权限
     */
    @PostMapping
    @PreAuthorize("hasPermission('permission', 'create')")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) {
        log.info("创建权限: {}", permission.getCode());
        Permission createdPermission = permissionService.createPermission(permission);
        return ResponseEntity.ok(createdPermission);
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('permission', 'update')")
    public ResponseEntity<Permission> updatePermission(@PathVariable Long id, 
                                                      @Valid @RequestBody Permission permission) {
        log.info("更新权限: {}", id);
        Permission updatedPermission = permissionService.updatePermission(id, permission);
        return ResponseEntity.ok(updatedPermission);
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('permission', 'delete')")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        log.info("删除权限: {}", id);
        permissionService.deletePermission(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据资源类型获取权限
     */
    @GetMapping("/resource/{resourceType}")
    @PreAuthorize("hasPermission('permission', 'read')")
    public ResponseEntity<List<Permission>> getPermissionsByResourceType(
            @PathVariable Permission.ResourceType resourceType) {
        log.info("根据资源类型获取权限: {}", resourceType);
        List<Permission> permissions = permissionService.getPermissionsByResourceType(resourceType);
        return ResponseEntity.ok(permissions);
    }
}