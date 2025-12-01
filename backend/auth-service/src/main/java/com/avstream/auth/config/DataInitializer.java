package com.avstream.auth.config;

import com.avstream.auth.entity.Permission;
import com.avstream.auth.entity.Role;
import com.avstream.auth.entity.Role.RoleType;
import com.avstream.auth.entity.Permission.ResourceType;
import com.avstream.auth.entity.Permission.ActionType;
import com.avstream.auth.repository.PermissionRepository;
import com.avstream.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 数据初始化配置
 * 用于初始化系统默认的权限和角色
 * 
 * @author AV Stream Team
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始初始化认证服务数据...");
        
        // 初始化权限
        initializePermissions();
        
        // 初始化角色
        initializeRoles();
        
        log.info("认证服务数据初始化完成");
    }

    /**
     * 初始化系统权限
     */
    private void initializePermissions() {
        if (permissionRepository.count() > 0) {
            log.info("权限数据已存在，跳过初始化");
            return;
        }

        List<Permission> permissions = Arrays.asList(
            // 用户管理权限
            createPermission("user:create", "创建用户", "创建新用户", ResourceType.USER, ActionType.CREATE),
            createPermission("user:read", "查看用户", "查看用户信息", ResourceType.USER, ActionType.READ),
            createPermission("user:update", "更新用户", "更新用户信息", ResourceType.USER, ActionType.UPDATE),
            createPermission("user:delete", "删除用户", "删除用户", ResourceType.USER, ActionType.DELETE),
            
            // 角色管理权限
            createPermission("role:create", "创建角色", "创建新角色", ResourceType.ROLE, ActionType.CREATE),
            createPermission("role:read", "查看角色", "查看角色信息", ResourceType.ROLE, ActionType.READ),
            createPermission("role:update", "更新角色", "更新角色信息", ResourceType.ROLE, ActionType.UPDATE),
            createPermission("role:delete", "删除角色", "删除角色", ResourceType.ROLE, ActionType.DELETE),
            
            // 权限管理权限
            createPermission("permission:create", "创建权限", "创建新权限", ResourceType.PERMISSION, ActionType.CREATE),
            createPermission("permission:read", "查看权限", "查看权限信息", ResourceType.PERMISSION, ActionType.READ),
            createPermission("permission:update", "更新权限", "更新权限信息", ResourceType.PERMISSION, ActionType.UPDATE),
            createPermission("permission:delete", "删除权限", "删除权限", ResourceType.PERMISSION, ActionType.DELETE),
            
            // 直播管理权限
            createPermission("live:create", "创建直播", "创建新直播", ResourceType.LIVE, ActionType.CREATE),
            createPermission("live:read", "查看直播", "查看直播信息", ResourceType.LIVE, ActionType.READ),
            createPermission("live:update", "更新直播", "更新直播信息", ResourceType.LIVE, ActionType.UPDATE),
            createPermission("live:delete", "删除直播", "删除直播", ResourceType.LIVE, ActionType.DELETE),
            
            // 媒体管理权限
            createPermission("media:create", "创建媒体", "上传新媒体文件", ResourceType.MEDIA, ActionType.CREATE),
            createPermission("media:read", "查看媒体", "查看媒体文件", ResourceType.MEDIA, ActionType.READ),
            createPermission("media:update", "更新媒体", "更新媒体信息", ResourceType.MEDIA, ActionType.UPDATE),
            createPermission("media:delete", "删除媒体", "删除媒体文件", ResourceType.MEDIA, ActionType.DELETE),
            
            // AI服务权限
            createPermission("ai:create", "使用AI服务", "使用AI功能", ResourceType.AI, ActionType.CREATE),
            createPermission("ai:read", "查看AI服务", "查看AI服务信息", ResourceType.AI, ActionType.READ),
            createPermission("ai:update", "更新AI服务", "更新AI服务配置", ResourceType.AI, ActionType.UPDATE),
            createPermission("ai:delete", "删除AI服务", "删除AI服务数据", ResourceType.AI, ActionType.DELETE),
            
            // 系统管理权限
            createPermission("system:read", "查看系统", "查看系统信息", ResourceType.SYSTEM, ActionType.READ),
            createPermission("system:update", "更新系统", "更新系统配置", ResourceType.SYSTEM, ActionType.UPDATE),
            createPermission("system:monitor", "监控系统", "监控系统状态", ResourceType.SYSTEM, ActionType.MONITOR),
            
            // 仪表板权限
            createPermission("dashboard:read", "查看仪表板", "查看仪表板数据", ResourceType.DASHBOARD, ActionType.READ)
        );

        permissionRepository.saveAll(permissions);
        log.info("初始化系统权限完成，共创建 {} 个权限", permissions.size());
    }

    /**
     * 初始化系统角色
     */
    private void initializeRoles() {
        if (roleRepository.count() > 0) {
            log.info("角色数据已存在，跳过初始化");
            return;
        }

        // 获取所有权限
        List<Permission> allPermissions = permissionRepository.findAll();
        
        // 创建超级管理员角色（拥有所有权限）
        Role superAdminRole = createRole("SUPER_ADMIN", "超级管理员", "系统最高权限管理员", RoleType.SYSTEM, allPermissions);
        
        // 创建管理员角色（拥有管理权限）
        Set<Permission> adminPermissions = new HashSet<>();
        for (Permission permission : allPermissions) {
            if (!permission.getPermissionCode().contains("system:") && 
                !permission.getPermissionCode().contains("permission:")) {
                adminPermissions.add(permission);
            }
        }
        Role adminRole = createRole("ADMIN", "管理员", "系统管理员", RoleType.SYSTEM, adminPermissions);
        
        // 创建普通用户角色（基础权限）
        Set<Permission> userPermissions = new HashSet<>();
        userPermissions.addAll(permissionRepository.findByPermissionCodeIn(Arrays.asList(
            "user:read", "live:read", "media:read", "ai:create", "dashboard:read"
        )));
        Role userRole = createRole("USER", "普通用户", "系统普通用户", RoleType.BUILTIN, userPermissions);
        
        // 创建访客角色（只读权限）
        Set<Permission> guestPermissions = new HashSet<>();
        guestPermissions.addAll(permissionRepository.findByPermissionCodeIn(Arrays.asList(
            "live:read", "media:read", "dashboard:read"
        )));
        Role guestRole = createRole("GUEST", "访客", "系统访客", RoleType.BUILTIN, guestPermissions);

        List<Role> roles = Arrays.asList(superAdminRole, adminRole, userRole, guestRole);
        roleRepository.saveAll(roles);
        log.info("初始化系统角色完成，共创建 {} 个角色", roles.size());
    }

    /**
     * 创建权限对象
     */
    private Permission createPermission(String code, String name, String description, 
                                       ResourceType resourceType, ActionType actionType) {
        Permission permission = new Permission();
        permission.setPermissionCode(code);
        permission.setPermissionName(name);
        permission.setDescription(description);
        permission.setResourceType(resourceType);
        permission.setActionType(actionType);
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());
        return permission;
    }

    /**
     * 创建角色对象
     */
    private Role createRole(String name, String displayName, String description, 
                           RoleType roleType, Set<Permission> permissions) {
        Role role = new Role();
        role.setRoleName(name);
        role.setDisplayName(displayName);
        role.setDescription(description);
        role.setRoleType(roleType);
        role.setPermissions(permissions);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        return role;
    }
}