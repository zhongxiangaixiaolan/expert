package com.qing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.entity.User;
import com.qing.expert.vo.UserStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 分页查询用户列表
     */
    IPage<User> selectUserPage(Page<User> page, @Param("keyword") String keyword, 
                               @Param("status") Integer status, @Param("isExpert") Integer isExpert);

    /**
     * 根据openid查询用户
     */
    User selectByOpenid(@Param("openid") String openid);

    /**
     * 获取用户统计信息
     */
    UserStatisticsVO getUserStatistics();

    /**
     * 获取在线用户数量
     */
    Long getOnlineUserCount(@Param("minutes") Integer minutes);

    /**
     * 获取新增用户数量
     */
    Long getNewUserCount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 批量更新用户状态
     */
    int batchUpdateStatus(@Param("userIds") List<Long> userIds, @Param("status") Integer status);
}
