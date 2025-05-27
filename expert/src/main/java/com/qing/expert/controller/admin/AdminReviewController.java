package com.qing.expert.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.review.ReviewQueryDTO;
import com.qing.expert.service.ReviewService;
import com.qing.expert.vo.review.ReviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 管理端评价控制器
 */
@Tag(name = "管理端评价管理", description = "管理端评价相关接口")
@RestController
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "分页查询评价列表")
    @GetMapping("/page")
    public Result<IPage<ReviewVO>> getReviewPage(@Valid ReviewQueryDTO queryDTO) {
        IPage<ReviewVO> page = reviewService.getReviewPage(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "根据ID查询评价详情")
    @GetMapping("/{id}")
    public Result<ReviewVO> getReviewById(@Parameter(description = "评价ID") @PathVariable Long id) {
        ReviewVO reviewVO = reviewService.getReviewById(id);
        return Result.success(reviewVO);
    }

    @Operation(summary = "删除评价")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteReview(@Parameter(description = "评价ID") @PathVariable Long id) {
        Boolean result = reviewService.deleteReview(id);
        return Result.success(result);
    }

    @Operation(summary = "回复评价")
    @PostMapping("/{id}/reply")
    public Result<Boolean> replyReview(
            @Parameter(description = "评价ID") @PathVariable Long id,
            @Parameter(description = "回复内容") @RequestParam String replyContent) {
        Boolean result = reviewService.replyReview(id, replyContent, 0L); // 管理员操作
        return Result.success(result);
    }

    @Operation(summary = "隐藏评价")
    @PostMapping("/{id}/hide")
    public Result<Boolean> hideReview(@Parameter(description = "评价ID") @PathVariable Long id) {
        Boolean result = reviewService.hideReview(id);
        return Result.success(result);
    }

    @Operation(summary = "显示评价")
    @PostMapping("/{id}/show")
    public Result<Boolean> showReview(@Parameter(description = "评价ID") @PathVariable Long id) {
        Boolean result = reviewService.showReview(id);
        return Result.success(result);
    }
}
