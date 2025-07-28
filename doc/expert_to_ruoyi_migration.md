# Expert 后端 → RuoYi-Vue 单体版 迁移实施方案

> 适用范围：将当前 `expert` Spring Boot 3 项目（位于 `/workspace/expert`）整体迁移/整合到已克隆的 **RuoYi-Vue** 单体框架 `/workspace/RouYi-Vue`，在保持业务功能完整的前提下，充分复用 RuoYi 提供的权限、代码生成、监控、定时任务等通用能力。

---

## 1. 迁移目标
1. **统一运行时**：所有业务功能最终以 RuoYi 标准 `ruoyi-admin` 应用启动运行。
2. **最小改动**：避免大规模重写；优先 *包级迁移 + 适配*。
3. **复用通用能力**：鉴权、菜单/角色、日志、代码生成、文件、缓存、定时任务全部用 RuoYi 内置实现；仅迁移业务域。
4. **逐步替换**：迁移过程中确保可编译、可启动，采用特性开关逐步切换到 RuoYi 能力。

---

## 2. 映射关系总览

| Expert 模块/包 | 迁移到 RuoYi 位置 | 说明 |
|---|---|---|
| `com.qing.expert.entity` | `ruoyi-system` 新增 package `com.ruoyi.expert.domain` | 仅保留纯 POJO；继承 `BaseEntity` → 继承 `com.ruoyi.common.core.domain.BaseEntity` 并删除自定义同名基类。 |
| `com.qing.expert.mapper` | `ruoyi-system` 新增 package `com.ruoyi.expert.mapper` | 直接保留 MyBatis-Plus 注解/Xml；在 `ruoyi-framework` 中统一扫描。 |
| `com.qing.expert.service` | `ruoyi-system` 新增 package `com.ruoyi.expert.service` | 拆分 `I***Service & ***ServiceImpl`，实现逻辑迁移时依赖 RuoYi 内置工具类（`RedisCache`、`SecurityUtils` 等）。 |
| `com.qing.expert.controller` | `ruoyi-admin` 新增 package `com.ruoyi.web.controller.expert` | Controller 需要：① 统一 `@RequestMapping("/expert/**")` 前缀；② 统一返回 `AjaxResult`；③ 接入 `@Log`、`@PreAuthorize` 注解。 |
| `config`, `filter`, `security` | **大部分舍弃** | RuoYi 已含完整 Spring Security、XSS 过滤、跨域配置；仅保留业务相关配置（如短信）迁移到 `ruoyi-framework` `SecurityConfig` 扩展。 |
| `vo`、`dto` | 与 Controller 同包 | 保留以供入参/出参封装；可后续改为 MapStruct。 |
| 资源文件 `application*.yml`, `mapper/*.xml` | 合并到 `ruoyi-admin/src/main/resources` | 保持分环境 yml；mapper XML 放 `mapper/expert` 子目录，并在 `mybatis-config.xml` 中新增扫描路径。 |

---

## 3. 分阶段实施步骤

### 阶段 0：环境准备
1. 确认两套代码均可单独编译、启动。  
2. 统一 **JDK 17**、**Spring Boot 2.7.x**（保持与 RuoYi 当前版本一致）；若 `expert` 已是 Boot 3，需依赖降级兼容。

### 阶段 1：实体与数据层迁移
1. 在 `ruoyi-system` 新建包 `com.ruoyi.expert.domain`，迁入所有 `entity`。  
   - 删除原 `BaseEntity`，令实体继承 `com.ruoyi.common.core.domain.BaseEntity`；如有字段冲突（`createTime`, `remark` 等）保留 RuoYi 规范。  
2. 在同模块下新建 `mapper` 包并迁入 Mapper 接口 + XML；  
   - `xml` 放 `resources/mapper/expert/*.xml`。  
3. 修改 `ruoyi-admin` 的 `@MapperScan`：`
   ```java
   @MapperScan({"com.ruoyi.**.mapper","com.ruoyi.expert.mapper"})
   ```

### 阶段 2：Service 迁移
1. 创建 `com.ruoyi.expert.service` & `impl`；保留接口名称风格 `I***Service`。  
2. **替换依赖**：
   - 缓存：`redisTemplate` → `com.ruoyi.common.core.redis.RedisCache`。
   - 用户上下文：`SecurityContextHolder` → `SecurityUtils.getUsername()`。

### 阶段 3：Controller 迁移
1. 逐个复制 Controller 至 `ruoyi-admin`，统一注解：
   - `@RestController@Validated`
   - 结果返回 `return AjaxResult.success(data)`；错误抛出 `ServiceException`。
2. 接入权限：用 RuoYi 的 `@PreAuthorize("@ss.hasPermi('expert:service:list')")`。  
3. 如需新增菜单，在 **RuoYi 系统管理 → 菜单管理** 配置相应路由，再为角色分配。

### 阶段 4：配置 & 安全
1. 将 `application.yml` 中自定义属性迁至 `ruoyi.yml`，并在 `YmlConfig` 中读取。  
2. 如 `expert` 定义了自有 `SecurityConfig`，改为 **继承** RuoYi `SecurityConfig`，只加白名单 & 自定义过滤器。

### 阶段 5：业务验证
1. 启动 `ruoyi-admin`，访问 `/dev-api/expert/**` 进行接口回归。  
2. 配合前端（RuoYi-Vue3）在 `src/api` 下新增请求文件，对应菜单栏集成。

### 阶段 6：收尾
1. 删除 `expert` 原工程；将其 CI/CD 流水线合并到 RuoYi。  
2. 编写单元测试 `Expert*ServiceTest`，保证迁移正确性。  
3. 更新 `README` 与运维文档。

---

## 4. 重点改造清单
| 事项 | 说明 |
|---|---|
| 日志 | 使用 `@Log` 记录操作；原手动 `logger` 保留 debug 级别即可 |
| 全局异常 | 去掉 `@RestControllerAdvice` 冲突，继承/扩展 `GlobalExceptionHandler` |
| 文件上传 | 统一使用 RuoYi 的 `OssUtil` 或 `LocalStorageService` |
| 定时任务 | 原 `@Scheduled` 可迁到 **系统监控 → 定时任务**，持久化管理 |
| 代码生成 | 新增业务表时，可用 RuoYi 代码生成器替代手写 CRUD |

---

## 5. 目录示例（迁移完成后）
```
ruoyi-system
└── src/main/java/com/ruoyi
    ├── expert
    │   ├── domain        # entity
    │   ├── mapper
    │   ├── service
    │   │   └── impl
    │   └── util|dto|vo
    └── …
ruoyi-admin
└── src/main/java/com/ruoyi/web/controller
    └── expert           # Rest 接口层
```

---

## 6. 风险与建议
1. **依赖冲突**：Boot 版本不一致需重点验证；优先以 RuoYi 版本为准。  
2. **字段命名差异**：如 `status`、`delFlag` 等与 RuoYi 重复要对齐。  
3. **权限模型冲突**：expert 若有自定义角色/权限，需要迁入 `sys_menu` & `sys_role`，并在代码里用权限标识控制。  
4. **测试覆盖**：迁移后的关键 Service/Controller 建议补单测 > 60%。

---

> 如需更细粒度的类/字段改造指导，可在迁移到具体模块时逐步补充。