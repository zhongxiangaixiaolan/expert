package com.qing.expert.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.dto.AuditProcessDTO;
import com.qing.expert.dto.AuditRecordDTO;
import com.qing.expert.entity.AuditRecord;
import com.qing.expert.vo.AuditRecordVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 审核记录Service接口
 */
public interface AuditRecordService extends IService<AuditRecord> {

    /**
     * 分页查询审核记录列表
     */
    IPage<AuditRecordVO> getAuditRecordPage(Page<AuditRecordVO> page,
                                            String auditType,
                                            String auditStatus,
                                            String auditorName,
                                            String submitterName,
                                            Integer priority,
                                            LocalDateTime startTime,
                                            LocalDateTime endTime);

    /**
     * 根据ID查询审核记录详情
     */
    AuditRecordVO getAuditRecordById(Long id);

    /**
     * 创建审核记录
     */
    boolean createAuditRecord(AuditRecordDTO dto);

    /**
     * 更新审核记录
     */
    boolean updateAuditRecord(Long id, AuditRecordDTO dto);

    /**
     * 删除审核记录
     */
    boolean deleteAuditRecord(Long id);

    /**
     * 批量删除审核记录
     */
    boolean batchDeleteAuditRecord(List<Long> ids);

    /**
     * 处理审核
     */
    boolean processAudit(AuditProcessDTO dto);

    /**
     * 批量处理审核
     */
    boolean batchProcessAudit(List<AuditProcessDTO> dtoList);

    /**
     * 查询待审核记录数量
     */
    int countPendingAuditRecords(String auditType, Long auditorId);

    /**
     * 查询审核统计数据
     */
    List<Map<String, Object>> getAuditStatistics(String auditType, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询审核人员工作量统计
     */
    List<Map<String, Object>> getAuditorWorkloadStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询超时未处理的审核记录
     */
    List<AuditRecordVO> getOverdueAuditRecords(Integer hours);

    /**
     * 自动分配审核人员
     */
    boolean autoAssignAuditor(Long auditRecordId, Long auditorId, String auditorName);

    /**
     * 提交审核申请
     */
    boolean submitAuditApplication(String auditType, Long businessId, String title, String content, 
                                   Long submitterId, String submitterName, Integer priority);
}
