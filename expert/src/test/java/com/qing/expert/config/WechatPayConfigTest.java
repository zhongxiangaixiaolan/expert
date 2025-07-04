package com.qing.expert.config;

import com.qing.expert.common.constant.ConfigConstant;
import com.qing.expert.service.SystemConfigService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 微信支付配置测试类
 */
@ExtendWith(MockitoExtension.class)
class WechatPayConfigTest {

    @Mock
    private SystemConfigService systemConfigService;

    @InjectMocks
    private WechatPayConfig wechatPayConfig;

    @Test
    void testWechatPayConfigDisabled() {
        // 模拟配置开关关闭
        when(systemConfigService.getBooleanValue(ConfigConstant.WeChat.PAY_CONFIG_ENABLED, true))
                .thenReturn(false);

        // 调用配置方法
        var config = wechatPayConfig.wechatPaySDKConfig();

        // 验证返回空配置或不抛出异常
        assertNotNull(config);
        
        // 验证没有调用其他配置获取方法
        verify(systemConfigService, never()).getConfigValue(ConfigConstant.WeChat.PAY_MCH_ID, "");
    }

    @Test
    void testWechatPayConfigEnabledButEmpty() {
        // 模拟配置开关开启但配置为空
        when(systemConfigService.getBooleanValue(ConfigConstant.WeChat.PAY_CONFIG_ENABLED, true))
                .thenReturn(true);
        when(systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_MCH_ID, ""))
                .thenReturn("");
        when(systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_PRIVATE_KEY_PATH, ""))
                .thenReturn("");
        when(systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_CERT_SERIAL_NO, ""))
                .thenReturn("");
        when(systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_API_V3_KEY, ""))
                .thenReturn("");

        // 验证抛出异常
        assertThrows(Exception.class, () -> wechatPayConfig.wechatPaySDKConfig());
    }
}
