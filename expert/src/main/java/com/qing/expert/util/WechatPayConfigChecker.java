package com.qing.expert.util;

import com.qing.expert.common.constant.ConfigConstant;
import com.qing.expert.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 微信支付配置检查工具
 * 用于验证微信支付配置的完整性和有效性
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WechatPayConfigChecker {

    private final SystemConfigService systemConfigService;

    /**
     * 检查微信支付配置
     * @return 配置检查结果
     */
    public ConfigCheckResult checkConfig() {
        ConfigCheckResult result = new ConfigCheckResult();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // 检查必要配置项
        String merchantId = systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_MCH_ID, "");
        if (!StringUtils.hasText(merchantId)) {
            errors.add("微信支付商户号(wechat_pay_mch_id)未配置");
        } else {
            log.info("✓ 商户号已配置：{}", merchantId);
        }

        String apiV3Key = systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_API_V3_KEY, "");
        if (!StringUtils.hasText(apiV3Key)) {
            errors.add("微信支付APIv3密钥(wechat_pay_api_v3_key)未配置");
        } else {
            log.info("✓ APIv3密钥已配置：{}***", apiV3Key.substring(0, Math.min(8, apiV3Key.length())));
        }

        String certSerialNo = systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_CERT_SERIAL_NO, "");
        if (!StringUtils.hasText(certSerialNo)) {
            errors.add("微信支付证书序列号(wechat_pay_cert_serial_no)未配置");
        } else {
            log.info("✓ 证书序列号已配置：{}", certSerialNo);
        }

        String privateKeyPath = systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_PRIVATE_KEY_PATH, "");
        if (!StringUtils.hasText(privateKeyPath)) {
            errors.add("微信支付私钥文件路径(wechat_pay_private_key_path)未配置");
        } else {
            // 检查文件是否存在
            File privateKeyFile = new File(privateKeyPath);
            if (!privateKeyFile.exists()) {
                errors.add("微信支付私钥文件不存在：" + privateKeyPath);
            } else if (!privateKeyFile.canRead()) {
                errors.add("微信支付私钥文件无法读取：" + privateKeyPath);
            } else {
                log.info("✓ 私钥文件已配置且可读：{}", privateKeyPath);
            }
        }

        String appId = systemConfigService.getConfigValue(ConfigConstant.WeChat.MINIAPP_APP_ID, "");
        if (!StringUtils.hasText(appId)) {
            errors.add("微信小程序AppID(wechat_miniapp_app_id)未配置");
        } else {
            log.info("✓ 小程序AppID已配置：{}", appId);
        }

        String notifyUrl = systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_NOTIFY_URL, "");
        if (!StringUtils.hasText(notifyUrl)) {
            warnings.add("微信支付回调地址(wechat_pay_notify_url)未配置，将使用默认值");
        } else if (!notifyUrl.startsWith("https://")) {
            warnings.add("微信支付回调地址建议使用HTTPS：" + notifyUrl);
        } else {
            log.info("✓ 支付回调地址已配置：{}", notifyUrl);
        }

        result.setErrors(errors);
        result.setWarnings(warnings);
        result.setValid(errors.isEmpty());

        return result;
    }

    /**
     * 打印配置检查结果
     */
    public void printCheckResult(ConfigCheckResult result) {
        log.info("=== 微信支付配置检查结果 ===");
        
        if (result.isValid()) {
            log.info("✅ 配置检查通过，微信支付功能可正常使用");
        } else {
            log.error("❌ 配置检查失败，发现 {} 个错误", result.getErrors().size());
            for (String error : result.getErrors()) {
                log.error("  - {}", error);
            }
        }

        if (!result.getWarnings().isEmpty()) {
            log.warn("⚠️ 发现 {} 个警告", result.getWarnings().size());
            for (String warning : result.getWarnings()) {
                log.warn("  - {}", warning);
            }
        }

        if (!result.isValid()) {
            log.info("📝 配置指南：");
            log.info("1. 登录微信商户平台 https://pay.weixin.qq.com/");
            log.info("2. 获取商户号、APIv3密钥");
            log.info("3. 下载API证书，获取证书序列号和私钥文件");
            log.info("4. 将配置信息更新到数据库 system_configs 表中");
        }
    }

    /**
     * 配置检查结果
     */
    public static class ConfigCheckResult {
        private boolean valid;
        private List<String> errors = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }

        public List<String> getWarnings() {
            return warnings;
        }

        public void setWarnings(List<String> warnings) {
            this.warnings = warnings;
        }
    }
}
