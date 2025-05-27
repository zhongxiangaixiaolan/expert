package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统配置实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("system_configs")
@Schema(description = "系统配置")
public class SystemConfig {

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "配置键")
    @TableField("config_key")
    private String configKey;

    @Schema(description = "配置值")
    @TableField("config_value")
    private String configValue;

    @Schema(description = "配置类型：STRING,NUMBER,BOOLEAN,JSON")
    @TableField("config_type")
    private String configType;

    @Schema(description = "配置分组：WECHAT,OSS,SYSTEM,PAY,BUSINESS")
    @TableField("config_group")
    private String configGroup;

    @Schema(description = "配置描述")
    @TableField("description")
    private String description;

    @Schema(description = "是否加密存储：0-否，1-是")
    @TableField("is_encrypted")
    private Integer isEncrypted;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
