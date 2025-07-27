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
    @GetMapping("/statistics")
    public Result<UserStatisticsVO> getUserStatistics() {
        log.info("接收到用户统计信息查询请求");
        UserStatisticsVO statistics = userService.getUserStatistics();
        log.info("用户统计信息查询完成: {}", statistics);
        return Result.success("获取成功", statistics);
    }

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

    public Result<PageResult<User>> getUserPage(@Validated UserQueryDTO queryDTO) {
        log.info("接收到用户分页查询请求: {}", queryDTO);
        PageResult<User> pageResult = userService.getUserPage(queryDTO);
        log.info("用户分页查询结果: 总数={}, 当前页数据量={}", pageResult.getTotal(), pageResult.getRecords().size());
        return Result.success("查询成功", pageResult);
    }

    public Result<User> getUserDetail(
        return Result.success("获取成功", user);
    }

    public Result<Void> updateUserStatus(
        boolean success = userService.updateUserStatus(userId, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }

    public Result<Void> batchUpdateUserStatus(@RequestBody BatchUpdateStatusDTO dto) {
        boolean success = userService.batchUpdateUserStatus(dto.getUserIds(), dto.getStatus());
        if (success) {
            return Result.success("批量状态更新成功");
        } else {
            return Result.error("批量状态更新失败");
        }
    }

    public Result<Void> deleteUser(
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    public Result<Void> batchDeleteUsers(@RequestBody BatchDeleteDTO dto) {
        boolean success = userService.batchDeleteUsers(dto.getUserIds());
        if (success) {
            return Result.success("批量删除成功");
        } else {
            return Result.error("批量删除失败");
        }
    }

    public Result<Void> resetUserPassword(
        if (success) {
            return Result.success("密码重置成功");
        } else {
            return Result.error("密码重置失败");
        }
    }
}
