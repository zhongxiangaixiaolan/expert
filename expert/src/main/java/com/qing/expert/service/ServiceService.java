package com.qing.expert.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.dto.ServiceDTO;
import com.qing.expert.entity.ServiceEntity;
import com.qing.expert.vo.ServiceVO;

import java.util.List;

/**
 * 服务Service接口
 */
public interface ServiceService extends IService<ServiceEntity> {

    /**
     * 分页查询服务列表
     */
    IPage<ServiceVO> getServicePage(Page<ServiceVO> page,
                                    Long expertId,
                                    Long categoryId,
                                    String name,
                                    Integer status,
                                    Integer isHot,
                                    Integer isRecommend);

    /**
     * 根据ID查询服务详情
     */
    ServiceVO getServiceById(Long id);

    /**
     * 创建服务
     */
    boolean createService(ServiceDTO dto);

    /**
     * 更新服务
     */
    boolean updateService(Long id, ServiceDTO dto);

    /**
     * 删除服务
     */
    boolean deleteService(Long id);

    /**
     * 批量删除服务
     */
    boolean batchDeleteService(List<Long> ids);

    /**
     * 更新服务状态
     */
    boolean updateServiceStatus(Long id, Integer status);

    /**
     * 批量更新服务状态
     */
    boolean batchUpdateServiceStatus(List<Long> ids, Integer status);

    /**
     * 更新服务热门状态
     */
    boolean updateServiceHotStatus(Long id, Integer isHot);

    /**
     * 更新服务推荐状态
     */
    boolean updateServiceRecommendStatus(Long id, Integer isRecommend);

    /**
     * 获取达人的服务列表
     */
    List<ServiceVO> getServicesByExpertId(Long expertId, Integer status);

    /**
     * 获取分类下的服务列表
     */
    List<ServiceVO> getServicesByCategoryId(Long categoryId, Integer status, Integer limit);

    /**
     * 获取热门服务列表
     */
    List<ServiceVO> getHotServices(Integer limit);

    /**
     * 获取推荐服务列表
     */
    List<ServiceVO> getRecommendServices(Integer limit);

    /**
     * 检查服务名称是否存在
     */
    boolean checkNameExists(String name, Long expertId, Long excludeId);

    /**
     * 调整服务排序
     */
    boolean adjustServiceSort(Long id, String direction);
}
