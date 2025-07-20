package com.qing.expert.config;

import com.qing.expert.common.util.LogUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

/**
 * 环境变量加载器
 * 统一从根目录.env文件加载配置
 */
public class EnvironmentLoader implements EnvironmentPostProcessor {

    private static final String ENV_FILE = ".env";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        loadEnvironmentFile(environment, ENV_FILE);
    }

    /**
     * 加载环境变量文件
     */
    private void loadEnvironmentFile(ConfigurableEnvironment environment, String fileName) {
        try {
            // 首先尝试从文件系统加载
            Resource resource = new FileSystemResource(fileName);
            if (!resource.exists()) {
                // 如果文件系统中不存在，尝试从classpath加载
                resource = new ClassPathResource(fileName);
            }

            if (resource.exists()) {
                Properties properties = new Properties();
                properties.load(resource.getInputStream());

                // 将属性添加到环境中
                PropertiesPropertySource propertySource = new PropertiesPropertySource(fileName, properties);
                environment.getPropertySources().addLast(propertySource);

                LogUtil.config("✅ 已加载环境配置文件: {}", fileName);
            }
        } catch (IOException e) {
            LogUtil.warn("⚠️ 无法加载环境配置文件: {} - {}", fileName, e.getMessage());
        }
    }
}
