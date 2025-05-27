package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 收藏实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("favorites")
public class Favorite {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 收藏类型：service-服务，expert-达人
     */
    @TableField("favorite_type")
    private String favoriteType;

    /**
     * 目标ID（服务ID或达人ID）
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
