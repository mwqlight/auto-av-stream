package com.avstream.auth.repository;

import com.avstream.auth.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 权限数据访问接口
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * 根据权限码查找权限
     */
    Optional<Permission> findByCode(String code);

    /**
     * 检查权限码是否存在
     */
    boolean existsByCode(String code);

    /**
     * 根据资源类型查找权限
     */
    List<Permission> findByResourceType(Permission.ResourceType resourceType);

    /**
     * 根据操作类型查找权限
     */
    List<Permission> findByAction(Permission.ActionType action);

    /**
     * 根据资源类型和操作类型查找权限
     */
    Optional<Permission> findByResourceTypeAndAction(Permission.ResourceType resourceType, 
                                                    Permission.ActionType action);

    /**
     * 根据角色ID查找权限
     */
    @Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.id = :roleId")
    List<Permission> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查找权限
     */
    @Query("SELECT DISTINCT p FROM Permission p JOIN p.roles r JOIN r.users u WHERE u.id = :userId")
    List<Permission> findByUserId(@Param("userId") Long userId);

    /**
     * 根据权限码列表查找权限
     */
    List<Permission> findByCodeIn(List<String> codes);
}