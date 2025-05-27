# 达人接单小程序开发计划

## 项目概述

这是一个微信小程序项目，实现达人服务接单平台。包含用户（顾客）、达人、管理员三个角色，支持服务发布、订单管理、合同签署等功能。

## 技术架构

- **后端**: Spring Boot 3.5.0 + MySQL + Redis + MyBatis Plus
- **前端管理**: Vue 3 + TypeScript + Vite + Pinia + Element Plus
- **小程序**: UniApp + Vue 3
- **支付**: 微信支付（动态配置）
- **文件存储**: 本地存储/阿里云 OSS（可切换）
- **配置管理**: 数据库动态配置（管理员可修改）

## 项目结构

```
expert/                 # 后端项目
├── src/main/java/
│   └── com/qing/expert/
├── src/main/resources/
└── pom.xml

web/myexpert/          # 前端管理项目
├── src/
├── package.json
└── vite.config.ts

uniapp/Uni_expert/     # 小程序项目
├── pages/
├── static/
├── App.vue
└── manifest.json
```

## 开发计划

### 第一阶段：基础架构搭建 ✅ (已完成)

#### 1.1 后端基础配置

- [x] Spring Boot 项目初始化
- [x] 数据库配置（MySQL + MyBatis Plus）
- [x] Redis 配置（缓存管理）
- [x] 微信小程序配置（动态配置）
- [x] 文件上传配置（本地/OSS 切换）
- [x] 跨域配置（CORS）
- [x] 统一响应格式（Result、PageResult）
- [x] 全局异常处理（GlobalExceptionHandler）
- [x] Swagger API 文档（OpenAPI 3）

#### 1.2 数据库设计

- [x] 用户表（users）
- [x] 达人表（experts）
- [x] 管理员表（admins）
- [x] **系统配置表（system_configs）** - 动态配置管理 ⭐
- [x] 服务分类表（categories）
- [x] 服务商品表（services）
- [x] 订单表（orders）
- [x] 合同表（contracts）
- [x] 轮播图表（banners）
- [x] 通告表（announcements）
- [x] 充值记录表（recharge_records）
- [x] 提现记录表（withdraw_records）
- [x] 收藏表（favorites）
- [x] 客服聊天表（chat_messages）
- [x] 文件存储记录表（file_records）

#### 1.3 基础类和工具

- [x] 实体类（BaseEntity、SystemConfig、Admin、User、Category）
- [x] 数据传输对象（DTO）
- [x] 响应对象（VO）
- [x] 工具类（JwtUtil、IpUtil）
- [x] 常量类和枚举类（CommonConstant、ConfigConstant、StatusEnum）

#### 1.4 已完成的核心模块

- [x] **系统配置管理** - 动态配置（微信、存储、系统、业务）
- [x] **管理员认证** - JWT 登录、权限控制
- [x] **用户管理** - 用户 CRUD、统计、状态管理
- [x] **分类管理** - 服务分类 CRUD、排序、状态管理
- [x] **达人管理** - 达人 CRUD、状态管理、统计信息
- [x] **服务管理** - 服务商品 CRUD、分类关联、状态管理
- [x] **订单管理** - 订单 CRUD、状态流转、取消退款、分页查询
- [x] **评价管理** - 评价 CRUD、回复功能、隐藏显示、评分统计
- [x] **轮播图管理** - 轮播图 CRUD、排序权重、状态管理、时间范围控制
- [x] **通告管理** - 通告 CRUD、类型管理、滚动显示、排序权重
- [x] **消息推送管理** - 消息记录 CRUD、微信模板消息、多渠道发送、统计分析
- [x] **测试接口** - 健康检查、数据库连接、Redis 连接测试

#### 1.5 已修复的问题

- [x] Result.java - 修复 int 类型调用 equals 方法的错误
- [x] JwtUtil.java - 更新为兼容的 JWT API 调用方式
- [x] RedisConfig.java - 修复过时的 Jackson 序列化器配置
- [x] pom.xml - 删除重复的 lombok 依赖

#### 1.6 最新修复的问题 ✅ (2024-12-19)

