package com.qing.expert.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qing.expert.common.enums.StatusEnum;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.common.result.PageResult;
import com.qing.expert.dto.UserQueryDTO;
import com.qing.expert.dto.user.UserLoginDTO;
import com.qing.expert.dto.user.UserUpdateDTO;
import com.qing.expert.entity.User;
import com.qing.expert.mapper.UserMapper;
import com.qing.expert.service.UserService;
import com.qing.expert.service.WechatApiService;
import com.qing.expert.util.FileUploadUtil;
import com.qing.expert.util.JwtUtil;
import com.qing.expert.vo.UserStatisticsVO;
import com.qing.expert.vo.user.UserLoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final FileUploadUtil fileUploadUtil;
    private final JwtUtil jwtUtil;
    private final WechatApiService wechatApiService;

    @Override
    public PageResult<User> getUserPage(UserQueryDTO queryDTO) {
        Page<User> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());

        IPage<User> result = baseMapper.selectUserPage(page,
                queryDTO.getKeyword(),
                queryDTO.getStatus(),
                queryDTO.getIsExpert());

        return PageResult.of(result);
    }

    @Override
    public User getByOpenid(String openid) {
        if (StrUtil.isBlank(openid)) {
            return null;
        }
        return baseMapper.selectByOpenid(openid);
    }

    @Override
    public UserStatisticsVO getUserStatistics() {
        try {
            return baseMapper.getUserStatistics();
        } catch (Exception e) {
            log.error("获取用户统计信息失败", e);
            throw new BusinessException("获取用户统计信息失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserStatus(Long userId, Integer status) {
        if (userId == null || status == null) {
            throw new BusinessException("参数不能为空");
        }

        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setStatus(status);
        boolean success = updateById(user);

        if (success) {
            log.info("更新用户状态成功：userId={}, status={}", userId, status);
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateUserStatus(List<Long> userIds, Integer status) {
        if (userIds == null || userIds.isEmpty() || status == null) {
            throw new BusinessException("参数不能为空");
        }

        try {
            int count = baseMapper.batchUpdateStatus(userIds, status);
            log.info("批量更新用户状态成功：count={}, status={}", count, status);
            return count > 0;
        } catch (Exception e) {
            log.error("批量更新用户状态失败", e);
            throw new BusinessException("批量更新用户状态失败");
        }
    }

    @Override
    public boolean resetUserPassword(Long userId) {
        // TODO: 实现重置用户密码逻辑
        // 微信小程序用户通常不需要密码重置功能
        throw new BusinessException("微信小程序用户无需重置密码");
    }

    @Override
    public User getUserDetail(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查用户是否有未完成的订单
        // TODO: 添加订单检查逻辑

        boolean success = removeById(userId);

        if (success) {
            log.info("删除用户成功：userId={}", userId);
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new BusinessException("用户ID列表不能为空");
        }

        // 检查用户是否存在未完成的订单
        // TODO: 添加批量订单检查逻辑

        boolean success = removeByIds(userIds);

        if (success) {
            log.info("批量删除用户成功：count={}", userIds.size());
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserBalance(Long userId, java.math.BigDecimal amount) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (amount == null) {
            throw new BusinessException("金额不能为空");
        }

        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 计算新余额
        java.math.BigDecimal newBalance = user.getBalance().add(amount);
        if (newBalance.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new BusinessException("余额不足");
        }

        // 更新用户余额
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, userId)
                .set(User::getBalance, newBalance)
                .set(User::getUpdateTime, LocalDateTime.now());

        boolean success = update(updateWrapper);

        if (success) {
            log.info("更新用户余额成功：userId={}, amount={}, newBalance={}", userId, amount, newBalance);
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rechargeUserBalance(Long userId, java.math.BigDecimal amount, String remark) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (amount == null || amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }

        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新用户余额
        boolean success = updateUserBalance(userId, amount);

        if (success) {
            log.info("用户充值成功：userId={}, amount={}, remark={}", userId, amount, remark);
            // TODO: 记录充值流水
        }

        return success;
    }

    @Override
    public UserLoginVO wechatLogin(UserLoginDTO loginDTO) {
        if (loginDTO == null || StrUtil.isBlank(loginDTO.getCode())) {
            throw new BusinessException("授权码不能为空");
        }

        try {
            // 1. 通过code获取微信用户的openid和session_key
            WechatApiService.WechatUserInfo wechatUserInfo = wechatApiService.getWechatUserInfo(loginDTO.getCode());
            String openid = wechatUserInfo.getOpenid();
            String sessionKey = wechatUserInfo.getSessionKey();

            log.info("获取微信用户信息成功：openid={}", openid);

            // 2. 尝试解密用户详细信息（如果提供了加密数据）
            WechatApiService.WechatUserDetail wechatUserDetail = null;
            if (StrUtil.isNotBlank(loginDTO.getEncryptedData()) && StrUtil.isNotBlank(loginDTO.getIv())) {
                wechatUserDetail = wechatApiService.decryptUserInfo(sessionKey, loginDTO.getEncryptedData(),
                        loginDTO.getIv());
            }

            // 3. 查找或创建用户
            User user = getByOpenid(openid);
            boolean isNewUser = false;

            if (user == null) {
                // 创建新用户
                user = new User();
                user.setOpenid(openid);
                user.setUnionid(wechatUserInfo.getUnionid());

                // 优先使用解密后的用户信息，其次使用前端传递的信息
                if (wechatUserDetail != null) {
                    user.setNickname(StrUtil.isNotBlank(wechatUserDetail.getNickName()) ? wechatUserDetail.getNickName()
                            : "微信用户");
                    user.setAvatar(wechatUserDetail.getAvatarUrl());
                    user.setGender(wechatUserDetail.getGender());
                } else {
                    user.setNickname(StrUtil.isNotBlank(loginDTO.getNickname()) ? loginDTO.getNickname() : "微信用户");
                    user.setAvatar(loginDTO.getAvatar());
                    user.setGender(loginDTO.getGender());
                }

                user.setStatus(StatusEnum.ENABLE.getCode());
                user.setBalance(java.math.BigDecimal.ZERO);
                user.setTotalRecharge(java.math.BigDecimal.ZERO);
                user.setTotalConsume(java.math.BigDecimal.ZERO);
                user.setIsExpert(0);
                user.setRegisterTime(LocalDateTime.now());
                user.setLastLoginTime(LocalDateTime.now());

                save(user);
                isNewUser = true;
                log.info("创建新用户成功：openid={}, nickname={}", openid, user.getNickname());
            } else {
                // 更新登录时间和用户信息（如果提供了新的信息）
                user.setLastLoginTime(LocalDateTime.now());

                // 优先使用解密后的用户信息更新
                if (wechatUserDetail != null) {
                    if (StrUtil.isNotBlank(wechatUserDetail.getNickName())) {
                        user.setNickname(wechatUserDetail.getNickName());
                    }
                    if (StrUtil.isNotBlank(wechatUserDetail.getAvatarUrl())) {
                        user.setAvatar(wechatUserDetail.getAvatarUrl());
                    }
                    if (wechatUserDetail.getGender() != null) {
                        user.setGender(wechatUserDetail.getGender());
                    }
                } else {
                    // 使用前端传递的信息更新
                    if (StrUtil.isNotBlank(loginDTO.getNickname())) {
                        user.setNickname(loginDTO.getNickname());
                    }
                    if (StrUtil.isNotBlank(loginDTO.getAvatar())) {
                        user.setAvatar(loginDTO.getAvatar());
                    }
                    if (loginDTO.getGender() != null) {
                        user.setGender(loginDTO.getGender());
                    }
                }

                updateById(user);
                log.info("用户登录成功：userId={}, openid={}", user.getId(), openid);
            }

            // 生成JWT token
            String token = jwtUtil.generateToken(user.getId(), user.getOpenid(), "USER");

            return new UserLoginVO(token, user, isNewUser);

        } catch (Exception e) {
            log.error("微信登录失败", e);
            throw new BusinessException("登录失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserProfile(UserUpdateDTO updateDTO) {
        if (updateDTO == null || updateDTO.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }

        User user = getById(updateDTO.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新用户信息
        if (StrUtil.isNotBlank(updateDTO.getNickname())) {
            user.setNickname(updateDTO.getNickname());
        }
        if (StrUtil.isNotBlank(updateDTO.getAvatar())) {
            user.setAvatar(updateDTO.getAvatar());
        }
        if (updateDTO.getGender() != null) {
            user.setGender(updateDTO.getGender());
        }
        if (StrUtil.isNotBlank(updateDTO.getRealName())) {
            user.setRealName(updateDTO.getRealName());
        }

        boolean success = updateById(user);
        if (success) {
            log.info("更新用户资料成功：userId={}", updateDTO.getId());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindPhone(UserUpdateDTO updateDTO) {
        if (updateDTO == null || updateDTO.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (StrUtil.isBlank(updateDTO.getPhone())) {
            throw new BusinessException("手机号不能为空");
        }

        User user = getById(updateDTO.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查手机号是否已被其他用户绑定
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, updateDTO.getPhone())
                .ne(User::getId, updateDTO.getId())
                .eq(User::getDeleted, 0);

        User existUser = getOne(wrapper);
        if (existUser != null) {
            throw new BusinessException("该手机号已被其他用户绑定");
        }

        // TODO: 验证微信加密数据中的手机号
        // 这里需要根据实际的微信小程序配置来实现手机号解密验证

        user.setPhone(updateDTO.getPhone());
        boolean success = updateById(user);

        if (success) {
            log.info("绑定手机号成功：userId={}, phone={}", updateDTO.getId(), updateDTO.getPhone());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadAvatar(Long userId, MultipartFile file) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException("头像文件不能为空");
        }

        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        try {
            // 上传头像文件
            String avatarPath = fileUploadUtil.uploadAvatar(file);

            // 删除旧头像（如果存在）
            if (StrUtil.isNotBlank(user.getAvatar())) {
                fileUploadUtil.deleteFile(user.getAvatar());
            }

            // 更新用户头像
            user.setAvatar(avatarPath);
            boolean success = updateById(user);

            if (success) {
                log.info("上传头像成功：userId={}, avatarPath={}", userId, avatarPath);
                return fileUploadUtil.getFileUrl(avatarPath);
            } else {
                // 如果更新失败，删除已上传的文件
                fileUploadUtil.deleteFile(avatarPath);
                throw new BusinessException("更新用户头像失败");
            }

        } catch (Exception e) {
            log.error("上传头像失败：userId={}", userId, e);
            throw new BusinessException("上传头像失败：" + e.getMessage());
        }
    }
}
