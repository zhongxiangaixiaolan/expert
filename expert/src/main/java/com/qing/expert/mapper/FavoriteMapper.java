package com.qing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.entity.Favorite;
import com.qing.expert.vo.favorite.FavoriteVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 收藏Mapper接口
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    /**
     * 分页查询用户收藏列表（包含详细信息）
     */
    IPage<FavoriteVO> selectUserFavoritePage(Page<FavoriteVO> page, 
                                           @Param("userId") Long userId, 
                                           @Param("favoriteType") String favoriteType);

    /**
     * 检查用户是否已收藏
     */
    int checkUserFavorite(@Param("userId") Long userId, 
                         @Param("favoriteType") String favoriteType, 
                         @Param("targetId") Long targetId);

    /**
     * 获取用户收藏统计
     */
    Map<String, Object> getUserFavoriteStatistics(@Param("userId") Long userId);

    /**
     * 获取服务的收藏数量
     */
    Long getServiceFavoriteCount(@Param("serviceId") Long serviceId);

    /**
     * 获取达人的收藏数量
     */
    Long getExpertFavoriteCount(@Param("expertId") Long expertId);

    /**
     * 批量删除用户收藏
     */
    int batchDeleteUserFavorites(@Param("userId") Long userId, 
                                @Param("favoriteType") String favoriteType);

    /**
     * 删除用户的收藏记录（验证用户权限）
     */
    int deleteUserFavorite(@Param("favoriteId") Long favoriteId, 
                          @Param("userId") Long userId);

    /**
     * 获取热门收藏的服务列表
     */
    IPage<FavoriteVO> selectHotFavoriteServices(Page<FavoriteVO> page, 
                                              @Param("limit") Integer limit);

    /**
     * 获取热门收藏的达人列表
     */
    IPage<FavoriteVO> selectHotFavoriteExperts(Page<FavoriteVO> page, 
                                             @Param("limit") Integer limit);
}
