package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("users")
@Schema(description = "用户")
public class User extends BaseEntity {

    @Schema(description = "微信openid")
    @TableField("openid")
    private String openid;

    @Schema(description = "微信unionid")
    @TableField("unionid")
    private String unionid;

    @Schema(description = "昵称")
    @TableField("nickname")
    private String nickname;

    @Schema(description = "头像URL")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "性别：0-未知，1-男，2-女")
    @TableField("gender")
    private Integer gender;

    @Schema(description = "手机号")
    @TableField("phone")
    private String phone;

    @Schema(description = "真实姓名")
    @TableField("real_name")
    private String realName;

    @Schema(description = "账户余额")
    @TableField("balance")
    private BigDecimal balance;

    @Schema(description = "累计充值金额")
    @TableField("total_recharge")
    private BigDecimal totalRecharge;

    @Schema(description = "累计消费金额")
    @TableField("total_consume")
    private BigDecimal totalConsume;

    @Schema(description = "状态：0-禁用，1-正常")
    @TableField("status")
    private Integer status;

    @Schema(description = "是否为达人：0-否，1-是")
    @TableField("is_expert")
    private Integer isExpert;

    @Schema(description = "注册时间")
    @TableField("register_time")
    private LocalDateTime registerTime;

    @Schema(description = "最后登录时间")
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
}
