package com.qing.expert.common.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类 - 提供彩色日志输出
 * 
 * @author qing
 * @since 2025-01-23
 */
@Slf4j
public class LogUtil {

    // ANSI 颜色代码
    public static final String RESET = "\033[0m";
    public static final String BLACK = "\033[30m";
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String YELLOW = "\033[33m";
    public static final String BLUE = "\033[34m";
    public static final String PURPLE = "\033[35m";
    public static final String CYAN = "\033[36m";
    public static final String WHITE = "\033[37m";
    
    // 背景色
    public static final String BG_BLACK = "\033[40m";
    public static final String BG_RED = "\033[41m";
    public static final String BG_GREEN = "\033[42m";
    public static final String BG_YELLOW = "\033[43m";
    public static final String BG_BLUE = "\033[44m";
    public static final String BG_PURPLE = "\033[45m";
    public static final String BG_CYAN = "\033[46m";
    public static final String BG_WHITE = "\033[47m";
    
    // 样式
    public static final String BOLD = "\033[1m";
    public static final String UNDERLINE = "\033[4m";

    /**
     * 获取指定类的Logger
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * 成功日志 - 绿色
     */
    public static void success(String message, Object... args) {
        log.info("✅ " + GREEN + message + RESET, args);
    }

    /**
     * 警告日志 - 黄色
     */
    public static void warning(String message, Object... args) {
        log.warn("⚠️ " + YELLOW + message + RESET, args);
    }

    /**
     * 错误日志 - 红色
     */
    public static void error(String message, Object... args) {
        log.error("❌ " + RED + message + RESET, args);
    }

    /**
     * 错误日志 - 红色（带异常）
     */
    public static void error(String message, Throwable throwable, Object... args) {
        log.error("❌ " + RED + message + RESET, args, throwable);
    }

    /**
     * 信息日志 - 蓝色
     */
    public static void info(String message, Object... args) {
        log.info("ℹ️ " + BLUE + message + RESET, args);
    }

    /**
     * 调试日志 - 紫色
     */
    public static void debug(String message, Object... args) {
        log.debug("🐛 " + PURPLE + message + RESET, args);
    }

    /**
     * 业务日志 - 青色
     */
    public static void business(String message, Object... args) {
        log.info("💼 " + CYAN + message + RESET, args);
    }

    /**
     * 性能日志 - 黄色背景
     */
    public static void performance(String message, Object... args) {
        log.info("⚡ " + BG_YELLOW + BLACK + message + RESET, args);
    }

    /**
     * 安全日志 - 红色背景
     */
    public static void security(String message, Object... args) {
        log.warn("🔒 " + BG_RED + WHITE + message + RESET, args);
    }

    /**
     * API请求日志
     */
    public static void apiRequest(String method, String url, String params) {
        log.info("📥 " + GREEN + "API请求: {} {} 参数: {}" + RESET, method, url, params);
    }

    /**
     * API响应日志
     */
    public static void apiResponse(String url, int status, String response) {
        String color = status >= 200 && status < 300 ? GREEN : RED;
        log.info("📤 " + color + "API响应: {} 状态: {} 响应: {}" + RESET, url, status, response);
    }

    /**
     * 数据库操作日志
     */
    public static void database(String operation, String table, String condition) {
        log.debug("🗄️ " + CYAN + "数据库操作: {} 表: {} 条件: {}" + RESET, operation, table, condition);
    }

    /**
     * 缓存操作日志
     */
    public static void cache(String operation, String key, String value) {
        log.debug("💾 " + PURPLE + "缓存操作: {} 键: {} 值: {}" + RESET, operation, key, value);
    }

    /**
     * 用户操作日志
     */
    public static void userAction(String userId, String action, String details) {
        log.info("👤 " + BLUE + "用户操作: 用户ID={} 操作={} 详情={}" + RESET, userId, action, details);
    }

    /**
     * 系统启动日志
     */
    public static void startup(String message, Object... args) {
        log.info("🚀 " + BOLD + GREEN + message + RESET, args);
    }

    /**
     * 系统关闭日志
     */
    public static void shutdown(String message, Object... args) {
        log.info("🛑 " + BOLD + RED + message + RESET, args);
    }

    /**
     * 配置加载日志
     */
    public static void config(String message, Object... args) {
        log.info("⚙️ " + YELLOW + message + RESET, args);
    }

    /**
     * 定时任务日志
     */
    public static void schedule(String message, Object... args) {
        log.info("⏰ " + CYAN + message + RESET, args);
    }

    /**
     * 文件操作日志
     */
    public static void file(String operation, String filename, String result) {
        log.info("📁 " + BLUE + "文件操作: {} 文件: {} 结果: {}" + RESET, operation, filename, result);
    }

    /**
     * 支付相关日志
     */
    public static void payment(String message, Object... args) {
        log.info("💰 " + GREEN + BOLD + message + RESET, args);
    }

    /**
     * 消息推送日志
     */
    public static void message(String message, Object... args) {
        log.info("📨 " + PURPLE + message + RESET, args);
    }

    /**
     * 验证码日志
     */
    public static void verification(String message, Object... args) {
        log.info("🔐 " + YELLOW + message + RESET, args);
    }

    /**
     * 订单相关日志
     */
    public static void order(String message, Object... args) {
        log.info("📋 " + CYAN + BOLD + message + RESET, args);
    }

    /**
     * 任务相关日志
     */
    public static void task(String message, Object... args) {
        log.info("📝 " + BLUE + message + RESET, args);
    }
}
