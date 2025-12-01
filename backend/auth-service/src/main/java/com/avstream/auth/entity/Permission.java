package com.avstream.auth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 权限实体类
 * 
 * @author AV Stream Team
 */
@Data
@Entity
@Table(name = "permissions", uniqueConstraints = {
    @UniqueConstraint(columnNames = "code")
})
@EqualsAndHashCode(exclude = {"roles", "createdAt", "updatedAt"})
@ToString(exclude = {"roles"})
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true)
    private String code;
    
    @NotBlank
    @Size(max = 200)
    @Column(nullable = false)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType resourceType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType action;
    
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 资源类型枚举
     */
    public enum ResourceType {
        USER,           // 用户管理
        ROLE,           // 角色管理
        PERMISSION,     // 权限管理
        MEDIA,          // 媒体管理
        LIVE,           // 直播管理
        AI,             // AI服务
        MONITOR,        // 监控管理
        SYSTEM,         // 系统管理
        DASHBOARD       // 驾驶舱
    }
    
    /**
     * 操作类型枚举
     */
    public enum ActionType {
        CREATE,     // 创建
        READ,       // 读取
        UPDATE,     // 更新
        DELETE,     // 删除
        EXECUTE,    // 执行
        MANAGE      // 管理
    }
    
    /**
     * 获取权限字符串表示（格式：资源:操作）
     */
    public String getPermissionString() {
        return resourceType.name().toLowerCase() + ":" + action.name().toLowerCase();
    }
}