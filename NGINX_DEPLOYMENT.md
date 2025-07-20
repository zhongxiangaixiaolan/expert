# Nginx代理部署方案

## 方案概述

使用Nginx作为反向代理服务器，统一处理前端、小程序和后端API请求，解决403权限问题。

## 核心原理

- **前端请求**：`http://localhost:3000/api/banners`
- **Nginx处理**：将`/api/*`重写为`/*`并代理到后端
- **后端接收**：`http://localhost:9090/banners`
- **SecurityConfig匹配**：`/banners` ✅ 匹配成功

## 部署方案

### 方案1：开发环境快速部署

1. **启动后端服务**
   ```bash
   cd expert
   mvn spring-boot:run
   ```

2. **启动前端服务**
   ```bash
   cd web
   npm run dev
   ```

3. **启动Nginx**
   ```bash
   # 使用开发环境配置
   nginx -c /path/to/nginx-dev.conf
   ```

4. **修改前端配置**
   ```typescript
   // web/src/config/index.ts
   export const ENV_CONFIG = {
     API_BASE_URL: 'http://localhost:3000', // 改为Nginx端口
     // ...其他配置
   }
   ```

5. **修改小程序配置**
   ```typescript
   // uniapp/Uni_expert/utils/request.ts
   const BASE_URL = 'http://localhost:3000' // 改为Nginx端口
   ```

### 方案2：Docker完整部署

1. **构建并启动所有服务**
   ```bash
   docker-compose -f docker-compose.nginx.yml up -d
   ```

2. **访问应用**
   - 管理后台：http://localhost/admin/
   - API接口：http://localhost/api/*
   - 小程序API：http://localhost/api/*

## 配置说明

### 关键Nginx配置

```nginx
# API接口代理 - 核心配置
location /api/ {
    # 重写URL，去掉/api前缀
    rewrite ^/api/(.*)$ /$1 break;
    
    # 代理到Spring Boot后端
    proxy_pass http://localhost:9090;
    
    # CORS配置支持小程序和Web端
    add_header 'Access-Control-Allow-Origin' '*' always;
    add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
    add_header 'Access-Control-Allow-Headers' 'Authorization,Content-Type' always;
}
```

### 路径映射示例

| 前端请求 | Nginx重写 | 后端接收 | SecurityConfig匹配 |
|---------|-----------|----------|-------------------|
| `/api/banners` | `/banners` | `/banners` | ✅ `/banners` |
| `/api/announcements` | `/announcements` | `/announcements` | ✅ `/announcements` |
| `/api/admin/auth/login` | `/admin/auth/login` | `/admin/auth/login` | ✅ `/admin/auth/login` |

## 优势分析

### 相比修改后端配置的优势

1. **无需修改后端代码**：保持原有SecurityConfig配置不变
2. **统一入口管理**：所有请求通过Nginx统一处理
3. **更好的扩展性**：可以轻松添加负载均衡、SSL、缓存等功能
4. **生产环境标准**：符合生产环境部署最佳实践
5. **灵活的路由配置**：可以为不同客户端配置不同的路由规则

### CORS问题解决

Nginx配置中包含完整的CORS支持：
- 支持所有来源（开发环境）
- 支持所有HTTP方法
- 支持预检请求处理
- 支持自定义请求头

## 小程序端配置

### 修改请求基础URL

```typescript
// uniapp/Uni_expert/utils/request.ts
const BASE_URL = process.env.NODE_ENV === 'development' 
  ? 'http://localhost:3000'  // 开发环境使用Nginx代理
  : 'https://api.yourdomain.com'  // 生产环境API域名
```

### 小程序域名配置

在微信小程序后台配置服务器域名：
- 开发环境：`http://localhost:3000`
- 生产环境：`https://api.yourdomain.com`

## 测试验证

### 1. API接口测试
```bash
# 测试轮播图接口
curl http://localhost:3000/api/banners

# 测试公告接口  
curl http://localhost:3000/api/announcements

# 测试管理员登录
curl -X POST http://localhost:3000/api/admin/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

### 2. 前端页面测试
- 访问：http://localhost:3000/admin/
- 检查控制台是否还有403错误
- 验证数据是否正常加载

### 3. 小程序测试
- 修改小程序请求URL为Nginx地址
- 重新编译小程序
- 测试各个页面数据加载

## 故障排除

### 常见问题

1. **Nginx启动失败**
   - 检查端口是否被占用
   - 检查配置文件语法：`nginx -t`

2. **代理不生效**
   - 检查upstream配置
   - 确认后端服务正常运行

3. **CORS问题**
   - 检查Nginx CORS配置
   - 确认预检请求处理

4. **静态资源404**
   - 检查静态资源代理配置
   - 确认后端静态资源路径

## 生产环境建议

1. **使用HTTPS**：配置SSL证书
2. **域名分离**：API和前端使用不同域名
3. **缓存策略**：为静态资源配置适当缓存
4. **日志监控**：配置访问日志和错误日志
5. **安全配置**：限制请求频率、IP白名单等
