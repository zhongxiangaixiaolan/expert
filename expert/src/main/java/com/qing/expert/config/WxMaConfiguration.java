package com.qing.expert.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;

import cn.hutool.core.util.StrUtil;
import com.qing.expert.common.constant.ConfigConstant;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 微信小程序配置类
 * 从数据库动态读取配置，替代默认的配置文件方式
 */
@Slf4j
@Configuration
public class WxMaConfiguration {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 微信小程序配置
     * 从数据库动态读取配置
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public WxMaConfig wxMaConfig() {
        try {
            // 从数据库获取微信小程序配置
            String appId = systemConfigService.getConfigValue(ConfigConstant.WeChat.MINIAPP_APP_ID);
            String appSecret = systemConfigService.getConfigValue(ConfigConstant.WeChat.MINIAPP_APP_SECRET);
            String token = systemConfigService.getConfigValue(ConfigConstant.WeChat.MINIAPP_TOKEN, "");
            String aesKey = systemConfigService.getConfigValue(ConfigConstant.WeChat.MINIAPP_AES_KEY, "");

            // 验证必要配置
            if (StrUtil.isBlank(appId) || StrUtil.isBlank(appSecret)) {
                log.error("微信小程序配置不完整，AppID或AppSecret为空，请通过管理后台配置");
                throw new BusinessException("微信小程序配置不完整，请在系统配置中设置AppID和AppSecret");
            }

            // 使用默认配置实现，让 WxJava 框架自动处理 Redis 集成
            WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();

            // 设置配置参数
            config.setAppid(appId);
            config.setSecret(appSecret);

            if (StrUtil.isNotBlank(token)) {
                config.setToken(token);
            }

            if (StrUtil.isNotBlank(aesKey)) {
                config.setAesKey(aesKey);
            }

            // 设置消息格式为JSON
            config.setMsgDataFormat("JSON");

            return config;

        } catch (Exception e) {
            log.error("微信小程序配置初始化失败", e);
            // 返回一个默认配置，避免应用启动失败
            WxMaDefaultConfigImpl defaultConfig = new WxMaDefaultConfigImpl();
            defaultConfig.setAppid("DEFAULT_APPID");
            defaultConfig.setSecret("DEFAULT_SECRET");
            return defaultConfig;
        }
    }

    /**
     * 微信小程序服务
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public WxMaService wxMaService(WxMaConfig wxMaConfig) {
        try {
            WxMaService wxMaService = new WxMaServiceImpl();
            wxMaService.setWxMaConfig(wxMaConfig);

            // 验证配置是否有效
            String appId = wxMaConfig.getAppid();
            if ("DEFAULT_APPID".equals(appId)) {
                log.warn("微信小程序服务使用默认配置，功能将不可用");
            }

            return wxMaService;
        } catch (Exception e) {
            log.error("创建微信小程序服务失败", e);
            throw new BusinessException("微信小程序服务初始化失败：" + e.getMessage());
        }
    }

    /**
     * 获取微信小程序服务的静态方法
     * 兼容原有代码调用方式
     */
    public static WxMaService getMaService() {
        // 这个方法主要用于兼容，实际使用时建议直接注入WxMaService
        throw new UnsupportedOperationException("请直接注入WxMaService使用，不要使用静态方法");
    }
}
