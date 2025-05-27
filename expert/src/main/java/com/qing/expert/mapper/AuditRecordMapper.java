package com.qing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.entity.AuditRecord;
import com.qing.expert.vo.AuditRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 审核记录Mapper接口
 */
@Mapper
public interface AuditRecordMapper extends BaseMapper<AuditRecord> {

    /**
     * 分页查询审核记录列表
     */
    IPage<AuditRecordVO> selectAuditRecordPage(Page<AuditRecordVO> page,
                                               @Param("auditType") String auditType,
                                               @Param("auditStatus") String auditStatus,
                                               @Param("auditorName") String auditorName,
                                               @Param("submitterName") String submitterName,
                                               @Param("priority") Integer priority,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 根据ID查询审核记录详情
     */
    AuditRecordVO selectAuditRecordById(@Param("id") Long id);

    /**
     * 查询待审核记录数量
     */
    int countPendingAuditRecords(@Param("auditType") String auditType,
                                 @Param("auditorId") Long auditorId);

    /**
     * 查询审核统计数据
     */
    List<Map<String, Object>> selectAuditStatistics(@Param("auditType") String auditType,
                                                     @Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime);

    /**
     * 查询审核人员工作量统计
     */
    List<Map<String, Object>> selectAuditorWorkloadStatistics(@Param("startTime") LocalDateTime startTime,
                                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 查询超时未处理的审核记录
     */
    List<AuditRecordVO> selectOverdueAuditRecords(@Param("hours") Integer hours);
}
