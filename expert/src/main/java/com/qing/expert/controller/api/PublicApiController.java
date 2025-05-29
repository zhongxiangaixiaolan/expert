package com.qing.expert.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.common.result.PageResult;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.ExpertQueryDTO;
import com.qing.expert.dto.order.OrderCreateDTO;
import com.qing.expert.entity.Announcement;
import com.qing.expert.entity.Banner;
import com.qing.expert.entity.Category;
import com.qing.expert.entity.ExpertPhoto;
import com.qing.expert.service.*;
import com.qing.expert.util.SecurityUtil;
import com.qing.expert.vo.ExpertDetailVO;
import com.qing.expert.vo.ServiceVO;
import com.qing.expert.vo.order.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公共API控制器
 * 注意：由于配置了context-path: /api，这里的路径是相对于context-path的
 */
@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name = "公共接口", description = "不需要特殊权限的公共接口")
public class PublicApiController {

    private final BannerService bannerService;
    private final AnnouncementService announcementService;
    private final CategoryService categoryService;
    private final ExpertService expertService;
    private final OrderService orderService;
    private final ServiceService serviceService;
    private final ExpertPhotoService expertPhotoService;

    @Operation(summary = "获取轮播图列表", description = "获取启用状态的轮播图列表")
    @GetMapping("/banners")
    public Result<List<Banner>> getBanners() {
        try {
            List<Banner> banners = bannerService.getEnabledBanners();
            return Result.success("获取成功", banners);
        } catch (Exception e) {
            log.error("获取轮播图列表失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取公告列表", description = "获取滚动显示的公告列表")
    @GetMapping("/announcements")
    public Result<List<Announcement>> getAnnouncements() {
        try {
            List<Announcement> announcements = announcementService.getScrollAnnouncements();
            return Result.success("获取成功", announcements);
        } catch (Exception e) {
            log.error("获取公告列表失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取公告详情", description = "根据ID获取公告详情")
    @GetMapping("/announcements/{id}")
    public Result<Announcement> getAnnouncementDetail(@Parameter(description = "公告ID") @PathVariable Long id) {
        try {
            Announcement announcement = announcementService.getById(id);
            if (announcement == null) {
                return Result.error("公告不存在");
            }
            return Result.success("获取成功", announcement);
        } catch (Exception e) {
            log.error("获取公告详情失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取分类列表", description = "获取启用状态的分类列表")
    @GetMapping("/categories")
    public Result<List<Category>> getCategories() {
        try {
            List<Category> categories = categoryService.getEnabledCategories();
            return Result.success("获取成功", categories);
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取分类详情", description = "根据分类ID获取分类详情")
    @GetMapping("/categories/{categoryId}")
    public Result<Category> getCategoryDetail(@Parameter(description = "分类ID") @PathVariable Long categoryId) {
        try {
            Category category = categoryService.getCategoryDetail(categoryId);
            return Result.success("获取成功", category);
        } catch (Exception e) {
            log.error("获取分类详情失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取达人列表", description = "分页获取达人列表")
    @GetMapping("/experts")
    public Result<PageResult<ExpertDetailVO>> getExperts(@Validated ExpertQueryDTO queryDTO) {
        try {
            // 只查询审核通过的达人
            queryDTO.setAuditStatus(1);
            PageResult<ExpertDetailVO> pageResult = expertService.getExpertPage(queryDTO);
            return Result.success("获取成功", pageResult);
        } catch (Exception e) {
            log.error("获取达人列表失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取达人详情", description = "根据ID获取达人详情")
    @GetMapping("/experts/{id}")
    public Result<ExpertDetailVO> getExpertDetail(@Parameter(description = "达人ID") @PathVariable Long id) {
        try {
            log.info("获取达人详情请求：expertId={}", id);
            ExpertDetailVO expertDetail = expertService.getExpertDetail(id);
            if (expertDetail == null) {
                log.warn("达人不存在：expertId={}", id);
                return Result.error("达人不存在");
            }
            log.info("获取达人详情成功：expertId={}, expertName={}", id, expertDetail.getExpertName());
            return Result.success("获取成功", expertDetail);
        } catch (Exception e) {
            log.error("获取达人详情失败：expertId={}", id, e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "创建订单", description = "创建新订单")
    @PostMapping("/orders")
    public Result<Long> createOrder(@Validated @RequestBody OrderCreateDTO createDTO) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            if (currentUserId == null) {
                return Result.error("请先登录");
            }

            Long orderId = orderService.createOrder(createDTO, currentUserId);
            return Result.success("创建成功", orderId);
        } catch (Exception e) {
            log.error("创建订单失败", e);
            return Result.error("创建失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取订单详情", description = "根据ID获取订单详情")
    @GetMapping("/orders/{id}")
    public Result<OrderVO> getOrderDetail(@Parameter(description = "订单ID") @PathVariable Long id) {
        try {
            OrderVO orderVO = orderService.getOrderById(id);
            if (orderVO == null) {
                return Result.error("订单不存在");
            }
            return Result.success("获取成功", orderVO);
        } catch (Exception e) {
            log.error("获取订单详情失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "取消订单", description = "取消订单")
    @PutMapping("/orders/{id}/cancel")
    public Result<Void> cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @RequestBody(required = false) String reason) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            if (currentUserId == null) {
                return Result.error("请先登录");
            }

            boolean success = orderService.cancelOrder(id, currentUserId, reason);
            if (success) {
                return Result.success("取消成功");
            } else {
                return Result.error("取消失败");
            }
        } catch (Exception e) {
            log.error("取消订单失败", e);
            return Result.error("取消失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取服务列表", description = "分页获取服务列表")
    @GetMapping("/services")
    public Result<IPage<ServiceVO>> getServices(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "达人ID") @RequestParam(required = false) Long expertId,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "是否热门") @RequestParam(required = false) Integer isHot,
            @Parameter(description = "是否推荐") @RequestParam(required = false) Integer isRecommend) {
        try {
            Page<ServiceVO> page = new Page<>(current, size);
            IPage<ServiceVO> result = serviceService.getServicePage(page, expertId, categoryId, keyword, 1, isHot,
                    isRecommend);
            return Result.success("获取成功", result);
        } catch (Exception e) {
            log.error("获取服务列表失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取服务详情", description = "根据服务ID获取服务详情")
    @GetMapping("/services/{serviceId}")
    public Result<ServiceVO> getServiceDetail(@Parameter(description = "服务ID") @PathVariable Long serviceId) {
        try {
            ServiceVO service = serviceService.getServiceById(serviceId);
            return Result.success("获取成功", service);
        } catch (Exception e) {
            log.error("获取服务详情失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取热门服务", description = "获取热门服务列表")
    @GetMapping("/services/hot")
    public Result<List<ServiceVO>> getHotServices(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<ServiceVO> services = serviceService.getHotServices(limit);
            return Result.success("获取成功", services);
        } catch (Exception e) {
            log.error("获取热门服务失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取推荐服务", description = "获取推荐服务列表")
    @GetMapping("/services/recommend")
    public Result<List<ServiceVO>> getRecommendServices(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<ServiceVO> services = serviceService.getRecommendServices(limit);
            return Result.success("获取成功", services);
        } catch (Exception e) {
            log.error("获取推荐服务失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取分类下的服务", description = "获取指定分类下的服务列表")
    @GetMapping("/categories/{categoryId}/services")
    public Result<List<ServiceVO>> getCategoryServices(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @Parameter(description = "数量限制") @RequestParam(required = false) Integer limit) {
        try {
            List<ServiceVO> services = serviceService.getServicesByCategoryId(categoryId, 1, limit);
            return Result.success("获取成功", services);
        } catch (Exception e) {
            log.error("获取分类服务失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取达人的服务", description = "获取指定达人的服务列表")
    @GetMapping("/experts/{expertId}/services")
    public Result<List<ServiceVO>> getExpertServices(@Parameter(description = "达人ID") @PathVariable Long expertId) {
        try {
            List<ServiceVO> services = serviceService.getServicesByExpertId(expertId, 1);
            return Result.success("获取成功", services);
        } catch (Exception e) {
            log.error("获取达人服务失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取热门达人", description = "获取热门达人列表")
    @GetMapping("/experts/hot")
    public Result<List<ExpertDetailVO>> getHotExperts(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<ExpertDetailVO> hotExperts = expertService.getHotExperts(limit);
            return Result.success("获取成功", hotExperts);
        } catch (Exception e) {
            log.error("获取热门达人失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取达人照片", description = "获取指定达人的照片列表")
    @GetMapping("/experts/{expertId}/photos")
    public Result<List<ExpertPhoto>> getExpertPhotos(@Parameter(description = "达人ID") @PathVariable Long expertId) {
        try {
            log.info("获取达人照片请求：expertId={}", expertId);
            List<ExpertPhoto> photos = expertPhotoService.getPhotosByExpertId(expertId);

            // 为每个照片添加完整的访问URL
            photos.forEach(photo -> {
                String photoUrl = expertPhotoService.getPhotoUrl(photo.getPhotoName());
                // 这里可以添加一个临时字段存储URL，或者在前端处理
                log.debug("照片URL：photoName={}, photoUrl={}", photo.getPhotoName(), photoUrl);
            });

            log.info("获取达人照片成功：expertId={}, 照片数量={}", expertId, photos.size());
            return Result.success("获取成功", photos);
        } catch (Exception e) {
            log.error("获取达人照片失败：expertId={}", expertId, e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }
}
