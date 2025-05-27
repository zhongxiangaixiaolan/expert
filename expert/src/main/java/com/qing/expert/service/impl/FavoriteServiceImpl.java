package com.qing.expert.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.dto.favorite.FavoriteCreateDTO;
import com.qing.expert.dto.favorite.FavoriteQueryDTO;
import com.qing.expert.entity.Favorite;
import com.qing.expert.mapper.FavoriteMapper;
import com.qing.expert.service.FavoriteService;
import com.qing.expert.vo.favorite.FavoriteVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 收藏服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private final FavoriteMapper favoriteMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addFavorite(FavoriteCreateDTO createDTO) {
        // 检查是否已收藏
        boolean exists = checkFavorite(createDTO.getUserId(), createDTO.getFavoriteType(), createDTO.getTargetId());
        if (exists) {
            throw new BusinessException("已经收藏过了");
        }

        // 验证收藏类型
        if (!"service".equals(createDTO.getFavoriteType()) && !"expert".equals(createDTO.getFavoriteType())) {
            throw new BusinessException("收藏类型不正确");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(createDTO.getUserId());
        favorite.setFavoriteType(createDTO.getFavoriteType());
        favorite.setTargetId(createDTO.getTargetId());
        favorite.setCreateTime(LocalDateTime.now());
        favorite.setUpdateTime(LocalDateTime.now());

        return save(favorite);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeFavorite(Long userId, String favoriteType, Long targetId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getFavoriteType, favoriteType)
               .eq(Favorite::getTargetId, targetId);

        return remove(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFavorite(Long favoriteId, Long userId) {
        int result = favoriteMapper.deleteUserFavorite(favoriteId, userId);
        return result > 0;
    }

    @Override
    public IPage<FavoriteVO> getFavoritePage(FavoriteQueryDTO queryDTO) {
        Page<FavoriteVO> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        return favoriteMapper.selectUserFavoritePage(page, queryDTO.getUserId(), queryDTO.getFavoriteType());
    }

    @Override
    public boolean checkFavorite(Long userId, String favoriteType, Long targetId) {
        int count = favoriteMapper.checkUserFavorite(userId, favoriteType, targetId);
        return count > 0;
    }

    @Override
    public Object getFavoriteStatistics(Long userId) {
        Map<String, Object> statistics = favoriteMapper.getUserFavoriteStatistics(userId);
        if (statistics == null) {
            statistics = new HashMap<>();
            statistics.put("totalCount", 0);
            statistics.put("serviceCount", 0);
            statistics.put("expertCount", 0);
        }
        return statistics;
    }

    @Override
    public IPage<FavoriteVO> getUserFavoriteServices(Long userId, Integer current, Integer size) {
        Page<FavoriteVO> page = new Page<>(current, size);
        return favoriteMapper.selectUserFavoritePage(page, userId, "service");
    }

    @Override
    public IPage<FavoriteVO> getUserFavoriteExperts(Long userId, Integer current, Integer size) {
        Page<FavoriteVO> page = new Page<>(current, size);
        return favoriteMapper.selectUserFavoritePage(page, userId, "expert");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteFavorites(Long userId, String favoriteType) {
        int result = favoriteMapper.batchDeleteUserFavorites(userId, favoriteType);
        return result > 0;
    }

    @Override
    public Long getServiceFavoriteCount(Long serviceId) {
        return favoriteMapper.getServiceFavoriteCount(serviceId);
    }

    @Override
    public Long getExpertFavoriteCount(Long expertId) {
        return favoriteMapper.getExpertFavoriteCount(expertId);
    }
}
