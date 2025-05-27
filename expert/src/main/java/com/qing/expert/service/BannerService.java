package com.qing.expert.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.common.result.PageResult;
import com.qing.expert.dto.banner.BannerQueryDTO;
import com.qing.expert.dto.banner.BannerSaveDTO;
import com.qing.expert.entity.Banner;

import java.util.List;

/**
 * 轮播图服务接口
 */
public interface BannerService extends IService<Banner> {

    /**
     * 分页查询轮播图列表
     */
    PageResult<Banner> getBannerPage(BannerQueryDTO queryDTO);

    /**
     * 获取轮播图详情
     */
    Banner getBannerDetail(Long bannerId);

    /**
     * 保存轮播图
     */
    boolean saveBanner(BannerSaveDTO saveDTO);

    /**
     * 删除轮播图
     */
    boolean deleteBanner(Long bannerId);

    /**
     * 更新轮播图状态
     */
    boolean updateBannerStatus(Long bannerId, Integer status);

    /**
     * 获取启用的轮播图列表（按排序权重排序）
     */
    List<Banner> getEnabledBanners();

    /**
     * 检查排序权重是否已存在
     */
    boolean checkSortOrderExists(Integer sortOrder, Long excludeId);

    /**
     * 批量更新排序权重
     */
    boolean updateSortOrders(List<Long> bannerIds, List<Integer> sortOrders);
}
