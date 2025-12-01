package com.avstream.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * 日志记录过滤器
 * 
 * @author AV Stream Team
 */
@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> implements Ordered {

    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            Instant startTime = Instant.now();
            ServerHttpRequest request = exchange.getRequest();

            // 记录请求信息
            String requestId = generateRequestId();
            String clientIP = getClientIP(request);
            String method = request.getMethod().name();
            String path = request.getPath().value();
            String query = request.getURI().getQuery();
            String userAgent = request.getHeaders().getFirst("User-Agent");
            String userId = request.getHeaders().getFirst("X-User-Id");
            String username = request.getHeaders().getFirst("X-Username");

            // 添加请求ID到请求头
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-Request-Id", requestId)
                    .build();

            ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

            // 记录请求日志
            log.info("请求开始 - requestId: {}, method: {}, path: {}, query: {}, clientIP: {}, userAgent: {}, userId: {}, username: {}",
                    requestId, method, path, query, clientIP, userAgent, userId, username);

            return chain.filter(modifiedExchange)
                    .doOnSuccess((result) -> {
                        ServerHttpResponse response = modifiedExchange.getResponse();
                        Instant endTime = Instant.now();
                        Duration duration = Duration.between(startTime, endTime);
                        
                        int statusCode = response.getStatusCode() != null ? 
                                response.getStatusCode().value() : 200;
                        
                        String logLevel = statusCode >= 400 ? "WARN" : "INFO";
                        
                        if (logLevel.equals("WARN")) {
                            log.warn("请求完成 - requestId: {}, status: {}, duration: {}ms",
                                    requestId, statusCode, duration.toMillis());
                        } else {
                            log.info("请求完成 - requestId: {}, status: {}, duration: {}ms",
                                    requestId, statusCode, duration.toMillis());
                        }
                    })
                    .doOnError((throwable) -> {
                        ServerHttpResponse response = modifiedExchange.getResponse();
                        Instant endTime = Instant.now();
                        Duration duration = Duration.between(startTime, endTime);
                        
                        int statusCode = response.getStatusCode() != null ? 
                                response.getStatusCode().value() : 500;
                        
                        log.error("请求异常 - requestId: {}, status: {}, duration: {}ms, error: {}",
                                requestId, statusCode, duration.toMillis(), throwable.getMessage(), throwable);
                    });
        };
    }

    /**
     * 生成请求ID
     */
    private String generateRequestId() {
        return "REQ_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    /**
     * 获取客户端IP
     */
    private String getClientIP(ServerHttpRequest request) {
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress() != null ? 
                 request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        }
        
        // 多个IP时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public static class Config {
        // 配置参数
    }
}