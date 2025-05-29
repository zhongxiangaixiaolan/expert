package com.qing.expert.common.exception;

import com.qing.expert.common.result.Result;
import com.qing.expert.common.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleBusinessException(BusinessException e) {
        LogUtil.warning("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常处理（@RequestBody）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        LogUtil.warning("参数校验异常: {}", message);
        return Result.badRequest(message);
    }

    /**
     * 参数校验异常处理（@ModelAttribute）
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        LogUtil.warning("参数绑定异常: {}", message);
        return Result.badRequest(message);
    }

    /**
     * 参数校验异常处理（@RequestParam）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        LogUtil.warning("约束违反异常: {}", message);
        return Result.badRequest(message);
    }

    /**
     * 参数类型转换异常处理
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = String.format("参数 '%s' 的值 '%s' 无法转换为 %s 类型",
                e.getName(), e.getValue(), e.getRequiredType().getSimpleName());
        LogUtil.warning("参数校验异常: {}", message);
        return Result.badRequest(message);
    }

    /**
     * 类型转换异常处理
     */
    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleTypeMismatchException(TypeMismatchException e) {
        String message = String.format("参数类型转换失败: %s", e.getMessage());
        LogUtil.warning("参数校验异常: {}", message);
        return Result.badRequest(message);
    }

    /**
     * 非法参数异常处理
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        LogUtil.warning("非法参数异常: {}", e.getMessage());
        return Result.badRequest(e.getMessage());
    }

    /**
     * 空指针异常处理
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleNullPointerException(NullPointerException e) {
        LogUtil.error("空指针异常: {}", e, e.getMessage());
        return Result.error("系统内部错误");
    }

    /**
     * 运行时异常处理
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        LogUtil.error("运行时异常: {}", e, e.getMessage());
        return Result.error("系统内部错误");
    }

    /**
     * 通用异常处理
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        LogUtil.error("系统异常: {}", e, e.getMessage());
        return Result.error("系统内部错误，请联系管理员");
    }
}
