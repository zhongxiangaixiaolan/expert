# 日志配置说明

## 概述

本项目已配置了完整的彩色日志输出系统，支持：

- 🎨 **20 多种彩色日志类型**，不同类型的日志以不同颜色显示
- 📁 **可配置的日志文件开关**，可以选择是否生成日志文件
- 🔧 **动态配置管理**，运行时可调整日志配置
- 🌍 **多环境支持**，开发/测试/生产环境不同的日志策略
- ⚡ **高性能异步日志**，不影响应用性能

## 日志颜色说明

### 基础日志级别

- ✅ **成功日志** - 绿色 (`LogUtil.success()`)
- ⚠️ **警告日志** - 黄色 (`LogUtil.warning()`)
- ❌ **错误日志** - 红色 (`LogUtil.error()`)
- ℹ️ **信息日志** - 蓝色 (`LogUtil.info()`)
- 🐛 **调试日志** - 紫色 (`LogUtil.debug()`)

### 业务日志类型

- 💼 **业务日志** - 青色 (`LogUtil.business()`)
- ⚡ **性能日志** - 黄色背景 (`LogUtil.performance()`)
- 🔒 **安全日志** - 红色背景 (`LogUtil.security()`)
- ⚙️ **配置日志** - 黄色 (`LogUtil.config()`)
- ⏰ **定时任务** - 青色 (`LogUtil.schedule()`)

### API 相关日志

- 📥 **API 请求** - 绿色 (`LogUtil.apiRequest()`)
- 📤 **API 响应** - 绿色/红色 (`LogUtil.apiResponse()`)

### 数据相关日志

- 🗄️ **数据库操作** - 青色 (`LogUtil.database()`)
- 💾 **缓存操作** - 紫色 (`LogUtil.cache()`)

### 系统相关日志

- 🚀 **系统启动** - 绿色加粗 (`LogUtil.startup()`)
- 🛑 **系统关闭** - 红色加粗 (`LogUtil.shutdown()`)
- 📁 **文件操作** - 蓝色 (`LogUtil.file()`)

### 业务功能日志

- 👤 **用户操作** - 蓝色 (`LogUtil.userAction()`)
- 💰 **支付相关** - 绿色加粗 (`LogUtil.payment()`)
- 📨 **消息推送** - 紫色 (`LogUtil.message()`)
- 🔐 **验证码** - 黄色 (`LogUtil.verification()`)
- 📋 **订单相关** - 青色加粗 (`LogUtil.order()`)
- 📝 **任务相关** - 蓝色 (`LogUtil.task()`)

## 使用方法

### 1. 基础使用

```java
import com.qing.expert.common.util.LogUtil;

// 成功日志
LogUtil.success("用户注册成功: 用户ID={}", userId);

// 警告日志
LogUtil.warning("用户尝试重复登录: 用户ID={}", userId);

// 错误日志
LogUtil.error("数据库连接失败", exception);

// 信息日志
LogUtil.info("处理用户请求: 用户ID={}", userId);

// 调试日志
LogUtil.debug("方法参数: param1={}, param2={}", param1, param2);
```

### 2. 业务日志

```java
// API请求响应
LogUtil.apiRequest("POST", "/api/user/login", "username=admin");
LogUtil.apiResponse("/api/user/login", 200, "登录成功");

// 数据库操作
LogUtil.database("SELECT", "user", "id = 1");
LogUtil.database("INSERT", "user", "name = '张三'");

// 缓存操作
LogUtil.cache("GET", "user:1", "用户信息");
LogUtil.cache("SET", "user:1", "更新用户信息");

// 用户操作
LogUtil.userAction("1001", "登录", "用户登录系统");
LogUtil.userAction("1001", "下单", "创建新订单");

// 支付相关
LogUtil.payment("支付订单: 订单号={}, 金额={}", orderNo, amount);

// 订单相关
LogUtil.order("创建订单: 订单号={}, 用户ID={}", orderNo, userId);

// 任务相关
LogUtil.task("发布任务: 任务ID={}, 标题={}", taskId, title);
```

## 日志文件配置

### 1. 配置文件开关

在 `application.yml` 中可以配置日志文件的生成开关：

```yaml
expert:
  log:
    # 是否启用文件日志 (true: 生成日志文件, false: 仅控制台输出)
    file-enabled: true
    # 日志文件路径
    file-path: logs/
    # 是否启用SQL日志文件
    sql-file-enabled: true
    # 是否启用错误日志文件
    error-file-enabled: true
    # 日志文件最大大小
    max-file-size: 100MB
    # 日志文件保留天数
    max-history: 30
    # 日志总大小限制
    total-size-cap: 3GB
```

### 2. 多环境配置

