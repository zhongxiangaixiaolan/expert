package com.qing.expert.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qing.expert.common.enums.OrderStatusEnum;
import com.qing.expert.common.enums.ReviewTypeEnum;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.dto.review.ReviewCreateDTO;
import com.qing.expert.dto.review.ReviewQueryDTO;
import com.qing.expert.entity.Order;
import com.qing.expert.entity.Review;
import com.qing.expert.mapper.ReviewMapper;
import com.qing.expert.service.OrderService;
import com.qing.expert.service.ReviewService;
import com.qing.expert.vo.review.ReviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReview(ReviewCreateDTO createDTO, Long userId) {
        // 检查是否可以评价
        if (!canReview(createDTO.getOrderId(), createDTO.getReviewType(), userId)) {
            throw new BusinessException("无法评价该订单");
        }

        // 获取订单信息
        Order order = orderService.getById(createDTO.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 创建评价实体
        Review review = new Review();
        BeanUtils.copyProperties(createDTO, review);
        
        // 设置评价基本信息
        review.setUserId(userId);
        review.setExpertId(order.getExpertId());
        review.setServiceId(order.getServiceId());
        review.setStatus(1); // 正常状态
        
        // 处理图片列表
        if (!CollectionUtils.isEmpty(createDTO.getImages())) {
            try {
                review.setImages(objectMapper.writeValueAsString(createDTO.getImages()));
            } catch (JsonProcessingException e) {
                log.error("序列化评价图片失败", e);
                throw new BusinessException("评价图片格式错误");
            }
        }
        
        // 保存评价
        boolean saved = save(review);
        if (!saved) {
            throw new BusinessException("创建评价失败");
        }
        
        log.info("创建评价成功，评价ID：{}，订单ID：{}", review.getId(), createDTO.getOrderId());
        return review.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteReview(Long id) {
        Review existReview = getById(id);
        if (existReview == null) {
            throw new BusinessException("评价不存在");
        }
        
        boolean deleted = removeById(id);
        if (deleted) {
            log.info("删除评价成功，评价ID：{}", id);
        }
        return deleted;
    }

    @Override
    public ReviewVO getReviewById(Long id) {
        return reviewMapper.selectReviewById(id);
    }

    @Override
    public IPage<ReviewVO> getReviewPage(ReviewQueryDTO queryDTO) {
        Page<ReviewVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return reviewMapper.selectReviewPage(page, queryDTO);
    }

    @Override
    public List<ReviewVO> getExpertReviews(Long expertId, Integer limit) {
        return reviewMapper.selectReviewsByExpertId(expertId, limit);
    }

    @Override
    public List<ReviewVO> getServiceReviews(Long serviceId, Integer limit) {
        return reviewMapper.selectReviewsByServiceId(serviceId, limit);
    }

    @Override
    public List<ReviewVO> getUserReviews(Long userId, Integer reviewType) {
        return reviewMapper.selectReviewsByUserId(userId, reviewType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean replyReview(Long reviewId, String replyContent, Long operatorId) {
        Review review = getById(reviewId);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        
        review.setReplyContent(replyContent);
        review.setReplyTime(LocalDateTime.now());
        
        boolean updated = updateById(review);
        if (updated) {
            log.info("回复评价成功，评价ID：{}，操作人：{}", reviewId, operatorId);
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean hideReview(Long reviewId) {
        Review review = getById(reviewId);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        
        review.setStatus(2); // 隐藏状态
        
        boolean updated = updateById(review);
        if (updated) {
            log.info("隐藏评价成功，评价ID：{}", reviewId);
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean showReview(Long reviewId) {
        Review review = getById(reviewId);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        
        review.setStatus(1); // 正常状态
        
        boolean updated = updateById(review);
        if (updated) {
            log.info("显示评价成功，评价ID：{}", reviewId);
        }
        return updated;
    }

    @Override
    public BigDecimal getExpertAvgRating(Long expertId) {
        BigDecimal avgRating = reviewMapper.calculateExpertAvgRating(expertId);
        return avgRating != null ? avgRating : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getServiceAvgRating(Long serviceId) {
        BigDecimal avgRating = reviewMapper.calculateServiceAvgRating(serviceId);
        return avgRating != null ? avgRating : BigDecimal.ZERO;
    }

    @Override
    public Long countExpertReviews(Long expertId) {
        return reviewMapper.countExpertReviews(expertId);
    }

    @Override
    public Long countServiceReviews(Long serviceId) {
        return reviewMapper.countServiceReviews(serviceId);
    }

    @Override
    public Boolean canReview(Long orderId, Integer reviewType, Long userId) {
        // 获取订单信息
        Order order = orderService.getById(orderId);
        if (order == null) {
            return false;
        }
        
        // 检查订单状态，只有已完成的订单才能评价
        if (!OrderStatusEnum.isCompleted(order.getStatus())) {
            return false;
        }
        
        // 检查评价权限
        if (ReviewTypeEnum.isUserToExpert(reviewType)) {
            // 用户评价达人：检查是否为订单用户
            if (!userId.equals(order.getUserId())) {
                return false;
            }
        } else if (ReviewTypeEnum.isExpertToUser(reviewType)) {
            // 达人评价用户：检查是否为订单达人
            if (!userId.equals(order.getExpertId())) {
                return false;
            }
        } else {
            return false;
        }
        
        // 检查是否已经评价过
        Review existReview = getReviewByOrderId(orderId, reviewType);
        return existReview == null;
    }

    @Override
    public Review getReviewByOrderId(Long orderId, Integer reviewType) {
        return reviewMapper.selectByOrderId(orderId, reviewType);
    }
}
