-- 插入达人照片测试数据
-- 确保达人照片表有数据供3D轮播组件展示

-- 为达人ID=5（营销策划师小张）添加照片
INSERT INTO expert_photos (expert_id, photo_name, photo_title, photo_description, sort_order, file_size, width, height, create_time, update_time) VALUES
(5, '40ed664f-db14-4568-adf5-2ac4f6370e30.png', '营销策划案例1', '品牌营销策划方案展示', 1, 1024000, 800, 600, NOW(), NOW()),
(5, '84c410d2-8aa4-478c-a33c-c41b56efe635.png', '营销策划案例2', '数字营销活动策划', 2, 1024000, 800, 600, NOW(), NOW()),
(5, 'd7ff8996-c1c6-4278-98bf-d5da7b1d3af6.png', '营销策划案例3', '社交媒体营销策略', 3, 1024000, 800, 600, NOW(), NOW());

-- 为达人ID=6（婚礼摄影师小李）添加照片
INSERT INTO expert_photos (expert_id, photo_name, photo_title, photo_description, sort_order, file_size, width, height, create_time, update_time) VALUES
(6, '40ed664f-db14-4568-adf5-2ac4f6370e30.png', '婚礼摄影作品1', '浪漫婚礼现场拍摄', 1, 1024000, 800, 600, NOW(), NOW()),
(6, '84c410d2-8aa4-478c-a33c-c41b56efe635.png', '婚礼摄影作品2', '户外婚礼摄影', 2, 1024000, 800, 600, NOW(), NOW());

-- 为达人ID=1（专业摄影师小赵）添加照片
INSERT INTO expert_photos (expert_id, photo_name, photo_title, photo_description, sort_order, file_size, width, height, create_time, update_time) VALUES
(1, 'd7ff8996-c1c6-4278-98bf-d5da7b1d3af6.png', '摄影作品1', '专业人像摄影', 1, 1024000, 800, 600, NOW(), NOW()),
(1, '40ed664f-db14-4568-adf5-2ac4f6370e30.png', '摄影作品2', '商业产品摄影', 2, 1024000, 800, 600, NOW(), NOW());

-- 为达人ID=2（设计达人小钱）添加照片
INSERT INTO expert_photos (expert_id, photo_name, photo_title, photo_description, sort_order, file_size, width, height, create_time, update_time) VALUES
(2, '84c410d2-8aa4-478c-a33c-c41b56efe635.png', '设计作品1', 'UI/UX设计案例', 1, 1024000, 800, 600, NOW(), NOW()),
(2, 'd7ff8996-c1c6-4278-98bf-d5da7b1d3af6.png', '设计作品2', '品牌视觉设计', 2, 1024000, 800, 600, NOW(), NOW());

-- 为达人ID=3（编程高手小王）添加照片
INSERT INTO expert_photos (expert_id, photo_name, photo_title, photo_description, sort_order, file_size, width, height, create_time, update_time) VALUES
(3, '40ed664f-db14-4568-adf5-2ac4f6370e30.png', '编程项目1', 'Web应用开发项目', 1, 1024000, 800, 600, NOW(), NOW()),
(3, '84c410d2-8aa4-478c-a33c-c41b56efe635.png', '编程项目2', '移动应用开发', 2, 1024000, 800, 600, NOW(), NOW());

-- 为达人ID=4（翻译专家小刘）添加照片
INSERT INTO expert_photos (expert_id, photo_name, photo_title, photo_description, sort_order, file_size, width, height, create_time, update_time) VALUES
(4, 'd7ff8996-c1c6-4278-98bf-d5da7b1d3af6.png', '翻译作品1', '商务文档翻译', 1, 1024000, 800, 600, NOW(), NOW()),
(4, '40ed664f-db14-4568-adf5-2ac4f6370e30.png', '翻译作品2', '技术文档翻译', 2, 1024000, 800, 600, NOW(), NOW());

-- 检查插入结果
SELECT 
    ep.id,
    ep.expert_id,
    e.expert_name,
    ep.photo_name,
    ep.photo_title,
    ep.sort_order,
    ep.create_time
FROM expert_photos ep
LEFT JOIN experts e ON ep.expert_id = e.id
ORDER BY ep.expert_id, ep.sort_order;
