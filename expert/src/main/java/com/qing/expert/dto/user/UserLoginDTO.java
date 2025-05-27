package com.qing.expert.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 用户登录DTO
 */
@Data
@Schema(description = "用户登录请求")
public class UserLoginDTO {

    @Schema(description = "微信授权码", required = true)
    @NotBlank(message = "授权码不能为空")
    private String code;

    @Schema(description = "加密数据")
    private String encryptedData;

    @Schema(description = "初始向量")
    private String iv;

    @Schema(description = "签名")
    private String signature;

    @Schema(description = "原始数据")
    private String rawData;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "性别：0-未知，1-男，2-女")
    private Integer gender;
}
