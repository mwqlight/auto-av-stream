package com.avstream.monitor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UnitTest {

    @Test
    void unitTest() {
        // 简单的单元测试，不依赖Spring上下文
        assertTrue(true);
    }

    @Test
    void calculationTest() {
        // 测试基本计算
        int result = 2 + 3;
        assertTrue(result == 5);
    }
}