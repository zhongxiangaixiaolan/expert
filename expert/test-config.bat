@echo off
echo ===== 微信支付配置开关测试 =====
echo.

echo 1. 检查当前配置状态...
mysql -u root -p123456 -e "USE expert_db; SELECT config_key, config_value FROM system_configs WHERE config_key = 'wechat_pay_config_enabled';"

echo.
echo 2. 当前配置说明：
echo    - false: 禁用微信支付配置验证，应用可正常启动
echo    - true:  启用微信支付配置验证，需要完整配置才能启动

echo.
echo 3. 切换配置开关：
echo    启用验证: mysql -u root -p123456 -e "USE expert_db; UPDATE system_configs SET config_value='true' WHERE config_key='wechat_pay_config_enabled';"
echo    禁用验证: mysql -u root -p123456 -e "USE expert_db; UPDATE system_configs SET config_value='false' WHERE config_key='wechat_pay_config_enabled';"

echo.
echo 4. 查看所有微信支付相关配置：
mysql -u root -p123456 -e "USE expert_db; SELECT config_key, CASE WHEN config_key LIKE '%secret%' OR config_key LIKE '%key%' THEN CONCAT(LEFT(config_value, 8), '***') ELSE config_value END as config_value FROM system_configs WHERE config_key LIKE '%wechat%' ORDER BY config_key;"

echo.
echo ===== 测试完成 =====
pause
