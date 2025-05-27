package com.qing.expert.vo.user;

import com.qing.expert.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户登录响应VO
 */
@Data
@Schema(description = "用户登录响应")
public class UserLoginVO {

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "令牌类型")
    private String tokenType = "Bearer";

    @Schema(description = "令牌过期时间（秒）")
    private Long expiresIn;

    @Schema(description = "用户信息")
    private User userInfo;

    @Schema(description = "是否为新用户")
    private Boolean isNewUser;

    @Schema(description = "是否为达人")
    private Boolean isExpert;

    public UserLoginVO() {}

    public UserLoginVO(String token, User userInfo, Boolean isNewUser) {
        this.token = token;
        this.userInfo = userInfo;
        this.isNewUser = isNewUser;
        this.isExpert = userInfo.getIsExpert() == 1;
        this.expiresIn = 7 * 24 * 60 * 60L; // 7天
    }
}
