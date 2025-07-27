package com.qing.expert.controller.admin;

import com.qing.expert.common.result.PageResult;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.announcement.AnnouncementQueryDTO;
import com.qing.expert.dto.announcement.AnnouncementSaveDTO;
import com.qing.expert.entity.Announcement;
import com.qing.expert.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通告管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/announcement")
@RequiredArgsConstructor
public class AnnouncementManageController {

    private final AnnouncementService announcementService;

    @GetMapping("/page")
    public Result<PageResult<Announcement>> getAnnouncementPage(@Validated AnnouncementQueryDTO queryDTO) {
        PageResult<Announcement> pageResult = announcementService.getAnnouncementPage(queryDTO);
        return Result.success("查询成功", pageResult);
    }

    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncementDetail(@PathVariable Long id) {
        Announcement announcement = announcementService.getAnnouncementDetail(id);
        return Result.success("获取成功", announcement);
    }

    @PostMapping
    public Result<Void> saveAnnouncement(@Validated @RequestBody AnnouncementSaveDTO saveDTO) {
        boolean success = announcementService.saveAnnouncement(saveDTO);
        if (success) {
            return Result.success(saveDTO.getId() != null ? "更新成功" : "新增成功");
        } else {
            return Result.error(saveDTO.getId() != null ? "更新失败" : "新增失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        boolean success = announcementService.deleteAnnouncement(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateAnnouncementStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = announcementService.updateAnnouncementStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }

    @GetMapping("/enabled")
    public Result<List<Announcement>> getEnabledAnnouncements() {
        List<Announcement> announcements = announcementService.getEnabledAnnouncements();
        return Result.success("获取成功", announcements);
    }

    @GetMapping("/scroll")
    public Result<List<Announcement>> getScrollAnnouncements() {
        List<Announcement> announcements = announcementService.getScrollAnnouncements();
        return Result.success("获取成功", announcements);
    }
}
