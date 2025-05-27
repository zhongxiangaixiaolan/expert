package com.qing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.entity.RecommendService;
import com.qing.expert.vo.RecommendServiceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 推荐服务Mapper接口
 */
@Mapper
public interface RecommendServiceMapper extends BaseMapper<RecommendService> {

    /**
     * 分页查询推荐服务列表（带关联信息）
     */
    IPage<RecommendServiceVO> selectRecommendServicePage(Page<RecommendServiceVO> page,
                                                         @Param("serviceId") Long serviceId,
                                                         @Param("recommendType") String recommendType,
                                                         @Param("status") Integer status,
                                                         @Param("expertName") String expertName,
                                                         @Param("serviceName") String serviceName);

    /**
     * 根据ID查询推荐服务详情（带关联信息）
     */
    RecommendServiceVO selectRecommendServiceById(@Param("id") Long id);

    /**
     * 查询推荐服务列表（带关联信息）
     */
    List<RecommendServiceVO> selectRecommendServiceList(@Param("recommendType") String recommendType,
                                                        @Param("status") Integer status,
                                                        @Param("limit") Integer limit);

    /**
     * 检查服务是否已被推荐
     */
    int checkServiceRecommended(@Param("serviceId") Long serviceId,
                                @Param("recommendType") String recommendType,
                                @Param("excludeId") Long excludeId);

    /**
     * 获取最大排序权重
     */
    Integer getMaxSortOrder(@Param("recommendType") String recommendType);
}
