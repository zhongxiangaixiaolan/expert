package com.qing.expert.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.dto.review.ReviewCreateDTO;
import com.qing.expert.dto.review.ReviewQueryDTO;
import com.qing.expert.entity.Review;
import com.qing.expert.vo.review.ReviewVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 评价服务接口
 */
public interface ReviewService extends IService<Review> {

    /**
     * 创建评价
     */
    Long createReview(ReviewCreateDTO createDTO, Long userId);

    /**
     * 删除评价
     */
    Boolean deleteReview(Long id);

    /**
     * 根据ID查询评价详情
     */
    ReviewVO getReviewById(Long id);

    /**
     * 分页查询评价列表
     */
    IPage<ReviewVO> getReviewPage(ReviewQueryDTO queryDTO);

    /**
     * 查询达人评价列表
     */
    List<ReviewVO> getExpertReviews(Long expertId, Integer limit);

    /**
     * 查询服务评价列表
     */
    List<ReviewVO> getServiceReviews(Long serviceId, Integer limit);

    /**
     * 查询用户评价列表
     */
    List<ReviewVO> getUserReviews(Long userId, Integer reviewType);

    /**
     * 回复评价
     */
    Boolean replyReview(Long reviewId, String replyContent, Long operatorId);

    /**
     * 隐藏评价
     */
    Boolean hideReview(Long reviewId);

    /**
     * 显示评价
     */
    Boolean showReview(Long reviewId);

    /**
     * 计算达人平均评分
     */
    BigDecimal getExpertAvgRating(Long expertId);

    /**
     * 计算服务平均评分
     */
    BigDecimal getServiceAvgRating(Long serviceId);

    /**
     * 统计达人评价数量
     */
    Long countExpertReviews(Long expertId);

    /**
     * 统计服务评价数量
     */
    Long countServiceReviews(Long serviceId);

    /**
     * 检查是否可以评价
     */
    Boolean canReview(Long orderId, Integer reviewType, Long userId);

    /**
     * 根据订单ID查询评价
     */
    Review getReviewByOrderId(Long orderId, Integer reviewType);
}
