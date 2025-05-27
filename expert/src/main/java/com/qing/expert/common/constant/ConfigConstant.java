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
        public static final String APP_ID = "wx_appid";
        public static final String APP_SECRET = "wx_appsecret";
        public static final String MCH_ID = "wx_mch_id";
        public static final String API_KEY = "wx_api_key";
        public static final String CERT_PATH = "wx_cert_path";
        public static final String NOTIFY_URL = "wx_notify_url";
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
