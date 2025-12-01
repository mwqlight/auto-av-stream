package com.avstream.auth.service;

import com.avstream.auth.entity.Permission;
import com.avstream.auth.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 权限管理服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {
    
    private final PermissionRepository permissionRepository;
    
    /**
     * 获取所有权限
     */
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }
    
    /**
     * 根据ID获取权限
     */
    public Optional<Permission> getPermissionById(Long id) {
        return permissionRepository.findById(id);
    }
    
    /**
     * 根据权限码获取权限
     */
    public Optional<Permission> getPermissionByCode(String code) {
        return permissionRepository.findByCode(code);
    }
    
    /**
     * 创建权限
     */
    @Transactional
    public Permission createPermission(Permission permission) {
        // 检查权限码是否已存在
        if (permissionRepository.existsByCode(permission.getCode())) {
            throw new IllegalArgumentException("权限码已存在: " + permission.getCode());
        }
        
        Permission savedPermission = permissionRepository.save(permission);
        log.info("创建权限成功: {} - {}", savedPermission.getCode(), savedPermission.getName());
        return savedPermission;
    }
    
    /**
     * 更新权限
     */
    @Transactional
    public Permission updatePermission(Long id, Permission permissionDetails) {
        Permission permission = permissionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("权限不存在: " + id));
        
        // 检查权限码是否与其他权限冲突
        if (!permission.getCode().equals(permissionDetails.getCode()) && 
            permissionRepository.existsByCode(permissionDetails.getCode())) {
            throw new IllegalArgumentException("权限码已存在: " + permissionDetails.getCode());
        }
        
        permission.setName(permissionDetails.getName());
        permission.setDescription(permissionDetails.getDescription());
        permission.setResourceType(permissionDetails.getResourceType());
        permission.setAction(permissionDetails.getAction());
        
        Permission updatedPermission = permissionRepository.save(permission);
        log.info("更新权限成功: {} - {}", updatedPermission.getCode(), updatedPermission.getName());
        return updatedPermission;
    }
    
    /**
     * 删除权限
     */
    @Transactional
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("权限不存在: " + id));
        
        // 检查权限是否被角色使用
        if (!permission.getRoles().isEmpty()) {
            throw new IllegalArgumentException("权限正在被角色使用，无法删除");
        }
        
        permissionRepository.delete(permission);
        log.info("删除权限成功: {} - {}", permission.getCode(), permission.getName());
    }
    
    /**
     * 根据资源类型获取权限
     */
    public List<Permission> getPermissionsByResourceType(Permission.ResourceType resourceType) {
        return permissionRepository.findByResourceType(resourceType);
    }
    
    /**
     * 检查权限是否存在
     */
    public boolean existsByCode(String code) {
        return permissionRepository.existsByCode(code);
    }
}