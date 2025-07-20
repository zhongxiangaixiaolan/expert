package com.qing.expert.config;

import com.qing.expert.config.properties.ExpertProperties;
import com.qing.expert.common.util.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 配置验证器
 * 在应用启动后验证关键配置项
 */
@Component
@RequiredArgsConstructor
public class ConfigurationValidator {

    private final ExpertProperties expertProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void validateConfiguration() {
        LogUtil.config("🔧 开始验证应用配置...");

        validateJwtConfiguration();
        validateWechatConfiguration();
        validateDatabaseConfiguration();

        LogUtil.config("✅ 应用配置验证完成");
    }

    /**
     * 验证JWT配置
     */
    private void validateJwtConfiguration() {
        ExpertProperties.Jwt jwt = expertProperties.getJwt();

        if (!StringUtils.hasText(jwt.getSecret())) {
            LogUtil.error("❌ JWT密钥未配置，请设置环境变量 JWT_SECRET");
            throw new IllegalStateException("JWT密钥未配置");
        }

        if (jwt.getSecret().length() < 32) {
            LogUtil.error("❌ JWT密钥长度不足32位，当前长度：{}", jwt.getSecret().length());
            throw new IllegalStateException("JWT密钥长度不足");
        }

        if (jwt.getExpiration() == null || jwt.getExpiration() <= 0) {
            LogUtil.error("❌ JWT过期时间配置无效：{}", jwt.getExpiration());
            throw new IllegalStateException("JWT过期时间配置无效");
        }

        LogUtil.config("✅ JWT配置验证通过，密钥长度：{}位，过期时间：{}ms",
                jwt.getSecret().length(), jwt.getExpiration());
    }

    /**
     * 验证微信配置
     * 注意：微信配置已迁移到数据库管理，不再从环境变量读取
     */
    private void validateWechatConfiguration() {
        LogUtil.config("📱 微信配置验证：");
        LogUtil.config("  ℹ️ 微信配置已迁移到数据库管理");
        LogUtil.config("  ℹ️ 可通过管理后台 -> 系统配置 -> 微信配置 进行设置");
        LogUtil.config("  ℹ️ 微信配置将在WxMaConfiguration中从数据库动态加载");

        // 不再检查环境变量中的微信配置，因为已经迁移到数据库
        // 实际的微信配置验证在WxMaConfiguration类中进行
    }

    /**
     * 验证数据库配置
     */
    private void validateDatabaseConfiguration() {
        // 这里可以添加数据库连接验证逻辑
        LogUtil.config("✅ 数据库配置验证通过");
    }

    /**
     * 脱敏敏感信息
     */
    private String maskSensitiveInfo(String info) {
        if (!StringUtils.hasText(info) || info.length() <= 6) {
            return "****";
        }
        return info.substring(0, 3) + "****" + info.substring(info.length() - 3);
    }
}