- [x] **样式变量缺失** - 修复 `$warning-color` 等状态颜色变量缺失问题
- [x] **缺失页面创建** - 创建了以下缺失的页面：
  - `pages/category/index.vue` - 分类列表页面
  - `pages/category/detail.vue` - 分类详情页面
  - `pages/user/profile.vue` - 用户资料页面
  - `pages/user/favorites.vue` - 收藏页面
  - `pages/user/transactions.vue` - 交易记录页面
  - `pages/user/messages.vue` - 消息中心页面
- [x] **后端接口补全** - 创建了缺失的后端 API 接口：
  - 收藏管理相关接口和实体类
  - 消息管理相关接口和 VO 类
  - 交易记录相关接口和 DTO 类
  - 服务管理公共接口扩展
- [x] **前端 API 文件** - 创建了对应的前端 API 调用文件：
  - `api/favorite.ts` - 收藏相关 API
  - `api/message.ts` - 消息相关 API
  - `api/transaction.ts` - 交易记录相关 API
  - `api/service.ts` - 服务相关 API

### 第二阶段：完善管理端业务模块 ✅ (已完成)

#### 2.1 达人管理模块 ✅

- [x] 达人实体类和 Mapper
- [x] 达人申请审核功能
- [x] 达人信息管理（CRUD）
- [x] 达人状态管理（在线/忙碌/下线）
- [x] 达人统计信息
- [x] 达人服务商品管理

#### 2.2 订单管理模块 ✅

- [x] 订单实体类和 Mapper
- [x] 订单列表查询（分页、筛选）
- [x] 订单状态流转管理
- [x] 订单取消和退款功能
- [x] 订单详情查看
- [x] 订单统计报表

#### 2.3 评价管理模块 ✅

- [x] 评价实体类和 Mapper
- [x] 用户评价达人、达人评价用户
- [x] 评价图片支持（JSON 数组存储）
- [x] 评价回复功能
- [x] 评价隐藏/显示管理
- [x] 评分统计（达人平均评分、服务平均评分）
- [x] 管理端评价管理接口

### 第三阶段：内容和财务管理模块 ✅ (已完成)

#### 3.1 内容管理模块 ✅ (已完成)

- [x] 轮播图管理（CRUD、排序权重、状态管理、时间范围控制）
- [x] 通告管理（CRUD、类型管理、滚动显示、排序权重）
- [x] **热门/推荐商品管理**（推荐类型管理、排序权重、时间范围控制）
- [x] **内容审核功能**（审核记录管理、审核流程、统计分析）

#### 3.2 财务管理模块 ✅ (已完成)

- [x] **充值记录管理** - 充值记录 CRUD、状态管理、支付回调处理
- [x] **提现申请审核** - 提现申请 CRUD、审核流程、转账确认
- [x] **财务统计报表** - 充值提现统计、收支分析、图表数据
- [x] **资金流水查询** - 分页查询、条件筛选、状态管理

#### 3.3 数据统计模块 ✅ (已完成)

- [x] **用户统计** - 新增、活跃、在线用户统计
- [x] **订单统计** - 数量、金额、状态分布统计
- [x] **达人统计** - 数量、评分、接单量统计
- [x] **财务统计** - 收入、支出、余额统计分析
- [x] **数据图表展示** - 日统计、月统计、支付方式统计

### 第四阶段：管理端前端开发（优先） 🚧 (进行中)

#### 4.1 管理端后端接口 ✅ (已完成)

- [x] 管理员认证模块
- [x] **系统配置管理模块**（微信配置、支付配置、存储配置）
- [x] 用户管理接口
- [x] 达人管理接口
- [x] 服务管理接口
- [x] 订单管理接口
- [x] 评价管理接口
- [x] 分类管理接口
- [x] **轮播图管理接口**（CRUD、排序、状态管理）
- [x] **通告管理接口**（CRUD、类型管理、滚动设置）
- [x] **推荐服务管理接口**（CRUD、推荐类型、排序权重）
- [x] **内容审核管理接口**（CRUD、审核流程、统计分析）
- [x] **支付记录管理接口**（CRUD、退款处理、统计分析）
- [x] **充值记录管理接口**（CRUD、状态管理、支付回调）
- [x] **提现记录管理接口**（CRUD、审核流程、转账确认）
- [x] **财务统计接口**（收支统计、图表数据、流水分析）
- [x] **消息推送管理接口**（CRUD、发送管理、统计分析、模板配置）
- [x] 测试接口

