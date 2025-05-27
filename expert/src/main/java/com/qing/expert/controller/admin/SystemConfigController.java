package com.qing.expert.controller.admin;

import com.qing.expert.common.constant.ConfigConstant;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.ConfigUpdateDTO;
import com.qing.expert.entity.SystemConfig;
import com.qing.expert.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统配置管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/config")
@RequiredArgsConstructor
@Tag(name = "系统配置管理", description = "系统配置的增删改查接口")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @Operation(summary = "获取所有配置分组", description = "获取系统所有配置分组信息")
    @GetMapping("/groups")
    public Result<Map<String, Map<String, String>>> getAllConfigGroups() {
        Map<String, Map<String, String>> result = Map.of(
                ConfigConstant.Group.WECHAT, systemConfigService.getWeChatConfigs(),
                ConfigConstant.Group.STORAGE, systemConfigService.getStorageConfigs(),
                ConfigConstant.Group.SYSTEM, systemConfigService.getSystemConfigs(),
                ConfigConstant.Group.BUSINESS, systemConfigService.getBusinessConfigs());
        return Result.success("获取成功", result);
    }

    @Operation(summary = "根据分组获取配置", description = "根据配置分组获取配置列表")
    @GetMapping("/group/{group}")
    public Result<Map<String, String>> getConfigsByGroup(
            @Parameter(description = "配置分组") @PathVariable String group) {
        Map<String, String> configs = systemConfigService.getConfigMapByGroup(group);
        return Result.success("获取成功", configs);
    }

    @Operation(summary = "获取微信配置", description = "获取微信小程序和支付相关配置")
    @GetMapping("/wechat")
    public Result<Map<String, String>> getWeChatConfigs() {
        Map<String, String> configs = systemConfigService.getWeChatConfigs();
        return Result.success("获取成功", configs);
    }

    @Operation(summary = "获取存储配置", description = "获取文件存储相关配置")
    @GetMapping("/storage")
    public Result<Map<String, String>> getStorageConfigs() {
        Map<String, String> configs = systemConfigService.getStorageConfigs();
        return Result.success("获取成功", configs);
    }

    @Operation(summary = "获取系统配置", description = "获取系统基础配置")
    @GetMapping("/system")
    public Result<Map<String, String>> getSystemConfigs() {
        Map<String, String> configs = systemConfigService.getSystemConfigs();
        return Result.success("获取成功", configs);
    }

    @Operation(summary = "获取业务配置", description = "获取业务相关配置")
    @GetMapping("/business")
    public Result<Map<String, String>> getBusinessConfigs() {
        Map<String, String> configs = systemConfigService.getBusinessConfigs();
        return Result.success("获取成功", configs);
    }

    @Operation(summary = "更新单个配置", description = "更新指定配置项的值")
    @PutMapping("/{configKey}")
    public Result<Void> updateConfig(
            @Parameter(description = "配置键") @PathVariable String configKey,
            @Parameter(description = "配置值") @RequestParam String configValue) {
        boolean success = systemConfigService.updateConfigValue(configKey, configValue);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    @Operation(summary = "批量更新配置", description = "批量更新多个配置项")
    @PutMapping("/batch")
    public Result<Void> batchUpdateConfigs(@Validated @RequestBody List<ConfigUpdateDTO> configUpdates) {
        boolean success = systemConfigService.batchUpdateConfigValues(configUpdates);
        if (success) {
            return Result.success("批量更新成功");
        } else {
            return Result.error("批量更新失败");
        }
    }

    @Operation(summary = "刷新配置缓存", description = "清除配置缓存，重新加载配置")
    @PostMapping("/refresh")
    public Result<Void> refreshCache() {
        systemConfigService.refreshCache();
        return Result.success("缓存刷新成功");
    }

    @Operation(summary = "获取配置详情", description = "根据配置键获取配置详细信息")
    @GetMapping("/detail/{configKey}")
    public Result<SystemConfig> getConfigDetail(
            @Parameter(description = "配置键") @PathVariable String configKey) {
        SystemConfig config = systemConfigService.lambdaQuery()
                .eq(SystemConfig::getConfigKey, configKey)
                .one();
        if (config != null) {
            return Result.success("获取成功", config);
        } else {
            return Result.notFound("配置不存在");
        }
    }
}
