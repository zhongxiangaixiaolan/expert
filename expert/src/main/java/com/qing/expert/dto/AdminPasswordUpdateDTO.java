package com.qing.expert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理员密码修改DTO
 */
@Data
@Schema(description = "管理员密码修改请求")
public class AdminPasswordUpdateDTO {

    @Schema(description = "原密码")
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @Schema(description = "新密码")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度必须在6-20个字符之间")
    private String newPassword;

    @Schema(description = "确认新密码")
    @NotBlank(message = "确认新密码不能为空")
    private String confirmPassword;
}
