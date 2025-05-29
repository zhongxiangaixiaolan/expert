package com.qing.expert.mapper;

import com.qing.expert.entity.ExpertPhoto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * ExpertPhotoMapper测试类
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
public class ExpertPhotoMapperTest {

    @Autowired
    private ExpertPhotoMapper expertPhotoMapper;

    @Test
    public void testSelectByExpertId() {
        try {
            log.info("开始测试 ExpertPhotoMapper.selectByExpertId 方法");
            
            // 测试查询达人ID为1的照片
            Long expertId = 1L;
            List<ExpertPhoto> photos = expertPhotoMapper.selectByExpertId(expertId);
            
            log.info("查询达人ID={} 的照片，结果数量: {}", expertId, photos.size());
            
            if (!photos.isEmpty()) {
                photos.forEach(photo -> {
                    log.info("照片信息: ID={}, 文件名={}, 标题={}", 
                        photo.getId(), photo.getPhotoName(), photo.getPhotoTitle());
                });
            }
            
            log.info("✅ ExpertPhotoMapper.selectByExpertId 测试成功");
            
        } catch (Exception e) {
            log.error("❌ ExpertPhotoMapper.selectByExpertId 测试失败", e);
            throw e;
        }
    }

    @Test
    public void testGetMaxSortOrder() {
        try {
            log.info("开始测试 ExpertPhotoMapper.getMaxSortOrder 方法");
            
            Long expertId = 1L;
            Integer maxSortOrder = expertPhotoMapper.getMaxSortOrder(expertId);
            
            log.info("达人ID={} 的最大排序号: {}", expertId, maxSortOrder);
            log.info("✅ ExpertPhotoMapper.getMaxSortOrder 测试成功");
            
        } catch (Exception e) {
            log.error("❌ ExpertPhotoMapper.getMaxSortOrder 测试失败", e);
            throw e;
        }
    }
}
