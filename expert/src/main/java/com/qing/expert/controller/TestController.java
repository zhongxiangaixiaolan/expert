package com.qing.expert.controller;

import com.qing.expert.common.result.Result;
import com.qing.expert.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Tag(name = "测试接口", description = "用于测试系统基础功能")
public class TestController {

    private final SystemConfigService systemConfigService;

    @Operation(summary = "健康检查", description = "检查系统是否正常运行")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("message", "系统运行正常");
        return Result.success("健康检查通过", data);
    }

    @Operation(summary = "数据库连接测试", description = "测试数据库连接和配置读取")
    @GetMapping("/database")
    public Result<Map<String, Object>> testDatabase() {
        try {
            // 测试读取系统配置
            String systemName = systemConfigService.getConfigValue("system_name", "默认系统名称");
            String contactPhone = systemConfigService.getConfigValue("contact_phone", "未设置");

            Map<String, Object> data = new HashMap<>();
            data.put("database_status", "连接正常");
            data.put("system_name", systemName);
            data.put("contact_phone", contactPhone);
            data.put("config_count", systemConfigService.count());

            return Result.success("数据库连接测试通过", data);
        } catch (Exception e) {
            return Result.error("数据库连接测试失败：" + e.getMessage());
        }
    }

    @Operation(summary = "Redis连接测试", description = "测试Redis连接和缓存功能")
    @GetMapping("/redis")
    public Result<Map<String, Object>> testRedis() {
        try {
            // 测试缓存功能
            String testValue = systemConfigService.getConfigValue("system_name");

            Map<String, Object> data = new HashMap<>();
            data.put("redis_status", "连接正常");
            data.put("cache_test", "缓存读取成功");
            data.put("test_value", testValue);

            return Result.success("Redis连接测试通过", data);
        } catch (Exception e) {
            return Result.error("Redis连接测试失败：" + e.getMessage());
        }
    }

    @Operation(summary = "配置管理测试", description = "测试系统配置管理功能")
    @GetMapping("/config")
    public Result<Map<String, Object>> testConfig() {
        try {
            Map<String, Object> data = new HashMap<>();

            // 测试各个配置分组
            data.put("wechat_configs", systemConfigService.getWeChatConfigs());
            data.put("storage_configs", systemConfigService.getStorageConfigs());
            data.put("system_configs", systemConfigService.getSystemConfigs());

            return Result.success("配置管理测试通过", data);
        } catch (Exception e) {
            return Result.error("配置管理测试失败：" + e.getMessage());
        }
    }
}
