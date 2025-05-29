package com.qing.expert.config;

import com.qing.expert.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全过滤器链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 启用CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 配置会话管理为无状态
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 添加JWT认证过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 配置请求授权
                .authorizeHttpRequests(auth -> auth
                        // 允许访问的路径 - 注意：由于配置了context-path: /api，这里的路径是相对于context-path的
                        .requestMatchers(
                                // 管理员认证接口
                                "/admin/auth/login",
                                "/admin/auth/logout",
                                "/admin/auth/encode-password",

                                // 用户端公开接口
                                "/user/login", // 用户微信登录

                                // 公共API接口（不需要认证）
                                "/banners", // 轮播图
                                "/announcements", // 公告
                                "/categories", // 分类列表
                                "/experts", // 达人列表（公开）
                                "/experts/**", // 达人详情和照片（公开）
                                "/services", // 服务列表（公开）
                                "/services/**", // 服务详情（公开）

                                // 静态资源和文件访问
                                "/static/**", // 允许访问静态资源 (相对于context-path /api)
                                "/static/avatars/**", // 允许访问头像文件 (相对于context-path /api)
                                "/static/banner/**", // 允许访问轮播图文件 (相对于context-path /api)
                                "/static/images/**", // 允许访问图片文件 (相对于context-path /api)
                                "/static/photos/**", // 允许访问照片文件 (相对于context-path /api)
                                "/files/**", // 允许访问静态文件 (相对于context-path /api)
                                "/avatars/**", // 允许访问头像文件 (相对于context-path /api)
                                "/photos/**", // 允许访问照片文件 (相对于context-path /api)

                                // 支付回调接口（不需要认证）
                                "/user/payment/callback/**", // 支付回调

                                // 系统接口
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/favicon.ico",
                                "/error",
                                "/actuator/**")
                        .permitAll()
                        // 其他请求需要认证
                        .anyRequest().authenticated());

        return http.build();
    }

    /**
     * CORS配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允许的源 - 包括微信小程序开发工具
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "*", // 允许所有来源（开发环境）
                "https://servicewechat.com", // 微信小程序开发工具
                "https://*.servicewechat.com" // 微信小程序相关域名
        ));

        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 允许的请求头
        configuration.setAllowedHeaders(List.of("*"));

        // 允许携带凭证
        configuration.setAllowCredentials(true);

        // 预检请求的缓存时间
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
