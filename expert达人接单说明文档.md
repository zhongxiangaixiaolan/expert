# 达人服务平台开发文档

## 项目概述

基于Spring Boot 3.5.0 + Java 17 + UniApp Vue3 + Web Vue3 的三角色服务平台。

**角色定义：**
- 用户（顾客）：发起合作需求，下单支付
- 达人：提供服务，接单完成任务
- 管理员：平台管理，订单分配

---

## 技术栈

### 后端技术栈
- **框架：** Spring Boot 3.5.0
- **JDK：** Java 17
- **数据库：** MySQL 8.0 + Redis 7.0
- **ORM：** MyBatis-Plus 3.5.5
- **安全认证：** Spring Security + JWT
- **文件存储：** 阿里云OSS/MinIO
- **消息推送：** WebSocket + 微信小程序订阅消息

### 前端技术栈
- **移动端：** UniApp + Vue3 + TypeScript + Pinia
- **管理端：** Vue3 + Element Plus + TypeScript
- **UI库：** uView Plus (UniApp) / Element Plus (Web)

---

## 数据库设计

dev的数据库连接账号root，密码1234

### 1. 用户表 (sys_user)
```sql
CREATE TABLE `sys_user` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `openid` varchar(100) UNIQUE COMMENT '微信openid',
  `nickname` varchar(50) COMMENT '昵称',
  `avatar` varchar(500) COMMENT '头像URL',
  `phone` varchar(20) COMMENT '手机号',
  `wechat_no` varchar(50) COMMENT '微信号',
  `role_type` tinyint DEFAULT 1 COMMENT '角色类型:1用户 2达人 3管理员',
  `status` tinyint DEFAULT 1 COMMENT '状态:0禁用 1正常',
  `balance` decimal(10,2) DEFAULT 0 COMMENT '余额',
  `last_login_time` datetime COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='用户表';
```

### 2. 达人表 (sys_talent)
```sql
CREATE TABLE `sys_talent` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `category_id` bigint COMMENT '分类ID',
  `talent_name` varchar(100) COMMENT '达人名称',
  `description` text COMMENT '详细描述',
  `service_price` decimal(10,2) COMMENT '服务价格',
  `talent_avatar` varchar(500) COMMENT '达人头像',
  `contact_info` varchar(200) COMMENT '联系方式',
  `status` tinyint DEFAULT 0 COMMENT '状态:0待审核 1正常 2忙碌 3禁用',
  `order_count` int DEFAULT 0 COMMENT '接单数量',
  `rating` decimal(3,2) DEFAULT 5.0 COMMENT '评分',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='达人表';
```

### 3. 服务分类表 (sys_category)
```sql
CREATE TABLE `sys_category` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `icon` varchar(500) COMMENT '分类图标',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `price_min` decimal(10,2) COMMENT '价格区间最小值',
  `price_max` decimal(10,2) COMMENT '价格区间最大值',
  `status` tinyint DEFAULT 1 COMMENT '状态:0禁用 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) COMMENT='服务分类表';
```

### 4. 订单表 (sys_order)
```sql
CREATE TABLE `sys_order` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `order_no` varchar(32) UNIQUE NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `talent_id` bigint COMMENT '达人ID',
  `category_id` bigint COMMENT '分类ID',
  `service_name` varchar(200) COMMENT '服务名称',
  `service_price` decimal(10,2) COMMENT '服务价格',
  `task_description` text COMMENT '任务描述',
  `expected_time` datetime COMMENT '期望完成时间',
  `order_status` tinyint DEFAULT 1 COMMENT '订单状态:1待接单 2服务中 3已完成 4售后中 5已取消',
  `payment_status` tinyint DEFAULT 0 COMMENT '支付状态:0未支付 1已支付 2已退款',
  `user_signature` varchar(500) COMMENT '用户签名图片',
  `talent_signature` varchar(500) COMMENT '达人签名图片',
  `completion_screenshots` json COMMENT '完成截图',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `accept_time` datetime COMMENT '接单时间',
  `complete_time` datetime COMMENT '完成时间',
  `cancel_time` datetime COMMENT '取消时间'
) COMMENT='订单表';
```

