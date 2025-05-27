package com.qing.expert.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 日志配置属性
 * 
 * @author qing
 * @since 2025-01-23
 */
@Data
@Component
@ConfigurationProperties(prefix = "expert.log")
public class LogProperties {

    /**
     * 是否启用文件日志 (true: 生成日志文件, false: 仅控制台输出)
     */
    private Boolean fileEnabled = true;

    /**
     * 日志文件路径
     */
    private String filePath = "logs/";

    /**
     * 是否启用SQL日志文件
     */
    private Boolean sqlFileEnabled = true;

    /**
     * 是否启用错误日志文件
     */
    private Boolean errorFileEnabled = true;

    /**
     * 日志文件最大大小
     */
    private String maxFileSize = "100MB";

    /**
     * 日志文件保留天数
     */
    private Integer maxHistory = 30;

    /**
     * 日志总大小限制
     */
    private String totalSizeCap = "3GB";
}
