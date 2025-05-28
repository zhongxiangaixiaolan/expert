package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 达人照片实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("expert_photos")
@Schema(description = "达人照片")
public class ExpertPhoto extends BaseEntity {

    @Schema(description = "达人ID")
    @TableField("expert_id")
    private Long expertId;

    @Schema(description = "照片文件名")
    @TableField("photo_name")
    private String photoName;

    @Schema(description = "照片标题")
    @TableField("photo_title")
    private String photoTitle;

    @Schema(description = "照片描述")
    @TableField("photo_description")
    private String photoDescription;

    @Schema(description = "排序顺序")
    @TableField("sort_order")
    private Integer sortOrder;

    @Schema(description = "文件大小（字节）")
    @TableField("file_size")
    private Long fileSize;

    @Schema(description = "图片宽度")
    @TableField("width")
    private Integer width;

    @Schema(description = "图片高度")
    @TableField("height")
    private Integer height;
}
