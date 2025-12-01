package com.avstream.media.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 
 * @author AV Stream Team
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置CORS跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 配置静态资源处理
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置预览资源访问
        registry.addResourceHandler("/preview/**")
                .addResourceLocations("file:./preview/");
        
        // 配置缩略图资源访问
        registry.addResourceHandler("/thumbnails/**")
                .addResourceLocations("file:./thumbnails/");
        
        // 配置转码资源访问
        registry.addResourceHandler("/transcoded/**")
                .addResourceLocations("file:./transcoded/");
    }
}