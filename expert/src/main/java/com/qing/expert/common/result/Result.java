package com.qing.expert.common.result;

import com.qing.expert.common.constant.CommonConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果
 */
@Data
@Schema(description = "统一响应结果")
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "响应码")
    private Integer code;

    @Schema(description = "响应消息")
    private String message;

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "时间戳")
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public Result(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<>(CommonConstant.SUCCESS_CODE, "操作成功");
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(CommonConstant.SUCCESS_CODE, "操作成功", data);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> Result<T> success(String message) {
        return new Result<>(CommonConstant.SUCCESS_CODE, message);
    }

    /**
     * 成功响应（自定义消息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(CommonConstant.SUCCESS_CODE, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error() {
        return new Result<>(CommonConstant.ERROR_CODE, "操作失败");
    }

    /**
     * 失败响应（自定义消息）
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(CommonConstant.ERROR_CODE, message);
    }

    /**
     * 失败响应（自定义状态码和消息）
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }

    /**
     * 未授权响应
     */
    public static <T> Result<T> unauthorized() {
        return new Result<>(CommonConstant.UNAUTHORIZED_CODE, "未授权访问");
    }

    /**
     * 未授权响应（自定义消息）
     */
    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(CommonConstant.UNAUTHORIZED_CODE, message);
    }

    /**
     * 禁止访问响应
     */
    public static <T> Result<T> forbidden() {
        return new Result<>(CommonConstant.FORBIDDEN_CODE, "禁止访问");
    }

    /**
     * 禁止访问响应（自定义消息）
     */
    public static <T> Result<T> forbidden(String message) {
        return new Result<>(CommonConstant.FORBIDDEN_CODE, message);
    }

    /**
     * 资源不存在响应
     */
    public static <T> Result<T> notFound() {
        return new Result<>(CommonConstant.NOT_FOUND_CODE, "资源不存在");
    }

    /**
     * 资源不存在响应（自定义消息）
     */
    public static <T> Result<T> notFound(String message) {
        return new Result<>(CommonConstant.NOT_FOUND_CODE, message);
    }

    /**
     * 参数错误响应
     */
    public static <T> Result<T> badRequest() {
        return new Result<>(CommonConstant.BAD_REQUEST_CODE, "参数错误");
    }

    /**
     * 参数错误响应（自定义消息）
     */
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(CommonConstant.BAD_REQUEST_CODE, message);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return Integer.valueOf(CommonConstant.SUCCESS_CODE).equals(this.code);
    }

    /**
     * 判断是否失败
     */
    public boolean isError() {
        return !isSuccess();
    }
}
