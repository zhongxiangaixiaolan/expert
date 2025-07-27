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
    @GetMapping("/statistics")
    public Result<ExpertStatisticsVO> getExpertStatistics() {
        ExpertStatisticsVO statistics = expertService.getExpertStatistics();
        return Result.success("获取成功", statistics);
    }

    public Result<PageResult<ExpertDetailVO>> getExpertPage(@Validated ExpertQueryDTO queryDTO) {
        PageResult<ExpertDetailVO> pageResult = expertService.getExpertPage(queryDTO);
        return Result.success("查询成功", pageResult);
    }

    public Result<ExpertDetailVO> getExpertDetail(
        if (expertDetail == null) {
            return Result.error("达人不存在");
        }
        return Result.success("获取成功", expertDetail);
    }

    public Result<Void> saveExpert(@Validated @RequestBody ExpertSaveDTO saveDTO) {
        boolean success = expertService.saveExpert(saveDTO);
        if (success) {
            return Result.success(saveDTO.getId() == null ? "新增成功" : "更新成功");
        } else {
            return Result.error(saveDTO.getId() == null ? "新增失败" : "更新失败");
        }
    }

    public Result<Void> deleteExpert(
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

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

    public Result<Void> updateExpertStatus(
        boolean success = expertService.updateExpertStatus(expertId, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }

    public Result<Void> batchUpdateExpertStatus(
            @RequestBody List<Long> expertIds,
            return Result.error("请选择要更新的达人");
        }
        boolean success = expertService.batchUpdateExpertStatus(expertIds, status);
        if (success) {
            return Result.success("批量状态更新成功");
        } else {
            return Result.error("批量状态更新失败");
        }
    }

    public Result<Void> auditExpert(@Validated @RequestBody ExpertAuditDTO auditDTO) {
        boolean success = expertService.auditExpert(auditDTO);
        if (success) {
            return Result.success("审核成功");
        } else {
            return Result.error("审核失败");
        }
    }

    public Result<Void> batchAuditExperts(
            @RequestBody List<Long> expertIds,
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

    public Result<List<ExpertDetailVO>> getHotExperts(
        return Result.success("获取成功", hotExperts);
    }

    public Result<Boolean> checkUserIsExpert(
        return Result.success("检查完成", isExpert);
    }

    public Result<List<ExpertDetailVO>> getExpertsByCategory(
        List<ExpertDetailVO> expertVOs = experts.stream()
                .map(expert -> expertService.getExpertDetail(expert.getId()))
                .filter(vo -> vo != null)
                .toList();
        return Result.success("获取成功", expertVOs);
    }

    public Result<Void> updateExpertHotStatus(
        boolean success = expertService.updateExpertHotStatus(expertId, isHot);
        if (success) {
            return Result.success(isHot == 1 ? "设置为热门达人成功" : "取消热门达人成功");
        } else {
            return Result.error(isHot == 1 ? "设置为热门达人失败" : "取消热门达人失败");
        }
    }

    public Result<Void> batchUpdateExpertHotStatus(
            @RequestBody List<Long> expertIds,
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