#### 4.2 管理端前端（Vue.js + Element Plus）

- [x] 管理员登录页面 ✅ (已修复登录问题)
- [ ] **系统配置页面**（微信小程序配置、支付配置、文件存储配置）
- [ ] 数据统计仪表板
- [ ] 用户管理页面
- [ ] 达人管理页面
- [ ] 服务管理页面
- [ ] 订单管理页面
- [ ] 评价管理页面
- [ ] 分类管理页面
- [ ] **轮播图管理页面**（图片上传、排序、状态管理）
- [ ] **通告管理页面**（内容编辑、类型选择、滚动设置）
- [ ] **推荐服务管理页面**（服务推荐、类型管理、排序权重）
- [ ] **内容审核管理页面**（审核处理、流程管理、统计分析）
- [ ] **充值记录管理页面**（记录查询、状态管理、支付处理）
- [ ] **提现记录管理页面**（申请审核、转账确认、状态管理）
- [ ] **财务统计页面**（数据图表、收支分析、统计报表）
- [ ] **消息推送管理页面**（消息记录管理、发送管理、模板配置、统计分析）

#### 4.3 新增的后端 API 接口 ✅ (已完成)

- [x] **收藏管理接口**（`/api/favorite/*`）

  - POST `/api/favorite/add` - 添加收藏
  - DELETE `/api/favorite/remove` - 取消收藏
  - DELETE `/api/favorite/{favoriteId}` - 删除收藏记录
  - GET `/api/favorite/list` - 获取收藏列表
  - GET `/api/favorite/check` - 检查收藏状态
  - GET `/api/favorite/statistics` - 获取收藏统计

- [x] **消息管理接口**（`/api/message/*`）

  - GET `/api/message/list` - 获取消息列表
  - GET `/api/message/{messageId}` - 获取消息详情
  - PUT `/api/message/{messageId}/read` - 标记消息已读
  - PUT `/api/message/batch-read` - 批量标记已读
  - DELETE `/api/message/{messageId}` - 删除消息
  - GET `/api/message/unread-count` - 获取未读数量
  - DELETE `/api/message/clear` - 清空消息

- [x] **交易记录接口**（`/api/transaction/*`）

  - GET `/api/transaction/list` - 获取交易记录列表
  - GET `/api/transaction/{transactionId}` - 获取交易记录详情
  - GET `/api/transaction/statistics` - 获取用户统计信息

- [x] **服务管理接口**（`/api/services/*`）

  - GET `/api/services` - 获取服务列表（分页）
  - GET `/api/services/{serviceId}` - 获取服务详情
  - GET `/api/services/hot` - 获取热门服务
  - GET `/api/services/recommend` - 获取推荐服务
  - GET `/api/categories/{categoryId}/services` - 获取分类下的服务
  - GET `/api/experts/{expertId}/services` - 获取达人的服务

- [x] **分类详情接口**
  - GET `/api/categories/{categoryId}` - 获取分类详情

#### 4.4 数据库表和 Service 实现完善 ✅ (2024-12-19)

- [x] **数据库表创建** - 创建了新的数据库表：

  - `favorites` - 收藏表（支持服务收藏和达人收藏）
  - `transaction_records` - 交易记录表（充值、消费、提现、退款）
  - 包含完整的索引和约束设计
  - 提供了测试数据和验证脚本

- [x] **Mapper 接口和 XML** - 创建了完整的数据访问层：

  - `FavoriteMapper.java` + `FavoriteMapper.xml` - 收藏数据访问
  - `TransactionMapper.java` + `TransactionMapper.xml` - 交易记录数据访问
  - 支持复杂查询、统计分析、分页查询等功能

- [x] **实体类创建** - 新增实体类：

  - `Favorite.java` - 收藏实体类
  - `TransactionRecord.java` - 交易记录实体类
  - `UserStatisticsVO.java` - 用户统计信息 VO

