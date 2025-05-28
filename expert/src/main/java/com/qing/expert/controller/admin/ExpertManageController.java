package com.qing.expert.controller.admin;

import com.qing.expert.common.result.PageResult;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.ExpertAuditDTO;
import com.qing.expert.dto.ExpertQueryDTO;
import com.qing.expert.dto.ExpertSaveDTO;
import com.qing.expert.entity.Expert;
import com.qing.expert.service.ExpertService;
import com.qing.expert.vo.ExpertDetailVO;
import com.qing.expert.vo.ExpertStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 达人管理控制器（管理端）
 */
@Slf4j
@RestController
@RequestMapping("/admin/expert")
@RequiredArgsConstructor
@Tag(name = "达人管理", description = "达人信息的增删改查、审核等接口")
public class ExpertManageController {

    private final ExpertService expertService;

    @Operation(summary = "获取达人统计信息", description = "获取达人总数、在线数、审核状态等统计信息")
    @GetMapping("/statistics")
    public Result<ExpertStatisticsVO> getExpertStatistics() {
        ExpertStatisticsVO statistics = expertService.getExpertStatistics();
        return Result.success("获取成功", statistics);
    }

    @Operation(summary = "分页查询达人列表", description = "根据条件分页查询达人列表")
    @GetMapping("/page")
    public Result<PageResult<ExpertDetailVO>> getExpertPage(@Validated ExpertQueryDTO queryDTO) {
        PageResult<ExpertDetailVO> pageResult = expertService.getExpertPage(queryDTO);
        return Result.success("查询成功", pageResult);
    }

    @Operation(summary = "获取达人详情", description = "根据达人ID获取详细信息")
    @GetMapping("/{expertId}")
    public Result<ExpertDetailVO> getExpertDetail(
            @Parameter(description = "达人ID") @PathVariable Long expertId) {
        ExpertDetailVO expertDetail = expertService.getExpertDetail(expertId);
        if (expertDetail == null) {
            return Result.error("达人不存在");
        }
        return Result.success("获取成功", expertDetail);
    }

    @Operation(summary = "保存达人信息", description = "新增或更新达人信息")
    @PostMapping("/save")
    public Result<Void> saveExpert(@Validated @RequestBody ExpertSaveDTO saveDTO) {
        boolean success = expertService.saveExpert(saveDTO);
        if (success) {
            return Result.success(saveDTO.getId() == null ? "新增成功" : "更新成功");
        } else {
            return Result.error(saveDTO.getId() == null ? "新增失败" : "更新失败");
        }
    }

