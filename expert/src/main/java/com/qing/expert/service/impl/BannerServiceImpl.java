package com.qing.expert.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qing.expert.common.enums.LinkTypeEnum;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.common.result.PageResult;
import com.qing.expert.dto.banner.BannerQueryDTO;
import com.qing.expert.dto.banner.BannerSaveDTO;
import com.qing.expert.entity.Banner;
import com.qing.expert.mapper.BannerMapper;
import com.qing.expert.service.BannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 轮播图服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Override
    public PageResult<Banner> getBannerPage(BannerQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        
        // 标题模糊查询
        if (StrUtil.isNotBlank(queryDTO.getTitle())) {
            wrapper.like(Banner::getTitle, queryDTO.getTitle());
        }
        
        // 链接类型查询
        if (StrUtil.isNotBlank(queryDTO.getLinkType())) {
            wrapper.eq(Banner::getLinkType, queryDTO.getLinkType());
        }
        
        // 状态查询
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Banner::getStatus, queryDTO.getStatus());
        }
        
        // 开始时间范围查询
        if (queryDTO.getStartTimeBegin() != null) {
            wrapper.ge(Banner::getStartTime, queryDTO.getStartTimeBegin());
        }
        if (queryDTO.getStartTimeEnd() != null) {
            wrapper.le(Banner::getStartTime, queryDTO.getStartTimeEnd());
        }
        
        // 结束时间范围查询
        if (queryDTO.getEndTimeBegin() != null) {
            wrapper.ge(Banner::getEndTime, queryDTO.getEndTimeBegin());
        }
        if (queryDTO.getEndTimeEnd() != null) {
            wrapper.le(Banner::getEndTime, queryDTO.getEndTimeEnd());
        }
        
        // 按排序权重和创建时间排序
        wrapper.orderByAsc(Banner::getSortOrder)
               .orderByDesc(Banner::getCreateTime);

        // 分页查询
        Page<Banner> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<Banner> result = page(page, wrapper);

        return PageResult.of(result);
    }

    @Override
    public Banner getBannerDetail(Long bannerId) {
        if (bannerId == null) {
            throw new BusinessException("轮播图ID不能为空");
        }

        Banner banner = getById(bannerId);
        if (banner == null) {
            throw new BusinessException("轮播图不存在");
        }

        return banner;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBanner(BannerSaveDTO saveDTO) {
        // 验证链接类型
        if (!LinkTypeEnum.isValidCode(saveDTO.getLinkType())) {
            throw new BusinessException("无效的链接类型");
        }

        // 验证时间范围
        if (saveDTO.getStartTime() != null && saveDTO.getEndTime() != null) {
            if (saveDTO.getStartTime().isAfter(saveDTO.getEndTime())) {
                throw new BusinessException("开始时间不能晚于结束时间");
            }
        }

        // 检查排序权重是否重复
        if (checkSortOrderExists(saveDTO.getSortOrder(), saveDTO.getId())) {
            throw new BusinessException("排序权重已存在，请选择其他权重值");
        }

        Banner banner = new Banner();
        BeanUtils.copyProperties(saveDTO, banner);

        if (saveDTO.getId() != null) {
            // 更新
            banner.setUpdateTime(LocalDateTime.now());
            return updateById(banner);
        } else {
            // 新增
            banner.setCreateTime(LocalDateTime.now());
            banner.setUpdateTime(LocalDateTime.now());
            return save(banner);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBanner(Long bannerId) {
        if (bannerId == null) {
            throw new BusinessException("轮播图ID不能为空");
        }

        Banner banner = getById(bannerId);
        if (banner == null) {
            throw new BusinessException("轮播图不存在");
        }

        return removeById(bannerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBannerStatus(Long bannerId, Integer status) {
        if (bannerId == null) {
            throw new BusinessException("轮播图ID不能为空");
        }

        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值无效");
        }

        Banner banner = getById(bannerId);
        if (banner == null) {
            throw new BusinessException("轮播图不存在");
        }

        banner.setStatus(status);
        banner.setUpdateTime(LocalDateTime.now());
        return updateById(banner);
    }

    @Override
    public List<Banner> getEnabledBanners() {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getStatus, 1)
               .and(w -> w.isNull(Banner::getStartTime)
                        .or(qw -> qw.le(Banner::getStartTime, LocalDateTime.now())))
               .and(w -> w.isNull(Banner::getEndTime)
                        .or(qw -> qw.ge(Banner::getEndTime, LocalDateTime.now())))
               .orderByAsc(Banner::getSortOrder)
               .orderByDesc(Banner::getCreateTime);

        return list(wrapper);
    }

    @Override
    public boolean checkSortOrderExists(Integer sortOrder, Long excludeId) {
        if (sortOrder == null) {
            return false;
        }

        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getSortOrder, sortOrder);
        if (excludeId != null) {
            wrapper.ne(Banner::getId, excludeId);
        }

        return count(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSortOrders(List<Long> bannerIds, List<Integer> sortOrders) {
        if (bannerIds == null || sortOrders == null || bannerIds.size() != sortOrders.size()) {
            throw new BusinessException("参数错误");
        }

        for (int i = 0; i < bannerIds.size(); i++) {
            Banner banner = new Banner();
            banner.setId(bannerIds.get(i));
            banner.setSortOrder(sortOrders.get(i));
            banner.setUpdateTime(LocalDateTime.now());
            updateById(banner);
        }

        return true;
    }
}
