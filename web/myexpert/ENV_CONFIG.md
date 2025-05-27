# 环境变量配置说明

## 概述

本项目使用环境变量来统一管理不同环境下的配置，避免硬编码 URL，提高项目的可维护性和部署灵活性。

## 环境变量列表

### VITE_API_BASE_URL

- **说明**: API 服务的基础 URL
- **开发环境**: `http://localhost:8080`
- **生产环境**: `https://your-domain.com`
- **用途**: 用于 API 请求的代理配置

### VITE_STATIC_BASE_URL

- **说明**: 静态资源的基础 URL
- **开发环境**: `http://localhost:8080`
- **生产环境**: `https://your-domain.com` 或 CDN 地址
- **用途**: 用于头像等静态文件的访问

## 配置文件

### .env

通用环境变量，所有环境都会加载

### .env.development

开发环境专用配置，仅在开发模式下加载

### .env.production

生产环境专用配置，仅在生产构建时加载

## 使用方式

### 在代码中使用

```typescript
// 直接使用环境变量
const apiUrl = import.meta.env.VITE_API_BASE_URL;

// 使用统一配置
import { ENV_CONFIG, buildUrl } from "@/config";

// 构建API URL
const apiUrl = buildUrl.api("/admin/users");

// 构建头像URL
const avatarUrl = buildUrl.avatar("avatar.jpg");
```

### 在 vite 配置中使用

```typescript
// vite.config.ts
export default defineConfig({
  server: {
    proxy: {
      "/api": {
        target: process.env.VITE_API_BASE_URL || "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
```

## 部署注意事项

1. **开发环境**: 确保后端服务运行在 `http://localhost:8080`
2. **生产环境**: 修改 `.env.production` 中的 URL 为实际的域名
3. **CDN**: 如果使用 CDN，可以将 `VITE_STATIC_BASE_URL` 设置为 CDN 地址

## 头像访问路径

- **后端接口**: `/api/avatars/{fileName}`
- **完整 URL**: `${VITE_STATIC_BASE_URL}/api/avatars/{fileName}`
- **示例**: `http://localhost:8080/api/avatars/avatar123.jpg`

## 故障排除

### 头像无法显示

1. 检查环境变量是否正确配置
2. 确认后端头像控制器 (`/api/avatars`) 正常工作
3. 检查文件是否存在于后端配置的头像目录
4. 查看浏览器网络面板的请求状态

### API 请求失败

1. 检查 `VITE_API_BASE_URL` 是否正确
2. 确认后端服务是否正常运行
3. 检查 vite 代理配置是否正确
