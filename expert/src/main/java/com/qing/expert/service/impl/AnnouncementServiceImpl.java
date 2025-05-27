package com.qing.expert.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qing.expert.common.enums.AnnouncementTypeEnum;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.common.result.PageResult;
import com.qing.expert.dto.announcement.AnnouncementQueryDTO;
import com.qing.expert.dto.announcement.AnnouncementSaveDTO;
import com.qing.expert.entity.Announcement;
import com.qing.expert.mapper.AnnouncementMapper;
import com.qing.expert.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通告服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement>
        implements AnnouncementService {

    @Override
    public PageResult<Announcement> getAnnouncementPage(AnnouncementQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();

        // 标题模糊查询
        if (StrUtil.isNotBlank(queryDTO.getTitle())) {
            wrapper.like(Announcement::getTitle, queryDTO.getTitle());
        }

        // 通告类型查询
        if (StrUtil.isNotBlank(queryDTO.getType())) {
            wrapper.eq(Announcement::getType, queryDTO.getType());
        }

        // 是否滚动显示查询
        if (queryDTO.getIsScroll() != null) {
            wrapper.eq(Announcement::getIsScroll, queryDTO.getIsScroll());
        }

        // 状态查询
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Announcement::getStatus, queryDTO.getStatus());
        }

        // 开始时间范围查询
        if (queryDTO.getStartTimeBegin() != null) {
            wrapper.ge(Announcement::getStartTime, queryDTO.getStartTimeBegin());
        }
        if (queryDTO.getStartTimeEnd() != null) {
            wrapper.le(Announcement::getStartTime, queryDTO.getStartTimeEnd());
        }

        // 结束时间范围查询
        if (queryDTO.getEndTimeBegin() != null) {
            wrapper.ge(Announcement::getEndTime, queryDTO.getEndTimeBegin());
        }
        if (queryDTO.getEndTimeEnd() != null) {
            wrapper.le(Announcement::getEndTime, queryDTO.getEndTimeEnd());
        }

        // 按排序权重和创建时间排序
        wrapper.orderByAsc(Announcement::getSortOrder)
                .orderByDesc(Announcement::getCreateTime);

        // 分页查询
        Page<Announcement> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<Announcement> result = page(page, wrapper);

        return PageResult.of(result);
    }

    @Override
    public Announcement getAnnouncementDetail(Long announcementId) {
        if (announcementId == null) {
            throw new BusinessException("通告ID不能为空");
        }

        Announcement announcement = getById(announcementId);
        if (announcement == null) {
            throw new BusinessException("通告不存在");
        }

        return announcement;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveAnnouncement(AnnouncementSaveDTO saveDTO) {
        // 验证通告类型
        if (!AnnouncementTypeEnum.isValidCode(saveDTO.getType())) {
            throw new BusinessException("无效的通告类型");
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

        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(saveDTO, announcement);

        if (saveDTO.getId() != null) {
            // 更新
            announcement.setUpdateTime(LocalDateTime.now());
            return updateById(announcement);
        } else {
            // 新增
            announcement.setCreateTime(LocalDateTime.now());
            announcement.setUpdateTime(LocalDateTime.now());
            return save(announcement);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAnnouncement(Long announcementId) {
        if (announcementId == null) {
            throw new BusinessException("通告ID不能为空");
        }

        Announcement announcement = getById(announcementId);
        if (announcement == null) {
            throw new BusinessException("通告不存在");
        }

        return removeById(announcementId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAnnouncementStatus(Long announcementId, Integer status) {
        if (announcementId == null) {
            throw new BusinessException("通告ID不能为空");
        }

        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值无效");
        }

        Announcement announcement = getById(announcementId);
        if (announcement == null) {
            throw new BusinessException("通告不存在");
        }

        announcement.setStatus(status);
        announcement.setUpdateTime(LocalDateTime.now());
        return updateById(announcement);
    }

    @Override
    public List<Announcement> getEnabledAnnouncements() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, 1)
                .and(w -> w.isNull(Announcement::getStartTime)
                        .or(qw -> qw.le(Announcement::getStartTime, LocalDateTime.now())))
                .and(w -> w.isNull(Announcement::getEndTime)
                        .or(qw -> qw.ge(Announcement::getEndTime, LocalDateTime.now())))
                .orderByAsc(Announcement::getSortOrder)
                .orderByDesc(Announcement::getCreateTime);

        return list(wrapper);
    }

    @Override
    public List<Announcement> getScrollAnnouncements() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, 1)
                .eq(Announcement::getIsScroll, 1)
                .and(w -> w.isNull(Announcement::getStartTime)
                        .or(qw -> qw.le(Announcement::getStartTime, LocalDateTime.now())))
                .and(w -> w.isNull(Announcement::getEndTime)
                        .or(qw -> qw.ge(Announcement::getEndTime, LocalDateTime.now())))
                .orderByAsc(Announcement::getSortOrder)
                .orderByDesc(Announcement::getCreateTime);

        return list(wrapper);
    }

    @Override
    public boolean checkSortOrderExists(Integer sortOrder, Long excludeId) {
        if (sortOrder == null) {
            return false;
        }

        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getSortOrder, sortOrder);
        if (excludeId != null) {
            wrapper.ne(Announcement::getId, excludeId);
        }

        return count(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSortOrders(List<Long> announcementIds, List<Integer> sortOrders) {
        if (announcementIds == null || sortOrders == null || announcementIds.size() != sortOrders.size()) {
            throw new BusinessException("参数错误");
        }

        for (int i = 0; i < announcementIds.size(); i++) {
            Announcement announcement = new Announcement();
            announcement.setId(announcementIds.get(i));
            announcement.setSortOrder(sortOrders.get(i));
            announcement.setUpdateTime(LocalDateTime.now());
            updateById(announcement);
        }

        return true;
    }
}