- [x] **Service 实现** - 完成了 Service 层实现：

  - `FavoriteServiceImpl.java` - 收藏服务实现
  - `TransactionServiceImpl.java` - 交易记录服务实现
  - 扩展了 `MessageRecordService` 接口，添加用户端消息管理方法

- [x] **功能特性** - 实现的核心功能：
  - 收藏管理：添加/取消收藏、收藏列表、收藏统计、重复收藏检查
  - 交易记录：充值/消费/提现/退款记录、交易统计、状态管理
  - 消息管理：用户消息列表、未读数量统计、批量操作
  - 数据安全：用户权限验证、事务管理、异常处理

### 第五阶段：小程序端开发 🚧 (进行中)

#### 5.1 小程序端（UniApp）

**用户端页面：**

- [x] 首页（搜索、轮播图、分类、热门商品）
- [x] 商品详情页（下单页面）
- [x] 订单页面（订单列表、订单详情）
- [x] 个人中心（登录、充值、收藏、客服）
- [x] **分类页面**（分类列表、分类详情）✅ 新增
- [x] **用户资料页面**（个人信息编辑、头像上传）✅ 新增
- [x] **收藏页面**（收藏服务、收藏达人）✅ 新增
- [x] **交易记录页面**（充值、消费、提现记录）✅ 新增
- [x] **消息中心页面**（系统消息、订单消息、服务消息）✅ 新增
- [ ] 手写签名组件

**达人端页面：**

- [x] 达人首页（待接单列表）
- [x] 订单管理
- [x] 个人中心（状态设置、提现）

**公共组件：**

- [x] 导航栏
- [x] 订单状态组件
- [x] 支付组件

### 第六阶段：高级功能 🚧 (进行中)

#### 6.1 支付模块 ✅ (已完成)

- [x] **支付记录管理**（支付创建、状态更新、回调处理）
- [x] **微信支付集成**（支付创建、回调处理、退款申请）
- [x] **余额支付**（余额扣减、充值功能）
- [x] **支付宝支付**（支付创建、回调处理）
- [x] **支付统计分析**（支付数据统计、用户支付汇总）
- [x] **超时订单处理**（自动取消超时支付订单）

#### 6.2 消息推送 ✅ (已完成)

- [x] **消息记录管理**（消息创建、状态管理、发送记录）
- [x] **微信模板消息配置**（模板 ID 配置、参数构建、发送接口）
- [x] **订单状态通知**（订单状态变更自动通知）
- [x] **系统消息推送**（系统通知、推广消息、批量发送）
- [x] **消息发送服务**（多渠道发送、重试机制、状态跟踪）
- [x] **消息统计分析**（发送成功率、阅读率、渠道统计）

#### 6.3 系统优化

- [ ] 性能优化
- [ ] 安全加固
- [ ] 日志记录
- [ ] 监控告警

### 第七阶段：测试和部署 (1-2 周)

#### 7.1 测试

- [ ] 单元测试
- [ ] 集成测试
- [ ] 压力测试
- [ ] 安全测试

#### 7.2 部署

- [ ] 服务器环境搭建
- [ ] 数据库部署
- [ ] 应用部署
- [ ] 域名配置
- [ ] SSL 证书配置

## 数据库表设计概要

### 核心表结构

1. **系统配置表 (system_configs)** - 新增重点

   - id, config_key, config_value, config_type, description, create_time, update_time

2. **用户表 (users)**

   - id, openid, nickname, avatar, phone, balance, status, create_time

3. **达人表 (experts)**

   - id, user_id, category_id, name, description, price_range, status, rating

4. **订单表 (orders)**

   - id, user_id, expert_id, service_id, amount, status, description, create_time

5. **服务表 (services)**

   - id, expert_id, category_id, name, price, description, images

6. **合同表 (contracts)**

   - id, order_id, user_signature, expert_signature, contract_content

7. **推荐服务表 (recommend_services)** - 新增

   - id, service_id, recommend_type, sort_order, recommend_reason, status, start_time, end_time

