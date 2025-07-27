package com.qing.expert.controller.admin;

import com.qing.expert.common.result.Result;
import com.qing.expert.entity.ExpertPhoto;
import com.qing.expert.service.ExpertPhotoService;
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
    @GetMapping("/{expertId}")
    public Result<List<ExpertPhoto>> getPhotosByExpertId(
            
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

    public Result<Map<String, Object>> uploadPhoto(
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

    public Result<Void> deletePhoto(
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

    public Result<Void> updatePhotoInfo(
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

    public Result<Void> updatePhotoSort(
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

    public Result<Void> deletePhotosByExpertId(
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
