package com.qing.expert.vo.withdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录VO
 */
@Data
@Schema(description = "提现记录VO")
public class WithdrawRecordVO {

    @Schema(description = "提现记录ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "提现订单号")
    private String orderNo;

    @Schema(description = "提现金额")
    private BigDecimal amount;

    @Schema(description = "手续费")
    private BigDecimal fee;

    @Schema(description = "实际到账金额")
    private BigDecimal realAmount;

    @Schema(description = "银行名称")
    private String bankName;

    @Schema(description = "银行账号")
    private String bankAccount;

    @Schema(description = "账户姓名")
    private String accountName;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "申请时间")
    private LocalDateTime applyTime;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "转账时间")
    private LocalDateTime transferTime;

    @Schema(description = "转账备注")
    private String transferRemark;

    @Schema(description = "用户手机号")
    private String userPhone;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
