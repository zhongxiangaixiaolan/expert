package com.qing.expert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页查询基础DTO
 */
@Data
@Schema(description = "分页查询基础DTO")
public class BasePageDTO {

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    // 兼容小程序端的参数名
    @Schema(description = "页码(兼容current)", example = "1")
    private Integer current;

    @Schema(description = "每页大小(兼容size)", example = "10")
    private Integer size;

    /**
     * 获取页码，最小为1，兼容current参数
     */
    public Integer getPageNum() {
        Integer page = pageNum;
        if (page == null && current != null) {
            page = current;
        }
        return page == null || page < 1 ? 1 : page;
    }

    /**
     * 获取每页大小，默认10，最大100，兼容size参数
     */
    public Integer getPageSize() {
        Integer size = pageSize;
        if (size == null && this.size != null) {
            size = this.size;
        }
        if (size == null || size < 1) {
            return 10;
        }
        return Math.min(size, 100);
    }
}
