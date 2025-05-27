package com.qing.expert.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.dto.RecommendServiceDTO;
import com.qing.expert.entity.RecommendService;
import com.qing.expert.vo.RecommendServiceVO;

import java.util.List;

/**
 * 推荐服务Service接口
 */
public interface RecommendServiceService extends IService<RecommendService> {

    /**
     * 分页查询推荐服务列表
     */
    IPage<RecommendServiceVO> getRecommendServicePage(Page<RecommendServiceVO> page,
                                                      Long serviceId,
                                                      String recommendType,
                                                      Integer status,
                                                      String expertName,
                                                      String serviceName);

    /**
     * 根据ID查询推荐服务详情
     */
    RecommendServiceVO getRecommendServiceById(Long id);

    /**
     * 创建推荐服务
     */
    boolean createRecommendService(RecommendServiceDTO dto);

    /**
     * 更新推荐服务
     */
    boolean updateRecommendService(Long id, RecommendServiceDTO dto);

    /**
     * 删除推荐服务
     */
    boolean deleteRecommendService(Long id);

    /**
     * 批量删除推荐服务
     */
    boolean batchDeleteRecommendService(List<Long> ids);

    /**
     * 更新推荐服务状态
     */
    boolean updateRecommendServiceStatus(Long id, Integer status);

    /**
     * 批量更新推荐服务状态
     */
    boolean batchUpdateRecommendServiceStatus(List<Long> ids, Integer status);

    /**
     * 查询推荐服务列表（前端展示用）
     */
    List<RecommendServiceVO> getRecommendServiceList(String recommendType, Integer limit);

    /**
     * 调整推荐服务排序
     */
    boolean adjustRecommendServiceSort(Long id, String direction);

    /**
     * 检查服务是否已被推荐
     */
    boolean checkServiceRecommended(Long serviceId, String recommendType, Long excludeId);
}
