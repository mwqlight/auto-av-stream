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
 * 角色实体类
 * 
 * @author AV Stream Team
 */
@Data
@Entity
@Table(name = "roles", uniqueConstraints = {
    @UniqueConstraint(columnNames = "name")
})
@EqualsAndHashCode(exclude = {"permissions", "users", "createdAt", "updatedAt"})
@ToString(exclude = {"permissions", "users"})
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true)
    private String name;
    
    @Size(max = 200)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType type = RoleType.CUSTOM;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();
    
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 角色类型枚举
     */
    public enum RoleType {
        SYSTEM,     // 系统角色（不可删除）
        BUILTIN,    // 内置角色（可修改权限）
        CUSTOM      // 自定义角色
    }
    
    /**
     * 检查是否为系统角色
     */
    public boolean isSystemRole() {
        return type == RoleType.SYSTEM;
    }
}