package com.qing.expert.controller;

import com.qing.expert.common.result.Result;
import com.qing.expert.common.util.LogUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 日志测试控制器
 * 注意：由于配置了context-path: /api，这里的路径是相对于context-path的
 *
 * @author qing
 * @since 2025-01-23
 */
@Tag(name = "日志测试", description = "日志测试相关接口")
@RestController
@RequestMapping("/log-test")
public class LogTestController {

    @Operation(summary = "测试成功日志")
    @GetMapping("/success")
    public Result<String> testSuccess() {
        LogUtil.success("这是一个成功日志测试");
        LogUtil.apiRequest("GET", "/api/log-test/success", "无参数");
        LogUtil.apiResponse("/api/log-test/success", 200, "成功响应");
        return Result.success("成功日志测试完成");
    }

    @Operation(summary = "测试警告日志")
    @GetMapping("/warning")
    public Result<String> testWarning() {
        LogUtil.warning("这是一个警告日志测试");
        LogUtil.apiRequest("GET", "/api/log-test/warning", "无参数");
        LogUtil.apiResponse("/api/log-test/warning", 200, "警告响应");
        return Result.success("警告日志测试完成");
    }

    @Operation(summary = "测试错误日志")
    @GetMapping("/error")
    public Result<String> testError() {
        LogUtil.error("这是一个错误日志测试");
        LogUtil.apiRequest("GET", "/api/log-test/error", "无参数");
        LogUtil.apiResponse("/api/log-test/error", 500, "错误响应");
        return Result.success("错误日志测试完成");
    }

    @Operation(summary = "测试信息日志")
    @GetMapping("/info")
    public Result<String> testInfo() {
        LogUtil.info("这是一个信息日志测试");
        LogUtil.apiRequest("GET", "/api/log-test/info", "无参数");
        LogUtil.apiResponse("/api/log-test/info", 200, "信息响应");
        return Result.success("信息日志测试完成");
    }

    @Operation(summary = "测试调试日志")
    @GetMapping("/debug")
    public Result<String> testDebug() {
        LogUtil.debug("这是一个调试日志测试");
        LogUtil.apiRequest("GET", "/api/log-test/debug", "无参数");
        LogUtil.apiResponse("/api/log-test/debug", 200, "调试响应");
        return Result.success("调试日志测试完成");
    }

    @Operation(summary = "测试业务日志")
    @GetMapping("/business")
    public Result<String> testBusiness() {
        LogUtil.business("这是一个业务日志测试");
        LogUtil.apiRequest("GET", "/api/log-test/business", "无参数");
        LogUtil.apiResponse("/api/log-test/business", 200, "业务响应");
        return Result.success("业务日志测试完成");
    }

    @Operation(summary = "测试数据库日志")
    @GetMapping("/database")
    public Result<String> testDatabase() {
        LogUtil.database("SELECT", "user", "id = 1");
        LogUtil.database("INSERT", "user", "name = '张三'");
        LogUtil.database("UPDATE", "user", "id = 1, name = '李四'");
        LogUtil.database("DELETE", "user", "id = 1");
        return Result.success("数据库日志测试完成");
    }

    @Operation(summary = "测试缓存日志")
    @GetMapping("/cache")
    public Result<String> testCache() {
        LogUtil.cache("GET", "user:1", "用户信息");
        LogUtil.cache("SET", "user:1", "更新用户信息");
        LogUtil.cache("DELETE", "user:1", "删除用户信息");
        return Result.success("缓存日志测试完成");
    }

    @Operation(summary = "测试用户操作日志")
    @GetMapping("/user-action")
    public Result<String> testUserAction() {
        LogUtil.userAction("1001", "登录", "用户登录系统");
        LogUtil.userAction("1001", "查看订单", "查看订单列表");
        LogUtil.userAction("1001", "下单", "创建新订单");
        LogUtil.userAction("1001", "支付", "完成订单支付");
        return Result.success("用户操作日志测试完成");
    }

    @Operation(summary = "测试支付日志")
    @GetMapping("/payment")
    public Result<String> testPayment() {
        LogUtil.payment("支付订单: 订单号={}, 金额={}, 支付方式={}", "202501230001", "100.00", "微信支付");
        LogUtil.payment("支付成功: 订单号={}, 交易号={}", "202501230001", "wx123456789");
        return Result.success("支付日志测试完成");
    }

    @Operation(summary = "测试订单日志")
    @GetMapping("/order")
    public Result<String> testOrder() {
        LogUtil.order("创建订单: 订单号={}, 用户ID={}, 金额={}", "202501230001", "1001", "100.00");
        LogUtil.order("订单状态变更: 订单号={}, 状态: {} -> {}", "202501230001", "待支付", "已支付");
        LogUtil.order("订单完成: 订单号={}, 完成时间={}", "202501230001", "2025-01-23 15:30:00");
        return Result.success("订单日志测试完成");
    }

    @Operation(summary = "测试任务日志")
    @GetMapping("/task")
    public Result<String> testTask() {
        LogUtil.task("发布任务: 任务ID={}, 标题={}, 预算={}", "T001", "网站开发", "5000.00");
        LogUtil.task("任务接单: 任务ID={}, 达人ID={}", "T001", "E001");
        LogUtil.task("任务完成: 任务ID={}, 完成时间={}", "T001", "2025-01-23 15:30:00");
        return Result.success("任务日志测试完成");
    }

    @Operation(summary = "测试消息推送日志")
    @GetMapping("/message")
    public Result<String> testMessage() {
        LogUtil.message("发送消息: 用户ID={}, 消息类型={}, 内容={}", "1001", "订单通知", "您的订单已支付成功");
        LogUtil.message("推送通知: 用户ID={}, 推送类型={}", "1001", "微信小程序通知");
        return Result.success("消息推送日志测试完成");
    }

    @Operation(summary = "测试所有类型日志")
    @GetMapping("/all")
    public Result<String> testAll() {
        LogUtil.startup("系统启动测试");
        LogUtil.success("操作成功测试");
        LogUtil.warning("警告信息测试");
        LogUtil.error("错误信息测试");
        LogUtil.info("普通信息测试");
        LogUtil.debug("调试信息测试");
        LogUtil.business("业务逻辑测试");
        LogUtil.performance("性能监控测试");
        LogUtil.security("安全检查测试");
        LogUtil.config("配置加载测试");
        LogUtil.schedule("定时任务测试");
        LogUtil.file("文件操作", "test.txt", "成功");
        LogUtil.verification("验证码发送: 手机号={}, 验证码={}", "13800138000", "123456");

        return Result.success("所有类型日志测试完成");
    }
}