8. **审核记录表 (audit_records)** - 新增

   - id, audit_type, business_id, audit_title, audit_content, audit_status, auditor_id, audit_time

9. **支付记录表 (payment_records)** - 新增

   - id, payment_no, third_party_no, user_id, order_id, payment_type, payment_amount, payment_status

10. **消息记录表 (message_records)** - 新增

- id, message_type, user_id, title, content, status, business_id, business_type, template_id, template_data, send_channel, send_time, read_time, expire_time, retry_count, max_retry_count, fail_reason, extra_data

11. **文件存储记录表 (file_records)** - 新增

- id, file_name, file_path, file_type, storage_type, file_size, create_time

## API 接口对接问题修复计划

### 🔍 发现的问题

1. **缺失的后端 API 控制器**

   - 缺少用户端登录认证控制器 (`/api/user/login`, `/api/user/profile` 等)
   - 缺少用户端订单控制器 (`/api/user/orders`)
   - 缺少达人端订单控制器 (`/api/expert/orders`)
   - 缺少公共 API 控制器 (`/api/orders`, `/api/services` 等)

2. **数据库字段与接口参数不匹配**

   - UniApp 中使用 `talentId` 但数据库表是 `expert_id`
   - UniApp 中使用 `taskDescription` 但数据库表是 `description`
   - 订单状态枚举值不一致
   - 用户信息字段映射问题

3. **接口路径不统一**
   - UniApp 调用管理端接口获取数据 (`/admin/expert/page`)
   - 应该调用用户端接口 (`/api/expert/list`)

### 📋 修复计划

#### 第一阶段：创建缺失的后端 API 控制器

1. **用户端认证控制器** (`UserAuthController`)
2. **用户端订单控制器** (`UserOrderController`)
3. **达人端订单控制器** (`ExpertOrderController`)
4. **公共 API 控制器** (`PublicApiController`)

#### 第二阶段：统一数据库字段与接口参数

1. **修正 UniApp 接口参数**
2. **创建 DTO 转换类**

#### 第三阶段：更新 UniApp 前端接口调用

1. **修正 API 路径**
2. **统一请求响应参数**

### ✅ 已完成的修复工作

#### 1. **修复了 UniApp 前端 API 接口路径重复问题** 🔧

- ✅ **修复了所有 API 文件中的路径重复问题**

  - `auth.ts` - 用户认证接口路径修复
  - `user.ts` - 用户管理接口路径修复
  - `order.ts` - 订单管理接口路径修复
  - `expert.ts` - 达人管理接口路径修复
  - `service.ts` - 服务管理接口路径修复
  - `home.ts` - 首页接口路径修复
  - `message.ts` - 消息接口路径修复
  - `favorite.ts` - 收藏接口路径修复
  - `transaction.ts` - 交易记录接口路径修复

- ✅ **优化了请求配置和错误处理**
  - 增加了请求超时时间（15 秒）
  - 添加了请求 ID 用于调试跟踪
  - 优化了 HTTP 错误信息映射
  - 改进了响应数据解析逻辑
  - 增强了错误日志记录

#### 2. **优化了后端跨域配置** 🌐

- ✅ **扩展了 CORS 允许的源**
  - 添加了对多个本地开发端口的支持
  - 增加了预检请求缓存时间
  - 支持 Web 前端和 UniApp 的跨域访问

#### 3. **创建了缺失的后端 API 控制器**

- ✅ **UserAuthController** (`/api/user/*`)

  - POST `/api/user/login` - 微信登录
  - GET `/api/user/profile` - 获取用户信息
  - PUT `/api/user/profile` - 更新用户信息
  - POST `/api/user/bind-phone` - 绑定手机号
  - POST `/api/user/upload-avatar` - 上传头像
  - POST `/api/user/logout` - 退出登录
  - GET `/api/user/check-login` - 检查登录状态

- ✅ **UserOrderController** (`/api/user/orders`)

  - GET `/api/user/orders` - 获取用户订单列表
  - GET `/api/user/orders/statistics` - 获取用户订单统计
  - POST `/api/user/orders` - 创建订单（重定向到公共接口）
  - PUT `/api/user/orders/{id}/cancel` - 取消订单
  - PUT `/api/user/orders/{id}/confirm` - 确认订单
  - POST `/api/user/orders/{id}/after-sale` - 申请售后

