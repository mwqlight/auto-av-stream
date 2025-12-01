package com.avstream.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * 限流过滤器
 * 
 * @author AV Stream Team
 */
@Component
@Slf4j
public class RateLimitFilter extends AbstractGatewayFilterFactory<RateLimitFilter.Config> {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    // Redis Lua脚本实现令牌桶算法
    private static final String RATE_LIMIT_SCRIPT = """
        local key = KEYS[1]
        local capacity = tonumber(ARGV[1])
        local refillRate = tonumber(ARGV[2])
        local now = tonumber(ARGV[3])
        local tokensRequested = tonumber(ARGV[4])
        
        local bucket = redis.call('hmget', key, 'tokens', 'lastRefill')
        local tokens = tonumber(bucket[1]) or capacity
        local lastRefill = tonumber(bucket[2]) or now
        
        -- 计算需要补充的令牌数
        local timePassed = now - lastRefill
        local tokensToAdd = math.floor(timePassed * refillRate)
        
        -- 更新令牌数
        tokens = math.min(capacity, tokens + tokensToAdd)
        
        -- 检查是否有足够的令牌
        if tokens >= tokensRequested then
            tokens = tokens - tokensRequested
            redis.call('hmset', key, 'tokens', tokens, 'lastRefill', now)
            redis.call('expire', key, math.ceil(capacity / refillRate) * 2)
            return 1
        else
            redis.call('hmset', key, 'tokens', tokens, 'lastRefill', lastRefill)
            redis.call('expire', key, math.ceil(capacity / refillRate) * 2)
            return 0
        end
        """;

    public RateLimitFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().value();
            
            // 获取限流键（基于IP或用户ID）
            String rateLimitKey = getRateLimitKey(request, config.getType());
            
            if (rateLimitKey == null) {
                // 无法识别限流键，直接放行
                return chain.filter(exchange);
            }

            String redisKey = String.format("rate_limit:%s:%s", config.getType(), rateLimitKey);
            
            List<String> args = Arrays.asList(
                    config.getCapacity().toString(),
                    config.getRefillRate().toString(),
                    String.valueOf(Instant.now().getEpochSecond()),
                    "1"
            );
            
            return redisTemplate.execute(RedisScript.of(RATE_LIMIT_SCRIPT, Long.class),
                    Arrays.asList(redisKey), args)
                    .next()
                    .flatMap(result -> {
                        if (result == 1) {
                            // 令牌足够，放行
                            log.debug("限流通行: key={}, path={}", redisKey, path);
                            return chain.filter(exchange);
                        } else {
                            // 令牌不足，限流
                            log.warn("请求被限流: key={}, path={}", redisKey, path);
                            return rateLimited(exchange.getResponse(), config.getType());
                        }
                    });
        };
    }

    /**
     * 获取限流键
     */
    private String getRateLimitKey(ServerHttpRequest request, String type) {
        switch (type) {
            case "ip":
                // 基于IP限流
                String ip = getClientIP(request);
                return ip != null ? ip : "unknown";
            case "user":
                // 基于用户ID限流
                String userId = request.getHeaders().getFirst("X-User-Id");
                return userId != null ? userId : "anonymous";
            case "path":
                // 基于路径限流
                return request.getPath().value();
            default:
                return null;
        }
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

    /**
     * 返回限流响应
     */
    private Mono<Void> rateLimited(ServerHttpResponse response, String type) {
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add("Content-Type", "application/json");
        response.getHeaders().add("Retry-After", "60");
        
        String message = String.format("请求过于频繁，基于%s限流", type);
        String body = String.format(
                "{\"code\":429,\"message\":\"%s\",\"data\":null,\"timestamp\":%d}",
                message, System.currentTimeMillis()
        );
        
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    public static class Config {
        private String type = "ip"; // 限流类型: ip, user, path
        private Integer capacity = 100; // 令牌桶容量
        private Double refillRate = 10.0; // 每秒补充令牌数

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getCapacity() {
            return capacity;
        }

        public void setCapacity(Integer capacity) {
            this.capacity = capacity;
        }

        public Double getRefillRate() {
            return refillRate;
        }

        public void setRefillRate(Double refillRate) {
            this.refillRate = refillRate;
        }
    }
}