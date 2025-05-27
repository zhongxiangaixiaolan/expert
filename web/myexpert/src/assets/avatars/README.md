# 头像资源目录

此目录用于存放用户头像相关的图片资源。

## 目录结构

```
avatars/
├── README.md                 # 说明文档
├── default-male.png         # 默认男性头像（前端使用）
├── default-female.png       # 默认女性头像（前端使用）
└── default-unknown.png      # 默认未知性别头像（前端使用）
```

注意：用户上传的头像存储在后端，不放在前端资源目录中。

## 头像规格要求

### 图片尺寸

- **推荐尺寸**: 200x200 像素（正方形）
- **最小尺寸**: 100x100 像素
- **最大尺寸**: 500x500 像素

### 文件大小

- **推荐大小**: 50KB 以下
- **最大大小**: 100KB

### 文件格式

- **支持格式**: JPG, JPEG, PNG, WebP
- **推荐格式**: PNG（支持透明背景）或 WebP（更小的文件大小）

### 图片质量

- **分辨率**: 72-150 DPI
- **色彩模式**: RGB
- **压缩质量**: 80-90%

## 默认头像说明

### default-male.png

- 用于性别为男性（gender=1）的用户
- 建议使用蓝色系或中性色调
- 简洁的图标风格

### default-female.png

- 用于性别为女性（gender=2）的用户
- 建议使用粉色系或温暖色调
- 简洁的图标风格

### default-unknown.png

- 用于性别未知（gender=0）或未设置的用户
- 建议使用灰色系或中性色调
- 通用的人物图标

## 使用方式

### 在代码中引用

```typescript
// 相对路径方式
const avatar = "default-male.png";

// 绝对路径方式
const avatar = "/src/assets/avatars/default-male.png";

// 完整URL方式
const avatar = "https://example.com/avatars/user123.jpg";
```

### 数据库存储建议

```sql
-- 用户上传的头像（只存储文件名）
UPDATE users SET avatar = '1_1640995200000.jpg' WHERE id = 1;
UPDATE users SET avatar = '2_1640995300000.png' WHERE id = 2;

-- 使用默认头像（存储NULL，前端根据性别自动选择）
UPDATE users SET avatar = NULL WHERE id = 3;

-- 外部URL头像（微信头像等）
UPDATE users SET avatar = 'https://thirdwx.qlogo.cn/mmopen/...' WHERE id = 4;
```

## 注意事项

1. **文件命名**: 使用小写字母和连字符，避免特殊字符
2. **图片优化**: 上传前请压缩图片以减少文件大小
3. **备份**: 重要的头像文件请做好备份
4. **CDN**: 生产环境建议使用 CDN 加速图片加载
5. **缓存**: 设置适当的浏览器缓存策略