### 5. 轮播图表 (sys_banner)
```sql
CREATE TABLE `sys_banner` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `title` varchar(200) COMMENT '标题',
  `image_url` varchar(500) COMMENT '图片URL',
  `link_url` varchar(500) COMMENT '跳转链接',
  `type` tinyint DEFAULT 1 COMMENT '类型:1达人轮播 2广告轮播',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` tinyint DEFAULT 1 COMMENT '状态:0禁用 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) COMMENT='轮播图表';
```

### 6. 收藏表 (sys_favorite)
```sql
CREATE TABLE `sys_favorite` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `talent_id` bigint NOT NULL COMMENT '达人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_user_talent` (`user_id`, `talent_id`)
) COMMENT='收藏表';
```

### 7. 通知公告表 (sys_announcement)
```sql
CREATE TABLE `sys_announcement` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` text COMMENT '内容',
  `type` tinyint DEFAULT 1 COMMENT '类型:1通告 2公告',
  `status` tinyint DEFAULT 1 COMMENT '状态:0禁用 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) COMMENT='通知公告表';
```

### 8. 交易记录表 (sys_transaction)
```sql
CREATE TABLE `sys_transaction` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `transaction_no` varchar(32) UNIQUE COMMENT '交易单号',
  `type` tinyint NOT NULL COMMENT '类型:1充值 2消费 3提现 4退款',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `balance_before` decimal(10,2) COMMENT '交易前余额',
  `balance_after` decimal(10,2) COMMENT '交易后余额',
  `status` tinyint DEFAULT 0 COMMENT '状态:0处理中 1成功 2失败',
  `description` varchar(500) COMMENT '描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) COMMENT='交易记录表';
```

### 9. 消息通知表 (sys_message)
```sql
CREATE TABLE `sys_message` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `title` varchar(200) NOT NULL COMMENT '消息标题',
  `content` text COMMENT '消息内容',
  `type` tinyint NOT NULL COMMENT '消息类型:1系统通知 2订单通知 3支付通知 4评价通知',
  `related_id` bigint COMMENT '关联ID(订单ID等)',
  `status` tinyint DEFAULT 0 COMMENT '状态:0未读 1已读',
  `push_status` tinyint DEFAULT 0 COMMENT '推送状态:0未推送 1已推送 2推送失败',
  `template_id` varchar(100) COMMENT '微信模板ID',
  `push_data` json COMMENT '推送数据',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `read_time` datetime COMMENT '阅读时间'
) COMMENT='消息通知表';
```

### 10. 评价表 (sys_evaluation)
```sql
CREATE TABLE `sys_evaluation` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '评价用户ID',
  `talent_id` bigint NOT NULL COMMENT '被评价达人ID',
  `rating` tinyint NOT NULL COMMENT '评分:1-5星',
  `service_rating` tinyint COMMENT '服务态度评分',
  `speed_rating` tinyint COMMENT '完成速度评分',
  `quality_rating` tinyint COMMENT '服务质量评分',
  `content` text COMMENT '评价内容',
  `images` json COMMENT '评价图片',
  `is_anonymous` tinyint DEFAULT 0 COMMENT '是否匿名:0否 1是',
  `status` tinyint DEFAULT 1 COMMENT '状态:0禁用 1正常',
  `talent_reply` text COMMENT '达人回复',
  `reply_time` datetime COMMENT '回复时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_order_user` (`order_id`, `user_id`)
) COMMENT='评价表';
```

### 11. 消息模板表 (sys_message_template)
```sql
CREATE TABLE `sys_message_template` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `template_code` varchar(50) UNIQUE NOT NULL COMMENT '模板编码',
  `template_name` varchar(200) COMMENT '模板名称',
  `wechat_template_id` varchar(100) COMMENT '微信模板ID',
  `title_template` varchar(500) COMMENT '标题模板',
  `content_template` text COMMENT '内容模板',
  `type` tinyint NOT NULL COMMENT '类型:1系统通知 2订单通知 3支付通知',
  `status` tinyint DEFAULT 1 COMMENT '状态:0禁用 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) COMMENT='消息模板表';
```

