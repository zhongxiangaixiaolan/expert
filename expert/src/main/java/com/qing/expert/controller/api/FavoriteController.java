package com.qing.expert.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.favorite.FavoriteCreateDTO;
import com.qing.expert.dto.favorite.FavoriteQueryDTO;
import com.qing.expert.service.FavoriteService;
import com.qing.expert.util.SecurityUtil;
import com.qing.expert.vo.favorite.FavoriteVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 收藏控制器
 * 注意：由于配置了context-path: /api，这里的路径是相对于context-path的
 */
@Slf4j
@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
    @PostMapping("/add")
    public Result<Void> addFavorite(@Validated @RequestBody FavoriteCreateDTO createDTO) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            createDTO.setUserId(currentUserId);

            boolean success = favoriteService.addFavorite(createDTO);
            if (success) {
                return Result.success("收藏成功");
            } else {
                return Result.error("收藏失败");
            }
        } catch (Exception e) {
            log.error("添加收藏失败", e);
            return Result.error("收藏失败：" + e.getMessage());
        }
    }

    public Result<Void> removeFavorite(
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();

            boolean success = favoriteService.removeFavorite(currentUserId, favoriteType, targetId);
            if (success) {
                return Result.success("取消收藏成功");
            } else {
                return Result.error("取消收藏失败");
            }
        } catch (Exception e) {
            log.error("取消收藏失败", e);
            return Result.error("取消收藏失败：" + e.getMessage());
        }
    }


            boolean success = favoriteService.deleteFavorite(favoriteId, currentUserId);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除收藏失败", e);
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    public Result<IPage<FavoriteVO>> getFavoriteList(@Validated FavoriteQueryDTO queryDTO) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            queryDTO.setUserId(currentUserId);

            IPage<FavoriteVO> page = favoriteService.getFavoritePage(queryDTO);
            return Result.success("获取成功", page);
        } catch (Exception e) {
            log.error("获取收藏列表失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<Boolean> checkFavorite(
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();

            boolean isFavorite = favoriteService.checkFavorite(currentUserId, favoriteType, targetId);
            return Result.success("检查完成", isFavorite);
        } catch (Exception e) {
            log.error("检查收藏状态失败", e);
            return Result.error("检查失败：" + e.getMessage());
        }
    }

    public Result<Object> getFavoriteStatistics() {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();

            Object statistics = favoriteService.getFavoriteStatistics(currentUserId);
            return Result.success("获取成功", statistics);
        } catch (Exception e) {
            log.error("获取收藏统计失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }
}
