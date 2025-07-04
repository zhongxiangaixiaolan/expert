package com.qing.expert.config;

import com.qing.expert.util.WechatPayConfigChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 微信支付启动检查器
 * 在应用启动时检查微信支付配置
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WechatPayStartupChecker implements ApplicationRunner {

    private final WechatPayConfigChecker configChecker;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始检查微信支付配置...");
        
        try {
            WechatPayConfigChecker.ConfigCheckResult result = configChecker.checkConfig();
            configChecker.printCheckResult(result);
            
            if (!result.isValid()) {
                log.warn("微信支付配置不完整，支付功能可能无法正常使用");
                log.warn("请参考配置指南完成微信支付配置");
            }
        } catch (Exception e) {
            log.error("微信支付配置检查失败", e);
        }
    }
}