项目支持不同环境的日志配置，通过 Spring Profile 控制：

#### 开发环境 (`spring.profiles.active=dev`)

- **日志输出**: 仅控制台输出
- **特点**: 快速启动，无文件 IO 开销
- **适用**: 本地开发调试

#### 测试环境 (`spring.profiles.active=test`)

- **日志输出**: 控制台输出 + 错误日志文件
- **特点**: 保留错误信息用于问题排查
- **适用**: 测试环境部署

#### 生产环境 (`spring.profiles.active=prod`)

- **日志输出**: 文件输出 + 错误日志文件
- **特点**: 完整的日志记录和归档
- **适用**: 生产环境部署

#### 默认配置 (无 Profile 或其他 Profile)

- **日志输出**: 仅控制台输出
- **特点**: 与开发环境相同
- **适用**: 快速测试

#### 生产环境 (`application-prod.yml`)

```yaml
expert:
  log:
    file-enabled: true # 启用所有日志文件
    sql-file-enabled: true
    error-file-enabled: true
    file-path: /var/log/expert/
    max-file-size: 200MB
    max-history: 30
```

### 3. 日志级别配置

在 `application.yml` 中配置不同包的日志级别：

```yaml
logging:
  level:
    root: info
    com.qing.expert: debug
    org.springframework: warn
    org.springframework.security: warn
```

## 动态配置管理

### 1. 日志文件配置管理

通过 API 接口动态管理日志文件配置：

```bash
# 获取当前日志配置
GET /api/log-config/current

# 获取日志配置状态
GET /api/log-config/status

# 设置文件日志开关
POST /api/log-config/file-enabled?enabled=true

# 设置SQL日志文件开关
POST /api/log-config/sql-file-enabled?enabled=false

# 设置错误日志文件开关
POST /api/log-config/error-file-enabled?enabled=true

# 设置日志文件路径
POST /api/log-config/file-path?filePath=logs/

# 快速切换模式
POST /api/log-config/dev-mode      # 开发模式 - 仅控制台输出
POST /api/log-config/test-mode     # 测试模式 - 仅错误日志文件
POST /api/log-config/prod-mode     # 生产模式 - 启用所有日志文件

# 重置为默认配置
POST /api/log-config/reset

# 获取配置建议
GET /api/log-config/suggestions
```

### 2. 日志级别动态调整

通过 API 接口动态调整日志级别：

```bash
# 获取当前日志级别
GET /api/log-level/current

# 设置指定包的日志级别
POST /api/log-level/set?packageName=com.qing.expert&level=DEBUG

# 开启调试模式
POST /api/log-level/debug-mode

# 开启生产模式
POST /api/log-level/production-mode

# 重置为默认配置
POST /api/log-level/reset
```

### 3. 启动时指定环境

可以通过启动参数指定不同的环境配置：

```bash
# 开发环境 (仅控制台输出)
java -jar expert.jar --spring.profiles.active=dev

# 测试环境 (仅错误日志文件)
java -jar expert.jar --spring.profiles.active=test

# 生产环境 (启用所有日志文件)
java -jar expert.jar --spring.profiles.active=prod
```

### 4. 日志文件说明

根据配置，日志文件可能保存到以下位置：

- `logs/expert.log` - 应用主日志 (当 `file-enabled=true` 时)
- `logs/error.log` - 错误日志 (当 `error-file-enabled=true` 时)
- `logs/sql.log` - SQL 日志 (当 `sql-file-enabled=true` 时)

日志文件自动滚动配置：

- 单个文件最大大小：可配置 (默认 100MB)
- 保留天数：可配置 (默认 30 天)
- 总大小限制：可配置 (默认 3GB)

## 测试接口

项目提供了完整的日志测试接口，启动应用后访问：

```
http://localhost:8080/swagger-ui.html
```

可以测试以下功能：

### 1. 日志测试接口 (`/api/log-test`)

- 测试各种类型的彩色日志输出
- 测试 API 请求响应日志
- 测试数据库操作日志
- 测试业务功能日志

### 2. 日志配置管理接口 (`/api/log-config`)

- 动态开关日志文件生成
- 切换不同的日志模式
- 调整日志文件配置
- 获取配置建议

### 3. 日志级别管理接口 (`/api/log-level`)

- 动态调整日志级别
- 切换调试/生产模式
- 查看当前日志配置

## 最佳实践

### 1. 环境配置建议

- **开发环境**: 使用 `dev` 配置，仅控制台输出，便于实时查看
- **测试环境**: 使用 `test` 配置，仅启用错误日志文件
- **生产环境**: 使用 `prod` 配置，启用所有日志文件

### 2. 日志文件开关使用

