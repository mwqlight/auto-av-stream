package com.avstream.media;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MediaServiceApplicationTests {

    @Test
    void contextLoads() {
        // 测试Spring Boot上下文加载
    }

    @Test
    void applicationStarts() {
        // 测试应用启动
        MediaServiceApplication.main(new String[]{});
    }
}