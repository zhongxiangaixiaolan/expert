package com.qing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.dto.review.ReviewQueryDTO;
import com.qing.expert.entity.Review;
import com.qing.expert.vo.review.ReviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 评价Mapper
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    /**
     * 分页查询评价列表
     */
    IPage<ReviewVO> selectReviewPage(Page<ReviewVO> page, @Param("query") ReviewQueryDTO queryDTO);

    /**
     * 根据ID查询评价详情
     */
    ReviewVO selectReviewById(@Param("id") Long id);

    /**
     * 查询达人评价列表
     */
    List<ReviewVO> selectReviewsByExpertId(@Param("expertId") Long expertId, @Param("limit") Integer limit);

    /**
     * 查询服务评价列表
     */
    List<ReviewVO> selectReviewsByServiceId(@Param("serviceId") Long serviceId, @Param("limit") Integer limit);

    /**
     * 计算达人平均评分
     */
    BigDecimal calculateExpertAvgRating(@Param("expertId") Long expertId);

    /**
     * 计算服务平均评分
     */
    BigDecimal calculateServiceAvgRating(@Param("serviceId") Long serviceId);

    /**
     * 统计达人评价数量
     */
    Long countExpertReviews(@Param("expertId") Long expertId);

    /**
     * 统计服务评价数量
     */
    Long countServiceReviews(@Param("serviceId") Long serviceId);

    /**
     * 根据订单ID查询评价
     */
    Review selectByOrderId(@Param("orderId") Long orderId, @Param("reviewType") Integer reviewType);

    /**
     * 查询用户评价列表
     */
    List<ReviewVO> selectReviewsByUserId(@Param("userId") Long userId, @Param("reviewType") Integer reviewType);
}
