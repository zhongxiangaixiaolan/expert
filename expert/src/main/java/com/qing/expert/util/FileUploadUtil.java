package com.qing.expert.util;

import com.qing.expert.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传工具类
 */
@Slf4j
@Component
public class FileUploadUtil {

    @Value("${expert.file.upload-path:uploads/}")
    private String uploadPath;

    @Value("${expert.file.max-size:10485760}")
    private long maxSize;

    @Value("${expert.file.allowed-types:jpg,jpeg,png,gif,pdf,doc,docx}")
    private String allowedTypes;

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小不能超过" + (maxSize / 1024 / 1024) + "MB");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("文件名不能为空");
        }

        String fileExtension = getFileExtension(originalFilename);
        if (!isAllowedFileType(fileExtension)) {
            throw new BusinessException("不支持的文件类型：" + fileExtension);
        }

        try {
            // 创建上传目录
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fullUploadPath = uploadPath + "/" + subDir + "/" + datePath;
            Path uploadDir = Paths.get(fullUploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 生成新文件名
            String newFileName = UUID.randomUUID().toString() + "." + fileExtension;
            Path filePath = uploadDir.resolve(newFileName);

            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            // 返回相对路径
            String relativePath = subDir + "/" + datePath + "/" + newFileName;
            log.info("文件上传成功：{}", relativePath);
            return relativePath;

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传头像
     */
    public String uploadAvatar(MultipartFile file) {
        return uploadAvatarFile(file);
    }

    /**
     * 上传头像文件（不使用日期文件夹）
     */
    public String uploadAvatarFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小不能超过" + (maxSize / 1024 / 1024) + "MB");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("文件名不能为空");
        }

        String fileExtension = getFileExtension(originalFilename);
        if (!isAllowedFileType(fileExtension)) {
            throw new BusinessException("不支持的文件类型：" + fileExtension);
        }

        try {
            // 创建avatars目录（不使用日期子目录）
            String fullUploadPath = uploadPath + "/avatars";
            Path uploadDir = Paths.get(fullUploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 生成新文件名
            String newFileName = UUID.randomUUID().toString() + "." + fileExtension;
            Path filePath = uploadDir.resolve(newFileName);

            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            // 只返回文件名，不包含路径
            log.info("头像文件上传成功：{}", newFileName);
            return newFileName;

        } catch (IOException e) {
            log.error("头像文件上传失败", e);
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传图片
     */
    public String uploadImage(MultipartFile file) {
        return uploadFile(file, "images");
    }

    /**
     * 上传文档
     */
    public String uploadDocument(MultipartFile file) {
        return uploadFile(file, "documents");
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }

        try {
            Path path = Paths.get(uploadPath, filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("文件删除成功：{}", filePath);
                return true;
            }
        } catch (IOException e) {
            log.error("文件删除失败：{}", filePath, e);
        }
        return false;
    }

    /**
     * 删除头像文件
     */
    public boolean deleteAvatarFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        try {
            // 头像文件在avatars目录下
            Path path = Paths.get(uploadPath, "avatars", fileName);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("头像文件删除成功：{}", fileName);
                return true;
            }
        } catch (IOException e) {
            log.error("头像文件删除失败：{}", fileName, e);
        }
        return false;
    }

    /**
     * 上传达人照片文件（不使用日期文件夹）
     */
    public String uploadPhotoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小不能超过" + (maxSize / 1024 / 1024) + "MB");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("文件名不能为空");
        }

        String fileExtension = getFileExtension(originalFilename);
        if (!isAllowedFileType(fileExtension)) {
            throw new BusinessException("不支持的文件类型：" + fileExtension);
        }

        try {
            // 创建photos目录（不使用日期子目录）
            String fullUploadPath = uploadPath + "/photos";
            Path uploadDir = Paths.get(fullUploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 生成新文件名
            String newFileName = UUID.randomUUID().toString() + "." + fileExtension;
            Path filePath = uploadDir.resolve(newFileName);

            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            // 只返回文件名，不包含路径
            log.info("照片文件上传成功：{}", newFileName);
            return newFileName;

        } catch (IOException e) {
            log.error("照片文件上传失败", e);
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 删除照片文件
     */
    public boolean deletePhotoFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        try {
            // 照片文件在photos目录下
            Path path = Paths.get(uploadPath, "photos", fileName);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("照片文件删除成功：{}", fileName);
                return true;
            }
        } catch (IOException e) {
            log.error("照片文件删除失败：{}", fileName, e);
        }
        return false;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 检查是否为允许的文件类型
     */
    private boolean isAllowedFileType(String fileExtension) {
        List<String> allowedTypeList = Arrays.asList(allowedTypes.split(","));
        return allowedTypeList.contains(fileExtension.toLowerCase());
    }

    /**
     * 上传轮播图文件（不使用日期文件夹）
     */
    public String uploadBannerFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小不能超过" + (maxSize / 1024 / 1024) + "MB");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("文件名不能为空");
        }

        String fileExtension = getFileExtension(originalFilename);
        if (!isAllowedFileType(fileExtension)) {
            throw new BusinessException("不支持的文件类型：" + fileExtension);
        }

        try {
            // 创建banner目录（不使用日期子目录）
            String fullUploadPath = uploadPath + "/banner";
            Path uploadDir = Paths.get(fullUploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 生成新文件名
            String newFileName = UUID.randomUUID().toString() + "." + fileExtension;
            Path filePath = uploadDir.resolve(newFileName);

            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            // 只返回文件名，不包含路径
            log.info("轮播图文件上传成功：{}", newFileName);
            return newFileName;

        } catch (IOException e) {
            log.error("轮播图文件上传失败", e);
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 删除轮播图文件
     */
    public boolean deleteBannerFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        try {
            // 轮播图文件在banner目录下
            Path path = Paths.get(uploadPath, "banner", fileName);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("轮播图文件删除成功：{}", fileName);
                return true;
            }
        } catch (IOException e) {
            log.error("轮播图文件删除失败：{}", fileName, e);
        }
        return false;
    }

    /**
     * 获取文件访问URL
     */
    public String getFileUrl(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return null;
        }
        // 这里可以根据实际情况返回完整的访问URL
        // 例如：return "http://localhost:8080/api/files/" + filePath;
        return "/api/files/" + filePath;
    }
}
