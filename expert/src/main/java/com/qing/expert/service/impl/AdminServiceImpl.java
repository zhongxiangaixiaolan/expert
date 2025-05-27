package com.qing.expert.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qing.expert.common.enums.AdminRoleEnum;
import com.qing.expert.common.enums.StatusEnum;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.dto.AdminLoginDTO;
import com.qing.expert.dto.AdminPasswordUpdateDTO;
import com.qing.expert.dto.AdminProfileUpdateDTO;
import com.qing.expert.entity.Admin;
import com.qing.expert.mapper.AdminMapper;
import com.qing.expert.service.AdminService;
import com.qing.expert.util.FileUploadUtil;
import com.qing.expert.util.JwtUtil;
import com.qing.expert.vo.AdminLoginVO;
import com.qing.expert.vo.AdminProfileVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理员服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final FileUploadUtil fileUploadUtil;

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

    @Override
    public AdminProfileVO getCurrentAdminProfile(Long adminId) {
        if (adminId == null) {
            throw new BusinessException("管理员ID不能为空");
        }

        Admin admin = getById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }

        AdminProfileVO profileVO = new AdminProfileVO();
        BeanUtils.copyProperties(admin, profileVO);
        profileVO.setRoleName(getRoleName(admin.getRole()));

        return profileVO;
    }

    @Override
    public boolean updateAdminProfile(Long adminId, AdminProfileUpdateDTO updateDTO) {
        if (adminId == null) {
            throw new BusinessException("管理员ID不能为空");
        }

        Admin admin = getById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }

        // 更新信息
        if (StrUtil.isNotBlank(updateDTO.getRealName())) {
            admin.setRealName(updateDTO.getRealName());
        }
        if (StrUtil.isNotBlank(updateDTO.getPhone())) {
            admin.setPhone(updateDTO.getPhone());
        }
        if (StrUtil.isNotBlank(updateDTO.getEmail())) {
            admin.setEmail(updateDTO.getEmail());
        }
        if (StrUtil.isNotBlank(updateDTO.getAvatar())) {
            admin.setAvatar(updateDTO.getAvatar());
        }

        boolean success = updateById(admin);
        if (success) {
            log.info("管理员信息更新成功：adminId={}", adminId);
        } else {
            log.error("管理员信息更新失败：adminId={}", adminId);
        }
        return success;
    }

    @Override
    public boolean updateAdminPassword(Long adminId, AdminPasswordUpdateDTO passwordDTO) {
        if (adminId == null) {
            throw new BusinessException("管理员ID不能为空");
        }

        // 验证新密码和确认密码是否一致
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            throw new BusinessException("新密码和确认密码不一致");
        }

        Admin admin = getById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }

        // 验证原密码
        if (!validatePassword(passwordDTO.getOldPassword(), admin.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 更新密码
        String encodedNewPassword = encodePassword(passwordDTO.getNewPassword());
        admin.setPassword(encodedNewPassword);

        boolean success = updateById(admin);
        if (success) {
            log.info("管理员密码修改成功：adminId={}", adminId);
        } else {
            log.error("管理员密码修改失败：adminId={}", adminId);
        }
        return success;
    }

    @Override
    public String uploadAdminAvatar(Long adminId, MultipartFile file) {
        if (adminId == null) {
            throw new BusinessException("管理员ID不能为空");
        }

        Admin admin = getById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }

        try {
            // 上传头像文件（只返回文件名）
            String avatarFileName = fileUploadUtil.uploadAvatar(file);

            // 删除旧头像（如果存在）
            if (StrUtil.isNotBlank(admin.getAvatar())) {
                fileUploadUtil.deleteAvatarFile(admin.getAvatar());
            }

            // 更新管理员头像（只存储文件名）
            admin.setAvatar(avatarFileName);
            boolean success = updateById(admin);

            if (success) {
                log.info("管理员头像上传成功：adminId={}, avatarFileName={}", adminId, avatarFileName);
                return avatarFileName;
            } else {
                // 如果更新失败，删除已上传的文件
                fileUploadUtil.deleteAvatarFile(avatarFileName);
                throw new BusinessException("更新管理员头像失败");
            }

        } catch (Exception e) {
            log.error("管理员头像上传失败：adminId={}", adminId, e);
            throw new BusinessException("头像上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取角色名称
     */
    private String getRoleName(String role) {
        AdminRoleEnum roleEnum = AdminRoleEnum.getByCode(role);
        return roleEnum != null ? roleEnum.getDesc() : role;
    }
}