- **开发调试**: 关闭文件日志 (`file-enabled=false`)，减少磁盘 IO
- **性能测试**: 关闭所有文件日志，避免影响性能测试结果
- **生产监控**: 启用所有文件日志，便于问题排查
- **临时调试**: 使用 API 接口动态开关，无需重启应用

### 3. 日志级别选择

- **ERROR**: 系统错误，需要立即处理
- **WARN**: 警告信息，可能存在问题
- **INFO**: 重要的业务信息
- **DEBUG**: 调试信息，开发时使用

### 4. 日志内容规范

- 使用有意义的日志消息
- 包含关键的业务参数
- 避免记录敏感信息（密码、身份证等）
- 使用结构化的日志格式
- 使用 `LogUtil` 工具类的专用方法

### 5. 性能考虑

- 生产环境建议使用 WARN 级别
- 避免在循环中打印大量日志
- 使用异步日志提高性能
- 根据需要关闭不必要的日志文件

### 6. 安全考虑

- 不要记录用户密码
- 敏感信息需要脱敏处理
- 定期清理过期日志文件
- 生产环境注意日志文件权限

## 故障排查

### 1. 日志不显示颜色

- 确认终端支持 ANSI 颜色代码
- 检查 `logback-spring.xml` 配置
- 确认使用的是 `LogUtil` 工具类

### 2. 日志文件不生成

- 检查日志文件开关配置 (`expert.log.file-enabled`)
- 检查 `logs/` 目录权限
- 确认 `logback-spring.xml` 配置正确
- 查看应用启动日志是否有错误
- 使用 `/api/log-config/status` 接口查看当前配置

### 3. 日志级别不生效

- 检查 `application.yml` 配置
- 使用动态调整接口验证
- 确认包名配置正确

### 4. 配置不生效

- 确认配置文件格式正确
- 检查是否有多个配置文件冲突
- 使用 `/api/log-config/current` 查看实际配置
- 重启应用使配置生效

### 5. Logback 初始化失败

**问题症状**：

```
Logging system failed to initialize using configuration from 'null'
java.lang.IllegalStateException: Could not initialize Logback logging from classpath:logback-spring.xml
```

**解决方案**：

- 确认已添加 Janino 依赖（用于支持条件配置）
- 检查 `logback-spring.xml` 中的条件表达式语法
- 确认所有引用的变量都已正确定义

**已修复**（2025-01-23）：

- ✅ 添加了 `org.codehaus.janino:janino:3.1.10` 依赖
- ✅ 修复了控制台输出模式中的变量引用问题
- ✅ **关键修复**: 移除了所有 `<springProperty>` 和 `<springProfile>` 扩展配置
- ✅ 改用标准的 Logback 系统属性配置（`${LOG_FILE:-logs/}` 等）
- ✅ **重要修复**: 修正了滚动策略类名 `SizeAndTimeBasedRollingPolicy`
- ✅ **路径修复**: 修正了文件路径分隔符，避免路径拼接错误
- ✅ 简化了 logback-spring.xml 配置，完全兼容 Spring Boot 3.3
- ✅ 保留了彩色日志、文件滚动、错误日志分离等核心功能
- ✅ 解决了配置扫描冲突问题，确保 Logback 正常初始化

## 扩展功能

### 1. 自定义日志类型

可以在 `LogUtil` 类中添加新的日志方法：

```java
public static void customLog(String message, Object... args) {
    log.info("🎯 " + CUSTOM_COLOR + message + RESET, args);
}
```

### 2. 日志监控

可以集成 ELK Stack 或其他日志监控系统来分析日志数据。

### 3. 日志告警

可以配置日志告警规则，当出现 ERROR 级别日志时自动发送通知。

### 4. 集成外部日志系统

- **ELK Stack**: 集成 Elasticsearch、Logstash、Kibana 进行日志分析
- **Prometheus + Grafana**: 集成指标监控和可视化
- **Fluentd**: 统一日志收集和转发

### 5. 日志分析

- 使用正则表达式提取关键信息
- 统计 API 调用频率和响应时间
- 分析用户行为和系统性能

## 快速开始

### 1. 开发环境快速配置

```bash
# 1. 启动应用 (开发模式 - 仅控制台输出)
java -jar expert.jar --spring.profiles.active=dev

# 2. 访问Swagger测试接口
http://localhost:8080/swagger-ui.html

# 3. 测试彩色日志
curl -X GET "http://localhost:8080/api/log-test/all"

# 4. 查看日志配置状态
curl -X GET "http://localhost:8080/api/log-config/status"
```

### 2. 生产环境部署

```bash
# 1. 创建日志目录
mkdir -p /var/log/expert

# 2. 启动应用 (生产模式 - 启用所有日志文件)
java -jar expert.jar --spring.profiles.active=prod

# 3. 监控日志文件
tail -f /var/log/expert/expert.log
tail -f /var/log/expert/error.log
```

