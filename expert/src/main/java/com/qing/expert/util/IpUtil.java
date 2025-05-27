package com.qing.expert.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP工具类
 */
@Slf4j
public class IpUtil {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final int IP_MAX_LENGTH = 15;

    /**
     * 获取客户端IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 处理多个IP的情况，取第一个非unknown的有效IP
        if (StrUtil.isNotBlank(ip) && ip.contains(",")) {
            String[] ips = ip.split(",");
            for (String ipItem : ips) {
                ipItem = ipItem.trim();
                if (StrUtil.isNotBlank(ipItem) && !UNKNOWN.equalsIgnoreCase(ipItem)) {
                    ip = ipItem;
                    break;
                }
            }
        }

        // 处理IPv6本地地址
        if (LOCALHOST_IPV6.equals(ip)) {
            ip = LOCALHOST_IPV4;
        }

        // 截取IP长度
        if (StrUtil.isNotBlank(ip) && ip.length() > IP_MAX_LENGTH) {
            ip = ip.substring(0, IP_MAX_LENGTH);
        }

        return StrUtil.isBlank(ip) ? UNKNOWN : ip;
    }

    /**
     * 判断是否为内网IP
     */
    public static boolean isInternalIp(String ip) {
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            return false;
        }

        if (LOCALHOST_IPV4.equals(ip) || LOCALHOST_IPV6.equals(ip)) {
            return true;
        }

        try {
            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return false;
            }

            int firstOctet = Integer.parseInt(parts[0]);
            int secondOctet = Integer.parseInt(parts[1]);

            // 10.0.0.0 - 10.255.255.255
            if (firstOctet == 10) {
                return true;
            }

            // 172.16.0.0 - 172.31.255.255
            if (firstOctet == 172 && secondOctet >= 16 && secondOctet <= 31) {
                return true;
            }

            // 192.168.0.0 - 192.168.255.255
            if (firstOctet == 192 && secondOctet == 168) {
                return true;
            }

            return false;
        } catch (Exception e) {
            log.warn("判断内网IP失败：{}", ip, e);
            return false;
        }
    }
}
