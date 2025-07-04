package com.qing.expert.util;

import com.qing.expert.common.constant.ConfigConstant;
import com.qing.expert.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 配置调试工具
 * 用于调试配置读取问题
 */
@Slf4j
@Component
@Order(1) // 确保在其他组件之前运行
@RequiredArgsConstructor
public class ConfigDebugTool implements CommandLineRunner {

    private final SystemConfigService systemConfigService;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== 配置调试工具启动 ===");
        
        try {
            // 测试配置读取
            String configKey = ConfigConstant.WeChat.PAY_CONFIG_ENABLED;
            
            // 获取字符串值
            String stringValue = systemConfigService.getConfigValue(configKey, "default");
            log.info("配置键: {}", configKey);
            log.info("字符串值: '{}'", stringValue);
            log.info("字符串长度: {}", stringValue.length());
            
            // 获取布尔值
            Boolean booleanValue = systemConfigService.getBooleanValue(configKey, true);
            log.info("布尔值: {}", booleanValue);
            
            // 手动解析
            boolean manualParse = Boolean.parseBoolean(stringValue);
            log.info("手动解析: {}", manualParse);
            
            // 测试不同的值
            log.info("'true'.parseBoolean(): {}", Boolean.parseBoolean("true"));
            log.info("'false'.parseBoolean(): {}", Boolean.parseBoolean("false"));
            log.info("'TRUE'.parseBoolean(): {}", Boolean.parseBoolean("TRUE"));
            log.info("'FALSE'.parseBoolean(): {}", Boolean.parseBoolean("FALSE"));
            
        } catch (Exception e) {
            log.error("配置调试失败", e);
        }
        
        log.info("=== 配置调试完成 ===");
    }
}
