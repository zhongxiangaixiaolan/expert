-- 更新微信小程序配置
-- 新的AppID: wx52c058048a9d15bb
-- 新的AppSecret: 9c9b2613c4a8193ff1d746ca366b5aa7

-- 更新微信小程序AppID
UPDATE system_configs 
SET config_value = 'wx52c058048a9d15bb', 
    update_time = NOW() 
WHERE config_key = 'wechat_miniapp_app_id';

-- 更新微信小程序AppSecret
UPDATE system_configs 
SET config_value = '9c9b2613c4a8193ff1d746ca366b5aa7', 
    update_time = NOW() 
WHERE config_key = 'wechat_miniapp_app_secret';

-- 如果记录不存在，则插入新记录
INSERT INTO system_configs (config_key, config_value, config_type, config_group, description, is_sensitive, create_time, update_time, deleted) 
SELECT 'wechat_miniapp_app_id', 'wx52c058048a9d15bb', 'STRING', 'WECHAT', '微信小程序AppID', 0, NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM system_configs WHERE config_key = 'wechat_miniapp_app_id');

INSERT INTO system_configs (config_key, config_value, config_type, config_group, description, is_sensitive, create_time, update_time, deleted) 
SELECT 'wechat_miniapp_app_secret', '9c9b2613c4a8193ff1d746ca366b5aa7', 'STRING', 'WECHAT', '微信小程序AppSecret', 1, NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM system_configs WHERE config_key = 'wechat_miniapp_app_secret');

-- 同时更新旧的配置键（如果存在）
UPDATE system_configs 
SET config_value = 'wx52c058048a9d15bb', 
    update_time = NOW() 
WHERE config_key = 'wx_appid';

UPDATE system_configs 
SET config_value = '9c9b2613c4a8193ff1d746ca366b5aa7', 
    update_time = NOW() 
WHERE config_key = 'wx_appsecret';

-- 查询更新结果
SELECT config_key, config_value, config_group, description, update_time 
FROM system_configs 
WHERE config_key IN ('wechat_miniapp_app_id', 'wechat_miniapp_app_secret', 'wx_appid', 'wx_appsecret')
ORDER BY config_key;
