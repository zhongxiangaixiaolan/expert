package com.qing.expert.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.dto.favorite.FavoriteCreateDTO;
import com.qing.expert.dto.favorite.FavoriteQueryDTO;
import com.qing.expert.entity.Favorite;
import com.qing.expert.vo.favorite.FavoriteVO;

/**
 * 收藏服务接口
 */
public interface FavoriteService extends IService<Favorite> {

    /**
     * 添加收藏
     */
    boolean addFavorite(FavoriteCreateDTO createDTO);

    /**
     * 取消收藏
     */
    boolean removeFavorite(Long userId, String favoriteType, Long targetId);

    /**
     * 删除收藏记录
     */
    boolean deleteFavorite(Long favoriteId, Long userId);

    /**
     * 分页查询收藏列表
     */
    IPage<FavoriteVO> getFavoritePage(FavoriteQueryDTO queryDTO);

    /**
     * 检查是否已收藏
     */
    boolean checkFavorite(Long userId, String favoriteType, Long targetId);

    /**
     * 获取收藏统计
     */
    Object getFavoriteStatistics(Long userId);

    /**
     * 获取用户收藏的服务列表
     */
    IPage<FavoriteVO> getUserFavoriteServices(Long userId, Integer current, Integer size);

    /**
     * 获取用户收藏的达人列表
     */
    IPage<FavoriteVO> getUserFavoriteExperts(Long userId, Integer current, Integer size);

    /**
     * 批量删除收藏
     */
    boolean batchDeleteFavorites(Long userId, String favoriteType);

    /**
     * 获取服务的收藏数量
     */
    Long getServiceFavoriteCount(Long serviceId);

    /**
     * 获取达人的收藏数量
     */
    Long getExpertFavoriteCount(Long expertId);
}
