package com.qing.expert.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 用户更新DTO
 */
@Data
@Schema(description = "用户信息更新请求")
public class UserUpdateDTO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户昵称")
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    @Schema(description = "用户头像URL")
    private String avatar;

    @Schema(description = "性别：0-未知，1-男，2-女")
    private Integer gender;

    @Schema(description = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "真实姓名")
    @Size(max = 20, message = "真实姓名长度不能超过20个字符")
    private String realName;

    @Schema(description = "加密数据（绑定手机号时使用）")
    private String encryptedData;

    @Schema(description = "初始向量（绑定手机号时使用）")
    private String iv;
}
