package com.qing.expert.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.common.result.PageResult;
import com.qing.expert.dto.UserQueryDTO;
import com.qing.expert.dto.user.UserLoginDTO;
import com.qing.expert.dto.user.UserUpdateDTO;
import com.qing.expert.entity.User;
import com.qing.expert.vo.UserStatisticsVO;
import com.qing.expert.vo.user.UserLoginVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 分页查询用户列表
     */
    PageResult<User> getUserPage(UserQueryDTO queryDTO);

    /**
     * 根据openid查询用户
     */
    User getByOpenid(String openid);

    /**
     * 获取用户统计信息
     */
    UserStatisticsVO getUserStatistics();

    /**
     * 更新用户状态
     */
    boolean updateUserStatus(Long userId, Integer status);

    /**
     * 批量更新用户状态
     */
    boolean batchUpdateUserStatus(List<Long> userIds, Integer status);

    /**
     * 重置用户密码
     */
    boolean resetUserPassword(Long userId);

    /**
     * 获取用户详情
     */
    User getUserDetail(Long userId);

    /**
     * 删除用户
     */
    boolean deleteUser(Long userId);

    /**
     * 批量删除用户
     */
    boolean batchDeleteUsers(List<Long> userIds);

    /**
     * 更新用户余额
     */
    boolean updateUserBalance(Long userId, java.math.BigDecimal amount);

    /**
     * 充值用户余额
     */
    boolean rechargeUserBalance(Long userId, java.math.BigDecimal amount, String remark);

    /**
     * 微信登录
     */
    UserLoginVO wechatLogin(UserLoginDTO loginDTO);

    /**
     * 更新用户资料
     */
    boolean updateUserProfile(UserUpdateDTO updateDTO);

    /**
     * 绑定手机号
     */
    boolean bindPhone(UserUpdateDTO updateDTO);

    /**
     * 上传头像
     */
    String uploadAvatar(Long userId, MultipartFile file);
}
