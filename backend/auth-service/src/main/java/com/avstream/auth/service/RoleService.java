package com.avstream.auth.service;

import com.avstream.auth.entity.Permission;
import com.avstream.auth.entity.Role;
import com.avstream.auth.repository.PermissionRepository;
import com.avstream.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 角色管理服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {
    
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    
    /**
     * 获取所有角色
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    
    /**
     * 根据ID获取角色
     */
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }
    
    /**
     * 根据角色名获取角色
     */
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }
    
    /**
     * 创建角色
     */
    @Transactional
    public Role createRole(Role role) {
        // 检查角色名是否已存在
        if (roleRepository.existsByName(role.getName())) {
            throw new IllegalArgumentException("角色名已存在: " + role.getName());
        }
        
        Role savedRole = roleRepository.save(role);
        log.info("创建角色成功: {} - {}", savedRole.getName(), savedRole.getDescription());
        return savedRole;
    }
    
    /**
     * 更新角色
     */
    @Transactional
    public Role updateRole(Long id, Role roleDetails) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + id));
        
        // 检查角色名是否与其他角色冲突
        if (!role.getName().equals(roleDetails.getName()) && 
            roleRepository.existsByName(roleDetails.getName())) {
            throw new IllegalArgumentException("角色名已存在: " + roleDetails.getName());
        }
        
        // 系统角色不允许修改类型
        if (role.isSystemRole() && role.getType() != roleDetails.getType()) {
            throw new IllegalArgumentException("系统角色不允许修改类型");
        }
        
        role.setName(roleDetails.getName());
        role.setDescription(roleDetails.getDescription());
        if (!role.isSystemRole()) {
            role.setType(roleDetails.getType());
        }
        
        Role updatedRole = roleRepository.save(role);
        log.info("更新角色成功: {} - {}", updatedRole.getName(), updatedRole.getDescription());
        return updatedRole;
    }
    
    /**
     * 删除角色
     */
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + id));
        
        // 系统角色不允许删除
        if (role.isSystemRole()) {
            throw new IllegalArgumentException("系统角色不允许删除");
        }
        
        // 检查角色是否被用户使用
        if (!role.getUsers().isEmpty()) {
            throw new IllegalArgumentException("角色正在被用户使用，无法删除");
        }
        
        roleRepository.delete(role);
        log.info("删除角色成功: {} - {}", role.getName(), role.getDescription());
    }
    
    /**
     * 为角色分配权限
     */
    @Transactional
    public Role assignPermissions(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + roleId));
        
        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
        
        // 检查权限是否存在
        if (permissions.size() != permissionIds.size()) {
            throw new IllegalArgumentException("部分权限不存在");
        }
        
        role.setPermissions(permissions);
        Role updatedRole = roleRepository.save(role);
        
        log.info("为角色分配权限成功: {}，权限数量: {}", role.getName(), permissions.size());
        return updatedRole;
    }
    
    /**
     * 移除角色的权限
     */
    @Transactional
    public Role removePermissions(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + roleId));
        
        Set<Permission> permissionsToRemove = new HashSet<>(permissionRepository.findAllById(permissionIds));
        role.getPermissions().removeAll(permissionsToRemove);
        
        Role updatedRole = roleRepository.save(role);
        log.info("从角色移除权限成功: {}，移除权限数量: {}", role.getName(), permissionsToRemove.size());
        return updatedRole;
    }
    
    /**
     * 根据用户ID获取角色
     */
    public List<Role> getRolesByUserId(Long userId) {
        return roleRepository.findByUserId(userId);
    }
    
    /**
     * 获取默认角色
     */
    public List<Role> getDefaultRoles() {
        return roleRepository.findDefaultRoles();
    }
    
    /**
     * 检查角色是否存在
     */
    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }
}