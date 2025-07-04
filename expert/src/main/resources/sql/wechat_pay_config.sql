-- 微信支付配置初始化脚本
-- 插入微信小程序和支付相关配置项

-- 删除旧的配置项（如果存在）
DELETE FROM system_configs WHERE config_key IN (
    'wechat_miniapp_app_id',
    'wechat_miniapp_app_secret', 
    'wechat_miniapp_token',
    'wechat_miniapp_aes_key',
    'wechat_pay_mch_id',
    'wechat_pay_api_v3_key',
    'wechat_pay_cert_serial_no',
    'wechat_pay_private_key_path',
    'wechat_pay_private_cert_path',
    'wechat_pay_notify_url'
);

-- 插入微信小程序配置
INSERT INTO system_configs (config_key, config_value, config_type, config_group, description, create_time, update_time) VALUES
('wechat_miniapp_app_id', 'wxc9b98294ace36519', 'STRING', 'WECHAT', '微信小程序AppID', NOW(), NOW()),
('wechat_miniapp_app_secret', '34887a584a83a6a53b010ae59ccc9f19', 'STRING', 'WECHAT', '微信小程序AppSecret', NOW(), NOW()),
('wechat_miniapp_token', '', 'STRING', 'WECHAT', '微信小程序Token（可选）', NOW(), NOW()),
('wechat_miniapp_aes_key', '', 'STRING', 'WECHAT', '微信小程序AES密钥（可选）', NOW(), NOW());

-- 插入微信支付配置
INSERT INTO system_configs (config_key, config_value, config_type, config_group, description, create_time, update_time) VALUES
('wechat_pay_config_enabled', 'false', 'BOOLEAN', 'WECHAT', '是否启用微信支付配置验证', NOW(), NOW()),
('wechat_pay_mch_id', '', 'STRING', 'WECHAT', '微信支付商户号', NOW(), NOW()),
('wechat_pay_api_v3_key', '', 'STRING', 'WECHAT', '微信支付API V3密钥', NOW(), NOW()),
('wechat_pay_cert_serial_no', '', 'STRING', 'WECHAT', '微信支付证书序列号', NOW(), NOW()),
('wechat_pay_private_key_path', '', 'STRING', 'WECHAT', '微信支付私钥文件路径', NOW(), NOW()),
('wechat_pay_private_cert_path', '', 'STRING', 'WECHAT', '微信支付证书文件路径', NOW(), NOW()),
('wechat_pay_notify_url', 'https://yourdomain.com/api/user/payment/callback/wechat', 'STRING', 'WECHAT', '微信支付回调地址', NOW(), NOW());

-- 更新旧的配置项（如果存在）
UPDATE system_configs SET 
    config_key = 'wechat_miniapp_app_id',
    config_group = 'WECHAT',
    description = '微信小程序AppID',
    update_time = NOW()
WHERE config_key = 'wx_appid';

UPDATE system_configs SET 
    config_key = 'wechat_miniapp_app_secret',
    config_group = 'WECHAT', 
    description = '微信小程序AppSecret',
    update_time = NOW()
WHERE config_key = 'wx_appsecret';

UPDATE system_configs SET 
    config_key = 'wechat_pay_mch_id',
    config_group = 'WECHAT',
    description = '微信支付商户号', 
    update_time = NOW()
WHERE config_key = 'wx_mch_id';

UPDATE system_configs SET 
    config_key = 'wechat_pay_api_v3_key',
    config_group = 'WECHAT',
    description = '微信支付API V3密钥',
    update_time = NOW() 
WHERE config_key = 'wx_api_key';
