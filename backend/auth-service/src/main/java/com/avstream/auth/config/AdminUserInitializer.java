package com.avstream.auth.config;

import com.avstream.auth.entity.Role;
import com.avstream.auth.entity.User;
import com.avstream.auth.repository.RoleRepository;
import com.avstream.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

/**
 * 管理员用户初始化配置
 * 用于初始化系统默认的管理员用户
 * 
 * @author AV Stream Team
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始初始化管理员用户...");
        
        // 检查是否已存在管理员用户
        Optional<User> existingAdmin = userRepository.findByUsername("admin");
        if (existingAdmin.isPresent()) {
            log.info("管理员用户已存在，跳过初始化");
            return;
        }
        
        // 获取超级管理员角色
        Optional<Role> superAdminRole = roleRepository.findByRoleName("SUPER_ADMIN");
        if (superAdminRole.isEmpty()) {
            log.error("超级管理员角色不存在，无法创建管理员用户");
            return;
        }
        
        // 创建管理员用户
        User adminUser = createAdminUser(superAdminRole.get());
        userRepository.save(adminUser);
        
        log.info("管理员用户初始化完成，用户名: admin，密码: admin123");
    }

    /**
     * 创建管理员用户
     */
    private User createAdminUser(Role superAdminRole) {
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setEmail("admin@avstream.com");
        adminUser.setDisplayName("系统管理员");
        adminUser.setPhone("13800138000");
        adminUser.setEnabled(true);
        adminUser.setAccountNonExpired(true);
        adminUser.setCredentialsNonExpired(true);
        adminUser.setAccountNonLocked(true);
        adminUser.setRoles(Collections.singleton(superAdminRole));
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setUpdatedAt(LocalDateTime.now());
        
        return adminUser;
    }
}