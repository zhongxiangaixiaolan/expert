package com.qing.expert.controller;

import com.qing.expert.common.result.Result;
import com.qing.expert.common.util.LogUtil;
import com.qing.expert.config.LogProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志配置管理控制器
 * 
 * @author qing
 * @since 2025-01-23
 */
@Tag(name = "日志配置管理", description = "管理日志文件生成开关和配置")
@RestController
@RequestMapping("/api/log-config")
@RequiredArgsConstructor
public class LogConfigController {

    private final LogProperties logProperties;

    @Operation(summary = "获取当前日志配置")
    @GetMapping("/current")
    public Result<LogProperties> getCurrentConfig() {
        LogUtil.config("获取当前日志配置");
        return Result.success(logProperties);
    }

    @Operation(summary = "获取日志配置状态")
    @GetMapping("/status")
    public Result<Map<String, Object>> getLogStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("fileEnabled", logProperties.getFileEnabled());
        status.put("sqlFileEnabled", logProperties.getSqlFileEnabled());
        status.put("errorFileEnabled", logProperties.getErrorFileEnabled());
        status.put("filePath", logProperties.getFilePath());
        status.put("maxFileSize", logProperties.getMaxFileSize());
        status.put("maxHistory", logProperties.getMaxHistory());
        status.put("totalSizeCap", logProperties.getTotalSizeCap());
        
