package com.qing.expert.controller;

import com.qing.expert.common.result.Result;
import com.qing.expert.common.util.LogUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志级别动态调整控制器
 * 
 * @author qing
 * @since 2025-01-23
 */
@Tag(name = "日志级别管理", description = "动态调整日志级别")
@RestController
@RequestMapping("/api/log-level")
public class LogLevelController {

    @Operation(summary = "获取当前日志级别")
    @GetMapping("/current")
    public Result<Map<String, String>> getCurrentLogLevels() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Map<String, String> logLevels = new HashMap<>();
        
        // 获取主要包的日志级别
        String[] packages = {
            "ROOT",
            "com.qing.expert",
            "org.springframework",
            "org.springframework.security",
            "org.springframework.web",
            "org.springframework.boot",
            "org.apache.catalina",
            "org.hibernate",
            "com.baomidou.mybatisplus"
        };
        
        for (String packageName : packages) {
            Logger logger = "ROOT".equals(packageName) ? 
                loggerContext.getLogger(Logger.ROOT_LOGGER_NAME) : 
                loggerContext.getLogger(packageName);
            
            Level level = logger.getLevel();
            logLevels.put(packageName, level != null ? level.toString() : "INHERITED");
        }
        
        LogUtil.config("获取当前日志级别配置");
        return Result.success(logLevels);
    }

    @Operation(summary = "设置指定包的日志级别")
    @PostMapping("/set")
    public Result<String> setLogLevel(
            @Parameter(description = "包名") @RequestParam String packageName,
            @Parameter(description = "日志级别") @RequestParam String level) {
        
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger logger = "ROOT".equals(packageName) ? 
                loggerContext.getLogger(Logger.ROOT_LOGGER_NAME) : 
                loggerContext.getLogger(packageName);
            
            Level logLevel = Level.valueOf(level.toUpperCase());
            logger.setLevel(logLevel);
            
            LogUtil.config("设置日志级别: 包={}, 级别={}", packageName, level);
            return Result.success("日志级别设置成功");
            
        } catch (IllegalArgumentException e) {
            LogUtil.warning("无效的日志级别: {}", level);
            return Result.error("无效的日志级别: " + level);
        } catch (Exception e) {
            LogUtil.error("设置日志级别失败", e);
            return Result.error("设置日志级别失败: " + e.getMessage());
        }
    }

    @Operation(summary = "重置日志级别为默认配置")
    @PostMapping("/reset")
    public Result<String> resetLogLevels() {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            
            // 重置为默认配置
            loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.INFO);
            loggerContext.getLogger("com.qing.expert").setLevel(Level.DEBUG);
            loggerContext.getLogger("org.springframework").setLevel(Level.WARN);
            loggerContext.getLogger("org.springframework.security").setLevel(Level.WARN);
            loggerContext.getLogger("org.springframework.web").setLevel(Level.WARN);
            loggerContext.getLogger("org.springframework.boot").setLevel(Level.WARN);
            loggerContext.getLogger("org.apache.catalina").setLevel(Level.WARN);
            loggerContext.getLogger("org.hibernate").setLevel(Level.WARN);
            loggerContext.getLogger("com.baomidou.mybatisplus").setLevel(Level.WARN);
            
            LogUtil.config("重置日志级别为默认配置");
            return Result.success("日志级别重置成功");
            
        } catch (Exception e) {
            LogUtil.error("重置日志级别失败", e);
            return Result.error("重置日志级别失败: " + e.getMessage());
        }
    }

    @Operation(summary = "开启调试模式")
    @PostMapping("/debug-mode")
    public Result<String> enableDebugMode() {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            
            // 设置为调试模式
            loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.DEBUG);
            loggerContext.getLogger("com.qing.expert").setLevel(Level.DEBUG);
            loggerContext.getLogger("org.springframework").setLevel(Level.INFO);
            loggerContext.getLogger("org.springframework.security").setLevel(Level.DEBUG);
            loggerContext.getLogger("org.springframework.web").setLevel(Level.DEBUG);
            
            LogUtil.config("开启调试模式");
            LogUtil.debug("调试模式已开启，将显示更详细的日志信息");
            return Result.success("调试模式已开启");
            
        } catch (Exception e) {
            LogUtil.error("开启调试模式失败", e);
            return Result.error("开启调试模式失败: " + e.getMessage());
        }
    }

    @Operation(summary = "关闭调试模式")
    @PostMapping("/production-mode")
    public Result<String> enableProductionMode() {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            
            // 设置为生产模式
            loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.WARN);
            loggerContext.getLogger("com.qing.expert").setLevel(Level.INFO);
            loggerContext.getLogger("org.springframework").setLevel(Level.WARN);
            loggerContext.getLogger("org.springframework.security").setLevel(Level.WARN);
            loggerContext.getLogger("org.springframework.web").setLevel(Level.WARN);
            
            LogUtil.config("开启生产模式");
            LogUtil.info("生产模式已开启，将减少日志输出");
            return Result.success("生产模式已开启");
            
        } catch (Exception e) {
            LogUtil.error("开启生产模式失败", e);
            return Result.error("开启生产模式失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取支持的日志级别")
    @GetMapping("/levels")
    public Result<String[]> getSupportedLevels() {
        String[] levels = {"TRACE", "DEBUG", "INFO", "WARN", "ERROR", "OFF"};
        LogUtil.info("获取支持的日志级别列表");
        return Result.success(levels);
    }
}
