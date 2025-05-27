package com.qing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qing.expert.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统配置Mapper
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    /**
     * 根据配置分组查询配置列表
     */
    List<SystemConfig> selectByGroup(@Param("configGroup") String configGroup);

    /**
     * 根据配置键查询配置
     */
    SystemConfig selectByKey(@Param("configKey") String configKey);

    /**
     * 批量更新配置
     */
    int batchUpdate(@Param("configs") List<SystemConfig> configs);
}
