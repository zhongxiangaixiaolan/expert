package com.qing.expert.controller;

import com.qing.expert.common.result.Result;
import com.qing.expert.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件访问控制器
 * 注意：由于配置了context-path: /api，这里的路径是相对于context-path的
 */
@Slf4j
@RestController
@RequestMapping("/files")
public class FileController {

    @Value("${expert.file.upload-path}")
    private String uploadPath;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    public ResponseEntity<Resource> getAvatar(
            File file = filePath.toFile();

            log.debug("请求头像文件: {}, 完整路径: {}", filename, filePath.toAbsolutePath());

            if (!file.exists() || !file.isFile()) {
                log.warn("头像文件不存在: {}", filePath.toAbsolutePath());
                return ResponseEntity.notFound().build();
            }

            // 创建资源
            Resource resource = new FileSystemResource(file);

            // 确定文件类型
            String contentType = getContentType(filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600") // 缓存1小时
                    .body(resource);

        } catch (Exception e) {
            log.error("获取头像文件失败: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Resource> getFile(HttpServletRequest request) {
        try {
            // 获取请求路径 - 由于控制器路径已改为 /files，这里需要相应调整
            String requestURI = request.getRequestURI();
            String contextPath = request.getContextPath();
            String requestPath;

            if (contextPath != null && !contextPath.isEmpty()) {
                // 有context-path的情况，如 /api/files/xxx -> xxx
                requestPath = requestURI.substring((contextPath + "/files/").length());
            } else {
                // 没有context-path的情况，如 /files/xxx -> xxx
                requestPath = requestURI.substring("/files/".length());
            }

            // 构建文件路径
            Path filePath = Paths.get(System.getProperty("user.dir"), uploadPath, requestPath);
            File file = filePath.toFile();

            if (!file.exists() || !file.isFile()) {
                log.warn("文件不存在: {}", filePath);
                return ResponseEntity.notFound().build();
            }

            // 创建资源
            Resource resource = new FileSystemResource(file);

            // 确定文件类型
            String contentType = getContentType(file.getName());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600") // 缓存1小时
                    .body(resource);

        } catch (Exception e) {
            log.error("获取文件失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public Result<Map<String, Object>> uploadAvatar(

            Map<String, Object> result = new HashMap<>();
            result.put("url", filePath);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());

            return Result.success("上传成功", result);
        } catch (Exception e) {
            log.error("上传头像失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    public Result<Map<String, Object>> uploadScreenshot(

            Map<String, Object> result = new HashMap<>();
            result.put("url", filePath);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());

            return Result.success("上传成功", result);
        } catch (Exception e) {
            log.error("上传截图失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    public Result<Map<String, Object>> uploadBanner(

            Map<String, Object> result = new HashMap<>();
            result.put("url", filePath);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());

            return Result.success("上传成功", result);
        } catch (Exception e) {
            log.error("上传轮播图失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    public Result<Map<String, Object>> uploadFile(
        try {
            String filePath;
            switch (type.toLowerCase()) {
                case "avatar":
                    filePath = fileUploadUtil.uploadAvatar(file);
                    break;
                case "banner":
                    filePath = fileUploadUtil.uploadBannerFile(file);
                    break;
                case "image":
                case "screenshot":
                    filePath = fileUploadUtil.uploadImage(file);
                    break;
                case "document":
                    filePath = fileUploadUtil.uploadDocument(file);
                    break;
                default:
                    filePath = fileUploadUtil.uploadFile(file, type);
                    break;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("url", filePath);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());

            return Result.success("上传成功", result);
        } catch (Exception e) {
            log.error("上传文件失败", e);
            return Result.error("上传失败：" + e.getMessage());
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
