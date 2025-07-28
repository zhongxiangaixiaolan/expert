package com.qing.expert.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.RecommendServiceDTO;
import com.qing.expert.service.RecommendServiceService;
import com.qing.expert.vo.RecommendServiceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 管理端推荐服务控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/recommend-services")
@RequiredArgsConstructor
@Validated
public class AdminRecommendServiceController {

    private final RecommendServiceService recommendServiceService;

    @GetMapping
    public Result<IPage<RecommendServiceVO>> getRecommendServicePage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) String recommendType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String expertName,
            @RequestParam(required = false) String serviceName) {
        Page<RecommendServiceVO> page = new Page<>(current, size);
        IPage<RecommendServiceVO> result = recommendServiceService.getRecommendServicePage(
                page, serviceId, recommendType, status, expertName, serviceName);
        return Result.success(result);
    }

    @GetMapping("/{id}")
        RecommendServiceVO recommendService = recommendServiceService.getRecommendServiceById(id);
        return Result.success(recommendService);
    }

    @PostMapping
        boolean success = recommendServiceService.createRecommendService(dto);
        return Result.success(success);
    }

    @PutMapping("/{id}")
            @Valid @RequestBody RecommendServiceDTO dto) {
        boolean success = recommendServiceService.updateRecommendService(id, dto);
        return Result.success(success);
    }

    @DeleteMapping("/{id}")
        boolean success = recommendServiceService.deleteRecommendService(id);
        return Result.success(success);
    }

    @DeleteMapping("/batch")
        boolean success = recommendServiceService.batchDeleteRecommendService(ids);
        return Result.success(success);
    }

    @PutMapping("/{id}/status")
        return Result.success(success);
    }

    @PutMapping("/batch/status")
        return Result.success(success);
    }

    @PutMapping("/{id}/sort")
        return Result.success(success);
    }

    @GetMapping("/check")
        boolean exists = recommendServiceService.checkServiceRecommended(serviceId, recommendType, excludeId);
        return Result.success(exists);
    }
}
