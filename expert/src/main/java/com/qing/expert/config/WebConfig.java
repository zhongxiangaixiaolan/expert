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
        log.info("静态资源访问通过控制器处理 (context-path: /api):");
        log.info("  头像文件: /api/avatars/** -> AvatarController (/avatars)");
        log.info("  其他文件: /api/files/** -> FileController (/files)");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:3030", // Web前端
                        "http://localhost:8080", // 本地开发
                        "http://127.0.0.1:3030", // Web前端备用
                        "http://127.0.0.1:8080" // 本地开发备用
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // 预检请求缓存时间
    }
}
