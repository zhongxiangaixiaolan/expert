# 达人照片管理功能说明

## 功能概述

为达人管理系统新增了照片数组功能，支持达人多张照片的上传、管理和3D轮播展示。

## 主要特性

### 1. 数据库设计
- 新增 `expert_photos` 表存储达人照片信息
- 支持照片标题、描述、排序等字段
- 与达人表建立外键关联，支持级联删除

### 2. 后端功能
- **文件存储**: 照片存储在 `src/main/resources/static/photos/` 目录
- **API接口**: 完整的照片CRUD操作接口
- **文件管理**: 自动处理文件上传、删除和访问
- **图片信息**: 自动获取图片尺寸和文件大小

### 3. 前端功能
- **3D轮播图**: 炫酷的3D横向滚动展示效果
- **照片管理**: 支持拖拽排序、编辑信息、删除操作
- **文件上传**: 支持多种图片格式，实时预览
- **响应式设计**: 适配桌面和移动端

## 技术实现

### 后端技术栈
- **Spring Boot**: 主框架
- **MyBatis Plus**: 数据库操作
- **文件上传**: MultipartFile处理
- **图片处理**: BufferedImage获取尺寸信息

### 前端技术栈
- **Vue 3**: 主框架
- **Element Plus**: UI组件库
- **VueDraggable**: 拖拽排序
- **CSS 3D Transform**: 3D轮播效果

## 文件结构

### 后端文件
```
expert/src/main/java/com/qing/expert/
├── entity/ExpertPhoto.java                    # 照片实体类
├── mapper/ExpertPhotoMapper.java              # 数据访问层
├── service/ExpertPhotoService.java            # 服务接口
├── service/impl/ExpertPhotoServiceImpl.java   # 服务实现
├── controller/PhotoController.java            # 照片访问控制器
├── controller/admin/ExpertPhotoController.java # 照片管理控制器
└── util/FileUploadUtil.java                   # 文件上传工具(扩展)

expert/src/main/resources/
├── mapper/ExpertPhotoMapper.xml               # MyBatis映射文件
├── sql/expert_db.sql                          # 数据库结构(更新)
├── sql/expert_db_data.sql                     # 示例数据(更新)
└── static/photos/                             # 照片存储目录
```

### 前端文件
```
web/myexpert/src/
├── components/
│   ├── PhotoCarousel3D.vue                   # 3D轮播图组件
│   └── PhotoManager.vue                      # 照片管理组件
├── api/photos.ts                              # 照片API接口
├── views/experts/ExpertDetailDialog.vue      # 达人详情对话框(更新)
└── assets/images/default-photo.png           # 默认照片占位符
```

## 使用说明

### 1. 环境准备
```bash
# 启动开发环境
./start-dev.bat

# 或手动启动
# 后端
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 前端
cd web/myexpert
npm run dev
```

### 2. 数据库初始化
```sql
-- 执行数据库更新脚本
source expert/src/main/resources/sql/expert_db.sql;
source expert/src/main/resources/sql/expert_db_data.sql;
```

### 3. 照片目录创建
```bash
# 确保照片存储目录存在
mkdir -p expert/src/main/resources/static/photos
```

### 4. 功能使用

#### 管理员端操作
1. 登录管理后台
2. 进入达人管理页面
3. 点击达人详情查看照片
4. 使用照片管理功能：
   - 上传新照片
   - 编辑照片信息
   - 拖拽调整排序
   - 删除不需要的照片

#### 3D轮播图操作
- **鼠标点击**: 切换到指定照片
- **键盘操作**: 左右箭头键切换
- **触摸操作**: 移动端支持触摸切换
- **自动播放**: 可配置自动轮播

## API接口

### 照片管理接口
```
GET    /admin/expert/photos/{expertId}           # 获取达人照片列表
POST   /admin/expert/photos/{expertId}/upload    # 上传照片
PUT    /admin/expert/photos/{photoId}            # 更新照片信息
DELETE /admin/expert/photos/{photoId}            # 删除照片
PUT    /admin/expert/photos/sort                 # 批量更新排序
DELETE /admin/expert/photos/expert/{expertId}    # 删除达人所有照片
```

### 照片访问接口
```
GET    /api/photos/{filename}                    # 获取照片文件
```

## 配置说明

### 文件上传配置
```yaml
# application.yml
expert:
  file:
    upload-path: src/main/resources/static/
    max-size: 10485760 # 10MB
    allowed-types: jpg,jpeg,png,gif,webp

spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 100MB
```

### 前端配置
```typescript
// 支持的文件格式
const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];

// 推荐的图片尺寸比例
const recommendedRatio = 3/4; // 宽:高 = 3:4
```

## 注意事项

### 1. 文件存储
- 照片文件存储在本地文件系统
- 数据库只存储文件名，不存储完整路径
- 删除照片时会同时删除文件和数据库记录

### 2. 性能优化
- 图片上传时自动获取尺寸信息
- 前端支持图片懒加载
- 3D轮播图使用CSS3硬件加速

### 3. 安全考虑
- 文件类型验证
- 文件大小限制
- 路径安全检查
- 防止目录遍历攻击

### 4. 浏览器兼容性
- 3D轮播图需要现代浏览器支持
- 建议使用Chrome、Firefox、Safari、Edge最新版本
- 移动端需要支持CSS 3D Transform

## 故障排除

### 常见问题

1. **照片上传失败**
   - 检查文件大小是否超限
   - 确认文件格式是否支持
   - 验证photos目录是否存在且有写权限

2. **3D轮播图显示异常**
   - 检查浏览器是否支持CSS 3D Transform
   - 确认照片文件是否存在
   - 验证照片URL是否正确

3. **拖拽排序不生效**
   - 确认vuedraggable依赖是否正确安装
   - 检查组件是否正确导入
   - 验证排序API是否正常响应

### 调试方法
```bash
# 查看后端日志
tail -f logs/expert.log

# 检查照片目录
ls -la expert/src/main/resources/static/photos/

# 测试照片访问
curl http://localhost:8080/api/photos/test.jpg
```

## 扩展功能

### 未来可能的增强
1. **云存储支持**: 集成阿里云OSS、腾讯云COS等
2. **图片压缩**: 自动压缩大尺寸图片
3. **水印功能**: 为上传的照片添加水印
4. **批量操作**: 支持批量上传和删除
5. **图片编辑**: 在线裁剪、滤镜等功能

### 自定义配置
可以通过修改配置文件来调整功能行为：
- 修改存储路径
- 调整文件大小限制
- 自定义支持的文件格式
- 配置3D轮播图参数

## 联系支持

如果在使用过程中遇到问题，请：
1. 查看本文档的故障排除部分
2. 检查控制台错误信息
3. 确认环境配置是否正确
4. 联系开发团队获取技术支持
