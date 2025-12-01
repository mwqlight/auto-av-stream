package com.avstream.auth.repository;

import com.avstream.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 角色数据访问接口
 * 
 * @author AV Stream Team
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * 根据角色名查找角色
     */
    Optional<Role> findByName(String name);

    /**
     * 根据角色类型查找角色
     */
    List<Role> findByType(Role.RoleType type);

    /**
     * 检查角色名是否存在
     */
    boolean existsByName(String name);

    /**
     * 根据权限查找角色
     */
    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.permission = :permission")
    List<Role> findByPermission(@Param("permission") String permission);

    /**
     * 查找默认角色
     */
    @Query("SELECT r FROM Role r WHERE r.isDefault = true")
    List<Role> findDefaultRoles();

    /**
     * 根据用户ID查找角色
     */
    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.id = :userId")
    List<Role> findByUserId(@Param("userId") Long userId);
}