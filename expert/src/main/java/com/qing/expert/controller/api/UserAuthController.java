package com.qing.expert.controller.api;

import com.qing.expert.common.result.Result;
import com.qing.expert.dto.user.UserLoginDTO;
import com.qing.expert.dto.user.UserUpdateDTO;
import com.qing.expert.entity.User;
import com.qing.expert.service.UserService;
import com.qing.expert.util.SecurityUtil;
import com.qing.expert.vo.user.UserLoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户端认证控制器
 * 注意：由于配置了context-path: /api，这里的路径是相对于context-path的
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
    @PostMapping("/login")
    public Result<UserLoginVO> wechatLogin(@Validated @RequestBody UserLoginDTO loginDTO) {
        try {
            UserLoginVO loginVO = userService.wechatLogin(loginDTO);
            return Result.success("登录成功", loginVO);
        } catch (Exception e) {
            log.error("微信登录失败", e);
            return Result.error("登录失败：" + e.getMessage());
        }
    }

    public Result<User> getUserProfile() {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            User user = userService.getUserDetail(currentUserId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            return Result.success("获取成功", user);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<Void> updateUserProfile(@Validated @RequestBody UserUpdateDTO updateDTO) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            updateDTO.setId(currentUserId);

            boolean success = userService.updateUserProfile(updateDTO);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    public Result<Void> bindPhone(@RequestBody UserUpdateDTO updateDTO) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            updateDTO.setId(currentUserId);

            boolean success = userService.bindPhone(updateDTO);
            if (success) {
                return Result.success("绑定成功");
            } else {
                return Result.error("绑定失败");
            }
        } catch (Exception e) {
            log.error("绑定手机号失败", e);
            return Result.error("绑定失败：" + e.getMessage());
        }
    }

            String avatarUrl = userService.uploadAvatar(currentUserId, file);
            return Result.success("上传成功", avatarUrl);
        } catch (Exception e) {
            log.error("上传头像失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    public Result<Void> logout() {
        try {
            // TODO: 实现登出逻辑（如果需要黑名单机制）
            return Result.success("退出成功");
        } catch (Exception e) {
            log.error("退出登录失败", e);
            return Result.error("退出失败：" + e.getMessage());
        }
    }

    public Result<Boolean> checkLogin() {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            boolean isLogin = currentUserId != null;
            return Result.success("检查成功", isLogin);
        } catch (Exception e) {
            return Result.success("未登录", false);
        }
    }
}
