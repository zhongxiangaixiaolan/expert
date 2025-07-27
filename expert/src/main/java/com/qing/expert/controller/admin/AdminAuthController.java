package com.qing.expert.controller.admin;

import com.qing.expert.common.result.Result;
import com.qing.expert.dto.AdminLoginDTO;
import com.qing.expert.entity.Admin;
import com.qing.expert.service.AdminService;
import com.qing.expert.util.IpUtil;
import com.qing.expert.util.SecurityUtil;
import com.qing.expert.vo.AdminLoginVO;
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
public class AdminAuthController {

    private final AdminService adminService;

    @PostMapping("/login")
    public Result<AdminLoginVO> login(@Validated @RequestBody AdminLoginDTO loginDTO,
            HttpServletRequest request) {
        String loginIp = IpUtil.getClientIp(request);
        AdminLoginVO loginVO = adminService.login(loginDTO, loginIp);
        return Result.success("登录成功", loginVO);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        // TODO: 实现登出逻辑（如果需要黑名单机制）
        return Result.success("登出成功");
    }

    @PostMapping("/encode-password")
    public Result<String> encodePassword(@RequestParam String password) {
        String encodedPassword = adminService.encodePassword(password);
        log.info("原始密码：{}，加密后：{}", password, encodedPassword);
        return Result.success("密码加密成功", encodedPassword);
    }

    @GetMapping("/current")
    public Result<AdminLoginVO.AdminInfoVO> getCurrentAdminInfo() {
        try {
            // 从JWT中获取当前管理员ID
            Long currentAdminId = SecurityUtil.getCurrentUserId();
            if (currentAdminId == null) {
                return Result.error("未找到当前登录管理员信息");
            }

            // 获取管理员详细信息
            Admin admin = adminService.getById(currentAdminId);
            if (admin == null) {
                return Result.error("管理员不存在");
            }

            // 构建响应数据
            AdminLoginVO.AdminInfoVO adminInfo = new AdminLoginVO.AdminInfoVO();
            adminInfo.setId(admin.getId());
            adminInfo.setUsername(admin.getUsername());
            adminInfo.setRealName(admin.getRealName());
            adminInfo.setAvatar(admin.getAvatar());
            adminInfo.setRole(admin.getRole());
            adminInfo.setRoleName(getAdminRoleName(admin.getRole()));

            return Result.success("获取成功", adminInfo);
        } catch (Exception e) {
            log.error("获取当前管理员信息失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取管理员角色名称
     */
    private String getAdminRoleName(String role) {
        // 这里可以根据角色代码返回对应的角色名称
        switch (role) {
            case "SUPER_ADMIN":
                return "超级管理员";
            case "ADMIN":
                return "管理员";
            default:
                return role;
        }
    }
}
