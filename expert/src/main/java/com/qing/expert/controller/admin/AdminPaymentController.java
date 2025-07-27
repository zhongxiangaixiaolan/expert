package com.qing.expert.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.common.result.Result;
import com.qing.expert.service.PaymentService;
import com.qing.expert.vo.PaymentRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 管理端支付控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/payment")
@RequiredArgsConstructor
@Validated
        IPage<PaymentRecordVO> result = paymentService.getPaymentRecordPage(
                page, userId, orderId, paymentType, paymentStatus, userNickname, userPhone, startTime, endTime);
        return Result.success(result);
    }

    @GetMapping("/records/{id}")
        PaymentRecordVO paymentRecord = paymentService.getPaymentRecordById(id);
        return Result.success(paymentRecord);
    }

    @PostMapping("/refund")
        boolean success = paymentService.refundPayment(paymentNo, refundAmount, refundReason);
        return Result.success(success);
    }

    @PostMapping("/cancel")
        return Result.success(success);
    }

    @GetMapping("/statistics")
        List<Map<String, Object>> statistics = paymentService.getPaymentStatistics(paymentType, startTime, endTime);
        return Result.success(statistics);
    }

    @GetMapping("/summary")
        return Result.success(summary);
    }

    @PostMapping("/handle-expired")
        int count = paymentService.handleExpiredPayments(minutes);
        return Result.success(count);
    }

    @GetMapping("/user/{userId}/amount")
        return Result.success(amount);
    }
}
