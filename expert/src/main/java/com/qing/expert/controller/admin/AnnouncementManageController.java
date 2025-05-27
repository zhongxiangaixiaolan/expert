package com.qing.expert.controller.admin;

import com.qing.expert.common.result.PageResult;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.announcement.AnnouncementQueryDTO;
import com.qing.expert.dto.announcement.AnnouncementSaveDTO;
import com.qing.expert.entity.Announcement;
import com.qing.expert.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "通告管理", description = "通告的增删改查接口")
public class AnnouncementManageController {

    private final AnnouncementService announcementService;

    @Operation(summary = "分页查询通告列表", description = "根据条件分页查询通告列表")
    @GetMapping("/page")
    public Result<PageResult<Announcement>> getAnnouncementPage(@Validated AnnouncementQueryDTO queryDTO) {
        PageResult<Announcement> pageResult = announcementService.getAnnouncementPage(queryDTO);
        return Result.success("查询成功", pageResult);
    }

    @Operation(summary = "获取通告详情", description = "根据通告ID获取通告详细信息")
    @GetMapping("/{announcementId}")
    public Result<Announcement> getAnnouncementDetail(
            @Parameter(description = "通告ID") @PathVariable Long announcementId) {
        Announcement announcement = announcementService.getAnnouncementDetail(announcementId);
        return Result.success("获取成功", announcement);
    }

    @Operation(summary = "保存通告", description = "新增或更新通告信息")
    @PostMapping("/save")
    public Result<Void> saveAnnouncement(@Validated @RequestBody AnnouncementSaveDTO saveDTO) {
        boolean success = announcementService.saveAnnouncement(saveDTO);
        if (success) {
            return Result.success(saveDTO.getId() != null ? "更新成功" : "新增成功");
        } else {
            return Result.error(saveDTO.getId() != null ? "更新失败" : "新增失败");
        }
    }

    @Operation(summary = "删除通告", description = "根据通告ID删除通告")
    @DeleteMapping("/{announcementId}")
    public Result<Void> deleteAnnouncement(
            @Parameter(description = "通告ID") @PathVariable Long announcementId) {
        boolean success = announcementService.deleteAnnouncement(announcementId);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @Operation(summary = "更新通告状态", description = "启用或禁用通告")
    @PostMapping("/{announcementId}/status")
    public Result<Void> updateAnnouncementStatus(
            @Parameter(description = "通告ID") @PathVariable Long announcementId,
            @Parameter(description = "状态：0-禁用，1-启用") @RequestParam Integer status) {
        boolean success = announcementService.updateAnnouncementStatus(announcementId, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }

    @Operation(summary = "获取启用的通告列表", description = "获取启用状态的通告列表，按排序权重排序")
    @GetMapping("/enabled")
    public Result<List<Announcement>> getEnabledAnnouncements() {
        List<Announcement> announcements = announcementService.getEnabledAnnouncements();
        return Result.success("获取成功", announcements);
    }

    @Operation(summary = "获取滚动显示的通告列表", description = "获取滚动显示的通告列表")
    @GetMapping("/scroll")
    public Result<List<Announcement>> getScrollAnnouncements() {
        List<Announcement> announcements = announcementService.getScrollAnnouncements();
        return Result.success("获取成功", announcements);
    }

    @Operation(summary = "批量更新排序权重", description = "批量更新通告的排序权重")
    @PostMapping("/sort")
    public Result<Void> updateSortOrders(
            @Parameter(description = "通告ID列表") @RequestParam List<Long> announcementIds,
            @Parameter(description = "排序权重列表") @RequestParam List<Integer> sortOrders) {
        boolean success = announcementService.updateSortOrders(announcementIds, sortOrders);
        if (success) {
            return Result.success("排序更新成功");
        } else {
            return Result.error("排序更新失败");
        }
    }
}
