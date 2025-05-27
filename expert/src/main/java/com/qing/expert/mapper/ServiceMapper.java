package com.qing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.entity.ServiceEntity;
import com.qing.expert.vo.ServiceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 服务Mapper接口
 */
@Mapper
public interface ServiceMapper extends BaseMapper<ServiceEntity> {

    /**
     * 分页查询服务列表
     */
    IPage<ServiceVO> selectServicePage(Page<ServiceVO> page,
                                       @Param("expertId") Long expertId,
                                       @Param("categoryId") Long categoryId,
                                       @Param("name") String name,
                                       @Param("status") Integer status,
                                       @Param("isHot") Integer isHot,
                                       @Param("isRecommend") Integer isRecommend);

    /**
     * 根据ID查询服务详情
     */
    ServiceVO selectServiceById(@Param("id") Long id);

    /**
     * 获取达人的服务列表
     */
    List<ServiceVO> selectServicesByExpertId(@Param("expertId") Long expertId,
                                             @Param("status") Integer status);

    /**
     * 获取分类下的服务列表
     */
    List<ServiceVO> selectServicesByCategoryId(@Param("categoryId") Long categoryId,
                                               @Param("status") Integer status,
                                               @Param("limit") Integer limit);

    /**
     * 获取热门服务列表
     */
    List<ServiceVO> selectHotServices(@Param("limit") Integer limit);

    /**
     * 获取推荐服务列表
     */
    List<ServiceVO> selectRecommendServices(@Param("limit") Integer limit);

    /**
     * 检查服务名称是否存在
     */
    int checkNameExists(@Param("name") String name,
                        @Param("expertId") Long expertId,
                        @Param("excludeId") Long excludeId);

    /**
     * 获取最大排序权重
     */
    Integer getMaxSortOrder(@Param("expertId") Long expertId);
}
