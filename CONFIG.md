# 达人接单平台配置说明

## 配置重构说明

本项目已完成配置系统重构，统一使用根目录的 `.env` 文件进行配置管理，不再区分开发、测试、生产环境。

## 配置文件位置

- **主配置文件**: `/.env` (项目根目录)
- **后端配置**: `/expert/src/main/resources/application.yml`
- **前端配置**: `/web/src/config/index.ts`
- **小程序配置**: `/uniapp/Uni_expert/config/env.ts`

## 配置项说明

### 应用基础配置
```bash
APP_NAME=达人接单平台                    # 应用名称
APP_VERSION=1.0.0                      # 应用版本
APP_DESCRIPTION=专业的达人服务平台       # 应用描述
APP_ENV=production                     # 应用环境
APP_DOMAIN=localhost:9090              # 应用域名
```

### 服务器配置
```bash
SERVER_PORT=9090                       # 服务器端口
SERVER_CONTEXT_PATH=/api               # API上下文路径
```

### 数据库配置
```bash
DB_HOST=localhost                      # 数据库主机
DB_PORT=3306                          # 数据库端口
DB_NAME=expert_db                     # 数据库名称
DB_USERNAME=root                      # 数据库用户名
DB_PASSWORD=1234                      # 数据库密码
```

### Redis配置
```bash
REDIS_HOST=localhost                  # Redis主机
REDIS_PORT=6379                      # Redis端口
REDIS_PASSWORD=                      # Redis密码（可选）
REDIS_DATABASE=0                     # Redis数据库编号
```

### JWT配置
```bash
JWT_SECRET=your_jwt_secret_here       # JWT密钥（生产环境必须修改）
JWT_EXPIRATION=86400000              # JWT过期时间（毫秒）
JWT_HEADER=Authorization             # JWT请求头名称
JWT_PREFIX=Bearer                    # JWT前缀
```

### 微信配置
```bash
WECHAT_APPID=wx52c058048a9d15bb      # 微信小程序AppID
WECHAT_SECRET=your_secret_here       # 微信小程序Secret
```

### 微信支付配置
```bash
WECHAT_PAY_MCHID=                    # 商户号
WECHAT_PAY_PRIVATE_KEY_PATH=         # 私钥文件路径
WECHAT_PAY_MERCHANT_SERIAL_NUMBER=   # 商户证书序列号
WECHAT_PAY_API_V3_KEY=              # API v3密钥
```

### 阿里云OSS配置
```bash
OSS_ENDPOINT=                        # OSS端点
OSS_ACCESS_KEY_ID=                   # 访问密钥ID
OSS_ACCESS_KEY_SECRET=               # 访问密钥Secret
OSS_BUCKET_NAME=                     # 存储桶名称
OSS_DOMAIN=                          # 访问域名
```

### 文件上传配置
```bash
UPLOAD_PATH=uploads/                 # 文件上传路径
UPLOAD_MAX_SIZE=10485760            # 最大文件大小（字节）
UPLOAD_ALLOWED_TYPES=jpg,jpeg,png,gif,pdf,doc,docx  # 允许的文件类型
```

### 邮件配置
```bash
MAIL_HOST=                          # 邮件服务器主机
MAIL_PORT=587                       # 邮件服务器端口
MAIL_USERNAME=                      # 邮件用户名
MAIL_PASSWORD=                      # 邮件密码
MAIL_FROM=                          # 发件人邮箱
```

### 短信配置
```bash
SMS_ACCESS_KEY_ID=                  # 短信服务访问密钥ID
SMS_ACCESS_KEY_SECRET=              # 短信服务访问密钥Secret
SMS_SIGN_NAME=                      # 短信签名
SMS_TEMPLATE_CODE=                  # 短信模板代码
```

### 日志配置
```bash
LOG_FILE_ENABLED=false              # 是否启用文件日志
LOG_FILE_PATH=logs/                 # 日志文件路径
LOG_LEVEL=info                      # 日志级别
LOG_SQL_ENABLED=false               # 是否启用SQL日志
LOG_ERROR_FILE_ENABLED=false        # 是否启用错误日志文件
LOG_MAX_FILE_SIZE=100MB             # 日志文件最大大小
LOG_MAX_HISTORY=30                  # 日志文件保留天数
LOG_TOTAL_SIZE_CAP=3GB              # 日志总大小限制
```

### 前端配置
```bash
VITE_APP_TITLE=达人接单管理系统       # 前端应用标题
VITE_APP_VERSION=1.0.0              # 前端应用版本
VITE_APP_DESCRIPTION=达人服务接单平台管理系统  # 前端应用描述
VITE_API_BASE_URL=http://localhost:9090      # API基础URL
VITE_API_TIMEOUT=10000              # API超时时间
VITE_STATIC_BASE_URL=http://localhost:9090   # 静态资源基础URL
VITE_UPLOAD_SIZE_LIMIT=10485760     # 前端文件上传大小限制
VITE_UPLOAD_ALLOWED_TYPES=jpg,jpeg,png,gif,pdf,doc,docx  # 前端允许的文件类型
VITE_ENABLE_MOCK=false              # 是否启用Mock数据
VITE_ENABLE_DEVTOOLS=true           # 是否启用开发工具
VITE_ENABLE_CONSOLE_LOG=true        # 是否启用控制台日志
```

### 业务配置
```bash
PLATFORM_COMMISSION_RATE=10.00     # 平台抽成比例（%）
MIN_WITHDRAW_AMOUNT=100.00          # 最小提现金额
ORDER_AUTO_CANCEL_MINUTES=30        # 订单自动取消时间（分钟）
ORDER_AUTO_CONFIRM_DAYS=7           # 订单自动确认时间（天）
```

### 功能开关
```bash
ENABLE_WECHAT_PAY=false             # 是否启用微信支付
ENABLE_OSS_STORAGE=false            # 是否启用OSS存储
ENABLE_EMAIL_NOTIFICATION=false     # 是否启用邮件通知
ENABLE_SMS_NOTIFICATION=false       # 是否启用短信通知
ENABLE_DEBUG_MODE=false             # 是否启用调试模式
```

## 部署说明

1. **复制配置文件**: 将 `.env` 文件复制到服务器
2. **修改配置**: 根据实际环境修改配置项
3. **重要安全配置**:
   - 修改 `JWT_SECRET` 为强密码
   - 设置正确的数据库连接信息
   - 配置微信相关密钥
   - 设置正确的域名和端口

## 注意事项

1. `.env` 文件包含敏感信息，不应提交到版本控制
2. 生产环境必须修改默认密码和密钥
3. 确保数据库和Redis服务正常运行
4. 微信相关配置需要在微信开发者平台获取
5. 文件上传路径需要有写入权限

## 故障排除

1. **数据库连接失败**: 检查数据库配置和服务状态
2. **Redis连接失败**: 检查Redis配置和服务状态
3. **JWT验证失败**: 检查JWT密钥配置
4. **文件上传失败**: 检查上传路径权限
5. **微信功能异常**: 检查微信配置和网络连接
