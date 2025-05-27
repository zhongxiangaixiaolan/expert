package com.qing.expert.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qing.expert.common.enums.AuditStatusEnum;
import com.qing.expert.common.enums.AuditTypeEnum;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.dto.AuditProcessDTO;
import com.qing.expert.dto.AuditRecordDTO;
import com.qing.expert.entity.AuditRecord;
import com.qing.expert.mapper.AuditRecordMapper;
import com.qing.expert.service.AuditRecordService;
import com.qing.expert.vo.AuditRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 审核记录Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditRecordServiceImpl extends ServiceImpl<AuditRecordMapper, AuditRecord> 
        implements AuditRecordService {

    @Override
    public IPage<AuditRecordVO> getAuditRecordPage(Page<AuditRecordVO> page,
                                                   String auditType,
                                                   String auditStatus,
                                                   String auditorName,
                                                   String submitterName,
                                                   Integer priority,
                                                   LocalDateTime startTime,
                                                   LocalDateTime endTime) {
        return baseMapper.selectAuditRecordPage(page, auditType, auditStatus, auditorName, 
                submitterName, priority, startTime, endTime);
    }

    @Override
    public AuditRecordVO getAuditRecordById(Long id) {
        if (id == null) {
            throw new BusinessException("审核记录ID不能为空");
        }
        return baseMapper.selectAuditRecordById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createAuditRecord(AuditRecordDTO dto) {
        // 验证审核类型
        AuditTypeEnum typeEnum = AuditTypeEnum.getByCode(dto.getAuditType());
        if (typeEnum == null) {
            throw new BusinessException("审核类型不正确");
        }

        // 验证审核状态
        if (dto.getAuditStatus() != null) {
            AuditStatusEnum statusEnum = AuditStatusEnum.getByCode(dto.getAuditStatus());
            if (statusEnum == null) {
                throw new BusinessException("审核状态不正确");
            }
        }

        AuditRecord auditRecord = new AuditRecord();
        BeanUtils.copyProperties(dto, auditRecord);

        // 设置默认值
        if (auditRecord.getAuditStatus() == null) {
            auditRecord.setAuditStatus(AuditStatusEnum.PENDING.getCode());
        }
        if (auditRecord.getPriority() == null) {
            auditRecord.setPriority(2); // 默认中等优先级
        }
        if (auditRecord.getSubmitTime() == null) {
            auditRecord.setSubmitTime(LocalDateTime.now());
        }

        return save(auditRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAuditRecord(Long id, AuditRecordDTO dto) {
        AuditRecord existingRecord = getById(id);
        if (existingRecord == null) {
            throw new BusinessException("审核记录不存在");
        }

        // 验证审核类型
        AuditTypeEnum typeEnum = AuditTypeEnum.getByCode(dto.getAuditType());
        if (typeEnum == null) {
            throw new BusinessException("审核类型不正确");
        }

        // 验证审核状态
        if (dto.getAuditStatus() != null) {
            AuditStatusEnum statusEnum = AuditStatusEnum.getByCode(dto.getAuditStatus());
            if (statusEnum == null) {
                throw new BusinessException("审核状态不正确");
            }
        }

        AuditRecord auditRecord = new AuditRecord();
        BeanUtils.copyProperties(dto, auditRecord);
        auditRecord.setId(id);

        return updateById(auditRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAuditRecord(Long id) {
        if (id == null) {
            throw new BusinessException("审核记录ID不能为空");
        }
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteAuditRecord(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("审核记录ID列表不能为空");
        }
        return removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean processAudit(AuditProcessDTO dto) {
        AuditRecord auditRecord = getById(dto.getId());
        if (auditRecord == null) {
            throw new BusinessException("审核记录不存在");
        }

        // 验证审核状态
        AuditStatusEnum statusEnum = AuditStatusEnum.getByCode(dto.getAuditStatus());
        if (statusEnum == null) {
            throw new BusinessException("审核状态不正确");
        }

        // 只有待审核状态才能处理
        if (!AuditStatusEnum.PENDING.getCode().equals(auditRecord.getAuditStatus())) {
            throw new BusinessException("只有待审核状态的记录才能处理");
        }

        LambdaUpdateWrapper<AuditRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AuditRecord::getId, dto.getId())
                .set(AuditRecord::getAuditStatus, dto.getAuditStatus())
                .set(AuditRecord::getAuditOpinion, dto.getAuditOpinion())
                .set(AuditRecord::getAuditorId, dto.getAuditorId())
                .set(AuditRecord::getAuditorName, dto.getAuditorName())
                .set(AuditRecord::getAuditTime, LocalDateTime.now())
                .set(AuditRecord::getUpdateTime, LocalDateTime.now());

        return update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchProcessAudit(List<AuditProcessDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            throw new BusinessException("审核处理列表不能为空");
        }

        for (AuditProcessDTO dto : dtoList) {
            processAudit(dto);
        }
        return true;
    }

    @Override
    public int countPendingAuditRecords(String auditType, Long auditorId) {
        return baseMapper.countPendingAuditRecords(auditType, auditorId);
    }

    @Override
    public List<Map<String, Object>> getAuditStatistics(String auditType, LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.selectAuditStatistics(auditType, startTime, endTime);
    }

    @Override
    public List<Map<String, Object>> getAuditorWorkloadStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.selectAuditorWorkloadStatistics(startTime, endTime);
    }

    @Override
    public List<AuditRecordVO> getOverdueAuditRecords(Integer hours) {
        if (hours == null || hours <= 0) {
            hours = 24; // 默认24小时
        }
        return baseMapper.selectOverdueAuditRecords(hours);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean autoAssignAuditor(Long auditRecordId, Long auditorId, String auditorName) {
        if (auditRecordId == null) {
            throw new BusinessException("审核记录ID不能为空");
        }
        if (auditorId == null) {
            throw new BusinessException("审核人员ID不能为空");
        }

        LambdaUpdateWrapper<AuditRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AuditRecord::getId, auditRecordId)
                .set(AuditRecord::getAuditorId, auditorId)
                .set(AuditRecord::getAuditorName, auditorName)
                .set(AuditRecord::getUpdateTime, LocalDateTime.now());

        return update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitAuditApplication(String auditType, Long businessId, String title, String content,
                                          Long submitterId, String submitterName, Integer priority) {
        // 验证审核类型
        AuditTypeEnum typeEnum = AuditTypeEnum.getByCode(auditType);
        if (typeEnum == null) {
            throw new BusinessException("审核类型不正确");
        }

        AuditRecord auditRecord = new AuditRecord();
        auditRecord.setAuditType(auditType);
        auditRecord.setBusinessId(businessId);
        auditRecord.setAuditTitle(title);
        auditRecord.setAuditContent(content);
        auditRecord.setAuditStatus(AuditStatusEnum.PENDING.getCode());
        auditRecord.setSubmitterId(submitterId);
        auditRecord.setSubmitterName(submitterName);
        auditRecord.setSubmitTime(LocalDateTime.now());
        auditRecord.setPriority(priority != null ? priority : 2);

        return save(auditRecord);
    }
}
