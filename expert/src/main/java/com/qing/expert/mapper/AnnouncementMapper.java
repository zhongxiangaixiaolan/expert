package com.qing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qing.expert.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通告Mapper接口
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}
