package com.qing.expert.controller.api;

import com.qing.expert.common.result.Result;
import com.qing.expert.dto.ExpertSaveDTO;
import com.qing.expert.entity.Expert;
import com.qing.expert.service.ExpertService;
import com.qing.expert.vo.ExpertDetailVO;
import com.qing.expert.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 达人端控制器
 * 注意：由于配置了context-path: /api，这里的路径是相对于context-path的
 */
@Slf4j
@RestController
@RequestMapping("/expert")
@RequiredArgsConstructor
@Tag(name = "达人端接口", description = "达人申请、资料管理等接口")
public class ExpertController {

    private final ExpertService expertService;

    @Operation(summary = "申请成为达人", description = "用户申请成为达人")
    @PostMapping("/apply")
    public Result<Void> applyExpert(@Validated @RequestBody ExpertSaveDTO saveDTO) {
        try {
            // 获取当前登录用户ID
            Long currentUserId = SecurityUtil.getCurrentUserId();
            saveDTO.setUserId(currentUserId);

            // 检查用户是否已经是达人
            if (expertService.checkUserIsExpert(currentUserId)) {
                return Result.error("您已经是达人，无需重复申请");
            }

            // 设置默认审核状态为待审核
            saveDTO.setAuditStatus(0);
            saveDTO.setStatus(0); // 默认下线状态

            boolean success = expertService.saveExpert(saveDTO);
            if (success) {
                return Result.success("申请成功，请等待审核");
            } else {
                return Result.error("申请失败");
            }
        } catch (Exception e) {
            log.error("申请成为达人失败", e);
            return Result.error("申请失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取当前达人信息", description = "获取当前登录用户的达人信息")
    @GetMapping("/profile")
    public Result<ExpertDetailVO> getExpertProfile() {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            Expert expert = expertService.getByUserId(currentUserId);

            if (expert == null) {
                return Result.error("您还不是达人");
            }

            ExpertDetailVO expertDetail = expertService.getExpertDetail(expert.getId());
            return Result.success("获取成功", expertDetail);
        } catch (Exception e) {
            log.error("获取达人信息失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "更新达人资料", description = "更新当前达人的资料信息")
    @PutMapping("/profile")
    public Result<Void> updateExpertProfile(@Validated @RequestBody ExpertSaveDTO saveDTO) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            Expert expert = expertService.getByUserId(currentUserId);

            if (expert == null) {
                return Result.error("您还不是达人");
            }

            // 设置达人ID和用户ID
            saveDTO.setId(expert.getId());
            saveDTO.setUserId(currentUserId);

            boolean success = expertService.saveExpert(saveDTO);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新达人资料失败", e);
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @Operation(summary = "检查用户是否为达人", description = "检查当前用户是否已经是达人")
    @GetMapping("/check")
    public Result<Boolean> checkIsExpert() {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            boolean isExpert = expertService.checkUserIsExpert(currentUserId);
            return Result.success("检查成功", isExpert);
        } catch (Exception e) {
            log.error("检查达人状态失败", e);
            return Result.error("检查失败：" + e.getMessage());
        }
    }

    @Operation(summary = "更新达人在线状态", description = "更新达人的在线状态（接单开关）")
    @PutMapping("/status")
    public Result<Void> updateExpertStatus(@Parameter(description = "状态：0-下线，1-在线，2-忙碌") @RequestParam Integer status) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            Expert expert = expertService.getByUserId(currentUserId);

            if (expert == null) {
                return Result.error("您还不是达人");
            }

            // 检查审核状态
            if (expert.getAuditStatus() != 1) {
                return Result.error("您的达人申请还未通过审核，无法修改状态");
            }

            boolean success = expertService.updateExpertStatus(expert.getId(), status);
            if (success) {
                String statusText = status == 1 ? "在线" : (status == 2 ? "忙碌" : "下线");
                return Result.success("状态已更新为：" + statusText);
            } else {
                return Result.error("状态更新失败");
            }
        } catch (Exception e) {
            log.error("更新达人状态失败", e);
            return Result.error("更新失败：" + e.getMessage());
        }
    }
}
