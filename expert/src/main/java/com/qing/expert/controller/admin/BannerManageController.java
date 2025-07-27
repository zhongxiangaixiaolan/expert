package com.qing.expert.controller.admin;

import com.qing.expert.common.result.PageResult;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.banner.BannerQueryDTO;
import com.qing.expert.dto.banner.BannerSaveDTO;
import com.qing.expert.entity.Banner;
import com.qing.expert.service.BannerService;
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
    @GetMapping("/page")
    public Result<PageResult<Banner>> getBannerPage(@Validated BannerQueryDTO queryDTO) {
        PageResult<Banner> pageResult = bannerService.getBannerPage(queryDTO);
        return Result.success("查询成功", pageResult);
    }

    public Result<Banner> getBannerDetail(
        return Result.success("获取成功", banner);
    }

    public Result<Void> saveBanner(@Validated @RequestBody BannerSaveDTO saveDTO) {
        boolean success = bannerService.saveBanner(saveDTO);
        if (success) {
            return Result.success(saveDTO.getId() != null ? "更新成功" : "新增成功");
        } else {
            return Result.error(saveDTO.getId() != null ? "更新失败" : "新增失败");
        }
    }

    public Result<Void> deleteBanner(
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    public Result<Void> updateBannerStatus(
        boolean success = bannerService.updateBannerStatus(bannerId, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }

    public Result<List<Banner>> getEnabledBanners() {
        List<Banner> banners = bannerService.getEnabledBanners();
        return Result.success("获取成功", banners);
    }

    public Result<Void> updateSortOrders(
        boolean success = bannerService.updateSortOrders(bannerIds, sortOrders);
        if (success) {
            return Result.success("排序更新成功");
        } else {
            return Result.error("排序更新失败");
        }
    }
}
