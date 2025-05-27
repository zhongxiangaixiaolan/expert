package com.qing.expert.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qing.expert.common.enums.StatusEnum;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.dto.CategorySaveDTO;
import com.qing.expert.entity.Category;
import com.qing.expert.mapper.CategoryMapper;
import com.qing.expert.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 服务分类服务实现类
 */
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    @Cacheable(value = "categories", key = "'all'")
    public List<Category> getAllCategories() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSortOrder, Category::getId);
        return list(wrapper);
    }

    @Override
    @Cacheable(value = "categories", key = "'enabled'")
    public List<Category> getEnabledCategories() {
        return baseMapper.selectEnabledCategories();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "categories", allEntries = true)
    public boolean saveCategory(CategorySaveDTO saveDTO) {
        // 参数校验
        if (saveDTO == null || StrUtil.isBlank(saveDTO.getName())) {
            throw new BusinessException("分类名称不能为空");
        }

        // 检查名称是否重复
        if (checkNameExists(saveDTO.getName(), saveDTO.getId())) {
            throw new BusinessException("分类名称已存在");
        }

        Category category;
        if (saveDTO.getId() != null) {
            // 更新
            category = getById(saveDTO.getId());
            if (category == null) {
                throw new BusinessException("分类不存在");
            }
            BeanUtil.copyProperties(saveDTO, category, "id");
        } else {
            // 新增
            category = new Category();
            BeanUtil.copyProperties(saveDTO, category);
            
            // 设置排序值
            if (category.getSortOrder() == null) {
                Integer maxSort = baseMapper.getMaxSortOrder();
                category.setSortOrder(maxSort != null ? maxSort + 1 : 1);
            }
        }

        boolean success = saveOrUpdate(category);
        if (success) {
            log.info("保存分类成功：{}", category.getName());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "categories", allEntries = true)
    public boolean deleteCategory(Long categoryId) {
        if (categoryId == null) {
            throw new BusinessException("分类ID不能为空");
        }

        Category category = getById(categoryId);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查分类是否被使用
        if (baseMapper.checkCategoryInUse(categoryId)) {
            throw new BusinessException("分类正在使用中，无法删除");
        }

        boolean success = removeById(categoryId);
        if (success) {
            log.info("删除分类成功：{}", category.getName());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "categories", allEntries = true)
    public boolean batchDeleteCategories(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new BusinessException("分类ID列表不能为空");
        }

        // 检查分类是否被使用
        for (Long categoryId : categoryIds) {
            if (baseMapper.checkCategoryInUse(categoryId)) {
                Category category = getById(categoryId);
                throw new BusinessException("分类【" + (category != null ? category.getName() : categoryId) + "】正在使用中，无法删除");
            }
        }

        boolean success = removeByIds(categoryIds);
        if (success) {
            log.info("批量删除分类成功：count={}", categoryIds.size());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "categories", allEntries = true)
    public boolean updateCategoryStatus(Long categoryId, Integer status) {
        if (categoryId == null || status == null) {
            throw new BusinessException("参数不能为空");
        }

        Category category = getById(categoryId);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        category.setStatus(status);
        boolean success = updateById(category);
        
        if (success) {
            log.info("更新分类状态成功：categoryId={}, status={}", categoryId, status);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "categories", allEntries = true)
    public boolean updateCategorySort(Long categoryId, Integer sortOrder) {
        if (categoryId == null || sortOrder == null) {
            throw new BusinessException("参数不能为空");
        }

        Category category = getById(categoryId);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        category.setSortOrder(sortOrder);
        boolean success = updateById(category);
        
        if (success) {
            log.info("更新分类排序成功：categoryId={}, sortOrder={}", categoryId, sortOrder);
        }
        return success;
    }

    @Override
    public boolean checkNameExists(String name, Long excludeId) {
        if (StrUtil.isBlank(name)) {
            return false;
        }

        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, name);
        if (excludeId != null) {
            wrapper.ne(Category::getId, excludeId);
        }

        return count(wrapper) > 0;
    }

    @Override
    public Category getCategoryDetail(Long categoryId) {
        if (categoryId == null) {
            throw new BusinessException("分类ID不能为空");
        }

        Category category = getById(categoryId);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        return category;
    }
}
