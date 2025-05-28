package com.qing.expert.controller.admin;

import com.qing.expert.common.result.Result;
import com.qing.expert.entity.ExpertPhoto;
import com.qing.expert.service.ExpertPhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 达人照片管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/expert/photos")
@RequiredArgsConstructor
@Tag(name = "达人照片管理", description = "达人照片管理相关接口")
public class ExpertPhotoController {

    private final ExpertPhotoService expertPhotoService;

    @Operation(summary = "获取达人照片列表", description = "根据达人ID获取照片列表")
    @GetMapping("/{expertId}")
    public Result<List<ExpertPhoto>> getPhotosByExpertId(
            @Parameter(description = "达人ID") @PathVariable Long expertId) {
        try {
            List<ExpertPhoto> photos = expertPhotoService.getPhotosByExpertId(expertId);
            
            // 为每个照片添加访问URL
            photos.forEach(photo -> {
                String photoUrl = expertPhotoService.getPhotoUrl(photo.getPhotoName());
                // 这里可以添加一个临时字段存储URL，或者在前端处理
            });
            
            return Result.success("获取成功", photos);
        } catch (Exception e) {
            log.error("获取达人照片列表失败：expertId={}", expertId, e);
            return Result.error("获取照片列表失败：" + e.getMessage());
        }
    }

    @Operation(summary = "上传达人照片", description = "为达人上传新照片")
    @PostMapping("/{expertId}/upload")
    public Result<Map<String, Object>> uploadPhoto(
            @Parameter(description = "达人ID") @PathVariable Long expertId,
            @Parameter(description = "照片文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "照片标题") @RequestParam(value = "photoTitle", required = false) String photoTitle,
            @Parameter(description = "照片描述") @RequestParam(value = "photoDescription", required = false) String photoDescription) {
        try {
            ExpertPhoto photo = expertPhotoService.uploadPhoto(expertId, file, photoTitle, photoDescription);
            
            Map<String, Object> result = new HashMap<>();
            result.put("photo", photo);
            result.put("photoUrl", expertPhotoService.getPhotoUrl(photo.getPhotoName()));
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());
            
            return Result.success("照片上传成功", result);
        } catch (Exception e) {
            log.error("上传达人照片失败：expertId={}", expertId, e);
            return Result.error("照片上传失败：" + e.getMessage());
        }
    }

    @Operation(summary = "删除达人照片", description = "删除指定的达人照片")
    @DeleteMapping("/{photoId}")
    public Result<Void> deletePhoto(
            @Parameter(description = "照片ID") @PathVariable Long photoId) {
        try {
            boolean success = expertPhotoService.deletePhoto(photoId);
            if (success) {
                return Result.success("照片删除成功");
            } else {
                return Result.error("照片删除失败");
            }
        } catch (Exception e) {
            log.error("删除达人照片失败：photoId={}", photoId, e);
            return Result.error("照片删除失败：" + e.getMessage());
        }
    }

    @Operation(summary = "更新照片信息", description = "更新照片的标题和描述")
    @PutMapping("/{photoId}")
    public Result<Void> updatePhotoInfo(
            @Parameter(description = "照片ID") @PathVariable Long photoId,
            @Parameter(description = "照片标题") @RequestParam(value = "photoTitle", required = false) String photoTitle,
            @Parameter(description = "照片描述") @RequestParam(value = "photoDescription", required = false) String photoDescription) {
        try {
            boolean success = expertPhotoService.updatePhotoInfo(photoId, photoTitle, photoDescription);
            if (success) {
                return Result.success("照片信息更新成功");
            } else {
                return Result.error("照片信息更新失败");
            }
        } catch (Exception e) {
            log.error("更新照片信息失败：photoId={}", photoId, e);
            return Result.error("照片信息更新失败：" + e.getMessage());
        }
    }

    @Operation(summary = "更新照片排序", description = "批量更新照片排序")
    @PutMapping("/sort")
    public Result<Void> updatePhotoSort(
            @Parameter(description = "照片ID列表（按新的排序顺序）") @RequestBody List<Long> photoIds) {
        try {
            boolean success = expertPhotoService.batchUpdatePhotoSort(photoIds);
            if (success) {
                return Result.success("照片排序更新成功");
            } else {
                return Result.error("照片排序更新失败");
            }
        } catch (Exception e) {
            log.error("更新照片排序失败", e);
            return Result.error("照片排序更新失败：" + e.getMessage());
        }
    }

    @Operation(summary = "删除达人所有照片", description = "删除指定达人的所有照片")
    @DeleteMapping("/expert/{expertId}")
    public Result<Void> deletePhotosByExpertId(
            @Parameter(description = "达人ID") @PathVariable Long expertId) {
        try {
            boolean success = expertPhotoService.deletePhotosByExpertId(expertId);
            if (success) {
                return Result.success("达人所有照片删除成功");
            } else {
                return Result.error("达人所有照片删除失败");
            }
        } catch (Exception e) {
            log.error("删除达人所有照片失败：expertId={}", expertId, e);
            return Result.error("删除达人所有照片失败：" + e.getMessage());
        }
    }
}
