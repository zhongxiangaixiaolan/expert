package com.qing.expert.common.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 简化的日志工具类
 *
 * @author qing
 * @since 2025-01-23
 */
@Slf4j
public class LogUtil {

    /**
     * 获取指定类的Logger
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * 信息日志
     */
    public static void info(String message, Object... args) {
        log.info(message, args);
    }

    /**
     * 警告日志
     */
    public static void warn(String message, Object... args) {
        log.warn(message, args);
    }

    /**
     * 错误日志
     */
    public static void error(String message, Object... args) {
        log.error(message, args);
    }

    /**
     * 错误日志（带异常）
     */
    public static void error(String message, Throwable throwable) {
        log.error(message, throwable);
    }

    /**
     * 调试日志
     */
    public static void debug(String message, Object... args) {
        log.debug(message, args);
    }

}
