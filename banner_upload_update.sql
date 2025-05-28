-- 轮播图上传功能数据库更新SQL
-- 执行日期：请根据实际情况填写
-- 说明：修改banners表的image_url字段注释，说明现在支持存储文件名或完整URL

-- 修改banners表的image_url字段注释
ALTER TABLE `banners` MODIFY COLUMN `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL或文件名（上传的文件只存储文件名）';

-- 可选：如果需要添加文件大小和尺寸信息字段（类似expert_photos表）
-- ALTER TABLE `banners` ADD COLUMN `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小（字节）' AFTER `image_url`;
-- ALTER TABLE `banners` ADD COLUMN `width` int NULL DEFAULT NULL COMMENT '图片宽度' AFTER `file_size`;
-- ALTER TABLE `banners` ADD COLUMN `height` int NULL DEFAULT NULL COMMENT '图片高度' AFTER `width`;

-- 更新现有数据说明：
-- 如果现有的banners表中已有数据且image_url存储的是完整URL，
-- 这些数据仍然可以正常显示，因为前端代码会自动判断是完整URL还是文件名
-- 新上传的轮播图将只存储文件名

-- 验证更新结果
SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, COLUMN_COMMENT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'banners' 
  AND COLUMN_NAME = 'image_url';
