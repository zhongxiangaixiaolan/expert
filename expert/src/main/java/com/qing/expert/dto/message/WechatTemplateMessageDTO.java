package com.qing.expert.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * 微信模板消息DTO
 */
@Data
@Schema(description = "微信模板消息参数")
public class WechatTemplateMessageDTO {

    @Schema(description = "接收用户OpenID", required = true)
    @NotBlank(message = "接收用户OpenID不能为空")
    private String toUser;

    @Schema(description = "模板ID", required = true)
    @NotBlank(message = "模板ID不能为空")
    private String templateId;

    @Schema(description = "跳转页面路径")
    private String page;

    @Schema(description = "表单ID")
    private String formId;

    @Schema(description = "模板数据", required = true)
    @NotNull(message = "模板数据不能为空")
    private Map<String, TemplateDataItem> data;

    @Schema(description = "小程序类型")
    private String miniprogramState = "formal";

    @Schema(description = "语言")
    private String lang = "zh_CN";

    /**
     * 模板数据项
     */
    @Data
    @Schema(description = "模板数据项")
    public static class TemplateDataItem {

        @Schema(description = "数据值", required = true)
        @NotBlank(message = "数据值不能为空")
        private String value;

        @Schema(description = "颜色")
        private String color;

        public TemplateDataItem() {
        }

        public TemplateDataItem(String value) {
            this.value = value;
        }

        public TemplateDataItem(String value, String color) {
            this.value = value;
            this.color = color;
        }
    }
}
