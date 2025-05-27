package com.qing.expert.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qing.expert.common.enums.RecommendTypeEnum;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.dto.RecommendServiceDTO;
import com.qing.expert.entity.RecommendService;
import com.qing.expert.entity.ServiceEntity;
import com.qing.expert.mapper.RecommendServiceMapper;
import com.qing.expert.service.RecommendServiceService;
import com.qing.expert.service.ServiceService;
import com.qing.expert.vo.RecommendServiceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 推荐服务Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendServiceServiceImpl extends ServiceImpl<RecommendServiceMapper, RecommendService>
        implements RecommendServiceService {

    private final ServiceService serviceService;

    @Override
    public IPage<RecommendServiceVO> getRecommendServicePage(Page<RecommendServiceVO> page,
            Long serviceId,
            String recommendType,
            Integer status,
            String expertName,
            String serviceName) {
        return baseMapper.selectRecommendServicePage(page, serviceId, recommendType, status, expertName, serviceName);
    }

    @Override
    public RecommendServiceVO getRecommendServiceById(Long id) {
        if (id == null) {
            throw new BusinessException("推荐服务ID不能为空");
        }
        return baseMapper.selectRecommendServiceById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRecommendService(RecommendServiceDTO dto) {
        // 验证服务是否存在
        ServiceEntity service = serviceService.getById(dto.getServiceId());
        if (service == null) {
            throw new BusinessException("服务不存在");
        }

        // 验证推荐类型
        RecommendTypeEnum typeEnum = RecommendTypeEnum.getByCode(dto.getRecommendType());
        if (typeEnum == null) {
            throw new BusinessException("推荐类型不正确");
        }

        // 检查服务是否已被推荐
        if (checkServiceRecommended(dto.getServiceId(), dto.getRecommendType(), null)) {
            throw new BusinessException("该服务已在此推荐类型中存在");
        }

        RecommendService recommendService = new RecommendService();
        BeanUtils.copyProperties(dto, recommendService);

        // 设置默认值
        if (recommendService.getStatus() == null) {
            recommendService.setStatus(1);
        }
        if (recommendService.getSortOrder() == null) {
            Integer maxSortOrder = baseMapper.getMaxSortOrder(dto.getRecommendType());
            recommendService.setSortOrder(maxSortOrder + 1);
        }

        return save(recommendService);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRecommendService(Long id, RecommendServiceDTO dto) {
        RecommendService existingRecommendService = getById(id);
        if (existingRecommendService == null) {
            throw new BusinessException("推荐服务不存在");
        }

        // 验证服务是否存在
        ServiceEntity service = serviceService.getById(dto.getServiceId());
        if (service == null) {
            throw new BusinessException("服务不存在");
        }

        // 验证推荐类型
        RecommendTypeEnum typeEnum = RecommendTypeEnum.getByCode(dto.getRecommendType());
        if (typeEnum == null) {
            throw new BusinessException("推荐类型不正确");
        }

        // 检查服务是否已被推荐（排除当前记录）
        if (checkServiceRecommended(dto.getServiceId(), dto.getRecommendType(), id)) {
            throw new BusinessException("该服务已在此推荐类型中存在");
        }

        RecommendService recommendService = new RecommendService();
        BeanUtils.copyProperties(dto, recommendService);
        recommendService.setId(id);

        return updateById(recommendService);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRecommendService(Long id) {
        if (id == null) {
            throw new BusinessException("推荐服务ID不能为空");
        }
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteRecommendService(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("推荐服务ID列表不能为空");
        }
        return removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRecommendServiceStatus(Long id, Integer status) {
        if (id == null) {
            throw new BusinessException("推荐服务ID不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值不正确");
        }

        LambdaUpdateWrapper<RecommendService> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(RecommendService::getId, id)
                .set(RecommendService::getStatus, status)
                .set(RecommendService::getUpdateTime, LocalDateTime.now());

        return update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateRecommendServiceStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("推荐服务ID列表不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值不正确");
        }

        LambdaUpdateWrapper<RecommendService> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(RecommendService::getId, ids)
                .set(RecommendService::getStatus, status)
                .set(RecommendService::getUpdateTime, LocalDateTime.now());

        return update(updateWrapper);
    }

    @Override
    public List<RecommendServiceVO> getRecommendServiceList(String recommendType, Integer limit) {
        return baseMapper.selectRecommendServiceList(recommendType, 1, limit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean adjustRecommendServiceSort(Long id, String direction) {
        if (id == null) {
            throw new BusinessException("推荐服务ID不能为空");
        }
        if (!"up".equals(direction) && !"down".equals(direction)) {
            throw new BusinessException("排序方向不正确");
        }

        RecommendService currentRecommendService = getById(id);
        if (currentRecommendService == null) {
            throw new BusinessException("推荐服务不存在");
        }

        // 查找相邻的推荐服务
        LambdaQueryWrapper<RecommendService> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RecommendService::getRecommendType, currentRecommendService.getRecommendType());

        if ("up".equals(direction)) {
            // 向上移动，找到排序权重大于当前的最小值
            queryWrapper.gt(RecommendService::getSortOrder, currentRecommendService.getSortOrder())
                    .orderByAsc(RecommendService::getSortOrder)
                    .last("LIMIT 1");
        } else {
            // 向下移动，找到排序权重小于当前的最大值
            queryWrapper.lt(RecommendService::getSortOrder, currentRecommendService.getSortOrder())
                    .orderByDesc(RecommendService::getSortOrder)
                    .last("LIMIT 1");
        }

        RecommendService targetRecommendService = getOne(queryWrapper);
        if (targetRecommendService == null) {
            throw new BusinessException("已经是" + ("up".equals(direction) ? "最前" : "最后") + "位置");
        }

        // 交换排序权重
        Integer currentSortOrder = currentRecommendService.getSortOrder();
        Integer targetSortOrder = targetRecommendService.getSortOrder();

        currentRecommendService.setSortOrder(targetSortOrder);
        targetRecommendService.setSortOrder(currentSortOrder);

        return updateById(currentRecommendService) && updateById(targetRecommendService);
    }

    @Override
    public boolean checkServiceRecommended(Long serviceId, String recommendType, Long excludeId) {
        int count = baseMapper.checkServiceRecommended(serviceId, recommendType, excludeId);
        return count > 0;
    }
}
