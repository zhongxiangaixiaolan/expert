package com.qing.expert.common.constant;

/**
 * 配置常量
 */
public class ConfigConstant {

    /**
     * 配置分组
     */
    public static class Group {
        public static final String WECHAT = "WECHAT";
        public static final String STORAGE = "STORAGE";
        public static final String SYSTEM = "SYSTEM";
        public static final String BUSINESS = "BUSINESS";
    }

    /**
     * 配置类型
     */
    public static class Type {
        public static final String STRING = "STRING";
        public static final String NUMBER = "NUMBER";
        public static final String BOOLEAN = "BOOLEAN";
        public static final String JSON = "JSON";
    }

    /**
     * 微信配置键
     */
    public static class WeChat {
        public static final String MINIAPP_APP_ID = "wechat_miniapp_app_id";
        public static final String MINIAPP_APP_SECRET = "wechat_miniapp_app_secret";
        public static final String MINIAPP_TOKEN = "wechat_miniapp_token";
        public static final String MINIAPP_AES_KEY = "wechat_miniapp_aes_key";
        public static final String PAY_MCH_ID = "wechat_pay_mch_id";
        public static final String PAY_API_V3_KEY = "wechat_pay_api_v3_key";
        public static final String PAY_CERT_SERIAL_NO = "wechat_pay_cert_serial_no";
        public static final String PAY_PRIVATE_KEY_PATH = "wechat_pay_private_key_path";
        public static final String PAY_PRIVATE_CERT_PATH = "wechat_pay_private_cert_path";
        public static final String PAY_NOTIFY_URL = "wechat_pay_notify_url";
        public static final String PAY_CONFIG_ENABLED = "wechat_pay_config_enabled";
    }

    /**
     * 存储配置键
     */
    public static class Storage {
        public static final String TYPE = "storage_type";
        public static final String OSS_ENDPOINT = "oss_endpoint";
        public static final String OSS_ACCESS_KEY = "oss_access_key";
        public static final String OSS_SECRET_KEY = "oss_secret_key";
        public static final String OSS_BUCKET = "oss_bucket";
        public static final String OSS_DOMAIN = "oss_domain";
    }

    /**
     * 系统配置键
     */
    public static class System {
        public static final String NAME = "system_name";
        public static final String LOGO = "system_logo";
        public static final String CONTACT_PHONE = "contact_phone";
        public static final String CONTACT_EMAIL = "contact_email";
    }

    /**
     * 业务配置键
     */
    public static class Business {
        public static final String PLATFORM_COMMISSION_RATE = "platform_commission_rate";
        public static final String MIN_WITHDRAW_AMOUNT = "min_withdraw_amount";
        public static final String ORDER_AUTO_CANCEL_TIME = "order_auto_cancel_time";
        public static final String ORDER_AUTO_COMPLETE_TIME = "order_auto_complete_time";
        public static final String WITHDRAW_FEE_RATE = "withdraw_fee_rate";
        public static final String RECHARGE_AMOUNTS = "recharge_amounts";
    }

    /**
     * 存储类型
     */
    public static class StorageType {
        public static final String LOCAL = "LOCAL";
        public static final String OSS = "OSS";
    }
}
