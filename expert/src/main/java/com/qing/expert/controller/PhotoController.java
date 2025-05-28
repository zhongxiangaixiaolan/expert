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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 照片访问控制器
 */
@Slf4j
@RestController
@RequestMapping("/photos")
public class PhotoController {

    @Value("${expert.file.upload-path:src/main/resources/static/}")
    private String uploadPath;

    /**
     * 获取照片文件
     * @param filename 文件名
     * @return 照片文件
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getPhoto(@PathVariable String filename) {
        try {
            // 构建文件路径
            Path filePath = Paths.get(uploadPath, "photos", filename);
            File file = filePath.toFile();

            if (!file.exists() || !file.isFile()) {
                log.warn("照片文件不存在: {}", filename);
                return ResponseEntity.notFound().build();
            }

            // 检查文件是否在允许的目录内（安全检查）
            Path photosDir = Paths.get(uploadPath, "photos").toAbsolutePath().normalize();
            Path requestedFile = filePath.toAbsolutePath().normalize();
            if (!requestedFile.startsWith(photosDir)) {
                log.warn("非法的文件访问请求: {}", filename);
                return ResponseEntity.badRequest().build();
            }

            Resource resource = new FileSystemResource(file);

            // 确定文件的MIME类型
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                // 根据文件扩展名确定MIME类型
                String extension = getFileExtension(filename).toLowerCase();
                switch (extension) {
                    case "jpg":
                    case "jpeg":
                        contentType = "image/jpeg";
                        break;
                    case "png":
                        contentType = "image/png";
                        break;
                    case "gif":
                        contentType = "image/gif";
                        break;
                    case "webp":
                        contentType = "image/webp";
                        break;
                    default:
                        contentType = "application/octet-stream";
                }
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600") // 缓存1小时
                    .body(resource);

        } catch (Exception e) {
            log.error("获取照片文件失败: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
