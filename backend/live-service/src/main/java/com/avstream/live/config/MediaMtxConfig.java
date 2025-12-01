package com.avstream.live.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * MediaMTX配置类
 */
@Slf4j
@Configuration
public class MediaMtxConfig {
    
    @Bean
    public RestTemplate mediaMtxRestTemplate(MediaMtxProperties mediaMtxProperties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofMillis(mediaMtxProperties.getTimeout()).toMillis());
        factory.setReadTimeout((int) Duration.ofMillis(mediaMtxProperties.getTimeout()).toMillis());
        
        RestTemplate restTemplate = new RestTemplate(factory);
        
        // 添加请求拦截器用于日志记录
        restTemplate.getInterceptors().add((request, body, execution) -> {
            log.debug("MediaMTX API请求: {} {}", request.getMethod(), request.getURI());
            return execution.execute(request, body);
        });
        
        return restTemplate;
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}