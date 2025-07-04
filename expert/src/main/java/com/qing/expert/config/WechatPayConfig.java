package com.qing.expert.config;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.qing.expert.common.constant.ConfigConstant;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * 微信支付配置类
 * 使用官方wechatpay-java SDK，支持APIv3和自动证书管理
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WechatPayConfig {

    private final SystemConfigService systemConfigService;

    /**
     * 微信支付配置
     * 使用RSAAutoCertificateConfig实现自动证书管理
     * 只有当配置属性 wechat.pay.config.enabled=true 时才创建此Bean
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "wechat.pay.config.enabled", havingValue = "true", matchIfMissing = false)
    public Config wechatPaySDKConfig() {
        try {
            log.info("微信支付配置已启用，开始初始化配置");

            // 从数据库获取微信支付配置
            String merchantId = systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_MCH_ID, "");
            String privateKeyPath = systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_PRIVATE_KEY_PATH, "");
            String merchantSerialNumber = systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_CERT_SERIAL_NO,
                    "");
            String apiV3Key = systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_API_V3_KEY, "");

            // 验证必要配置
            validateConfig(merchantId, privateKeyPath, merchantSerialNumber, apiV3Key);

            // 构建配置
            Config config = new RSAAutoCertificateConfig.Builder()
                    .merchantId(merchantId)
                    .privateKeyFromPath(privateKeyPath)
                    .merchantSerialNumber(merchantSerialNumber)
                    .apiV3Key(apiV3Key)
                    .build();

            log.info("微信支付配置初始化成功，商户号：{}", merchantId);
            return config;

        } catch (Exception e) {
            log.error("微信支付配置初始化失败", e);
            throw new BusinessException("微信支付配置初始化失败：" + e.getMessage());
        }
    }

    /**
     * JSAPI支付服务
     * 只有当微信支付配置启用时才创建此Bean
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "wechat.pay.config.enabled", havingValue = "true", matchIfMissing = false)
    public JsapiService jsapiService(Config wechatPaySDKConfig) {
        log.info("创建JSAPI支付服务");
        return new JsapiService.Builder().config(wechatPaySDKConfig).build();
    }

    /**
     * JSAPI支付扩展服务
     * 提供便捷的支付参数生成功能
     * 只有当微信支付配置启用时才创建此Bean
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "wechat.pay.config.enabled", havingValue = "true", matchIfMissing = false)
    public JsapiServiceExtension jsapiServiceExtension(Config wechatPaySDKConfig) {
        log.info("创建JSAPI支付扩展服务");
        return new JsapiServiceExtension.Builder().config(wechatPaySDKConfig).build();
    }

    /**
     * 验证微信支付配置
     */
    private void validateConfig(String merchantId, String privateKeyPath, String merchantSerialNumber,
            String apiV3Key) {
        if (!StringUtils.hasText(merchantId)) {
            throw new BusinessException("微信支付商户号未配置");
        }
        if (!StringUtils.hasText(privateKeyPath)) {
            throw new BusinessException("微信支付私钥路径未配置");
        }
        if (!StringUtils.hasText(merchantSerialNumber)) {
            throw new BusinessException("微信支付证书序列号未配置");
        }
        if (!StringUtils.hasText(apiV3Key)) {
            throw new BusinessException("微信支付APIv3密钥未配置");
        }
    }
}
