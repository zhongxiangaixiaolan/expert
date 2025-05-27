package com.qing.expert.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.dto.ConfigUpdateDTO;
import com.qing.expert.entity.SystemConfig;

import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口
 */
public interface SystemConfigService extends IService<SystemConfig> {

    /**
     * 根据配置键获取配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置键获取配置值（带默认值）
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 根据配置键获取整数配置值
     */
    Integer getIntValue(String configKey);

    /**
     * 根据配置键获取整数配置值（带默认值）
     */
    Integer getIntValue(String configKey, Integer defaultValue);

    /**
     * 根据配置键获取布尔配置值
     */
    Boolean getBooleanValue(String configKey);

    /**
     * 根据配置键获取布尔配置值（带默认值）
     */
    Boolean getBooleanValue(String configKey, Boolean defaultValue);

    /**
     * 根据配置分组获取配置列表
     */
    List<SystemConfig> getConfigsByGroup(String configGroup);

    /**
     * 根据配置分组获取配置Map
     */
    Map<String, String> getConfigMapByGroup(String configGroup);

    /**
     * 更新配置值
     */
    boolean updateConfigValue(String configKey, String configValue);

    /**
     * 批量更新配置
     */
    boolean batchUpdateConfigs(List<SystemConfig> configs);

    /**
     * 批量更新配置值（简化版本）
     */
    boolean batchUpdateConfigValues(List<ConfigUpdateDTO> configUpdates);

    /**
     * 刷新配置缓存
     */
    void refreshCache();

    /**
     * 获取微信小程序配置
     */
    Map<String, String> getWeChatConfigs();

    /**
     * 获取文件存储配置
     */
    Map<String, String> getStorageConfigs();

    /**
     * 获取系统基础配置
     */
    Map<String, String> getSystemConfigs();

    /**
     * 获取业务配置
     */
    Map<String, String> getBusinessConfigs();
}
