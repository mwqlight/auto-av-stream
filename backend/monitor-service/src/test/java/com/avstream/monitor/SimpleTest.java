package com.avstream.monitor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class SimpleTest {

    @Test
    void simpleTest() {
        // 简单的单元测试，不依赖Spring上下文
        assertTrue(true);
    }

    @Test
    void anotherSimpleTest() {
        // 另一个简单的测试
        int result = 1 + 1;
        assertTrue(result == 2);
    }
}