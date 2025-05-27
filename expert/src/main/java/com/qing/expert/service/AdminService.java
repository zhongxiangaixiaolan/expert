package com.qing.expert.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.dto.AdminLoginDTO;
import com.qing.expert.entity.Admin;
import com.qing.expert.vo.AdminLoginVO;

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
}
