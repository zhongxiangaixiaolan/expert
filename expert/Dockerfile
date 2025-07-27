# 多阶段构建 - 达人接单平台后端服务
# 基于 OpenJDK 17 和 Maven 3.9

# 第一阶段：构建阶段
FROM maven:latest AS builder

# 设置工作目录
WORKDIR /app

# 复制 Maven 配置文件
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# 下载依赖（利用Docker缓存层）
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用
RUN ./mvnw clean package -DskipTests -B

# 第二阶段：运行阶段
FROM openjdk:17-jdk-slim

# 设置时区并安装必要工具
ENV TZ=Asia/Shanghai
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/* && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建应用用户
RUN groupadd -r expert && useradd -r -g expert expert

# 设置工作目录
WORKDIR /app

# 创建必要的目录
RUN mkdir -p /app/uploads /app/logs && \
    chown -R expert:expert /app

# 从构建阶段复制jar包
COPY --from=builder /app/target/spring_expert-0.0.1-SNAPSHOT.jar app.jar

# 更改文件所有者
RUN chown expert:expert app.jar

# 切换到非root用户
USER expert

# 暴露端口
EXPOSE 9090

# 健康检查 - 使用API文档端点
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:9090/api/v3/api-docs || exit 1

# 启动应用
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=docker", \
    "-Xms512m", \
    "-Xmx1024m", \
    "-jar", \
    "app.jar"]
