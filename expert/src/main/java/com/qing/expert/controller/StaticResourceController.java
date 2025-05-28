package com.qing.expert.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 静态资源访问控制器
 */
@Slf4j
@RestController
@RequestMapping("/static")
public class StaticResourceController {

    @Value("${expert.file.upload-path:src/main/resources/static/}")
    private String uploadPath;

    /**
     * 获取头像文件
     */
    @GetMapping("/avatars/{fileName}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String fileName) {
        try {
            // 构建文件路径
            Path filePath = Paths.get(uploadPath, "avatars", fileName);

            // 检查文件是否存在
            if (!Files.exists(filePath)) {
                log.warn("头像文件不存在：{}", fileName);
                return ResponseEntity.notFound().build();
            }

            // 创建资源
            Resource resource = new FileSystemResource(filePath);

            // 获取文件的MIME类型
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setCacheControl("max-age=31536000"); // 缓存一年

            log.debug("返回头像文件：{}", fileName);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            log.error("获取头像文件失败：{}", fileName, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取轮播图文件
     */
    @GetMapping("/banner/{fileName}")
    public ResponseEntity<Resource> getBanner(@PathVariable String fileName) {
        try {
            // 构建文件路径
            Path filePath = Paths.get(uploadPath, "banner", fileName);

            // 检查文件是否存在
            if (!Files.exists(filePath)) {
                log.warn("轮播图文件不存在：{}", fileName);
                return ResponseEntity.notFound().build();
            }

            // 创建资源
            Resource resource = new FileSystemResource(filePath);

            // 获取文件的MIME类型
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setCacheControl("max-age=31536000"); // 缓存一年

            log.debug("返回轮播图文件：{}", fileName);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            log.error("获取轮播图文件失败：{}", fileName, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取通用图片文件
     */
    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        try {
            // 构建文件路径
            Path filePath = Paths.get(uploadPath, "images", fileName);

            // 检查文件是否存在
            if (!Files.exists(filePath)) {
                log.warn("图片文件不存在：{}", fileName);
                return ResponseEntity.notFound().build();
            }

            // 创建资源
            Resource resource = new FileSystemResource(filePath);

            // 获取文件的MIME类型
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setCacheControl("max-age=31536000"); // 缓存一年

            log.debug("返回图片文件：{}", fileName);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            log.error("获取图片文件失败：{}", fileName, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
