package com.qing.expert.controller.admin;

import com.qing.expert.common.result.PageResult;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.banner.BannerQueryDTO;
import com.qing.expert.dto.banner.BannerSaveDTO;
import com.qing.expert.entity.Banner;
import com.qing.expert.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/banner")
@RequiredArgsConstructor
@Tag(name = "轮播图管理", description = "轮播图的增删改查接口")
public class BannerManageController {

    private final BannerService bannerService;

    @Operation(summary = "分页查询轮播图列表", description = "根据条件分页查询轮播图列表")
    @GetMapping("/page")
    public Result<PageResult<Banner>> getBannerPage(@Validated BannerQueryDTO queryDTO) {
        PageResult<Banner> pageResult = bannerService.getBannerPage(queryDTO);
        return Result.success("查询成功", pageResult);
    }

    @Operation(summary = "获取轮播图详情", description = "根据轮播图ID获取轮播图详细信息")
    @GetMapping("/{bannerId}")
    public Result<Banner> getBannerDetail(
            @Parameter(description = "轮播图ID") @PathVariable Long bannerId) {
        Banner banner = bannerService.getBannerDetail(bannerId);
        return Result.success("获取成功", banner);
    }

    @Operation(summary = "保存轮播图", description = "新增或更新轮播图信息")
    @PostMapping("/save")
    public Result<Void> saveBanner(@Validated @RequestBody BannerSaveDTO saveDTO) {
        boolean success = bannerService.saveBanner(saveDTO);
        if (success) {
            return Result.success(saveDTO.getId() != null ? "更新成功" : "新增成功");
        } else {
            return Result.error(saveDTO.getId() != null ? "更新失败" : "新增失败");
        }
    }

    @Operation(summary = "删除轮播图", description = "根据轮播图ID删除轮播图")
    @DeleteMapping("/{bannerId}")
    public Result<Void> deleteBanner(
            @Parameter(description = "轮播图ID") @PathVariable Long bannerId) {
        boolean success = bannerService.deleteBanner(bannerId);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @Operation(summary = "更新轮播图状态", description = "启用或禁用轮播图")
    @PostMapping("/{bannerId}/status")
    public Result<Void> updateBannerStatus(
            @Parameter(description = "轮播图ID") @PathVariable Long bannerId,
            @Parameter(description = "状态：0-禁用，1-启用") @RequestParam Integer status) {
        boolean success = bannerService.updateBannerStatus(bannerId, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }

    @Operation(summary = "获取启用的轮播图列表", description = "获取启用状态的轮播图列表，按排序权重排序")
    @GetMapping("/enabled")
    public Result<List<Banner>> getEnabledBanners() {
        List<Banner> banners = bannerService.getEnabledBanners();
        return Result.success("获取成功", banners);
    }

    @Operation(summary = "批量更新排序权重", description = "批量更新轮播图的排序权重")
    @PostMapping("/sort")
    public Result<Void> updateSortOrders(
            @Parameter(description = "轮播图ID列表") @RequestParam List<Long> bannerIds,
            @Parameter(description = "排序权重列表") @RequestParam List<Integer> sortOrders) {
        boolean success = bannerService.updateSortOrders(bannerIds, sortOrders);
        if (success) {
            return Result.success("排序更新成功");
        } else {
            return Result.error("排序更新失败");
        }
    }
}
