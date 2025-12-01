package com.avstream.ai.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 数据库配置类
 */
@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
@EntityScan("com.avstream.ai.entity")
@EnableJpaRepositories("com.avstream.ai.repository")
public class DatabaseConfig {
    
    // JPA配置已通过application.yml文件配置
    // 这里主要配置实体扫描和事务管理
}