package com.qing.expert;

import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.qing.expert.common.util.LogUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 达人接单小程序启动类
 */
@SpringBootApplication(exclude = { WxMaAutoConfiguration.class })
@MapperScan("com.qing.expert.mapper")
@EnableCaching
public class ExpertApplication {

    public static void main(String[] args) {
        try {
            LogUtil.info("达人接单小程序正在启动...");

            ConfigurableApplicationContext context = SpringApplication.run(ExpertApplication.class, args);

            String port = context.getEnvironment().getProperty("server.port", "8080");
            String contextPath = context.getEnvironment().getProperty("server.servlet.context-path", "");

            LogUtil.info("达人接单小程序启动成功！");
            LogUtil.info("应用访问地址: http://localhost:{}{}", port, contextPath);
            LogUtil.info("当前激活的配置文件: {}", String.join(",", context.getEnvironment().getActiveProfiles()));

        } catch (Exception e) {
            LogUtil.error("达人接单小程序启动失败", e);
            System.exit(1);
        }
    }

}
