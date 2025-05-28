package com.qing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qing.expert.entity.ExpertPhoto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 达人照片Mapper接口
 */
@Mapper
public interface ExpertPhotoMapper extends BaseMapper<ExpertPhoto> {

    /**
     * 根据达人ID查询照片列表
     * @param expertId 达人ID
     * @return 照片列表
     */
    List<ExpertPhoto> selectByExpertId(@Param("expertId") Long expertId);

    /**
     * 根据达人ID删除所有照片
     * @param expertId 达人ID
     * @return 删除数量
     */
    int deleteByExpertId(@Param("expertId") Long expertId);

    /**
     * 更新照片排序
     * @param photoId 照片ID
     * @param sortOrder 排序顺序
     * @return 更新数量
     */
    int updateSortOrder(@Param("photoId") Long photoId, @Param("sortOrder") Integer sortOrder);

    /**
     * 获取达人照片的最大排序号
     * @param expertId 达人ID
     * @return 最大排序号
     */
    Integer getMaxSortOrder(@Param("expertId") Long expertId);
}
