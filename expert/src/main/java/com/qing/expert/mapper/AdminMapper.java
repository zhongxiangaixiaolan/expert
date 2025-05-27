package com.qing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qing.expert.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 管理员Mapper
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * 根据用户名查询管理员
     */
    Admin selectByUsername(@Param("username") String username);

    /**
     * 更新最后登录信息
     */
    int updateLastLogin(@Param("id") Long id, @Param("loginIp") String loginIp);
}
