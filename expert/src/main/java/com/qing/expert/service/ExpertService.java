package com.qing.expert.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.common.result.PageResult;
import com.qing.expert.dto.ExpertAuditDTO;
import com.qing.expert.dto.ExpertQueryDTO;
import com.qing.expert.dto.ExpertSaveDTO;
import com.qing.expert.entity.Expert;
import com.qing.expert.vo.ExpertDetailVO;
import com.qing.expert.vo.ExpertStatisticsVO;

import java.util.List;

/**
 * 达人服务接口
 */
public interface ExpertService extends IService<Expert> {

    /**
     * 分页查询达人列表
     */
    PageResult<ExpertDetailVO> getExpertPage(ExpertQueryDTO queryDTO);

    /**
     * 获取达人统计信息
     */
    ExpertStatisticsVO getExpertStatistics();

    /**
     * 保存达人（新增或更新）
     */
    boolean saveExpert(ExpertSaveDTO saveDTO);

    /**
     * 获取达人详情
     */
    ExpertDetailVO getExpertDetail(Long expertId);

    /**
     * 删除达人
     */
    boolean deleteExpert(Long expertId);

    /**
     * 批量删除达人
     */
    boolean batchDeleteExperts(List<Long> expertIds);

    /**
     * 更新达人状态
     */
    boolean updateExpertStatus(Long expertId, Integer status);

    /**
     * 批量更新达人状态
     */
    boolean batchUpdateExpertStatus(List<Long> expertIds, Integer status);

    /**
     * 达人审核
     */
    boolean auditExpert(ExpertAuditDTO auditDTO);

    /**
     * 批量达人审核
     */
    boolean batchAuditExperts(List<Long> expertIds, Integer auditStatus, String auditRemark);

    /**
     * 根据用户ID查询达人信息
     */
    Expert getByUserId(Long userId);

    /**
     * 检查用户是否已经是达人
     */
    boolean checkUserIsExpert(Long userId);

    /**
     * 根据分类ID获取达人列表
     */
    List<Expert> getByCategoryId(Long categoryId);

    /**
     * 获取热门达人列表
     */
    List<ExpertDetailVO> getHotExperts(Integer limit);

    /**
     * 更新达人统计信息
     */
    boolean updateExpertStatistics(Long expertId, Integer orderCount, Integer completeCount, Double rating);

    /**
     * 检查达人名称是否存在
     */
    boolean checkExpertNameExists(String expertName, Long excludeId);

    /**
     * 获取达人总数
     */
    Long getTotalExpertCount();

    /**
     * 获取在线达人数量
     */
    Long getOnlineExpertCount();

    /**
     * 获取待审核达人数量
     */
    Long getPendingExpertCount();

    /**
     * 设置达人热门状态
     */
    boolean updateExpertHotStatus(Long expertId, Integer isHot);

    /**
     * 批量设置达人热门状态
     */
    boolean batchUpdateExpertHotStatus(List<Long> expertIds, Integer isHot);
}
