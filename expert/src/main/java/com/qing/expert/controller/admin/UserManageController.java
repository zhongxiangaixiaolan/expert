package com.qing.expert.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qing.expert.common.result.PageResult;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.BatchDeleteDTO;
import com.qing.expert.dto.BatchUpdateStatusDTO;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        log.info("接收到用户统计信息查询请求");
        UserStatisticsVO statistics = userService.getUserStatistics();
        log.info("用户统计信息查询完成: {}", statistics);
        return Result.success("获取成功", statistics);
    }

    @Operation(summary = "测试用户数据", description = "测试用户数据是否正常")
    @GetMapping("/test")
    public Result<Object> testUserData() {
        try {
            log.info("开始测试用户数据");

            // 直接查询用户总数
            long totalCount = userService.count();
            log.info("用户总数: {}", totalCount);

            // 查询前5个用户
            List<User> users = userService.list(new LambdaQueryWrapper<User>()
                    .eq(User::getDeleted, 0)
                    .last("LIMIT 5"));
            log.info("前5个用户: {}", users);

            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", totalCount);
            result.put("sampleUsers", users);

            return Result.success("测试成功", result);
        } catch (Exception e) {
            log.error("测试用户数据失败", e);
            return Result.error("测试失败: " + e.getMessage());
        }
    }

    @Operation(summary = "分页查询用户列表", description = "根据条件分页查询用户列表")
    @GetMapping("/page")
    public Result<PageResult<User>> getUserPage(@Validated UserQueryDTO queryDTO) {
        log.info("接收到用户分页查询请求: {}", queryDTO);
        PageResult<User> pageResult = userService.getUserPage(queryDTO);
        log.info("用户分页查询结果: 总数={}, 当前页数据量={}", pageResult.getTotal(), pageResult.getRecords().size());
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
    public Result<Void> batchUpdateUserStatus(@RequestBody BatchUpdateStatusDTO dto) {
        boolean success = userService.batchUpdateUserStatus(dto.getUserIds(), dto.getStatus());
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
    public Result<Void> batchDeleteUsers(@RequestBody BatchDeleteDTO dto) {
        boolean success = userService.batchDeleteUsers(dto.getUserIds());
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
