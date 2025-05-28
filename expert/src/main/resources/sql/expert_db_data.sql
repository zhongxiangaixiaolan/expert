/*
 Navicat MySQL Data Transfer

 Source Server         : localhost_3306-80
 Source Server Type    : MySQL
 Source Server Version : 80400
 Source Host           : localhost:3306
 Source Schema         : expert_db

 Target Server Type    : MySQL
 Target Server Version : 80400
 File Encoding         : 65001

 Date: 28/05/2025 12:28:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admins
-- ----------------------------
DROP TABLE IF EXISTS `admins`;
CREATE TABLE `admins`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（加密:admin123）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ADMIN' COMMENT '角色：SUPER_ADMIN,ADMIN',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后登录IP',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE,
  INDEX `idx_username`(`username`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admins
-- ----------------------------
INSERT INTO `admins` VALUES (1, 'admin', '$2a$10$lbTc4N60cczI6VODWCJpGud6mo4c3fdB7bkLPYnDKGAek1fdU5Ks.', '超级管理员', '15478654785', '25746qwe8@qq.com', '322cb157-27a6-4773-9bca-3d50e1ac0d62.png', 'SUPER_ADMIN', 1, '2025-05-27 19:16:47', '127.0.0.1', '2025-05-23 20:25:30', '2025-05-27 19:16:47', 0);

-- ----------------------------
-- Table structure for announcements
-- ----------------------------
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '通告内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'NOTICE' COMMENT '通告类型：NOTICE,ACTIVITY,SYSTEM',
  `is_scroll` tinyint(1) NULL DEFAULT 1 COMMENT '是否滚动显示：0-否，1-是',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序权重',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_type`(`type`) USING BTREE,
  INDEX `idx_sort_order`(`sort_order`) USING BTREE,
  INDEX `idx_deleted`(`deleted`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of announcements
-- ----------------------------

-- ----------------------------
-- Table structure for audit_records
-- ----------------------------
DROP TABLE IF EXISTS `audit_records`;
CREATE TABLE `audit_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `audit_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '审核类型（EXPERT-达人审核，SERVICE-服务审核，REVIEW-评价审核，WITHDRAW-提现审核，CONTENT-内容审核）',
  `business_id` bigint NOT NULL COMMENT '关联业务ID',
  `audit_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '审核标题',
  `audit_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '审核内容',
  `audit_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '审核状态（PENDING-待审核，APPROVED-审核通过，REJECTED-审核拒绝）',
  `audit_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '审核意见',
  `auditor_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `auditor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核人姓名',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `submitter_id` bigint NOT NULL COMMENT '提交人ID',
  `submitter_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提交人姓名',
  `submit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `priority` tinyint NOT NULL DEFAULT 2 COMMENT '优先级（1-低，2-中，3-高）',
  `attachments` json NULL COMMENT '附件信息（JSON格式）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `updater` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_audit_type`(`audit_type`) USING BTREE,
  INDEX `idx_business_id`(`business_id`) USING BTREE,
  INDEX `idx_audit_status`(`audit_status`) USING BTREE,
  INDEX `idx_auditor_id`(`auditor_id`) USING BTREE,
  INDEX `idx_submitter_id`(`submitter_id`) USING BTREE,
  INDEX `idx_priority`(`priority`) USING BTREE,
  INDEX `idx_submit_time`(`submit_time`) USING BTREE,
  INDEX `idx_audit_time`(`audit_time`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_deleted`(`deleted`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '审核记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of audit_records
-- ----------------------------
INSERT INTO `audit_records` VALUES (1, 'EXPERT', 1, '达人认证申请', '申请成为平台认证达人，专业技能：网站开发', 'PENDING', NULL, NULL, NULL, NULL, 1, '张三', '2024-01-15 10:00:00', 2, NULL, '2025-05-23 22:12:54', '2025-05-23 22:12:54', 'system', NULL, 0);
INSERT INTO `audit_records` VALUES (2, 'SERVICE', 1, '服务上架申请', '申请上架网站开发服务，价格：5000元', 'PENDING', NULL, NULL, NULL, NULL, 1, '张三', '2024-01-15 11:00:00', 2, NULL, '2025-05-23 22:12:54', '2025-05-23 22:12:54', 'system', NULL, 0);
INSERT INTO `audit_records` VALUES (3, 'REVIEW', 1, '用户评价审核', '用户对服务的评价内容需要审核', 'APPROVED', '评价内容符合规范，审核通过', 1001, '管理员A', '2024-01-14 16:00:00', 2, '李四', '2024-01-14 15:30:00', 1, NULL, '2025-05-23 22:12:54', '2025-05-23 22:12:54', 'system', 'admin', 0);
INSERT INTO `audit_records` VALUES (4, 'WITHDRAW', 1, '提现申请审核', '申请提现1000元到银行卡', 'PENDING', NULL, NULL, NULL, NULL, 1, '张三', '2024-01-15 14:00:00', 3, NULL, '2025-05-23 22:12:54', '2025-05-23 22:12:54', 'system', NULL, 0);
INSERT INTO `audit_records` VALUES (5, 'CONTENT', 1, '内容审核申请', '轮播图内容需要审核', 'REJECTED', '轮播图内容不符合平台规范，请重新提交', 1002, '管理员B', '2024-01-13 10:30:00', 3, '王五', '2024-01-13 09:00:00', 2, NULL, '2025-05-23 22:12:54', '2025-05-23 22:12:54', 'system', 'admin', 0);

-- ----------------------------
-- Table structure for banners
-- ----------------------------
DROP TABLE IF EXISTS `banners`;
CREATE TABLE `banners`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '轮播图标题',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `link_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跳转链接',
  `link_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'NONE' COMMENT '链接类型：NONE,URL,SERVICE,CATEGORY',
  `link_id` bigint NULL DEFAULT NULL COMMENT '关联ID（服务ID或分类ID）',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序权重',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_sort_order`(`sort_order`) USING BTREE,
  INDEX `idx_deleted`(`deleted`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '轮播图表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of banners
-- ----------------------------

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类描述',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类图标URL',
  `icon_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'iconify' COMMENT '图标类型：iconify,emoji,url',
  `icon_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '#409eff' COMMENT '图标颜色',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序权重',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_sort_order`(`sort_order`) USING BTREE,
  INDEX `idx_deleted`(`deleted`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES (1, '摄影服务', '专业摄影服务，包括人像摄影、风景摄影、商业摄影等', 'Brush', 'iconify', '#ff6b35', 1, 1, '2024-01-01 10:00:00', '2025-05-27 15:24:46', 0);
INSERT INTO `categories` VALUES (2, '设计服务', 'UI/UX设计、平面设计、品牌设计等专业设计服务', 'Brush', 'iconify', '#e91e63', 2, 1, '2024-01-01 10:00:00', '2025-05-27 15:24:46', 0);
INSERT INTO `categories` VALUES (3, '编程开发', '网站开发、APP开发、小程序开发等技术服务', 'Monitor', 'iconify', '#007aff', 3, 1, '2024-01-01 10:00:00', '2025-05-27 15:24:47', 0);
INSERT INTO `categories` VALUES (4, '翻译服务', '英语翻译、多语种翻译、文档翻译等语言服务', 'ChatLineRound', 'iconify', '#4cd964', 4, 1, '2024-01-01 10:00:00', '2025-05-27 15:24:47', 0);
INSERT INTO `categories` VALUES (5, '营销策划', '品牌策划、活动策划、数字营销等营销服务', 'ShoppingBag', 'iconify', '#409eff', 5, 1, '2024-01-01 10:00:00', '2025-05-27 15:24:17', 0);
INSERT INTO `categories` VALUES (6, '咨询服务', '商业咨询、法律咨询、财务咨询等专业咨询', 'ChatDotRound', 'iconify', '#5bc0de', 6, 1, '2024-01-01 10:00:00', '2025-05-27 15:24:47', 0);

-- ----------------------------
-- Table structure for chat_messages
-- ----------------------------
DROP TABLE IF EXISTS `chat_messages`;
CREATE TABLE `chat_messages`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `admin_id` bigint NULL DEFAULT NULL COMMENT '客服ID',
  `message_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'TEXT' COMMENT '消息类型：TEXT,IMAGE,FILE',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `sender_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发送者类型：USER,ADMIN',
  `is_read` tinyint(1) NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_admin_id`(`admin_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  CONSTRAINT `chat_messages_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `chat_messages_ibfk_2` FOREIGN KEY (`admin_id`) REFERENCES `admins` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客服聊天表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chat_messages
-- ----------------------------

-- ----------------------------
-- Table structure for contracts
-- ----------------------------
DROP TABLE IF EXISTS `contracts`;
CREATE TABLE `contracts`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `contract_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '合同内容',
  `user_signature` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户签名图片URL',
  `expert_signature` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '达人签名图片URL',
  `user_sign_time` datetime NULL DEFAULT NULL COMMENT '用户签名时间',
  `expert_sign_time` datetime NULL DEFAULT NULL COMMENT '达人签名时间',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '合同状态：0-待签署，1-用户已签，2-达人已签，3-双方已签',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_id`(`order_id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  CONSTRAINT `contracts_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '合同表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of contracts
-- ----------------------------

-- ----------------------------
-- Table structure for experts
-- ----------------------------
DROP TABLE IF EXISTS `experts`;
CREATE TABLE `experts`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `category_id` bigint NOT NULL COMMENT '服务分类ID',
  `expert_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '达人名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '达人描述',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '达人头像',
  `price_min` decimal(8, 2) NULL DEFAULT 0.00 COMMENT '最低价格',
  `price_max` decimal(8, 2) NULL DEFAULT 0.00 COMMENT '最高价格',
  `rating` decimal(3, 2) NULL DEFAULT 5.00 COMMENT '评分（1-5分）',
  `order_count` int NULL DEFAULT 0 COMMENT '接单数量',
  `complete_count` int NULL DEFAULT 0 COMMENT '完成数量',
  `complete_rate` decimal(5, 2) NULL DEFAULT 100.00 COMMENT '完成率',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：0-下线，1-在线，2-忙碌',
  `audit_status` tinyint(1) NULL DEFAULT 0 COMMENT '审核状态：0-待审核，1-通过，2-拒绝',
  `audit_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_category_id`(`category_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_audit_status`(`audit_status`) USING BTREE,
  INDEX `idx_rating`(`rating`) USING BTREE,
  CONSTRAINT `experts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `experts_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '达人表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for expert_photos
-- ----------------------------
DROP TABLE IF EXISTS `expert_photos`;
CREATE TABLE `expert_photos`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `expert_id` bigint NOT NULL COMMENT '达人ID',
  `photo_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '照片文件名',
  `photo_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '照片标题',
  `photo_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '照片描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序顺序',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小（字节）',
  `width` int NULL DEFAULT NULL COMMENT '图片宽度',
  `height` int NULL DEFAULT NULL COMMENT '图片高度',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_expert_id`(`expert_id`) USING BTREE,
  INDEX `idx_sort_order`(`sort_order`) USING BTREE,
  CONSTRAINT `expert_photos_ibfk_1` FOREIGN KEY (`expert_id`) REFERENCES `experts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '达人照片表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of experts
-- ----------------------------
INSERT INTO `experts` VALUES (1, 4, 1, '专业摄影师小赵', '专业摄影服务，擅长人像、风景、商业摄影，拥有10年摄影经验，设备齐全，服务周到。', '1.png', 200.00, 2000.00, 4.80, 156, 142, 91.03, 1, 1, '摄影技术专业，作品质量高', '2024-01-10 09:00:00', '2025-05-27 23:41:41', 0);
INSERT INTO `experts` VALUES (2, 5, 2, '设计达人小钱', '专业UI/UX设计师，精通Photoshop、Illustrator、Figma等设计软件，为多家知名企业提供设计服务。', '1.png', 300.00, 1500.00, 4.90, 89, 85, 95.51, 1, 1, '设计理念先进，执行力强', '2024-01-08 14:30:00', '2025-05-27 23:41:44', 0);
INSERT INTO `experts` VALUES (3, 10, 3, '编程高手小王', '全栈开发工程师，精通Java、Python、JavaScript等多种编程语言，有丰富的项目开发经验。', '1.png', 500.00, 3000.00, 4.70, 67, 58, 86.57, 2, 1, '技术能力强，项目经验丰富', '2024-01-07 10:15:00', '2025-05-27 23:41:46', 0);
INSERT INTO `experts` VALUES (4, 14, 4, '翻译专家小刘', '专业英语翻译，英语专业八级，有海外留学经历，擅长商务翻译、文学翻译、技术翻译。', '1.png', 100.00, 800.00, 4.60, 234, 218, 93.16, 1, 1, '语言功底扎实，翻译准确', '2024-01-03 13:45:00', '2025-05-27 23:41:48', 0);
INSERT INTO `experts` VALUES (5, 1, 5, '营销策划师小张', '资深营销策划师，擅长品牌策划、活动策划、数字营销，为多个品牌制定成功的营销方案。', '1.png', 800.00, 5000.00, 4.50, 45, 38, 84.44, 0, 1, '1133', '2024-01-15 10:30:00', '2025-05-27 23:41:51', 0);
INSERT INTO `experts` VALUES (6, 2, 1, '婚礼摄影师小李', '专业婚礼摄影师，专注婚礼跟拍，擅长捕捉美好瞬间，让每一个重要时刻都成为永恒回忆。', '1.png', 1000.00, 8000.00, 4.90, 78, 76, 97.44, 1, 1, '婚礼摄影经验丰富，客户满意度高', '2024-01-14 15:20:00', '2025-05-27 23:41:54', 0);

-- ----------------------------
-- Records of expert_photos
-- ----------------------------
INSERT INTO `expert_photos` VALUES (1, 1, 'photo_1_1.jpg', '人像摄影作品', '专业人像摄影，展现自然美感', 1, 245760, 1080, 1440, '2024-01-10 09:30:00', '2024-01-10 09:30:00', 0);
INSERT INTO `expert_photos` VALUES (2, 1, 'photo_1_2.jpg', '风景摄影作品', '大自然风光摄影，色彩丰富', 2, 312480, 1080, 1440, '2024-01-10 09:35:00', '2024-01-10 09:35:00', 0);
INSERT INTO `expert_photos` VALUES (3, 1, 'photo_1_3.jpg', '商业摄影作品', '产品商业摄影，细节精致', 3, 198720, 1080, 1440, '2024-01-10 09:40:00', '2024-01-10 09:40:00', 0);
INSERT INTO `expert_photos` VALUES (4, 2, 'photo_2_1.jpg', 'UI设计作品', '移动端界面设计，简洁美观', 1, 156800, 1080, 1440, '2024-01-08 15:00:00', '2024-01-08 15:00:00', 0);
INSERT INTO `expert_photos` VALUES (5, 2, 'photo_2_2.jpg', '品牌设计作品', '企业品牌视觉设计，专业规范', 2, 223360, 1080, 1440, '2024-01-08 15:05:00', '2024-01-08 15:05:00', 0);
INSERT INTO `expert_photos` VALUES (6, 3, 'photo_3_1.jpg', '项目开发截图', '全栈项目开发，功能完善', 1, 189440, 1080, 1440, '2024-01-07 10:45:00', '2024-01-07 10:45:00', 0);
INSERT INTO `expert_photos` VALUES (7, 3, 'photo_3_2.jpg', '代码展示', '优雅的代码结构，注释清晰', 2, 167680, 1080, 1440, '2024-01-07 10:50:00', '2024-01-07 10:50:00', 0);
INSERT INTO `expert_photos` VALUES (8, 6, 'photo_6_1.jpg', '婚礼现场', '浪漫婚礼现场，幸福瞬间', 1, 278400, 1080, 1440, '2024-01-14 15:30:00', '2024-01-14 15:30:00', 0);
INSERT INTO `expert_photos` VALUES (9, 6, 'photo_6_2.jpg', '新人合影', '新人甜蜜合影，爱意满满', 2, 234560, 1080, 1440, '2024-01-14 15:35:00', '2024-01-14 15:35:00', 0);
INSERT INTO `expert_photos` VALUES (10, 6, 'photo_6_3.jpg', '婚礼细节', '婚礼细节拍摄，记录美好', 3, 201600, 1080, 1440, '2024-01-14 15:40:00', '2024-01-14 15:40:00', 0);

-- ----------------------------
-- Table structure for favorites
-- ----------------------------
DROP TABLE IF EXISTS `favorites`;
CREATE TABLE `favorites`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `favorite_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收藏类型：service-服务，expert-达人',
  `target_id` bigint NOT NULL COMMENT '目标ID（服务ID或达人ID）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_type_target`(`user_id`, `favorite_type`, `target_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_favorite_type`(`favorite_type`) USING BTREE,
  INDEX `idx_target_id`(`target_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收藏表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of favorites
-- ----------------------------

-- ----------------------------
-- Table structure for file_records
-- ----------------------------
DROP TABLE IF EXISTS `file_records`;
CREATE TABLE `file_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原始文件名',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '存储文件名',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件路径',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件类型',
  `file_size` bigint NULL DEFAULT 0 COMMENT '文件大小（字节）',
  `storage_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'LOCAL' COMMENT '存储类型：LOCAL,OSS',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务类型：AVATAR,SIGNATURE,SERVICE,BANNER',
  `business_id` bigint NULL DEFAULT NULL COMMENT '业务关联ID',
  `upload_user_id` bigint NULL DEFAULT NULL COMMENT '上传用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_business_type`(`business_type`) USING BTREE,
  INDEX `idx_business_id`(`business_id`) USING BTREE,
  INDEX `idx_upload_user_id`(`upload_user_id`) USING BTREE,
  INDEX `idx_storage_type`(`storage_type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件存储记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of file_records
-- ----------------------------

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `expert_id` bigint NULL DEFAULT NULL COMMENT '达人ID',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `service_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务名称',
  `service_price` decimal(8, 2) NOT NULL COMMENT '服务价格',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `amount` decimal(8, 2) NOT NULL COMMENT '订单金额',
  `paid_amount` decimal(8, 2) NULL DEFAULT 0.00 COMMENT '实付金额',
  `discount_amount` decimal(8, 2) NULL DEFAULT 0.00 COMMENT '优惠金额',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详细需求描述',
  `expected_time` datetime NULL DEFAULT NULL COMMENT '期望完成时间',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '订单状态：1-待接单，2-已接单，3-服务中，4-已完成，5-已取消，6-售后中',
  `pay_status` tinyint(1) NULL DEFAULT 0 COMMENT '支付状态：0-未支付，1-已支付，2-已退款',
  `pay_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付方式：WECHAT,BALANCE',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `accept_time` datetime NULL DEFAULT NULL COMMENT '接单时间',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始服务时间',
  `finish_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '取消原因',
  `user_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户备注',
  `expert_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '达人备注',
  `admin_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员备注',
  `contact_info` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系方式',
  `service_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务地址',
  `appointment_time` datetime NULL DEFAULT NULL COMMENT '预约时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no`) USING BTREE,
  INDEX `service_id`(`service_id`) USING BTREE,
  INDEX `idx_order_no`(`order_no`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_expert_id`(`expert_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_pay_status`(`pay_status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_title`(`title`) USING BTREE,
  INDEX `idx_start_time`(`start_time`) USING BTREE,
  INDEX `idx_finish_time`(`finish_time`) USING BTREE,
  INDEX `idx_appointment_time`(`appointment_time`) USING BTREE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`expert_id`) REFERENCES `experts` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------

-- ----------------------------
-- Table structure for payment_records
-- ----------------------------
DROP TABLE IF EXISTS `payment_records`;
CREATE TABLE `payment_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `payment_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付单号（系统生成）',
  `third_party_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '第三方支付单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_id` bigint NULL DEFAULT NULL COMMENT '订单ID（可为空，充值时为空）',
  `payment_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付类型（WECHAT_PAY-微信支付，BALANCE_PAY-余额支付，ALIPAY-支付宝支付）',
  `payment_amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `actual_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '实际支付金额',
  `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '支付状态（PENDING-待支付，PAYING-支付中，SUCCESS-支付成功，FAILED-支付失败，CANCELLED-支付取消，REFUNDED-已退款，REFUNDING-退款中）',
  `payment_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付描述',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `callback_time` datetime NULL DEFAULT NULL COMMENT '回调时间',
  `callback_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '回调数据（JSON格式）',
  `refund_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '退款金额',
  `refund_time` datetime NULL DEFAULT NULL COMMENT '退款时间',
  `refund_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '退款原因',
  `client_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '客户端IP',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `updater` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_payment_no`(`payment_no`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE,
  INDEX `idx_payment_type`(`payment_type`) USING BTREE,
  INDEX `idx_payment_status`(`payment_status`) USING BTREE,
  INDEX `idx_third_party_no`(`third_party_no`) USING BTREE,
  INDEX `idx_payment_time`(`payment_time`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_deleted`(`deleted`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '支付记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of payment_records
-- ----------------------------

-- ----------------------------
-- Table structure for recharge_records
-- ----------------------------
DROP TABLE IF EXISTS `recharge_records`;
CREATE TABLE `recharge_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '充值订单号',
  `amount` decimal(8, 2) NOT NULL COMMENT '充值金额',
  `pay_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '支付方式：WECHAT,ALIPAY',
  `pay_order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '第三方支付订单号',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态：0-待支付，1-已支付，2-支付失败',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_order_no`(`order_no`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_deleted`(`deleted`) USING BTREE,
  CONSTRAINT `recharge_records_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '充值记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of recharge_records
-- ----------------------------

-- ----------------------------
-- Table structure for recommend_services
-- ----------------------------
DROP TABLE IF EXISTS `recommend_services`;
CREATE TABLE `recommend_services`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `recommend_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '推荐类型（HOT-热门推荐，FEATURED-精选推荐，NEW-新品推荐，DISCOUNT-优惠推荐）',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序权重（数值越大越靠前）',
  `recommend_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '推荐理由',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `updater` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_service_recommend`(`service_id`, `recommend_type`, `deleted`) USING BTREE,
  INDEX `idx_service_id`(`service_id`) USING BTREE,
  INDEX `idx_recommend_type`(`recommend_type`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_sort_order`(`sort_order`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_deleted`(`deleted`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '推荐服务表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of recommend_services
-- ----------------------------

-- ----------------------------
-- Table structure for services
-- ----------------------------
DROP TABLE IF EXISTS `services`;
CREATE TABLE `services`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `expert_id` bigint NOT NULL COMMENT '达人ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '服务描述',
  `price` decimal(8, 2) NOT NULL COMMENT '服务价格',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '服务图片（JSON数组）',
  `rating` decimal(3, 2) NULL DEFAULT 5.00 COMMENT '服务评分（1-5分）',
  `order_count` int NULL DEFAULT 0 COMMENT '订单数量',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务标签',
  `is_hot` tinyint(1) NULL DEFAULT 0 COMMENT '是否热门：0-否，1-是',
  `is_recommend` tinyint(1) NULL DEFAULT 0 COMMENT '是否推荐：0-否，1-是',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序权重',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_expert_id`(`expert_id`) USING BTREE,
  INDEX `idx_category_id`(`category_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_is_hot`(`is_hot`) USING BTREE,
  INDEX `idx_is_recommend`(`is_recommend`) USING BTREE,
  INDEX `idx_price`(`price`) USING BTREE,
  INDEX `idx_rating`(`rating`) USING BTREE,
  INDEX `idx_order_count`(`order_count`) USING BTREE,
  CONSTRAINT `services_ibfk_1` FOREIGN KEY (`expert_id`) REFERENCES `experts` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `services_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务商品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of services
-- ----------------------------

-- ----------------------------
-- Table structure for system_configs
-- ----------------------------
DROP TABLE IF EXISTS `system_configs`;
CREATE TABLE `system_configs`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '配置值',
  `config_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'STRING' COMMENT '配置类型：STRING,NUMBER,BOOLEAN,JSON',
  `config_group` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置分组：WECHAT,OSS,SYSTEM,PAY',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配置描述',
  `is_encrypted` tinyint(1) NULL DEFAULT 0 COMMENT '是否加密存储：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `config_key`(`config_key`) USING BTREE,
  INDEX `idx_config_group`(`config_group`) USING BTREE,
  INDEX `idx_config_key`(`config_key`) USING BTREE,
  INDEX `idx_deleted`(`deleted`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_configs
-- ----------------------------
INSERT INTO `system_configs` VALUES (1, 'wx_appid', 'wxc9b98294ace36519', 'STRING', 'WECHAT', '微信小程序AppID', 0, '2025-05-23 20:25:30', '2025-05-23 20:25:30', 0);
INSERT INTO `system_configs` VALUES (2, 'wx_appsecret', '34887a584a83a6a53b010ae59ccc9f19', 'STRING', 'WECHAT', '微信小程序AppSecret', 1, '2025-05-23 20:25:30', '2025-05-23 20:25:30', 0);
INSERT INTO `system_configs` VALUES (3, 'wx_mch_id', '', 'STRING', 'WECHAT', '微信支付商户号', 0, '2025-05-23 20:25:30', '2025-05-23 20:25:30', 0);
INSERT INTO `system_configs` VALUES (4, 'wx_api_key', '', 'STRING', 'WECHAT', '微信支付API密钥', 1, '2025-05-23 20:25:30', '2025-05-23 20:25:30', 0);
INSERT INTO `system_configs` VALUES (5, 'storage_type', 'LOCAL', 'STRING', 'STORAGE', '文件存储类型：LOCAL,OSS', 0, '2025-05-23 20:25:30', '2025-05-24 11:03:19', 0);
INSERT INTO `system_configs` VALUES (6, 'oss_endpoint', '', 'STRING', 'STORAGE', 'OSS端点地址', 0, '2025-05-23 20:25:30', '2025-05-24 11:03:19', 0);
INSERT INTO `system_configs` VALUES (7, 'oss_access_key', '', 'STRING', 'STORAGE', 'OSS访问密钥', 1, '2025-05-23 20:25:30', '2025-05-24 11:03:19', 0);
INSERT INTO `system_configs` VALUES (8, 'oss_secret_key', '', 'STRING', 'STORAGE', 'OSS密钥', 1, '2025-05-23 20:25:30', '2025-05-24 11:03:19', 0);
INSERT INTO `system_configs` VALUES (9, 'oss_bucket', '', 'STRING', 'STORAGE', 'OSS存储桶名称', 0, '2025-05-23 20:25:30', '2025-05-24 11:03:19', 0);
INSERT INTO `system_configs` VALUES (10, 'system_name', '达人接单平台', 'STRING', 'SYSTEM', '系统名称', 0, '2025-05-23 20:25:30', '2025-05-24 10:54:04', 0);
INSERT INTO `system_configs` VALUES (11, 'contact_phone', '400-888-88881', 'STRING', 'SYSTEM', '客服电话', 0, '2025-05-23 20:25:30', '2025-05-24 10:54:04', 0);
INSERT INTO `system_configs` VALUES (12, 'system_logo', '', 'STRING', 'SYSTEM', '系统Logo地址', 0, '2025-05-24 10:53:03', '2025-05-24 10:54:04', 0);
INSERT INTO `system_configs` VALUES (13, 'platform_commission_rate', '12', 'NUMBER', 'BUSINESS', '平台抽成比例（%）', 0, '2025-05-24 10:53:03', '2025-05-27 19:08:30', 0);
INSERT INTO `system_configs` VALUES (14, 'min_withdraw_amount', '100', 'NUMBER', 'BUSINESS', '最小提现金额（元）', 0, '2025-05-24 10:53:03', '2025-05-27 19:08:30', 0);

-- ----------------------------
-- Table structure for transaction_records
-- ----------------------------
DROP TABLE IF EXISTS `transaction_records`;
CREATE TABLE `transaction_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '交易类型：recharge-充值，consume-消费，withdraw-提现，refund-退款',
  `amount` decimal(10, 2) NOT NULL COMMENT '交易金额',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '交易状态：0-处理中，1-成功，2-失败，3-已取消',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '交易描述',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单号',
  `payment_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付方式（充值时）',
  `service_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务名称（消费时）',
  `withdraw_account` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提现账户（提现时）',
  `order_id` bigint NULL DEFAULT NULL COMMENT '关联订单ID',
  `third_party_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '第三方交易号',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `finish_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_type`(`type`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '交易记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of transaction_records
-- ----------------------------
INSERT INTO `transaction_records` VALUES (1, 1, 'recharge', 100.00, 1, '微信充值', 'R202412190001', '微信支付', NULL, NULL, NULL, NULL, NULL, '2025-05-27 12:25:15', NULL, '2025-05-27 12:25:15');
INSERT INTO `transaction_records` VALUES (2, 1, 'consume', 50.00, 1, '购买服务：专业摄影服务', 'C202412190001', '余额支付', NULL, NULL, NULL, NULL, NULL, '2025-05-27 12:25:15', NULL, '2025-05-27 12:25:15');
INSERT INTO `transaction_records` VALUES (3, 1, 'withdraw', 30.00, 0, '提现到银行卡', 'W202412190001', '银行卡', NULL, NULL, NULL, NULL, NULL, '2025-05-27 12:25:15', NULL, '2025-05-27 12:25:15');
INSERT INTO `transaction_records` VALUES (4, 2, 'recharge', 200.00, 1, '支付宝充值', 'R202412190002', '支付宝', NULL, NULL, NULL, NULL, NULL, '2025-05-27 12:25:15', NULL, '2025-05-27 12:25:15');
INSERT INTO `transaction_records` VALUES (5, 2, 'consume', 80.00, 1, '购买服务：设计咨询', 'C202412190002', '余额支付', NULL, NULL, NULL, NULL, NULL, '2025-05-27 12:25:15', NULL, '2025-05-27 12:25:15');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `openid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '微信openid',
  `unionid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信unionid',
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint(1) NULL DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `balance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '账户余额',
  `total_recharge` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '累计充值金额',
  `total_consume` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '累计消费金额',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `is_expert` tinyint(1) NULL DEFAULT 0 COMMENT '是否为达人：0-否，1-是',
  `register_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `openid`(`openid`) USING BTREE,
  INDEX `idx_openid`(`openid`) USING BTREE,
  INDEX `idx_phone`(`phone`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_is_expert`(`is_expert`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'wx_openid_001', 'wx_unionid_001', '张三', '1.png', 1, '13800138001', '张三', 299.50, 500.00, 200.50, 1, 0, '2024-01-15 10:30:00', '2024-01-20 14:25:30', '2025-05-24 11:24:22', '2025-05-24 12:05:48', 0);
INSERT INTO `users` VALUES (2, 'wx_openid_002', 'wx_unionid_002', '李四', '2.png', 2, '13800138002', '李四', 150.00, 300.00, 150.00, 1, 0, '2024-01-14 15:20:00', '2024-01-19 09:15:45', '2025-05-24 11:24:22', '2025-05-24 12:33:59', 0);
INSERT INTO `users` VALUES (3, 'wx_openid_003', 'wx_unionid_003', '王五', '0.png', 0, '13800138003', '王五', 0.00, 0.00, 0.00, 0, 0, '2024-01-13 09:15:00', '2024-01-13 09:15:00', '2025-05-24 11:24:22', '2025-05-24 12:33:55', 0);
INSERT INTO `users` VALUES (4, 'wx_openid_004', 'wx_unionid_004', '赵六', '1.png', 1, '13800138004', '赵六', 1250.80, 2000.00, 749.20, 1, 1, '2024-01-10 08:45:00', '2024-01-20 16:30:20', '2025-05-24 11:24:22', '2025-05-24 12:06:02', 0);
INSERT INTO `users` VALUES (5, 'wx_openid_005', 'wx_unionid_005', '钱七', '2.png', 2, '13800138005', '钱七', 890.30, 1500.00, 609.70, 1, 1, '2024-01-08 14:20:00', '2024-01-20 11:45:15', '2025-05-24 11:24:22', '2025-05-24 12:33:50', 0);
INSERT INTO `users` VALUES (6, 'wx_openid_006', 'wx_unionid_006', '孙八', '1.png', 1, '13800138006', '孙八', 75.20, 100.00, 24.80, 1, 0, '2024-01-12 16:30:00', '2024-01-18 13:20:10', '2025-05-24 11:24:22', '2025-05-24 12:32:41', 0);
INSERT INTO `users` VALUES (7, 'wx_openid_007', 'wx_unionid_007', '周九', '2.png', 2, '13800138007', '周九', 320.00, 500.00, 180.00, 1, 0, '2024-01-11 11:15:00', '2024-01-19 15:40:25', '2025-05-24 11:24:22', '2025-05-24 12:33:45', 0);
INSERT INTO `users` VALUES (8, 'wx_openid_008', 'wx_unionid_008', '吴十', '0.png', 0, NULL, NULL, 0.00, 0.00, 0.00, 1, 0, '2024-01-16 20:45:00', '2024-01-16 20:45:00', '2025-05-24 11:24:22', '2025-05-24 12:33:38', 0);
INSERT INTO `users` VALUES (9, 'wx_openid_009', 'wx_unionid_009', '郑十一', '1.png', 1, '13800138009', '郑十一', 50.00, 50.00, 0.00, 0, 0, '2024-01-09 12:30:00', '2024-01-15 10:20:30', '2025-05-24 11:24:22', '2025-05-24 12:32:53', 0);
INSERT INTO `users` VALUES (10, 'wx_openid_010', 'wx_unionid_010', '王十二', '2.png', 2, '13800138010', '王十二', 200.00, 800.00, 600.00, 0, 1, '2024-01-07 09:00:00', '2024-01-14 17:30:45', '2025-05-24 11:24:22', '2025-05-24 12:33:33', 0);
INSERT INTO `users` VALUES (11, 'wx_openid_011', 'wx_unionid_011', '陈十三', '1.png', 1, '13800138011', '陈十三', 100.00, 100.00, 0.00, 1, 0, '2024-01-19 14:20:00', '2024-01-20 08:30:15', '2025-05-24 11:24:22', '2025-05-24 12:32:57', 0);
INSERT INTO `users` VALUES (12, 'wx_openid_012', 'wx_unionid_012', '林十四', '2.png', 2, '13800138012', '林十四', 0.00, 0.00, 0.00, 1, 0, '2024-01-20 10:15:00', '2024-01-20 10:15:00', '2025-05-24 11:24:22', '2025-05-24 12:33:25', 0);
INSERT INTO `users` VALUES (13, 'wx_openid_013', 'wx_unionid_013', '黄十五', '1.png', 1, '13800138013', '黄十五', 2500.00, 5000.00, 2500.00, 1, 0, '2024-01-05 16:45:00', '2024-01-20 12:10:20', '2025-05-24 11:24:22', '2025-05-24 12:33:04', 0);
INSERT INTO `users` VALUES (14, 'wx_openid_014', 'wx_unionid_014', '刘十六', '1.png', 1, '13800138014', '刘十六', 3200.50, 8000.00, 4799.50, 1, 1, '2024-01-03 13:30:00', '2024-01-20 18:45:30', '2025-05-24 11:24:22', '2025-05-24 12:33:06', 0);
INSERT INTO `users` VALUES (15, 'wx_openid_015', 'wx_unionid_015', '游客用户', '0.png', 0, NULL, NULL, 0.00, 0.00, 0.00, 1, 0, '2024-01-18 22:30:00', '2024-01-18 22:30:00', '2025-05-24 11:24:22', '2025-05-24 12:33:19', 0);
INSERT INTO `users` VALUES (1927329306829893634, 'test_openid_123456', NULL, '微信用户', 'https://thirdwx.qlogo.cn/mmopen/vi_32/POgEwh4mIHO4nibH0KlMECNjjGxQUq24ZEaGT4poC6icRiccVGKSyXwibcPq4BWmiaIGuG1icwxaQX6grC9VemZoJ8rg/132', 0, NULL, NULL, 0.00, 0.00, 0.00, 1, 0, '2025-05-27 19:41:29', '2025-05-27 19:42:33', '2025-05-27 19:41:29', '2025-05-27 19:41:29', 0);
INSERT INTO `users` VALUES (1927332176149008386, 'oPRKV67Nl3VqKDYLVljtpat5WDpc', NULL, '微信用户', 'https://thirdwx.qlogo.cn/mmopen/vi_32/POgEwh4mIHO4nibH0KlMECNjjGxQUq24ZEaGT4poC6icRiccVGKSyXwibcPq4BWmiaIGuG1icwxaQX6grC9VemZoJ8rg/132', 0, NULL, NULL, 0.00, 0.00, 0.00, 1, 0, '2025-05-27 19:52:54', '2025-05-27 19:52:54', '2025-05-27 19:52:54', '2025-05-27 19:52:54', 0);

-- ----------------------------
-- Table structure for withdraw_records
-- ----------------------------
DROP TABLE IF EXISTS `withdraw_records`;
CREATE TABLE `withdraw_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '提现订单号',
  `amount` decimal(8, 2) NOT NULL COMMENT '提现金额',
  `fee` decimal(8, 2) NULL DEFAULT 0.00 COMMENT '手续费',
  `real_amount` decimal(8, 2) NOT NULL COMMENT '实际到账金额',
  `bank_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '银行名称',
  `bank_account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '银行账号',
  `account_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '账户姓名',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态：0-待审核，1-审核通过，2-审核拒绝，3-已转账',
  `audit_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核备注',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `transfer_time` datetime NULL DEFAULT NULL COMMENT '转账时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_order_no`(`order_no`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_deleted`(`deleted`) USING BTREE,
  CONSTRAINT `withdraw_records_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '提现记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of withdraw_records
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
