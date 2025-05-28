package com.qing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.entity.Expert;
import com.qing.expert.vo.ExpertDetailVO;
import com.qing.expert.vo.ExpertStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 达人Mapper
 */
@Mapper
public interface ExpertMapper extends BaseMapper<Expert> {

    /**
     * 分页查询达人列表（包含用户和分类信息）
     */
    IPage<ExpertDetailVO> selectExpertPage(Page<ExpertDetailVO> page,
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("status") Integer status,
            @Param("auditStatus") Integer auditStatus,
            @Param("minRating") Double minRating,
            @Param("maxRating") Double maxRating,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("createStartTime") LocalDateTime createStartTime,
            @Param("createEndTime") LocalDateTime createEndTime,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder);

    /**
     * 根据用户ID查询达人信息
     */
    Expert selectByUserId(@Param("userId") Long userId);

    /**
     * 获取达人详情（包含用户和分类信息）
     */
    ExpertDetailVO selectExpertDetail(@Param("expertId") Long expertId);

    /**
     * 获取达人统计信息
     */
    ExpertStatisticsVO getExpertStatistics();

    /**
     * 获取在线达人数量
     */
    Long getOnlineExpertCount();

    /**
     * 获取忙碌达人数量
     */
    Long getBusyExpertCount();

    /**
     * 获取下线达人数量
     */
    Long getOfflineExpertCount();

    /**
     * 获取待审核达人数量
     */
    Long getPendingExpertCount();

    /**
     * 获取审核通过达人数量
     */
    Long getApprovedExpertCount();

    /**
     * 获取审核拒绝达人数量
     */
    Long getRejectedExpertCount();

    /**
     * 获取新增达人数量
     */
    Long getNewExpertCount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 获取平均评分
     */
    Double getAvgRating();

    /**
     * 获取总接单数
     */
    Long getTotalOrderCount();

    /**
     * 获取总完成数
     */
    Long getTotalCompleteCount();

    /**
     * 获取平均完成率
     */
    Double getAvgCompleteRate();

    /**
     * 批量更新达人状态
     */
    int batchUpdateStatus(@Param("expertIds") List<Long> expertIds, @Param("status") Integer status);

    /**
     * 批量更新达人审核状态
     */
    int batchUpdateAuditStatus(@Param("expertIds") List<Long> expertIds,
            @Param("auditStatus") Integer auditStatus,
            @Param("auditRemark") String auditRemark);

    /**
     * 更新达人统计信息
     */
    int updateExpertStatistics(@Param("expertId") Long expertId,
            @Param("orderCount") Integer orderCount,
            @Param("completeCount") Integer completeCount,
            @Param("rating") Double rating);

    /**
     * 检查用户是否已经是达人
     */
    boolean checkUserIsExpert(@Param("userId") Long userId);

    /**
     * 根据分类ID获取达人列表
     */
    List<Expert> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 获取热门达人列表
     */
    List<ExpertDetailVO> selectHotExperts(@Param("limit") Integer limit);

    /**
     * 更新达人热门状态
     */
    int updateExpertHotStatus(@Param("expertId") Long expertId, @Param("isHot") Integer isHot);

    /**
     * 批量更新达人热门状态
     */
    int batchUpdateExpertHotStatus(@Param("expertIds") List<Long> expertIds, @Param("isHot") Integer isHot);
}
