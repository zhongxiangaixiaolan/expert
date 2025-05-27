package com.qing.expert.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.RecommendServiceDTO;
import com.qing.expert.service.RecommendServiceService;
import com.qing.expert.vo.RecommendServiceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "管理端推荐服务管理", description = "推荐服务的增删改查、状态管理、排序等功能")
public class AdminRecommendServiceController {

    private final RecommendServiceService recommendServiceService;

    @GetMapping("/page")
    @Operation(summary = "分页查询推荐服务列表", description = "支持按服务ID、推荐类型、状态、达人姓名、服务名称等条件筛选")
    public Result<IPage<RecommendServiceVO>> getRecommendServicePage(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "服务ID") @RequestParam(required = false) Long serviceId,
            @Parameter(description = "推荐类型") @RequestParam(required = false) String recommendType,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "达人姓名") @RequestParam(required = false) String expertName,
            @Parameter(description = "服务名称") @RequestParam(required = false) String serviceName) {

        Page<RecommendServiceVO> page = new Page<>(current, size);
        IPage<RecommendServiceVO> result = recommendServiceService.getRecommendServicePage(
                page, serviceId, recommendType, status, expertName, serviceName);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询推荐服务详情")
    public Result<RecommendServiceVO> getRecommendServiceById(
            @Parameter(description = "推荐服务ID") @PathVariable @NotNull Long id) {
        RecommendServiceVO recommendService = recommendServiceService.getRecommendServiceById(id);
        return Result.success(recommendService);
    }

    @PostMapping
    @Operation(summary = "创建推荐服务")
    public Result<Boolean> createRecommendService(@Valid @RequestBody RecommendServiceDTO dto) {
        boolean success = recommendServiceService.createRecommendService(dto);
        return Result.success(success);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新推荐服务")
    public Result<Boolean> updateRecommendService(
            @Parameter(description = "推荐服务ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody RecommendServiceDTO dto) {
        boolean success = recommendServiceService.updateRecommendService(id, dto);
        return Result.success(success);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除推荐服务")
    public Result<Boolean> deleteRecommendService(
            @Parameter(description = "推荐服务ID") @PathVariable @NotNull Long id) {
        boolean success = recommendServiceService.deleteRecommendService(id);
        return Result.success(success);
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除推荐服务")
    public Result<Boolean> batchDeleteRecommendService(
            @Parameter(description = "推荐服务ID列表") @RequestBody @NotEmpty List<Long> ids) {
        boolean success = recommendServiceService.batchDeleteRecommendService(ids);
        return Result.success(success);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新推荐服务状态")
    public Result<Boolean> updateRecommendServiceStatus(
            @Parameter(description = "推荐服务ID") @PathVariable @NotNull Long id,
            @Parameter(description = "状态") @RequestParam @NotNull Integer status) {
        boolean success = recommendServiceService.updateRecommendServiceStatus(id, status);
        return Result.success(success);
    }

    @PutMapping("/batch/status")
    @Operation(summary = "批量更新推荐服务状态")
    public Result<Boolean> batchUpdateRecommendServiceStatus(
            @Parameter(description = "推荐服务ID列表") @RequestBody @NotEmpty List<Long> ids,
            @Parameter(description = "状态") @RequestParam @NotNull Integer status) {
        boolean success = recommendServiceService.batchUpdateRecommendServiceStatus(ids, status);
        return Result.success(success);
    }

    @PutMapping("/{id}/sort")
    @Operation(summary = "调整推荐服务排序")
    public Result<Boolean> adjustRecommendServiceSort(
            @Parameter(description = "推荐服务ID") @PathVariable @NotNull Long id,
            @Parameter(description = "排序方向", example = "up") @RequestParam String direction) {
        boolean success = recommendServiceService.adjustRecommendServiceSort(id, direction);
        return Result.success(success);
    }

    @GetMapping("/check")
    @Operation(summary = "检查服务是否已被推荐")
    public Result<Boolean> checkServiceRecommended(
            @Parameter(description = "服务ID") @RequestParam @NotNull Long serviceId,
            @Parameter(description = "推荐类型") @RequestParam String recommendType,
            @Parameter(description = "排除的推荐服务ID") @RequestParam(required = false) Long excludeId) {
        boolean exists = recommendServiceService.checkServiceRecommended(serviceId, recommendType, excludeId);
        return Result.success(exists);
    }
}