- ✅ **ExpertOrderController** (`/api/expert/orders`)

  - GET `/api/expert/orders` - 获取达人订单列表
  - GET `/api/expert/orders/pending` - 获取待接单列表
  - GET `/api/expert/orders/statistics` - 获取达人订单统计
  - PUT `/api/expert/orders/{id}/accept` - 接单
  - PUT `/api/expert/orders/{id}/start` - 开始服务
  - PUT `/api/expert/orders/{id}/complete` - 完成订单

- ✅ **PublicApiController** (`/api/*`)
  - GET `/api/banners` - 获取轮播图列表
  - GET `/api/announcements` - 获取公告列表
  - GET `/api/announcements/{id}` - 获取公告详情
  - GET `/api/categories` - 获取分类列表
  - GET `/api/categories/{id}` - 获取分类详情
  - GET `/api/experts` - 获取达人列表
  - GET `/api/experts/{id}` - 获取达人详情
  - POST `/api/orders` - 创建订单
  - GET `/api/orders/{id}` - 获取订单详情
  - PUT `/api/orders/{id}/cancel` - 取消订单

#### 2. **创建了缺失的 DTO 和 VO 类**

- ✅ **UserLoginDTO** - 用户登录请求参数
- ✅ **UserUpdateDTO** - 用户信息更新参数
- ✅ **UserLoginVO** - 用户登录响应数据

#### 3. **统一了数据库字段与接口参数**

- ✅ **订单相关字段统一**

  - `talentId` → `expertId`
  - `taskDescription` → `description`
  - `orderStatus` → `status`
  - `paymentStatus` → `payStatus`
  - `completeTime` → `finishTime`
  - 添加了缺失字段：`serviceId`, `title`, `amount`, `payType`, `payTime`, `startTime`, `cancelReason`, `userRemark`, `expertRemark`, `contactInfo`, `serviceAddress`, `appointmentTime`

- ✅ **用户信息字段统一**

  - 移除了 `wechatNo`, `roleType`
  - 添加了 `unionid`, `gender`, `realName`, `totalRecharge`, `totalConsume`, `isExpert`, `registerTime`

- ✅ **订单状态枚举统一**
  - 修正为与数据库表一致：1-待接单, 2-已接单, 3-服务中, 4-已完成, 5-已取消, 6-售后中

#### 4. **更新了 UniApp 前端接口调用**

- ✅ **修正 API 路径**

  - 轮播图：`/admin/banner/enabled` → `/api/banners`
  - 公告：`/admin/announcement/scroll` → `/api/announcements`
  - 分类：`/admin/category/enabled` → `/api/categories`
  - 达人：`/admin/expert/page` → `/api/experts`

- ✅ **统一请求响应参数**
  - 订单接口参数已统一
  - 用户信息接口参数已统一
  - 达人信息接口参数已统一

### 🔧 需要后续完善的工作

#### 1. **服务层实现**

- 需要在 `UserServiceImpl` 中实现新增的方法：

  - `wechatLogin(UserLoginDTO loginDTO)`
  - `updateUserProfile(UserUpdateDTO updateDTO)`
  - `bindPhone(UserUpdateDTO updateDTO)`
  - `uploadAvatar(Long userId, MultipartFile file)`

- 需要在 `OrderServiceImpl` 中实现新增的方法：
  - `cancelOrder(Long orderId, Long userId, String reason)`
  - `confirmOrder(Long orderId, Long userId)`
  - `applyAfterSale(Long orderId, Long userId, String reason)`
  - `getUserOrderStatistics(Long userId)`
  - `getExpertOrderStatistics(Long expertId)`
  - `completeOrder(Long orderId, Long expertId, Map<String, Object> data)`

#### 2. **认证和权限**

- 完善 JWT 认证机制
- 实现微信登录逻辑
- 添加接口权限控制

#### 3. **文件上传**

- 实现头像上传功能
- 配置文件存储路径

#### 4. **测试验证**

- 测试所有新增的 API 接口
- 验证前后端数据传输
- 检查字段映射是否正确

