package com.qing.expert.controller.admin;

import com.qing.expert.common.result.Result;
import com.qing.expert.dto.AdminLoginDTO;
import com.qing.expert.service.AdminService;
import com.qing.expert.util.IpUtil;
import com.qing.expert.vo.AdminLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 管理员认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
@Tag(name = "管理员认证", description = "管理员登录、登出等认证相关接口")
public class AdminAuthController {

    private final AdminService adminService;

    @Operation(summary = "管理员登录", description = "管理员用户名密码登录")
    @PostMapping("/login")
    public Result<AdminLoginVO> login(@Validated @RequestBody AdminLoginDTO loginDTO,
            HttpServletRequest request) {
        String loginIp = IpUtil.getClientIp(request);
        AdminLoginVO loginVO = adminService.login(loginDTO, loginIp);
        return Result.success("登录成功", loginVO);
    }

    @Operation(summary = "管理员登出", description = "管理员登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // TODO: 实现登出逻辑（如果需要黑名单机制）
        return Result.success("登出成功");
    }

    @Operation(summary = "生成密码", description = "生成BCrypt加密密码（仅用于开发测试）")
    @GetMapping("/encode-password")
    public Result<String> encodePassword(@RequestParam String password) {
        String encodedPassword = adminService.encodePassword(password);
        log.info("原始密码：{}，加密后：{}", password, encodedPassword);
        return Result.success("密码加密成功", encodedPassword);
    }

    @Operation(summary = "获取当前管理员信息", description = "获取当前登录管理员的基本信息")
    @GetMapping("/info")
    public Result<AdminLoginVO.AdminInfoVO> getCurrentAdminInfo() {
        // TODO: 从JWT中获取当前管理员信息
        return Result.success("获取成功");
    }
}
