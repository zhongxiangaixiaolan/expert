package com.qing.expert.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qing.expert.common.enums.AdminRoleEnum;
import com.qing.expert.common.enums.StatusEnum;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.dto.AdminLoginDTO;
import com.qing.expert.entity.Admin;
import com.qing.expert.mapper.AdminMapper;
import com.qing.expert.service.AdminService;
import com.qing.expert.util.JwtUtil;
import com.qing.expert.vo.AdminLoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 管理员服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AdminLoginVO login(AdminLoginDTO loginDTO, String loginIp) {
        // 参数校验
        if (loginDTO == null || StrUtil.isBlank(loginDTO.getUsername()) || StrUtil.isBlank(loginDTO.getPassword())) {
            throw new BusinessException("用户名或密码不能为空");
        }

        // 查询管理员
        Admin admin = getByUsername(loginDTO.getUsername());
        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查账户状态
        if (!StatusEnum.isEnable(admin.getStatus())) {
            throw new BusinessException("账户已被禁用，请联系管理员");
        }

        // 验证密码
        if (!validatePassword(loginDTO.getPassword(), admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 生成JWT令牌
        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), admin.getRole());

        // 更新最后登录信息
        updateLastLogin(admin.getId(), loginIp);

        // 构建响应
        AdminLoginVO loginVO = new AdminLoginVO();
        loginVO.setAccessToken(token);
        loginVO.setExpiresIn(jwtUtil.getExpirationSeconds());

        // 构建管理员信息
        AdminLoginVO.AdminInfoVO adminInfo = new AdminLoginVO.AdminInfoVO();
        adminInfo.setId(admin.getId());
        adminInfo.setUsername(admin.getUsername());
        adminInfo.setRealName(admin.getRealName());
        adminInfo.setAvatar(admin.getAvatar());
        adminInfo.setRole(admin.getRole());
        adminInfo.setRoleName(getRoleName(admin.getRole()));
        loginVO.setAdminInfo(adminInfo);

        log.info("管理员登录成功：{}", admin.getUsername());
        return loginVO;
    }

    @Override
    public Admin getByUsername(String username) {
        if (StrUtil.isBlank(username)) {
            return null;
        }
        return baseMapper.selectByUsername(username);
    }

    @Override
    public boolean updateLastLogin(Long adminId, String loginIp) {
        if (adminId == null) {
            return false;
        }
        try {
            return baseMapper.updateLastLogin(adminId, loginIp) > 0;
        } catch (Exception e) {
            log.error("更新管理员最后登录信息失败：adminId={}, loginIp={}", adminId, loginIp, e);
            return false;
        }
    }

    @Override
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        if (StrUtil.isBlank(rawPassword) || StrUtil.isBlank(encodedPassword)) {
            return false;
        }
        try {
            return passwordEncoder.matches(rawPassword, encodedPassword);
        } catch (Exception e) {
            log.error("密码验证失败", e);
            return false;
        }
    }

    @Override
    public String encodePassword(String rawPassword) {
        if (StrUtil.isBlank(rawPassword)) {
            throw new BusinessException("密码不能为空");
        }
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 获取角色名称
     */
    private String getRoleName(String role) {
        AdminRoleEnum roleEnum = AdminRoleEnum.getByCode(role);
        return roleEnum != null ? roleEnum.getDesc() : role;
    }
}