### 3. 常用操作

```bash
# 临时关闭文件日志 (仅控制台输出)
curl -X POST "http://localhost:8080/api/log-config/dev-mode"

# 临时启用所有日志文件
curl -X POST "http://localhost:8080/api/log-config/prod-mode"

# 开启调试模式
curl -X POST "http://localhost:8080/api/log-level/debug-mode"

# 查看当前配置
curl -X GET "http://localhost:8080/api/log-config/current"
```

---

## 总结

本日志系统提供了完整的日志管理解决方案：

✅ **彩色日志输出** - 20 多种颜色类型，快速识别问题
✅ **可配置文件开关** - 灵活控制日志文件生成
✅ **多环境支持** - 开发/测试/生产环境不同策略
✅ **动态配置管理** - 运行时调整，无需重启
✅ **高性能异步日志** - 不影响应用性能
✅ **完整的测试接口** - 便于验证和调试

通过合理配置和使用，可以大大提高开发效率和问题排查能力！

## 控制台日志优化 (2025-01-23 更新)

### 极简控制台模式

为了减少控制台日志噪音，已对日志配置进行了以下优化：

#### 1. 框架日志严格控制

所有第三方框架日志已设置为 `OFF` 级别（完全关闭），包括：

- ✅ Spring Framework 全系列 (除 spring.boot 保留 ERROR)
- ✅ MyBatis Plus / Hibernate ORM 框架
- ✅ Tomcat / Netty 服务器组件
- ✅ MySQL / Redis 数据库连接器
- ✅ Jackson / YAML 序列化组件
- ✅ Swagger / SpringDoc API 文档
- ✅ Logback 自身的日志输出

#### 2. 根日志级别优化

- **根日志级别**: 设置为 `ERROR`，只显示系统错误
- **应用日志级别**: `com.qing.expert` 包保持 `INFO` 级别
- **SQL 日志**: 只记录到文件，不在控制台显示

#### 3. 控制台输出格式

采用极简格式，只显示关键信息：

```
14:30:25 ERROR c.q.expert.service 用户登录失败
14:30:26 INFO  c.q.expert.controller 接收到新订单请求
```

格式说明：

- `HH:mm:ss` - 时间（去掉毫秒）
- `LEVEL` - 日志级别（彩色显示）
- `logger{15}` - 类名（最多 15 字符）
- `message` - 日志消息（彩色显示）

#### 4. 配置效果

**优化前**：

- 大量框架启动日志
- 数据库连接池日志
- Spring 容器初始化日志
- 各种 INFO/DEBUG 级别日志

**优化后**：

- 只显示应用程序的 INFO 及以上级别日志
- 只显示系统的 ERROR 级别日志
- 框架日志完全静默
- 控制台输出清爽简洁

#### 5. 使用建议

**开发调试时**：

```bash
# 如需查看详细日志，可临时开启调试模式
curl -X POST "http://localhost:8080/api/log-level/debug-mode"

# 或查看日志文件
tail -f logs/expert.log
```

**生产环境**：

- 控制台只显示关键错误信息
- 详细日志记录在文件中
- 便于快速发现问题

这样的配置既保证了重要信息不丢失，又让控制台输出保持简洁清晰！

### 6. Logback 状态日志关闭 (2025-01-23 最新更新)

**问题**：即使设置了框架日志为 OFF，仍然会看到类似以下的 Logback 状态日志：

```
00:23:59,841 |-INFO in ch.qos.logback.classic.jul.LevelChangePropagator@4b629f13 - Propagating OFF level on Logger[com.mysql.cj] onto the JUL framework
```

**解决方案**：

1. **在 logback-spring.xml 中添加状态监听器**：

   ```xml
   <configuration debug="false" scan="false">
       <!-- 关闭 Logback 状态日志 -->
       <statusListener class="ch.qos.logback.core.status.NopStatusListener" />
   ```

2. **在 application.yml 中关闭相关日志**：

   ```yaml
   logging:
     level:
       org.springframework.boot.logging: "off" # 关闭Spring Boot日志系统的日志
       ch.qos.logback: "off" # 关闭Logback自身日志
   ```

3. **关闭 MyBatis 控制台 SQL 日志**：
   ```yaml
   mybatis-plus:
     configuration:
       # log-impl: org.apache.ibatis.logging.stdout.StdOutImpl # 已注释关闭
   ```

**配置说明**：

- `debug="false"`: 关闭 Logback 调试模式
- `scan="false"`: 关闭配置文件扫描
- `NopStatusListener`: 空状态监听器，不输出任何状态信息

现在控制台将完全静默，只显示应用程序的重要日志！
