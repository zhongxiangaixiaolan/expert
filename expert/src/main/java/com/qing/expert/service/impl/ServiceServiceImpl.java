package com.qing.expert.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.dto.ServiceDTO;
import com.qing.expert.entity.ServiceEntity;
import com.qing.expert.mapper.ServiceMapper;
import com.qing.expert.service.ServiceService;
import com.qing.expert.vo.ServiceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 服务Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceServiceImpl extends ServiceImpl<ServiceMapper, ServiceEntity> implements ServiceService {

    @Override
    public IPage<ServiceVO> getServicePage(Page<ServiceVO> page,
                                           Long expertId,
                                           Long categoryId,
                                           String name,
                                           Integer status,
                                           Integer isHot,
                                           Integer isRecommend) {
        return baseMapper.selectServicePage(page, expertId, categoryId, name, status, isHot, isRecommend);
    }

    @Override
    public ServiceVO getServiceById(Long id) {
        if (id == null) {
            throw new BusinessException("服务ID不能为空");
        }
        return baseMapper.selectServiceById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createService(ServiceDTO dto) {
        // 检查服务名称是否已存在
        if (checkNameExists(dto.getName(), dto.getExpertId(), null)) {
            throw new BusinessException("该达人已存在同名服务");
        }

        ServiceEntity serviceEntity = new ServiceEntity();
        BeanUtils.copyProperties(dto, serviceEntity);

        // 设置默认值
        if (serviceEntity.getStatus() == null) {
            serviceEntity.setStatus(1);
        }
        if (serviceEntity.getIsHot() == null) {
            serviceEntity.setIsHot(0);
        }
        if (serviceEntity.getIsRecommend() == null) {
            serviceEntity.setIsRecommend(0);
        }
        if (serviceEntity.getSortOrder() == null) {
            Integer maxSortOrder = baseMapper.getMaxSortOrder(dto.getExpertId());
            serviceEntity.setSortOrder(maxSortOrder != null ? maxSortOrder + 1 : 1);
        }

        return save(serviceEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateService(Long id, ServiceDTO dto) {
        ServiceEntity existingService = getById(id);
        if (existingService == null) {
            throw new BusinessException("服务不存在");
        }

        // 检查服务名称是否已存在（排除当前记录）
        if (checkNameExists(dto.getName(), dto.getExpertId(), id)) {
            throw new BusinessException("该达人已存在同名服务");
        }

        ServiceEntity serviceEntity = new ServiceEntity();
        BeanUtils.copyProperties(dto, serviceEntity);
        serviceEntity.setId(id);

        return updateById(serviceEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteService(Long id) {
        if (id == null) {
            throw new BusinessException("服务ID不能为空");
        }
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteService(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("服务ID列表不能为空");
        }
        return removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateServiceStatus(Long id, Integer status) {
        if (id == null) {
            throw new BusinessException("服务ID不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值不正确");
        }

        LambdaUpdateWrapper<ServiceEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ServiceEntity::getId, id)
                .set(ServiceEntity::getStatus, status)
                .set(ServiceEntity::getUpdateTime, LocalDateTime.now());

        return update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateServiceStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("服务ID列表不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值不正确");
        }

        LambdaUpdateWrapper<ServiceEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ServiceEntity::getId, ids)
                .set(ServiceEntity::getStatus, status)
                .set(ServiceEntity::getUpdateTime, LocalDateTime.now());

        return update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateServiceHotStatus(Long id, Integer isHot) {
        if (id == null) {
            throw new BusinessException("服务ID不能为空");
        }
        if (isHot == null || (isHot != 0 && isHot != 1)) {
            throw new BusinessException("热门状态值不正确");
        }

        LambdaUpdateWrapper<ServiceEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ServiceEntity::getId, id)
                .set(ServiceEntity::getIsHot, isHot)
                .set(ServiceEntity::getUpdateTime, LocalDateTime.now());

        return update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateServiceRecommendStatus(Long id, Integer isRecommend) {
        if (id == null) {
            throw new BusinessException("服务ID不能为空");
        }
        if (isRecommend == null || (isRecommend != 0 && isRecommend != 1)) {
            throw new BusinessException("推荐状态值不正确");
        }

        LambdaUpdateWrapper<ServiceEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ServiceEntity::getId, id)
                .set(ServiceEntity::getIsRecommend, isRecommend)
                .set(ServiceEntity::getUpdateTime, LocalDateTime.now());

        return update(updateWrapper);
    }

    @Override
    public List<ServiceVO> getServicesByExpertId(Long expertId, Integer status) {
        return baseMapper.selectServicesByExpertId(expertId, status);
    }

    @Override
    public List<ServiceVO> getServicesByCategoryId(Long categoryId, Integer status, Integer limit) {
        return baseMapper.selectServicesByCategoryId(categoryId, status, limit);
    }

    @Override
    public List<ServiceVO> getHotServices(Integer limit) {
        return baseMapper.selectHotServices(limit);
    }

    @Override
    public List<ServiceVO> getRecommendServices(Integer limit) {
        return baseMapper.selectRecommendServices(limit);
    }

    @Override
    public boolean checkNameExists(String name, Long expertId, Long excludeId) {
        int count = baseMapper.checkNameExists(name, expertId, excludeId);
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean adjustServiceSort(Long id, String direction) {
        if (id == null) {
            throw new BusinessException("服务ID不能为空");
        }
        if (!"up".equals(direction) && !"down".equals(direction)) {
            throw new BusinessException("排序方向不正确");
        }

        ServiceEntity currentService = getById(id);
        if (currentService == null) {
            throw new BusinessException("服务不存在");
        }

        // 查找相邻的服务
        LambdaQueryWrapper<ServiceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceEntity::getExpertId, currentService.getExpertId());

        if ("up".equals(direction)) {
            // 向上移动，找到排序权重大于当前的最小值
            queryWrapper.gt(ServiceEntity::getSortOrder, currentService.getSortOrder())
                    .orderByAsc(ServiceEntity::getSortOrder)
                    .last("LIMIT 1");
        } else {
            // 向下移动，找到排序权重小于当前的最大值
            queryWrapper.lt(ServiceEntity::getSortOrder, currentService.getSortOrder())
                    .orderByDesc(ServiceEntity::getSortOrder)
                    .last("LIMIT 1");
        }

        ServiceEntity targetService = getOne(queryWrapper);
        if (targetService == null) {
            throw new BusinessException("已经是" + ("up".equals(direction) ? "最前" : "最后") + "位置");
        }

        // 交换排序权重
        Integer currentSortOrder = currentService.getSortOrder();
        Integer targetSortOrder = targetService.getSortOrder();

        currentService.setSortOrder(targetSortOrder);
        targetService.setSortOrder(currentSortOrder);

        return updateById(currentService) && updateById(targetService);
    }
}
