package com.qing.expert.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 配置静态资源映射
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${expert.file.upload-path:uploads/}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源现在通过控制器处理，不需要在这里配置
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(
                        "http://localhost:*", // 本地开发环境
                        "http://127.0.0.1:*", // 本地开发环境备用
                        "https://servicewechat.com", // 微信小程序开发工具
                        "https://*.servicewechat.com", // 微信小程序相关域名
                        "*" // 开发环境允许所有来源
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // 预检请求缓存时间
    }
}
