package com.qing.expert.controller.admin;

import com.qing.expert.common.result.Result;
import com.qing.expert.dto.AdminPasswordUpdateDTO;
import com.qing.expert.dto.AdminProfileUpdateDTO;
import com.qing.expert.service.AdminService;
import com.qing.expert.util.SecurityUtil;
import com.qing.expert.vo.AdminProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员个人中心控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/profile")
@RequiredArgsConstructor
@Tag(name = "管理员个人中心", description = "管理员个人信息管理相关接口")
public class AdminProfileController {

    private final AdminService adminService;

    @Operation(summary = "获取个人信息", description = "获取当前管理员的个人信息")
    @GetMapping
    public Result<AdminProfileVO> getProfile() {
        try {
            Long currentAdminId = SecurityUtil.getCurrentUserId();
            if (currentAdminId == null) {
                return Result.error("未找到当前登录管理员信息");
            }

            AdminProfileVO profile = adminService.getCurrentAdminProfile(currentAdminId);
            return Result.success("获取成功", profile);
        } catch (Exception e) {
            log.error("获取管理员个人信息失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "更新个人信息", description = "更新管理员个人信息")
    @PutMapping
    public Result<Void> updateProfile(@Validated @RequestBody AdminProfileUpdateDTO updateDTO) {
        try {
            Long currentAdminId = SecurityUtil.getCurrentUserId();
            if (currentAdminId == null) {
                return Result.error("未找到当前登录管理员信息");
            }

            boolean success = adminService.updateAdminProfile(currentAdminId, updateDTO);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新管理员个人信息失败", e);
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @Operation(summary = "修改密码", description = "修改管理员登录密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(@Validated @RequestBody AdminPasswordUpdateDTO passwordDTO) {
        try {
            Long currentAdminId = SecurityUtil.getCurrentUserId();
            if (currentAdminId == null) {
                return Result.error("未找到当前登录管理员信息");
            }

            boolean success = adminService.updateAdminPassword(currentAdminId, passwordDTO);
            if (success) {
                return Result.success("密码修改成功");
            } else {
                return Result.error("密码修改失败");
            }
        } catch (Exception e) {
            log.error("修改管理员密码失败", e);
            return Result.error("密码修改失败：" + e.getMessage());
        }
    }

    @Operation(summary = "上传头像", description = "上传管理员头像")
    @PostMapping("/avatar")
    public Result<Map<String, Object>> uploadAvatar(
            @Parameter(description = "头像文件") @RequestParam("file") MultipartFile file) {
        try {
            Long currentAdminId = SecurityUtil.getCurrentUserId();
            if (currentAdminId == null) {
                return Result.error("未找到当前登录管理员信息");
            }

            String avatarPath = adminService.uploadAdminAvatar(currentAdminId, file);

            Map<String, Object> result = new HashMap<>();
            result.put("url", avatarPath);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());

            return Result.success("头像上传成功", result);
        } catch (Exception e) {
            log.error("上传管理员头像失败", e);
            return Result.error("头像上传失败：" + e.getMessage());
        }
    }
}
