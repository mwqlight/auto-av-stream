package com.avstream.monitor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 监控服务应用测试
 */
@SpringBootTest
@ActiveProfiles("test")
class MonitorApplicationTest {

    @Test
    void contextLoads() {
        // 测试Spring上下文加载
    }
    
    @Test
    void basicTest() {
        // 基础测试
        assert true : "基础测试通过";
    }
}