package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("withdraw_records")
@Schema(description = "提现记录")
public class WithdrawRecord extends BaseEntity {

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "提现订单号")
    @TableField("order_no")
    private String orderNo;

    @Schema(description = "提现金额")
    @TableField("amount")
    private BigDecimal amount;

    @Schema(description = "手续费")
    @TableField("fee")
    private BigDecimal fee;

    @Schema(description = "实际到账金额")
    @TableField("real_amount")
    private BigDecimal realAmount;

    @Schema(description = "银行名称")
    @TableField("bank_name")
    private String bankName;

    @Schema(description = "银行账号")
    @TableField("bank_account")
    private String bankAccount;

    @Schema(description = "账户姓名")
    @TableField("account_name")
    private String accountName;

    @Schema(description = "状态：0-待审核，1-审核通过，2-审核拒绝，3-已转账")
    @TableField("status")
    private Integer status;

    @Schema(description = "申请时间")
    @TableField("apply_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime applyTime;

    @Schema(description = "审核备注")
    @TableField("audit_remark")
    private String auditRemark;

    @Schema(description = "审核时间")
    @TableField("audit_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime auditTime;

    @Schema(description = "转账时间")
    @TableField("transfer_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime transferTime;

    @Schema(description = "转账备注")
    @TableField("transfer_remark")
    private String transferRemark;
}
