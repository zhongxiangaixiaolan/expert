package com.qing.expert.controller.admin;

import com.qing.expert.common.result.PageResult;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.UserQueryDTO;
import com.qing.expert.entity.User;
import com.qing.expert.service.UserService;
import com.qing.expert.vo.UserStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户信息的增删改查接口")
public class UserManageController {

    private final UserService userService;

    @Operation(summary = "获取用户统计信息", description = "获取用户总数、在线数等统计信息")
    @GetMapping("/statistics")
    public Result<UserStatisticsVO> getUserStatistics() {
        UserStatisticsVO statistics = userService.getUserStatistics();
        return Result.success("获取成功", statistics);
    }

    @Operation(summary = "分页查询用户列表", description = "根据条件分页查询用户列表")
    @GetMapping("/page")
    public Result<PageResult<User>> getUserPage(@Validated UserQueryDTO queryDTO) {
        PageResult<User> pageResult = userService.getUserPage(queryDTO);
        return Result.success("查询成功", pageResult);
    }

    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    @GetMapping("/{userId}")
    public Result<User> getUserDetail(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        User user = userService.getUserDetail(userId);
        return Result.success("获取成功", user);
    }

    @Operation(summary = "更新用户状态", description = "启用或禁用用户")
    @PutMapping("/{userId}/status")
    public Result<Void> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "状态：0-禁用，1-启用") @RequestParam Integer status) {
        boolean success = userService.updateUserStatus(userId, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }

    @Operation(summary = "批量更新用户状态", description = "批量启用或禁用用户")
    @PutMapping("/batch/status")
    public Result<Void> batchUpdateUserStatus(
            @Parameter(description = "用户ID列表") @RequestBody List<Long> userIds,
            @Parameter(description = "状态：0-禁用，1-启用") @RequestParam Integer status) {
        boolean success = userService.batchUpdateUserStatus(userIds, status);
        if (success) {
            return Result.success("批量状态更新成功");
        } else {
            return Result.error("批量状态更新失败");
        }
    }

    @Operation(summary = "删除用户", description = "删除指定用户")
    @DeleteMapping("/{userId}")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        boolean success = userService.deleteUser(userId);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @Operation(summary = "批量删除用户", description = "批量删除用户")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteUsers(
            @Parameter(description = "用户ID列表") @RequestBody List<Long> userIds) {
        boolean success = userService.batchDeleteUsers(userIds);
        if (success) {
            return Result.success("批量删除成功");
        } else {
            return Result.error("批量删除失败");
        }
    }

    @Operation(summary = "重置用户密码", description = "重置用户密码为默认密码")
    @PutMapping("/{userId}/reset-password")
    public Result<Void> resetUserPassword(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        boolean success = userService.resetUserPassword(userId);
        if (success) {
            return Result.success("密码重置成功");
        } else {
            return Result.error("密码重置失败");
        }
    }
}