        LogUtil.config("获取日志配置状态");
        return Result.success(status);
    }

    @Operation(summary = "设置文件日志开关")
    @PostMapping("/file-enabled")
    public Result<String> setFileEnabled(
            @Parameter(description = "是否启用文件日志") @RequestParam Boolean enabled) {
        
        Boolean oldValue = logProperties.getFileEnabled();
        logProperties.setFileEnabled(enabled);
        
        if (enabled) {
            LogUtil.config("✅ 文件日志已启用 - 日志将保存到文件: {}", logProperties.getFilePath());
        } else {
            LogUtil.config("❌ 文件日志已禁用 - 仅控制台输出");
        }
        
        LogUtil.config("文件日志开关变更: {} -> {}", oldValue, enabled);
        
        return Result.success(enabled ? "文件日志已启用" : "文件日志已禁用，仅控制台输出");
    }

    @Operation(summary = "设置SQL日志文件开关")
    @PostMapping("/sql-file-enabled")
    public Result<String> setSqlFileEnabled(
            @Parameter(description = "是否启用SQL日志文件") @RequestParam Boolean enabled) {
        
        Boolean oldValue = logProperties.getSqlFileEnabled();
        logProperties.setSqlFileEnabled(enabled);
        
        LogUtil.config("SQL日志文件开关变更: {} -> {}", oldValue, enabled);
        
        return Result.success(enabled ? "SQL日志文件已启用" : "SQL日志文件已禁用");
    }

    @Operation(summary = "设置错误日志文件开关")
    @PostMapping("/error-file-enabled")
    public Result<String> setErrorFileEnabled(
            @Parameter(description = "是否启用错误日志文件") @RequestParam Boolean enabled) {
        
        Boolean oldValue = logProperties.getErrorFileEnabled();
        logProperties.setErrorFileEnabled(enabled);
        
        LogUtil.config("错误日志文件开关变更: {} -> {}", oldValue, enabled);
        
        return Result.success(enabled ? "错误日志文件已启用" : "错误日志文件已禁用");
    }

    @Operation(summary = "设置日志文件路径")
    @PostMapping("/file-path")
    public Result<String> setFilePath(
            @Parameter(description = "日志文件路径") @RequestParam String filePath) {
        
        String oldValue = logProperties.getFilePath();
        
        // 确保路径以 / 结尾
        if (!filePath.endsWith("/")) {
            filePath += "/";
        }
        
        logProperties.setFilePath(filePath);
        
        LogUtil.config("日志文件路径变更: {} -> {}", oldValue, filePath);
        
        return Result.success("日志文件路径已更新为: " + filePath);
    }

    @Operation(summary = "设置日志文件最大大小")
    @PostMapping("/max-file-size")
    public Result<String> setMaxFileSize(
            @Parameter(description = "日志文件最大大小 (如: 100MB)") @RequestParam String maxFileSize) {
        
        String oldValue = logProperties.getMaxFileSize();
        logProperties.setMaxFileSize(maxFileSize);
        
        LogUtil.config("日志文件最大大小变更: {} -> {}", oldValue, maxFileSize);
        
        return Result.success("日志文件最大大小已更新为: " + maxFileSize);
    }

    @Operation(summary = "设置日志保留天数")
    @PostMapping("/max-history")
    public Result<String> setMaxHistory(
            @Parameter(description = "日志保留天数") @RequestParam Integer maxHistory) {
        
        Integer oldValue = logProperties.getMaxHistory();
        logProperties.setMaxHistory(maxHistory);
        
        LogUtil.config("日志保留天数变更: {} -> {}", oldValue, maxHistory);
        
        return Result.success("日志保留天数已更新为: " + maxHistory + " 天");
    }

    @Operation(summary = "重置日志配置为默认值")
    @PostMapping("/reset")
    public Result<String> resetConfig() {
        logProperties.setFileEnabled(true);
        logProperties.setSqlFileEnabled(true);
        logProperties.setErrorFileEnabled(true);
        logProperties.setFilePath("logs/");
        logProperties.setMaxFileSize("100MB");
        logProperties.setMaxHistory(30);
        logProperties.setTotalSizeCap("3GB");
        
        LogUtil.config("日志配置已重置为默认值");
        
        return Result.success("日志配置已重置为默认值");
    }

    @Operation(summary = "开发模式配置 (仅控制台输出)")
    @PostMapping("/dev-mode")
    public Result<String> setDevMode() {
        logProperties.setFileEnabled(false);
        logProperties.setSqlFileEnabled(false);
        logProperties.setErrorFileEnabled(false);
        
        LogUtil.config("🔧 已切换到开发模式 - 仅控制台输出，不生成日志文件");
        
        return Result.success("已切换到开发模式 - 仅控制台输出");
    }

    @Operation(summary = "生产模式配置 (启用所有日志文件)")
    @PostMapping("/prod-mode")
    public Result<String> setProdMode() {
        logProperties.setFileEnabled(true);
        logProperties.setSqlFileEnabled(true);
        logProperties.setErrorFileEnabled(true);
        logProperties.setFilePath("logs/");
        
        LogUtil.config("🚀 已切换到生产模式 - 启用所有日志文件");
        
        return Result.success("已切换到生产模式 - 启用所有日志文件");
    }

    @Operation(summary = "测试模式配置 (仅启用错误日志文件)")
    @PostMapping("/test-mode")
    public Result<String> setTestMode() {
        logProperties.setFileEnabled(false);
        logProperties.setSqlFileEnabled(false);
        logProperties.setErrorFileEnabled(true);
        
        LogUtil.config("🧪 已切换到测试模式 - 仅启用错误日志文件");
        
        return Result.success("已切换到测试模式 - 仅启用错误日志文件");
    }

    @Operation(summary = "获取配置建议")
    @GetMapping("/suggestions")
    public Result<Map<String, String>> getConfigSuggestions() {
        Map<String, String> suggestions = new HashMap<>();
        suggestions.put("开发环境", "建议关闭文件日志，仅使用控制台输出，便于实时查看");
        suggestions.put("测试环境", "建议仅启用错误日志文件，减少磁盘占用");
        suggestions.put("生产环境", "建议启用所有日志文件，便于问题排查和监控");
        suggestions.put("性能测试", "建议关闭所有文件日志，避免IO影响性能测试结果");
        suggestions.put("调试模式", "建议启用所有日志文件，并设置较短的保留时间");
        
        LogUtil.info("获取日志配置建议");
        
        return Result.success(suggestions);
    }
}
