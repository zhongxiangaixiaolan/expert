package com.qing.expert.controller.admin;

import com.qing.expert.common.constant.ConfigConstant;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.ConfigUpdateDTO;
import com.qing.expert.entity.SystemConfig;
import com.qing.expert.service.SystemConfigService;
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
    @GetMapping("/groups")
    public Result<Map<String, Map<String, String>>> getAllConfigGroups() {
        Map<String, Map<String, String>> result = Map.of(
                ConfigConstant.Group.WECHAT, systemConfigService.getWeChatConfigs(),
                ConfigConstant.Group.STORAGE, systemConfigService.getStorageConfigs(),
                ConfigConstant.Group.SYSTEM, systemConfigService.getSystemConfigs(),
                ConfigConstant.Group.BUSINESS, systemConfigService.getBusinessConfigs());
        return Result.success("获取成功", result);
    }

    public Result<Map<String, String>> getConfigsByGroup(
        return Result.success("获取成功", configs);
    }

    public Result<Map<String, String>> getWeChatConfigs() {
        Map<String, String> configs = systemConfigService.getWeChatConfigs();
        return Result.success("获取成功", configs);
    }

    public Result<Map<String, String>> getStorageConfigs() {
        Map<String, String> configs = systemConfigService.getStorageConfigs();
        return Result.success("获取成功", configs);
    }

    public Result<Map<String, String>> getSystemConfigs() {
        Map<String, String> configs = systemConfigService.getSystemConfigs();
        return Result.success("获取成功", configs);
    }

    public Result<Map<String, String>> getBusinessConfigs() {
        Map<String, String> configs = systemConfigService.getBusinessConfigs();
        return Result.success("获取成功", configs);
    }

    public Result<Void> updateConfig(
        boolean success = systemConfigService.updateConfigValue(configKey, configValue);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    public Result<Void> batchUpdateConfigs(@Validated @RequestBody List<ConfigUpdateDTO> configUpdates) {
        boolean success = systemConfigService.batchUpdateConfigValues(configUpdates);
        if (success) {
            return Result.success("批量更新成功");
        } else {
            return Result.error("批量更新失败");
        }
    }

    public Result<Void> refreshCache() {
        systemConfigService.refreshCache();
        return Result.success("缓存刷新成功");
    }

    public Result<SystemConfig> getConfigDetail(
                .eq(SystemConfig::getConfigKey, configKey)
                .one();
        if (config != null) {
            return Result.success("获取成功", config);
        } else {
            return Result.notFound("配置不存在");
        }
    }
}
