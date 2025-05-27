package com.qing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qing.expert.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 服务分类Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 获取启用状态的分类列表
     */
    List<Category> selectEnabledCategories();

    /**
     * 根据名称查询分类
     */
    Category selectByName(@Param("name") String name);

    /**
     * 获取最大排序值
     */
    Integer getMaxSortOrder();

    /**
     * 检查分类是否被使用
     */
    boolean checkCategoryInUse(@Param("categoryId") Long categoryId);
}