    @Operation(summary = "删除达人", description = "根据达人ID删除达人信息")
    @DeleteMapping("/{expertId}")
    public Result<Void> deleteExpert(
            @Parameter(description = "达人ID") @PathVariable Long expertId) {
        boolean success = expertService.deleteExpert(expertId);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @Operation(summary = "批量删除达人", description = "根据达人ID列表批量删除达人")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteExperts(@RequestBody List<Long> expertIds) {
        if (expertIds == null || expertIds.isEmpty()) {
            return Result.error("请选择要删除的达人");
        }
        boolean success = expertService.batchDeleteExperts(expertIds);
        if (success) {
            return Result.success("批量删除成功");
        } else {
            return Result.error("批量删除失败");
        }
    }

    @Operation(summary = "更新达人状态", description = "更新达人在线状态")
    @PutMapping("/{expertId}/status")
    public Result<Void> updateExpertStatus(
            @Parameter(description = "达人ID") @PathVariable Long expertId,
            @Parameter(description = "状态：0-下线，1-在线，2-忙碌") @RequestParam Integer status) {
        boolean success = expertService.updateExpertStatus(expertId, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }

    @Operation(summary = "批量更新达人状态", description = "批量更新达人在线状态")
    @PutMapping("/batch/status")
    public Result<Void> batchUpdateExpertStatus(
            @RequestBody List<Long> expertIds,
            @Parameter(description = "状态：0-下线，1-在线，2-忙碌") @RequestParam Integer status) {
        if (expertIds == null || expertIds.isEmpty()) {
            return Result.error("请选择要更新的达人");
        }
        boolean success = expertService.batchUpdateExpertStatus(expertIds, status);
        if (success) {
            return Result.success("批量状态更新成功");
        } else {
            return Result.error("批量状态更新失败");
        }
    }

    @Operation(summary = "达人审核", description = "审核达人申请")
    @PutMapping("/audit")
    public Result<Void> auditExpert(@Validated @RequestBody ExpertAuditDTO auditDTO) {
        boolean success = expertService.auditExpert(auditDTO);
        if (success) {
            return Result.success("审核成功");
        } else {
            return Result.error("审核失败");
        }
    }

    @Operation(summary = "批量达人审核", description = "批量审核达人申请")
    @PutMapping("/batch/audit")
    public Result<Void> batchAuditExperts(
            @RequestBody List<Long> expertIds,
            @Parameter(description = "审核状态：1-审核通过，2-审核拒绝") @RequestParam Integer auditStatus,
            @Parameter(description = "审核备注") @RequestParam String auditRemark) {
        if (expertIds == null || expertIds.isEmpty()) {
            return Result.error("请选择要审核的达人");
        }
        boolean success = expertService.batchAuditExperts(expertIds, auditStatus, auditRemark);
        if (success) {
            return Result.success("批量审核成功");
        } else {
            return Result.error("批量审核失败");
        }
    }

    @Operation(summary = "获取热门达人", description = "获取热门达人列表")
    @GetMapping("/hot")
    public Result<List<ExpertDetailVO>> getHotExperts(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<ExpertDetailVO> hotExperts = expertService.getHotExperts(limit);
        return Result.success("获取成功", hotExperts);
    }

    @Operation(summary = "检查用户是否为达人", description = "检查指定用户是否已经是达人")
    @GetMapping("/check/{userId}")
    public Result<Boolean> checkUserIsExpert(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        boolean isExpert = expertService.checkUserIsExpert(userId);
        return Result.success("检查完成", isExpert);
    }

    @Operation(summary = "根据分类获取达人", description = "根据服务分类ID获取达人列表")
    @GetMapping("/category/{categoryId}")
    public Result<List<ExpertDetailVO>> getExpertsByCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        List<Expert> experts = expertService.getByCategoryId(categoryId);
        List<ExpertDetailVO> expertVOs = experts.stream()
                .map(expert -> expertService.getExpertDetail(expert.getId()))
                .filter(vo -> vo != null)
                .toList();
        return Result.success("获取成功", expertVOs);
    }

    @Operation(summary = "设置达人热门状态", description = "设置达人是否为热门达人")
    @PutMapping("/{expertId}/hot")
    public Result<Void> updateExpertHotStatus(
            @Parameter(description = "达人ID") @PathVariable Long expertId,
            @Parameter(description = "是否热门：0-否，1-是") @RequestParam Integer isHot) {
        boolean success = expertService.updateExpertHotStatus(expertId, isHot);
        if (success) {
            return Result.success(isHot == 1 ? "设置为热门达人成功" : "取消热门达人成功");
        } else {
            return Result.error(isHot == 1 ? "设置为热门达人失败" : "取消热门达人失败");
        }
    }

    @Operation(summary = "批量设置达人热门状态", description = "批量设置达人是否为热门达人")
    @PutMapping("/batch/hot")
    public Result<Void> batchUpdateExpertHotStatus(
            @RequestBody List<Long> expertIds,
            @Parameter(description = "是否热门：0-否，1-是") @RequestParam Integer isHot) {
        if (expertIds == null || expertIds.isEmpty()) {
            return Result.error("请选择要设置的达人");
        }
        boolean success = expertService.batchUpdateExpertHotStatus(expertIds, isHot);
        if (success) {
            return Result.success(isHot == 1 ? "批量设置为热门达人成功" : "批量取消热门达人成功");
        } else {
            return Result.error(isHot == 1 ? "批量设置为热门达人失败" : "批量取消热门达人失败");
        }
    }
}
