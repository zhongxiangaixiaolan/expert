package com.qing.expert.util;

import com.qing.expert.common.constant.ConfigConstant;
import com.qing.expert.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 配置测试工具
 * 用于在应用启动时测试微信支付配置开关功能
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigTestTool implements CommandLineRunner {

    private final SystemConfigService systemConfigService;

    @Override
    public void run(String... args) throws Exception {
        // 简化日志输出，只在配置有问题时才输出
        testWechatPayConfigSwitch();
    }

    private void testWechatPayConfigSwitch() {
        try {
            // 获取当前配置开关状态
            Boolean configEnabled = systemConfigService.getBooleanValue(
                    ConfigConstant.WeChat.PAY_CONFIG_ENABLED, true);

            // 只在配置启用但不完整时才输出警告
            if (configEnabled) {
                // 检查配置完整性
                String merchantId = systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_MCH_ID, "");
                String apiV3Key = systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_API_V3_KEY, "");
                String certSerialNo = systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_CERT_SERIAL_NO, "");
                String privateKeyPath = systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_PRIVATE_KEY_PATH,
                        "");

                boolean hasConfig = !merchantId.isEmpty() && !apiV3Key.isEmpty()
                        && !certSerialNo.isEmpty() && !privateKeyPath.isEmpty();

                if (!hasConfig) {
                    log.warn("❌ 微信支付配置不完整，可能导致启动失败");
                    log.warn("💡 建议将 wechat_pay_config_enabled 设置为 false 以跳过验证");
                }
            }

        } catch (Exception e) {
            log.error("配置测试失败", e);
        }
    }
}
