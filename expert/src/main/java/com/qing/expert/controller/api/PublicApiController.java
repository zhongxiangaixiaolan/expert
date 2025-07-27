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

    public Result<List<Announcement>> getAnnouncements() {
        try {
            List<Announcement> announcements = announcementService.getScrollAnnouncements();
            return Result.success("获取成功", announcements);
        } catch (Exception e) {
            log.error("获取公告列表失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

            if (announcement == null) {
                return Result.error("公告不存在");
            }
            return Result.success("获取成功", announcement);
        } catch (Exception e) {
            log.error("获取公告详情失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<List<Category>> getCategories() {
        try {
            List<Category> categories = categoryService.getEnabledCategories();
            return Result.success("获取成功", categories);
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

            return Result.success("获取成功", category);
        } catch (Exception e) {
            log.error("获取分类详情失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

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

            if (orderVO == null) {
                return Result.error("订单不存在");
            }
            return Result.success("获取成功", orderVO);
        } catch (Exception e) {
            log.error("获取订单详情失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<Void> cancelOrder(
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

    public Result<IPage<ServiceVO>> getServices(
            IPage<ServiceVO> result = serviceService.getServicePage(page, expertId, categoryId, keyword, 1, isHot,
                    isRecommend);
            return Result.success("获取成功", result);
        } catch (Exception e) {
            log.error("获取服务列表失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

            return Result.success("获取成功", service);
        } catch (Exception e) {
            log.error("获取服务详情失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<List<ServiceVO>> getHotServices(
            return Result.success("获取成功", services);
        } catch (Exception e) {
            log.error("获取热门服务失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<List<ServiceVO>> getRecommendServices(
            return Result.success("获取成功", services);
        } catch (Exception e) {
            log.error("获取推荐服务失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<List<ServiceVO>> getCategoryServices(
        try {
            List<ServiceVO> services = serviceService.getServicesByCategoryId(categoryId, 1, limit);
            return Result.success("获取成功", services);
        } catch (Exception e) {
            log.error("获取分类服务失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

            return Result.success("获取成功", services);
        } catch (Exception e) {
            log.error("获取达人服务失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<List<ExpertDetailVO>> getHotExperts(
            return Result.success("获取成功", hotExperts);
        } catch (Exception e) {
            log.error("获取热门达人失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

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
