package com.qing.expert.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.review.ReviewQueryDTO;
import com.qing.expert.service.ReviewService;
import com.qing.expert.vo.review.ReviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 管理端评价控制器
 */
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    public Result<IPage<ReviewVO>> getReviewPage(@Valid ReviewQueryDTO queryDTO) {
        IPage<ReviewVO> page = reviewService.getReviewPage(queryDTO);
        return Result.success(page);
    }

        return Result.success(reviewVO);
    }

        return Result.success(result);
    }

    public Result<Boolean> replyReview(
        Boolean result = reviewService.replyReview(id, replyContent, 0L); // 管理员操作
        return Result.success(result);
    }

        return Result.success(result);
    }

        return Result.success(result);
    }
}
