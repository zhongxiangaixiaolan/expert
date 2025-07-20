package com.qing.expert.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 达人接单平台配置属性
 */
@Component
@ConfigurationProperties(prefix = "expert")
@Data
public class ExpertProperties {

    /**
     * JWT配置
     */
    private Jwt jwt = new Jwt();

    /**
     * 微信配置
     */
    private Wechat wechat = new Wechat();

    /**
     * 微信支付配置
     */
    private WechatPay wechatPay = new WechatPay();

    /**
     * 阿里云OSS配置
     */
    private Oss oss = new Oss();

    /**
     * 文件上传配置
     */
    private File file = new File();

    /**
     * 日志配置
     */
    private Log log = new Log();

    /**
     * 邮件配置
     */
    private Mail mail = new Mail();

    /**
     * 短信配置
     */
    private Sms sms = new Sms();

    @Data
    public static class Jwt {
        /**
         * JWT密钥
         */
        private String secret;

        /**
         * JWT过期时间（毫秒）
         */
        private Long expiration = 86400000L;

        /**
         * JWT请求头名称
         */
        private String header = "Authorization";

        /**
         * JWT前缀
         */
        private String prefix = "Bearer ";
    }

    @Data
    public static class Wechat {
        /**
         * 微信小程序AppID
         */
        private String appid;

        /**
         * 微信小程序Secret
         */
        private String secret;
    }

    @Data
    public static class WechatPay {
        /**
         * 商户号
         */
        private String mchid;

        /**
         * 私钥路径
         */
        private String privateKeyPath;

        /**
         * 商户证书序列号
         */
        private String merchantSerialNumber;

        /**
         * API v3密钥
         */
        private String apiV3Key;
    }

    @Data
    public static class Oss {
        /**
         * OSS端点
         */
        private String endpoint;

        /**
         * 访问密钥ID
         */
        private String accessKeyId;

        /**
         * 访问密钥Secret
         */
        private String accessKeySecret;

        /**
         * 存储桶名称
         */
        private String bucketName;

        /**
         * 访问域名
         */
        private String domain;
    }

    @Data
    public static class File {
        /**
         * 文件上传路径
         */
        private String uploadPath = "src/main/resources/static/";

        /**
         * 文件大小限制（字节）
         */
        private Long maxSize = 10485760L;

        /**
         * 允许的文件类型
         */
        private String allowedTypes = "jpg,jpeg,png,gif,pdf,doc,docx";
    }

    @Data
    public static class Log {
        /**
         * 是否启用文件日志
         */
        private Boolean fileEnabled = false;

        /**
         * 日志文件路径
         */
        private String filePath = "logs/";

        /**
         * 是否启用SQL日志文件
         */
        private Boolean sqlFileEnabled = false;

        /**
         * 是否启用错误日志文件
         */
        private Boolean errorFileEnabled = false;

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

    @Data
    public static class Mail {
        /**
         * 邮件服务器主机
         */
        private String host;

        /**
         * 邮件服务器端口
         */
        private Integer port = 587;

        /**
         * 用户名
         */
        private String username;

        /**
         * 密码
         */
        private String password;

        /**
         * 发件人邮箱
         */
        private String from;
    }

    @Data
    public static class Sms {
        /**
         * 短信服务访问密钥ID
         */
        private String accessKeyId;

        /**
         * 短信服务访问密钥Secret
         */
        private String accessKeySecret;

        /**
         * 短信签名
         */
        private String signName;

        /**
         * 短信模板代码
         */
        private String templateCode;
    }
}
