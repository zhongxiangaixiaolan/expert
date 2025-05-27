package com.qing.expert.filter;

import com.qing.expert.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 跳过静态资源请求的JWT验证
        String requestURI = request.getRequestURI();
        if (isStaticResourceRequest(requestURI)) {
            log.debug("跳过静态资源请求的JWT验证: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 获取JWT令牌
            String token = getTokenFromRequest(request);

            if (StringUtils.hasText(token)) {
                // 验证令牌并设置认证信息
                if (validateTokenAndSetAuthentication(token, request)) {
                    log.debug("JWT令牌验证成功，用户已认证");
                } else {
                    log.debug("JWT令牌验证失败");
                }
            } else {
                log.debug("请求中未找到JWT令牌");
            }
        } catch (Exception e) {
            log.error("JWT认证过滤器处理异常：{}", e.getMessage());
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 判断是否为公开访问的请求（不需要认证）
     * 注意：由于配置了context-path: /api，这里需要考虑完整的请求路径
     */
    private boolean isStaticResourceRequest(String requestURI) {
        // 静态资源
        if (requestURI.startsWith("/api/files/") ||
                requestURI.startsWith("/api/avatars/") ||
                requestURI.startsWith("/files/") || // 相对于context-path的路径
                requestURI.startsWith("/avatars/") || // 相对于context-path的路径
                requestURI.startsWith("/favicon.ico") ||
                requestURI.startsWith("/webjars/") ||
                requestURI.startsWith("/swagger-ui/") ||
                requestURI.startsWith("/v3/api-docs/")) {
            return true;
        }

        // 管理员认证接口
        if (requestURI.startsWith("/api/admin/auth/") ||
                requestURI.startsWith("/admin/auth/")) {
            return true;
        }

        // 用户端公开接口
        if (requestURI.equals("/api/user/login") ||
                requestURI.equals("/user/login")) {
            return true;
        }

        // 公共API接口
        if (requestURI.startsWith("/api/banners") ||
                requestURI.startsWith("/banners") ||
                requestURI.startsWith("/api/announcements") ||
                requestURI.startsWith("/announcements") ||
                requestURI.startsWith("/api/categories") ||
                requestURI.startsWith("/categories") ||
                requestURI.startsWith("/api/experts") ||
                requestURI.startsWith("/experts") ||
                requestURI.startsWith("/api/services") ||
                requestURI.startsWith("/services")) {
            return true;
        }

        // 支付回调接口
        if (requestURI.startsWith("/api/user/payment/callback/") ||
                requestURI.startsWith("/user/payment/callback/")) {
            return true;
        }

        return false;
    }

    /**
     * 从请求中获取JWT令牌
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtUtil.getHeader());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtUtil.getPrefix() + " ")) {
            return bearerToken.substring(jwtUtil.getPrefix().length() + 1);
        }
        return null;
    }

    /**
     * 验证令牌并设置认证信息
     */
    private boolean validateTokenAndSetAuthentication(String token, HttpServletRequest request) {
        try {
            // 从令牌中获取用户信息
            String username = jwtUtil.getUsernameFromToken(token);
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            // 验证令牌
            if (StringUtils.hasText(username) && jwtUtil.validateToken(token, username)) {
                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));

                // 设置认证详情
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 将用户信息添加到认证对象中
                authentication.setDetails(new JwtAuthenticationDetails(userId, username, role, request));

                // 设置到安全上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("用户认证成功：username={}, userId={}, role={}", username, userId, role);
                return true;
            }
        } catch (Exception e) {
            log.error("验证JWT令牌时发生异常：{}", e.getMessage());
        }
        return false;
    }

    /**
     * JWT认证详情类
     */
    public static class JwtAuthenticationDetails extends WebAuthenticationDetailsSource {
        private final Long userId;
        private final String username;
        private final String role;
        private final String remoteAddress;
        private final String sessionId;

        public JwtAuthenticationDetails(Long userId, String username, String role, HttpServletRequest request) {
            this.userId = userId;
            this.username = username;
            this.role = role;
            this.remoteAddress = request.getRemoteAddr();
            this.sessionId = request.getSession(false) != null ? request.getSession().getId() : null;
        }

        public Long getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getRole() {
            return role;
        }

        public String getRemoteAddress() {
            return remoteAddress;
        }

        public String getSessionId() {
            return sessionId;
        }

        @Override
        public String toString() {
            return "JwtAuthenticationDetails{" +
                    "userId=" + userId +
                    ", username='" + username + '\'' +
                    ", role='" + role + '\'' +
                    ", remoteAddress='" + remoteAddress + '\'' +
                    ", sessionId='" + sessionId + '\'' +
                    '}';
        }
    }
}
