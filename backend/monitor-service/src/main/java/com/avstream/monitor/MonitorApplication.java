package com.avstream.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 监控服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EntityScan("com.avstream.monitor.entity")
@EnableJpaRepositories("com.avstream.monitor.repository")
public class MonitorApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
    }
}