package com.qing.expert.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 头像访问控制器
 * 注意：由于配置了context-path: /api，这里的路径是相对于context-path的
 */
@Slf4j
@RestController
@RequestMapping("/avatars")
@Tag(name = "头像管理", description = "头像文件访问接口")
public class AvatarController {

    @Value("${expert.file.upload-path:uploads/}")
    private String uploadPath;

    @Operation(summary = "获取头像文件", description = "根据文件名获取头像文件")
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getAvatar(
            @Parameter(description = "文件名") @PathVariable String filename) {
        try {
            // 构建文件路径
            Path filePath = Paths.get(System.getProperty("user.dir"), uploadPath, "avatars", filename);
            File file = filePath.toFile();

            // 调试日志 - 已验证路径配置正确，可根据需要启用
            // log.info("=== 头像文件请求详情 ===");
            // log.info("请求文件名: {}", filename);
            // log.info("工作目录: {}", System.getProperty("user.dir"));
            // log.info("上传路径配置: {}", uploadPath);
            // log.info("完整文件路径: {}", filePath.toAbsolutePath());
            // log.info("文件是否存在: {}", file.exists());
            // log.info("是否为文件: {}", file.isFile());

            // if (file.exists()) {
            // log.info("文件大小: {} bytes", file.length());
            // log.info("文件可读: {}", file.canRead());
            // }

            // // 列出avatars目录下的所有文件
            // File avatarsDir = new File(System.getProperty("user.dir"), uploadPath +
            // "avatars");
            // if (avatarsDir.exists() && avatarsDir.isDirectory()) {
            // log.info("avatars目录下的文件列表:");
            // File[] files = avatarsDir.listFiles();
            // if (files != null) {
            // for (File f : files) {
            // log.info(" - {}", f.getName());
            // }
            // }
            // }

            if (!file.exists() || !file.isFile()) {
                log.warn("头像文件不存在: {}", filePath.toAbsolutePath());
                return ResponseEntity.notFound().build();
            }

            // 创建资源
            Resource resource = new FileSystemResource(file);

            // 确定文件类型
            String contentType = getContentType(filename);
            // log.info("文件类型: {}", contentType);
            // log.info("=== 头像文件请求成功 ===");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600") // 缓存1小时
                    .body(resource);

        } catch (Exception e) {
            log.error("获取头像文件失败: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根据文件扩展名确定内容类型
     */
    private String getContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "svg":
                return "image/svg+xml";
            default:
                return "application/octet-stream";
        }
    }
}
