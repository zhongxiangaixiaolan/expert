# 微信小程序配置说明

## 问题描述

当前系统出现 `invalid appsecret` 错误（错误码40125），这是因为微信小程序的AppID和AppSecret配置不正确导致的。

## 错误信息
```
{"errcode":40125,"errmsg":"invalid appsecret, rid: 6867be73-3e347658-22efe770"}
```

## 解决方案

### 1. 获取正确的AppID和AppSecret

1. 登录 [微信公众平台](https://mp.weixin.qq.com/)
2. 进入您的小程序管理后台
3. 在 "开发" -> "开发管理" -> "开发设置" 中找到：
   - **AppID (小程序ID)**：以 `wx` 开头的字符串
   - **AppSecret (小程序密钥)**：32位字符串

### 2. 配置方法

#### 方法一：修改配置文件
编辑 `expert/src/main/resources/application.yml` 文件：

```yaml
expert:
  wechat:
    miniapp:
      app-id: 您的真实AppID  # 例如：wx1234567890abcdef
      app-secret: 您的真实AppSecret  # 例如：abcdef1234567890abcdef1234567890
```

#### 方法二：使用环境变量
设置环境变量：
```bash
export EXPERT_WECHAT_MINIAPP_APP_ID=您的真实AppID
export EXPERT_WECHAT_MINIAPP_APP_SECRET=您的真实AppSecret
```

#### 方法三：使用启动参数
```bash
java -jar expert-1.0.0.jar \
  --expert.wechat.miniapp.app-id=您的真实AppID \
  --expert.wechat.miniapp.app-secret=您的真实AppSecret
```

### 3. 验证配置

配置完成后，重启应用，检查日志中是否还有 `invalid appsecret` 错误。

### 4. 注意事项

1. **保密性**：AppSecret是敏感信息，不要提交到版本控制系统
2. **权限**：确保小程序已发布或在开发者工具中正确配置
3. **网络**：确保服务器能访问微信API（api.weixin.qq.com）
4. **IP白名单**：如果配置了IP白名单，确保服务器IP在白名单中

### 5. 常见错误码

- `40001`: AppSecret错误
- `40013`: AppID无效
- `40125`: AppSecret无效
- `61009`: code无效（通常是code过期或重复使用）

### 6. 测试建议

1. 先在微信开发者工具中测试小程序登录功能
2. 确认AppID和AppSecret在微信公众平台中是正确的
3. 检查小程序是否已正确配置服务器域名

## 相关文档

- [微信小程序登录文档](https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/login.html)
- [微信小程序服务端API](https://developers.weixin.qq.com/miniprogram/dev/api-backend/)
