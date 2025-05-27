package com.qing.expert.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qing.expert.common.constant.ConfigConstant;
import com.qing.expert.dto.ConfigUpdateDTO;
import com.qing.expert.entity.SystemConfig;
import com.qing.expert.mapper.SystemConfigMapper;
import com.qing.expert.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现类
 */
@Slf4j
@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig>
        implements SystemConfigService {

    @Override
    @Cacheable(value = "config", key = "#configKey")
    public String getConfigValue(String configKey) {
        return getConfigValue(configKey, null);
    }

    @Override
    @Cacheable(value = "config", key = "#configKey")
    public String getConfigValue(String configKey, String defaultValue) {
        if (StrUtil.isBlank(configKey)) {
            return defaultValue;
        }

        SystemConfig config = baseMapper.selectByKey(configKey);
        if (config != null && StrUtil.isNotBlank(config.getConfigValue())) {
            return config.getConfigValue();
        }
        return defaultValue;
    }

    @Override
    public Integer getIntValue(String configKey) {
        return getIntValue(configKey, null);
    }

    @Override
    public Integer getIntValue(String configKey, Integer defaultValue) {
        String value = getConfigValue(configKey);
        if (StrUtil.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("配置值转换为整数失败：{} = {}", configKey, value);
            return defaultValue;
        }
    }

    @Override
    public Boolean getBooleanValue(String configKey) {
        return getBooleanValue(configKey, null);
    }

    @Override
    public Boolean getBooleanValue(String configKey, Boolean defaultValue) {
        String value = getConfigValue(configKey);
        if (StrUtil.isBlank(value)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    @Override
    @Cacheable(value = "configGroup", key = "#configGroup")
    public List<SystemConfig> getConfigsByGroup(String configGroup) {
        return baseMapper.selectByGroup(configGroup);
    }

    @Override
    @Cacheable(value = "configGroupMap", key = "#configGroup")
    public Map<String, String> getConfigMapByGroup(String configGroup) {
        List<SystemConfig> configs = getConfigsByGroup(configGroup);
        return configs.stream()
                .collect(Collectors.toMap(
                        SystemConfig::getConfigKey,
                        config -> StrUtil.nullToDefault(config.getConfigValue(), ""),
                        (existing, replacement) -> existing));
    }

    @Override
    @CacheEvict(value = { "config", "configGroup", "configGroupMap" }, allEntries = true)
    public boolean updateConfigValue(String configKey, String configValue) {
        if (StrUtil.isBlank(configKey)) {
            return false;
        }

        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);

        SystemConfig config = getOne(wrapper);
        if (config != null) {
            config.setConfigValue(configValue);
            return updateById(config);
        }
        return false;
    }

    @Override
    @CacheEvict(value = { "config", "configGroup", "configGroupMap" }, allEntries = true)
    public boolean batchUpdateConfigs(List<SystemConfig> configs) {
        if (configs == null || configs.isEmpty()) {
            return false;
        }

        try {
            return baseMapper.batchUpdate(configs) > 0;
        } catch (Exception e) {
            log.error("批量更新配置失败", e);
            return false;
        }
    }

    @Override
    @CacheEvict(value = { "config", "configGroup", "configGroupMap" }, allEntries = true)
    public boolean batchUpdateConfigValues(List<ConfigUpdateDTO> configUpdates) {
        if (configUpdates == null || configUpdates.isEmpty()) {
            return false;
        }

        try {
            // 转换为SystemConfig对象
            List<SystemConfig> configs = configUpdates.stream()
                    .map(dto -> {
                        SystemConfig config = new SystemConfig();
                        config.setConfigKey(dto.getConfigKey());
                        config.setConfigValue(dto.getConfigValue());
                        return config;
                    })
                    .collect(Collectors.toList());

            return baseMapper.batchUpdate(configs) > 0;
        } catch (Exception e) {
            log.error("批量更新配置值失败", e);
            return false;
        }
    }

    @Override
    @CacheEvict(value = { "config", "configGroup", "configGroupMap" }, allEntries = true)
    public void refreshCache() {
        log.info("刷新系统配置缓存");
    }

    @Override
    public Map<String, String> getWeChatConfigs() {
        return getConfigMapByGroup(ConfigConstant.Group.WECHAT);
    }

    @Override
    public Map<String, String> getStorageConfigs() {
        return getConfigMapByGroup(ConfigConstant.Group.STORAGE);
    }

    @Override
    public Map<String, String> getSystemConfigs() {
        return getConfigMapByGroup(ConfigConstant.Group.SYSTEM);
    }

    @Override
    public Map<String, String> getBusinessConfigs() {
        return getConfigMapByGroup(ConfigConstant.Group.BUSINESS);
    }
}
