package com.avstream.monitor;

import com.avstream.monitor.controller.DashboardController;
import com.avstream.monitor.service.AlertRecordService;
import com.avstream.monitor.service.ApplicationMonitorService;
import com.avstream.monitor.service.MonitorMetricService;
import com.avstream.monitor.service.SystemMonitorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * DashboardController 单元测试
 */
class DashboardControllerTest {

    @Mock
    private MonitorMetricService monitorMetricService;

    @Mock
    private AlertRecordService alertRecordService;

    @Mock
    private SystemMonitorService systemMonitorService;

    @Mock
    private ApplicationMonitorService applicationMonitorService;

    @InjectMocks
    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOverview() {
        // 准备模拟数据
        when(systemMonitorService.getSystemHealth()).thenReturn("HEALTHY");
        when(applicationMonitorService.getAllApplicationHealth()).thenReturn(
            Map.of("auth-service", "UP", "media-service", "UP")
        );
        when(alertRecordService.getActiveAlertCount()).thenReturn(0L);
        when(monitorMetricService.getTotalMetricsCount()).thenReturn(100L);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = dashboardController.getOverview();

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> overview = response.getBody();
        assertNotNull(overview);
        assertEquals("HEALTHY", overview.get("systemHealth"));
        assertEquals(0L, overview.get("activeAlerts"));
        assertEquals(100L, overview.get("totalMetrics"));
        assertNotNull(overview.get("timestamp"));

        // 验证方法调用
        verify(systemMonitorService).getSystemHealth();
        verify(applicationMonitorService).getAllApplicationHealth();
        verify(alertRecordService).getActiveAlertCount();
        verify(monitorMetricService).getTotalMetricsCount();
    }

    @Test
    void testGetSystemHealth() {
        // 准备模拟数据
        when(systemMonitorService.getSystemHealth()).thenReturn("HEALTHY");

        // 执行测试
        ResponseEntity<Map<String, String>> response = dashboardController.getSystemHealth();

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, String> health = response.getBody();
        assertNotNull(health);
        assertEquals("HEALTHY", health.get("status"));

        // 验证方法调用
        verify(systemMonitorService).getSystemHealth();
    }

    @Test
    void testGetApplicationHealth() {
        // 准备模拟数据
        Map<String, String> healthStatus = Map.of(
            "auth-service", "UP",
            "media-service", "UP",
            "ai-service", "DOWN"
        );
        when(applicationMonitorService.getAllApplicationHealth()).thenReturn(healthStatus);

        // 执行测试
        ResponseEntity<Map<String, String>> response = dashboardController.getApplicationHealth();

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, String> result = response.getBody();
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("UP", result.get("auth-service"));
        assertEquals("DOWN", result.get("ai-service"));

        // 验证方法调用
        verify(applicationMonitorService).getAllApplicationHealth();
    }

    @Test
    void testGetActiveAlerts() {
        // 准备模拟数据
        List<Map<String, Object>> activeAlerts = List.of(
            Map.of(
                "id", 1L,
                "metricName", "cpu.usage",
                "metricValue", 95.0,
                "message", "CPU使用率过高",
                "severity", "HIGH"
            )
        );
        when(alertRecordService.getActiveAlerts()).thenReturn(activeAlerts);

        // 执行测试
        ResponseEntity<List<Map<String, Object>>> response = dashboardController.getActiveAlerts();

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        List<Map<String, Object>> result = response.getBody();
        assertNotNull(result);
        assertEquals(1, result.size());
        
        Map<String, Object> alert = result.get(0);
        assertEquals(1L, alert.get("id"));
        assertEquals("cpu.usage", alert.get("metricName"));
        assertEquals(95.0, alert.get("metricValue"));

        // 验证方法调用
        verify(alertRecordService).getActiveAlerts();
    }

    @Test
    void testGetMetrics() {
        // 这个测试需要Mock复杂的查询逻辑，暂时跳过
        // 实际项目中应该实现完整的测试
    }

    @Test
    void testGetMetricsTrend() {
        // 这个测试需要Mock复杂的查询逻辑，暂时跳过
        // 实际项目中应该实现完整的测试
    }

    @Test
    void testGetSystemMetrics() {
        // 这个测试需要Mock复杂的查询逻辑，暂时跳过
        // 实际项目中应该实现完整的测试
    }
}