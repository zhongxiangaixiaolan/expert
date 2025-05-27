package com.qing.expert.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理员登录响应VO
 */
@Data
@Schema(description = "管理员登录响应")
public class AdminLoginVO {

    @Schema(description = "访问令牌")
    private String accessToken;

    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "过期时间（秒）")
    private Long expiresIn;

    @Schema(description = "管理员信息")
    private AdminInfoVO adminInfo;

    @Data
    @Schema(description = "管理员信息")
    public static class AdminInfoVO {
        
        @Schema(description = "管理员ID")
        private Long id;

        @Schema(description = "用户名")
        private String username;

        @Schema(description = "真实姓名")
        private String realName;

        @Schema(description = "头像")
        private String avatar;

        @Schema(description = "角色")
        private String role;

        @Schema(description = "角色名称")
        private String roleName;
    }
}
