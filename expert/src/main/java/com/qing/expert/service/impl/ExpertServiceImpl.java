package com.qing.expert.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qing.expert.common.enums.ExpertAuditStatusEnum;
import com.qing.expert.common.enums.ExpertStatusEnum;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.common.result.PageResult;
import com.qing.expert.dto.ExpertAuditDTO;
import com.qing.expert.dto.ExpertQueryDTO;
import com.qing.expert.dto.ExpertSaveDTO;
import com.qing.expert.entity.Expert;
import com.qing.expert.entity.ExpertPhoto;
import com.qing.expert.entity.User;
import com.qing.expert.mapper.ExpertMapper;
import com.qing.expert.service.ExpertPhotoService;
import com.qing.expert.service.ExpertService;
import com.qing.expert.service.UserService;
import com.qing.expert.vo.ExpertDetailVO;
import com.qing.expert.vo.ExpertStatisticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 达人服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpertServiceImpl extends ServiceImpl<ExpertMapper, Expert> implements ExpertService {

    private final ExpertMapper expertMapper;
    private final UserService userService;
    private final ExpertPhotoService expertPhotoService;

    @Override
    public PageResult<ExpertDetailVO> getExpertPage(ExpertQueryDTO queryDTO) {
        // 构建分页对象
        Page<ExpertDetailVO> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());

        // 处理时间参数
        LocalDateTime createStartTime = null;
        LocalDateTime createEndTime = null;
        if (StringUtils.hasText(queryDTO.getCreateStartTime())) {
            createStartTime = LocalDateTime.parse(queryDTO.getCreateStartTime() + " 00:00:00",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (StringUtils.hasText(queryDTO.getCreateEndTime())) {
            createEndTime = LocalDateTime.parse(queryDTO.getCreateEndTime() + " 23:59:59",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        // 执行分页查询
        IPage<ExpertDetailVO> pageResult = expertMapper.selectExpertPage(
                page,
                queryDTO.getKeyword(),
                queryDTO.getCategoryId(),
                queryDTO.getStatus(),
                queryDTO.getAuditStatus(),
                queryDTO.getMinRating(),
                queryDTO.getMaxRating(),
                queryDTO.getMinPrice(),
                queryDTO.getMaxPrice(),
                createStartTime,
                createEndTime,
                queryDTO.getSortField(),
                queryDTO.getSortOrder());

        // 处理状态描述
        pageResult.getRecords().forEach(this::fillStatusDesc);

        return PageResult.of(pageResult);
    }

    @Override
    public ExpertStatisticsVO getExpertStatistics() {
        ExpertStatisticsVO statistics = expertMapper.getExpertStatistics();
        if (statistics == null) {
            statistics = new ExpertStatisticsVO();
        }

        // 补充统计信息
        statistics.setOnlineCount(expertMapper.getOnlineExpertCount());
        statistics.setBusyCount(expertMapper.getBusyExpertCount());
        statistics.setOfflineCount(expertMapper.getOfflineExpertCount());
        statistics.setPendingCount(expertMapper.getPendingExpertCount());
        statistics.setApprovedCount(expertMapper.getApprovedExpertCount());
        statistics.setRejectedCount(expertMapper.getRejectedExpertCount());

        // 计算时间范围
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.minusDays(7);
        LocalDateTime monthStart = now.minusDays(30);

        statistics.setTodayNewCount(expertMapper.getNewExpertCount(todayStart, now));
        statistics.setWeekNewCount(expertMapper.getNewExpertCount(weekStart, now));
        statistics.setMonthNewCount(expertMapper.getNewExpertCount(monthStart, now));

        // 补充其他统计信息
        statistics.setAvgRating(expertMapper.getAvgRating());
        statistics.setTotalOrderCount(expertMapper.getTotalOrderCount());
        statistics.setTotalCompleteCount(expertMapper.getTotalCompleteCount());
        statistics.setAvgCompleteRate(expertMapper.getAvgCompleteRate());

        return statistics;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveExpert(ExpertSaveDTO saveDTO) {
        // 验证用户是否存在
        User user = userService.getById(saveDTO.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        Expert expert;
        if (saveDTO.getId() != null) {
            // 更新操作
            expert = this.getById(saveDTO.getId());
            if (expert == null) {
                throw new BusinessException("达人不存在");
            }

            // 检查达人名称是否重复
            if (checkExpertNameExists(saveDTO.getExpertName(), saveDTO.getId())) {
                throw new BusinessException("达人名称已存在");
            }
        } else {
            // 新增操作
            expert = new Expert();

            // 检查用户是否已经是达人
            if (checkUserIsExpert(saveDTO.getUserId())) {
                throw new BusinessException("该用户已经是达人");
            }

            // 检查达人名称是否重复
            if (checkExpertNameExists(saveDTO.getExpertName(), null)) {
                throw new BusinessException("达人名称已存在");
            }
        }

        // 复制属性
        BeanUtils.copyProperties(saveDTO, expert);

        // 验证价格范围
        if (saveDTO.getPriceMin() != null && saveDTO.getPriceMax() != null) {
            if (saveDTO.getPriceMin().compareTo(saveDTO.getPriceMax()) > 0) {
                throw new BusinessException("最低价格不能大于最高价格");
            }
        }

        boolean result = this.saveOrUpdate(expert);

        // 如果是新增达人，更新用户的达人标识
        if (saveDTO.getId() == null && result) {
            user.setIsExpert(1);
            userService.updateById(user);
        }

        return result;
    }

    @Override
    public ExpertDetailVO getExpertDetail(Long expertId) {
        ExpertDetailVO expertDetail = expertMapper.selectExpertDetail(expertId);
        if (expertDetail != null) {
            fillStatusDesc(expertDetail);
            // 加载达人照片列表
            List<ExpertPhoto> photos = expertPhotoService.getPhotosByExpertId(expertId);
            expertDetail.setPhotos(photos);
        }
        return expertDetail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExpert(Long expertId) {
        Expert expert = this.getById(expertId);
        if (expert == null) {
            throw new BusinessException("达人不存在");
        }

        // 删除达人
        boolean result = this.removeById(expertId);

        // 更新用户的达人标识
        if (result) {
            User user = userService.getById(expert.getUserId());
            if (user != null) {
                user.setIsExpert(0);
                userService.updateById(user);
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteExperts(List<Long> expertIds) {
        if (expertIds == null || expertIds.isEmpty()) {
            return false;
        }

        // 获取要删除的达人信息
        List<Expert> experts = this.listByIds(expertIds);

        // 删除达人
        boolean result = this.removeByIds(expertIds);

        // 更新用户的达人标识
        if (result) {
            for (Expert expert : experts) {
                User user = userService.getById(expert.getUserId());
                if (user != null) {
                    user.setIsExpert(0);
                    userService.updateById(user);
                }
            }
        }

        return result;
    }

    @Override
    public boolean updateExpertStatus(Long expertId, Integer status) {
        Expert expert = this.getById(expertId);
        if (expert == null) {
            throw new BusinessException("达人不存在");
        }

        expert.setStatus(status);
        return this.updateById(expert);
    }

    @Override
    public boolean batchUpdateExpertStatus(List<Long> expertIds, Integer status) {
        if (expertIds == null || expertIds.isEmpty()) {
            return false;
        }

        return expertMapper.batchUpdateStatus(expertIds, status) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditExpert(ExpertAuditDTO auditDTO) {
        Expert expert = this.getById(auditDTO.getExpertId());
        if (expert == null) {
            throw new BusinessException("达人不存在");
        }

        expert.setAuditStatus(auditDTO.getAuditStatus());
        expert.setAuditRemark(auditDTO.getAuditRemark());

        return this.updateById(expert);
    }

    @Override
    public boolean batchAuditExperts(List<Long> expertIds, Integer auditStatus, String auditRemark) {
        if (expertIds == null || expertIds.isEmpty()) {
            return false;
        }

        return expertMapper.batchUpdateAuditStatus(expertIds, auditStatus, auditRemark) > 0;
    }

    @Override
    public Expert getByUserId(Long userId) {
        return expertMapper.selectByUserId(userId);
    }

    @Override
    public boolean checkUserIsExpert(Long userId) {
        return expertMapper.checkUserIsExpert(userId);
    }

    @Override
    public List<Expert> getByCategoryId(Long categoryId) {
        return expertMapper.selectByCategoryId(categoryId);
    }

    @Override
    public List<ExpertDetailVO> getHotExperts(Integer limit) {
        List<ExpertDetailVO> hotExperts = expertMapper.selectHotExperts(limit);
        hotExperts.forEach(this::fillStatusDesc);
        return hotExperts;
    }

    @Override
    public boolean updateExpertStatistics(Long expertId, Integer orderCount, Integer completeCount, Double rating) {
        return expertMapper.updateExpertStatistics(expertId, orderCount, completeCount, rating) > 0;
    }

    @Override
    public boolean checkExpertNameExists(String expertName, Long excludeId) {
        LambdaQueryWrapper<Expert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Expert::getExpertName, expertName);
        if (excludeId != null) {
            wrapper.ne(Expert::getId, excludeId);
        }
        return this.count(wrapper) > 0;
    }

    @Override
    public Long getTotalExpertCount() {
        return this.count();
    }

    @Override
    public Long getOnlineExpertCount() {
        return expertMapper.getOnlineExpertCount();
    }

    @Override
    public Long getPendingExpertCount() {
        return expertMapper.getPendingExpertCount();
    }

    /**
     * 填充状态描述
     */
    private void fillStatusDesc(ExpertDetailVO expertDetail) {
        // 填充状态描述
        ExpertStatusEnum statusEnum = ExpertStatusEnum.getByCode(expertDetail.getStatus());
        if (statusEnum != null) {
            expertDetail.setStatusDesc(statusEnum.getDesc());
        }

        // 填充审核状态描述
        ExpertAuditStatusEnum auditStatusEnum = ExpertAuditStatusEnum.getByCode(expertDetail.getAuditStatus());
        if (auditStatusEnum != null) {
            expertDetail.setAuditStatusDesc(auditStatusEnum.getDesc());
        }
    }
}
