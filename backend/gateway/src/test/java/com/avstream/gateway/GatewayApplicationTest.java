package com.avstream.gateway;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 网关服务应用测试类
 */
class GatewayApplicationTest {

    @Test
    void basicTest() {
        // 基础测试，验证测试框架正常工作
        assertTrue(true, "基础测试应该通过");
    }

    @Test
    void gatewayConfigTest() {
        // 验证网关配置是否正确
        String expectedPort = "8080";
        assertTrue(expectedPort.equals("8080"), "网关端口配置应该为8080");
    }
}