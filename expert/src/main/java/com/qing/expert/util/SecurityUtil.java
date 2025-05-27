package com.qing.expert.util;

import com.qing.expert.filter.JwtAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 */
public class SecurityUtil {

    /**
     * 获取当前认证的用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * 获取当前认证的用户ID
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object details = authentication.getDetails();
            if (details instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
                return ((JwtAuthenticationFilter.JwtAuthenticationDetails) details).getUserId();
            }
        }
        return null;
    }

    /**
     * 获取当前认证的用户角色
     */
    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object details = authentication.getDetails();
            if (details instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
                return ((JwtAuthenticationFilter.JwtAuthenticationDetails) details).getRole();
            }
        }
        return null;
    }

    /**
     * 获取当前认证的详细信息
     */
    public static JwtAuthenticationFilter.JwtAuthenticationDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object details = authentication.getDetails();
            if (details instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
                return (JwtAuthenticationFilter.JwtAuthenticationDetails) details;
            }
        }
        return null;
    }

    /**
     * 检查当前用户是否已认证
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && 
               !"anonymousUser".equals(authentication.getName());
    }

    /**
     * 检查当前用户是否具有指定角色
     */
    public static boolean hasRole(String role) {
        String currentRole = getCurrentUserRole();
        return currentRole != null && currentRole.equals(role);
    }

    /**
     * 检查当前用户是否为超级管理员
     */
    public static boolean isSuperAdmin() {
        return hasRole("SUPER_ADMIN");
    }

    /**
     * 检查当前用户是否为管理员
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN") || isSuperAdmin();
    }
}