## 🔧 接口修复总结

### 修复前的问题

1. **API 路径重复问题**

   ```typescript
   // 错误示例：路径中包含重复的 /api
   request.post("/api/user/login", params); // 实际请求: http://localhost:8080/api/api/user/login
   ```

2. **跨域配置不完整**

   ```java
   // 只支持单一源
   .allowedOrigins("http://localhost:3030")
   ```

3. **错误处理不够完善**
   - 缺少详细的 HTTP 错误信息映射
   - 响应数据解析容错性差
   - 调试信息不足

### 修复后的改进

1. **API 路径标准化**

   ```typescript
   // 修复后：移除路径中的重复 /api
   request.post("/user/login", params); // 实际请求: http://localhost:8080/api/user/login
   ```

2. **完善的跨域支持**

   ```java
   // 支持多个开发环境
   .allowedOrigins(
       "http://localhost:3030",     // Web前端
       "http://localhost:8080",     // 本地开发
       "http://127.0.0.1:3030",     // Web前端备用
       "http://127.0.0.1:8080"      // 本地开发备用
   )
   ```

3. **增强的错误处理**

   ```typescript
   // 添加请求ID跟踪
   options.header["X-Request-ID"] = requestId;

   // 详细的错误信息映射
   const errorMap = {
     400: "请求参数错误",
     401: "未授权，请重新登录",
     403: "拒绝访问",
     // ...更多错误类型
   };
   ```

## API 接口设计概要

### 用户端接口

- POST /api/user/login - 微信登录
- GET /api/user/profile - 获取用户信息
- PUT /api/user/profile - 更新用户信息
- POST /api/user/bind-phone - 绑定手机号
- POST /api/user/upload-avatar - 上传头像
- GET /api/user/orders - 获取用户订单列表
- POST /api/user/recharge - 余额充值

### 达人端接口

- POST /api/expert/apply - 申请成为达人
- GET /api/expert/profile - 获取达人信息
- PUT /api/expert/profile - 更新达人资料
- PUT /api/expert/status - 更新在线状态
- GET /api/expert/orders - 获取达人订单列表
- GET /api/expert/workspace/data - 获取工作台数据

### 公共接口

- GET /api/banners - 获取轮播图
- GET /api/announcements - 获取公告
- GET /api/categories - 获取分类列表
- GET /api/experts - 获取达人列表
- GET /api/services - 获取服务列表
- POST /api/orders - 创建订单
- GET /api/orders/{id} - 获取订单详情
- PUT /api/orders/{id}/accept - 接单
- PUT /api/orders/{id}/start - 开始服务
- PUT /api/orders/{id}/complete - 完成订单
- PUT /api/orders/{id}/cancel - 取消订单

### 管理端接口

- POST /api/admin/login - 管理员登录
- GET /api/admin/statistics - 获取统计数据
- **GET/POST /api/admin/configs - 系统配置管理（重点）**
- GET /api/admin/users - 用户管理
- GET /api/admin/orders - 订单管理
- **GET/POST/PUT/DELETE /api/admin/banners - 轮播图管理**
- **GET/POST/PUT/DELETE /api/admin/announcements - 通告管理**
- **GET/POST/PUT/DELETE /api/admin/recommend-services - 推荐服务管理**
- **GET/POST/PUT/DELETE /api/admin/audit - 内容审核管理**
- **GET/POST/PUT/DELETE /api/admin/message - 消息推送管理**

## 开发规范

### 代码规范

- 使用统一的代码格式化工具
- 遵循 RESTful API 设计规范
- 使用统一的错误码和响应格式
- 添加必要的注释和文档

### 数据库规范

- 使用统一的命名规范
- 添加必要的索引
- 设置合适的字段类型和长度
- 添加外键约束

### 安全规范

- 敏感信息加密存储
- API 接口权限控制
- 输入参数验证
- SQL 注入防护

## 部署环境

### 开发环境

- JDK 17
- MySQL 8.0
- Redis 6.0
- Node.js 18+

### 生产环境

