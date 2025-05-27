package com.qing.expert.config;

import com.qing.expert.common.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 日志配置类
 *
 * @author qing
 * @since 2025-01-23
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogConfig {

    private final LogProperties logProperties;
    private final Environment environment;

    /**
     * 应用启动完成后的日志输出
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        LogUtil.startup("🎉 应用已完全启动并准备就绪！");

        // 显示日志配置状态
        String[] activeProfiles = environment.getActiveProfiles();
        String profileInfo = activeProfiles.length > 0 ? String.join(",", activeProfiles) : "default";

        LogUtil.config("📁 日志配置状态:");
        LogUtil.config("  🏷️ 当前环境: {}", profileInfo);
        LogUtil.config("  📂 日志文件路径: {}", logProperties.getFilePath());
        LogUtil.config("  📏 文件最大大小: {}", logProperties.getMaxFileSize());
        LogUtil.config("  📅 保留天数: {} 天", logProperties.getMaxHistory());

        // 根据环境显示日志输出方式
        if ("dev".equals(profileInfo) || "default".equals(profileInfo)) {
            LogUtil.config("  🔧 当前为开发模式 - 仅控制台输出");
        } else if ("prod".equals(profileInfo)) {
            LogUtil.config("  📄 生产模式 - 文件输出 + 错误日志");
        } else if ("test".equals(profileInfo)) {
            LogUtil.config("  🧪 测试模式 - 控制台输出 + 错误日志");
        }

        LogUtil.config("📋 日志颜色说明:");
        LogUtil.config("  ✅ 成功日志 - 绿色");
        LogUtil.config("  ⚠️ 警告日志 - 黄色");
        LogUtil.config("  ❌ 错误日志 - 红色");
        LogUtil.config("  ℹ️ 信息日志 - 蓝色");
        LogUtil.config("  🐛 调试日志 - 紫色");
        LogUtil.config("  💼 业务日志 - 青色");
        LogUtil.config("  📥 API请求 - 绿色");
        LogUtil.config("  📤 API响应 - 绿色/红色");
        LogUtil.config("  🗄️ 数据库操作 - 青色");
        LogUtil.config("  💾 缓存操作 - 紫色");
        LogUtil.config("  👤 用户操作 - 蓝色");
        LogUtil.config("  💰 支付相关 - 绿色加粗");
        LogUtil.config("  📨 消息推送 - 紫色");
        LogUtil.config("  📋 订单相关 - 青色加粗");
        LogUtil.config("  📝 任务相关 - 蓝色");

        LogUtil.config("🔧 可通过 /api/log-config 接口动态调整日志配置");
    }
}
