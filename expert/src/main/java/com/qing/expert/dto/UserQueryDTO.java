package com.qing.expert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户查询DTO
 */
@Data
@Schema(description = "用户查询条件")
public class UserQueryDTO {

    @Schema(description = "当前页码", example = "1")
    private Long current = 1L;

    @Schema(description = "每页大小", example = "10")
    private Long size = 10L;

    @Schema(description = "搜索关键词（昵称、手机号）")
    private String keyword;

    @Schema(description = "用户状态：0-禁用，1-正常")
    private Integer status;

    @Schema(description = "是否为达人：0-否，1-是")
    private Integer isExpert;

    @Schema(description = "性别：0-未知，1-男，2-女")
    private Integer gender;

    @Schema(description = "注册开始时间", example = "2024-01-01")
    private String registerStartTime;

    @Schema(description = "注册结束时间", example = "2024-12-31")
    private String registerEndTime;
}
