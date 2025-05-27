package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 管理员实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("admins")
@Schema(description = "管理员")
public class Admin extends BaseEntity {

    @Schema(description = "用户名")
    @TableField("username")
    private String username;

    @Schema(description = "密码")
    @TableField("password")
    @JsonIgnore
    private String password;

    @Schema(description = "真实姓名")
    @TableField("real_name")
    private String realName;

    @Schema(description = "手机号")
    @TableField("phone")
    private String phone;

    @Schema(description = "邮箱")
    @TableField("email")
    private String email;

    @Schema(description = "头像URL")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "角色：SUPER_ADMIN,ADMIN")
    @TableField("role")
    private String role;

    @Schema(description = "状态：0-禁用，1-启用")
    @TableField("status")
    private Integer status;

    @Schema(description = "最后登录时间")
    @TableField("last_login_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastLoginTime;

    @Schema(description = "最后登录IP")
    @TableField("last_login_ip")
    private String lastLoginIp;
}