- 服务器：阿里云 ECS
- 数据库：阿里云 RDS MySQL
- 缓存：阿里云 Redis
- 文件存储：阿里云 OSS
- 域名：已备案域名
- SSL：免费 SSL 证书

## 项目里程碑

- **里程碑 1**: 基础架构搭建完成 (2 周)
- **里程碑 2**: 核心业务模块完成 (5 周)
- **里程碑 3**: 管理端开发完成（优先） (7 周)
- **里程碑 4**: 小程序端开发完成 (11 周)
- **里程碑 5**: 高级功能开发完成 (14 周)
- **里程碑 6**: 测试和部署完成 (16 周)

## 风险评估

### 技术风险

- 微信小程序 API 变更
- 支付接口对接复杂度
- 高并发性能问题

### 业务风险

- 需求变更频繁
- 用户体验不佳
- 安全漏洞风险

### 应对措施

- 及时关注微信官方文档更新
- 提前进行支付接口测试
- 进行性能压测和优化
- 建立完善的测试流程
- 定期进行安全审计

---

## 微信小程序配置信息

- **AppID**: wxc9b98294ace36519
- **AppSecret**: 34887a584a83a6a53b010ae59ccc9f19
- **配置方式**: 通过管理端动态配置（存储在 system_configs 表）

## 系统配置管理

### 配置项类型

1. **微信小程序配置**

   - wx_appid: 小程序 AppID
   - wx_appsecret: 小程序 AppSecret
   - wx_mch_id: 商户号
   - wx_api_key: 支付密钥

2. **文件存储配置**

   - storage_type: 存储类型（local/oss）
   - oss_endpoint: OSS 端点
   - oss_access_key: OSS 访问密钥
   - oss_secret_key: OSS 密钥
   - oss_bucket: OSS 存储桶

3. **系统基础配置**
   - system_name: 系统名称
   - system_logo: 系统 Logo
   - contact_phone: 客服电话

## 日志配置管理

### 日志级别优化

为了减少控制台日志输出，突出重要信息，已对日志配置进行优化：

1. **控制台日志**

   - 应用程序日志：INFO 级别
   - 框架日志：ERROR 级别（大幅减少 Spring、MyBatis 等框架日志）
   - 根日志：WARN 级别

2. **日志格式美化**

   - 时间：简化为 HH:mm:ss 格式
   - 日志级别：红色加粗显示
   - 线程名：蓝色显示
   - 类名：青色显示，截取为 20 字符
   - 消息内容：绿色显示

3. **日志文件控制**
   - 默认启用日志文件生成
   - 可通过配置 `app.log.file-enabled: false` 禁用文件日志
   - 可通过启动参数 `--spring.profiles.active=no-file-log` 禁用文件日志

### 日志配置文件

- **logback-spring.xml**: 主要日志配置，支持条件配置
- **application.yml**: 日志级别和格式配置

### 使用方法

1. **启用文件日志**（默认）：

   ```bash
   java -jar expert.jar
   ```

2. **禁用文件日志**：

   ```bash
   java -jar expert.jar --spring.profiles.active=no-file-log
   ```

3. **修改配置文件**：
   ```yaml
   app:
     log:
       file-enabled: false # 禁用文件日志
   ```

---

## 默认登录信息

### 管理员账号

- **用户名**: admin
- **密码**: admin123
- **角色**: 超级管理员

### 登录地址

- **管理端**: http://localhost:3030/login
- **后端 API**: http://localhost:8080/api

### 密码重置方法

如果数据库中的密码不正确，可以通过以下方式生成新的 BCrypt 加密密码：

1. **启动后端服务**
2. **访问密码生成接口**：
   ```
   GET http://localhost:8080/api/admin/auth/encode-password?password=admin123
   ```
3. **复制返回的加密密码**，更新数据库：
   ```sql
   UPDATE admins SET password = '新生成的加密密码' WHERE username = 'admin';
   ```

**示例**：

- 原始密码：`admin123`
- 加密后密码：`$2a$10$...` (每次生成都不同，但都能验证同一个原始密码)

---

**开发团队**: 1 人全栈开发
**预计工期**: 16 周
**项目状态**: 规划阶段
**开发优先级**: 管理端优先
