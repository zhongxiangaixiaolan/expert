package com.qing.expert.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.dto.CategorySaveDTO;
import com.qing.expert.entity.Category;

import java.util.List;

/**
 * 服务分类服务接口
 */
public interface CategoryService extends IService<Category> {

    /**
     * 获取所有分类列表
     */
    List<Category> getAllCategories();

    /**
     * 获取启用状态的分类列表
     */
    List<Category> getEnabledCategories();

    /**
     * 保存分类（新增或更新）
     */
    boolean saveCategory(CategorySaveDTO saveDTO);

    /**
     * 删除分类
     */
    boolean deleteCategory(Long categoryId);

    /**
     * 批量删除分类
     */
    boolean batchDeleteCategories(List<Long> categoryIds);

    /**
     * 更新分类状态
     */
    boolean updateCategoryStatus(Long categoryId, Integer status);

    /**
     * 更新分类排序
     */
    boolean updateCategorySort(Long categoryId, Integer sortOrder);

    /**
     * 检查分类名称是否存在
     */
    boolean checkNameExists(String name, Long excludeId);

    /**
     * 获取分类详情
     */
    Category getCategoryDetail(Long categoryId);
}
