package com.qing.expert.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.dto.AdminLoginDTO;
import com.qing.expert.dto.AdminPasswordUpdateDTO;
import com.qing.expert.dto.AdminProfileUpdateDTO;
import com.qing.expert.entity.Admin;
import com.qing.expert.vo.AdminLoginVO;
import com.qing.expert.vo.AdminProfileVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理员服务接口
 */
public interface AdminService extends IService<Admin> {

    /**
     * 管理员登录
     */
    AdminLoginVO login(AdminLoginDTO loginDTO, String loginIp);

    /**
     * 根据用户名查询管理员
     */
    Admin getByUsername(String username);

    /**
     * 更新最后登录信息
     */
    boolean updateLastLogin(Long adminId, String loginIp);

    /**
     * 验证密码
     */
    boolean validatePassword(String rawPassword, String encodedPassword);

    /**
     * 加密密码
     */
    String encodePassword(String rawPassword);

    /**
     * 获取当前管理员信息
     */
    AdminProfileVO getCurrentAdminProfile(Long adminId);

    /**
     * 更新管理员个人信息
     */
    boolean updateAdminProfile(Long adminId, AdminProfileUpdateDTO updateDTO);

    /**
     * 修改管理员密码
     */
    boolean updateAdminPassword(Long adminId, AdminPasswordUpdateDTO passwordDTO);

    /**
     * 上传管理员头像
     */
    String uploadAdminAvatar(Long adminId, MultipartFile file);
}
