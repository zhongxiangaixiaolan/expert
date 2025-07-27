package com.qing.expert.controller.admin;

import com.qing.expert.common.result.Result;
import com.qing.expert.dto.CategorySaveDTO;
import com.qing.expert.entity.Category;
import com.qing.expert.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
    @GetMapping("/list")
    public Result<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return Result.success("获取成功", categories);
    }

    public Result<List<Category>> getEnabledCategories() {
        List<Category> categories = categoryService.getEnabledCategories();
        return Result.success("获取成功", categories);
    }

    public Result<Category> getCategoryDetail(
        return Result.success("获取成功", category);
    }

    public Result<Void> saveCategory(@Validated @RequestBody CategorySaveDTO saveDTO) {
        boolean success = categoryService.saveCategory(saveDTO);
        if (success) {
            return Result.success(saveDTO.getId() != null ? "更新成功" : "新增成功");
        } else {
            return Result.error(saveDTO.getId() != null ? "更新失败" : "新增失败");
        }
    }

    public Result<Void> updateCategoryStatus(
        boolean success = categoryService.updateCategoryStatus(categoryId, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }

    public Result<Void> updateCategorySort(
        boolean success = categoryService.updateCategorySort(categoryId, sortOrder);
        if (success) {
            return Result.success("排序更新成功");
        } else {
            return Result.error("排序更新失败");
        }
    }

    public Result<Void> deleteCategory(
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    public Result<Void> batchDeleteCategories(
        if (success) {
            return Result.success("批量删除成功");
        } else {
            return Result.error("批量删除失败");
        }
    }

    public Result<Boolean> checkCategoryName(
        boolean exists = categoryService.checkNameExists(name, excludeId);
        return Result.success("检查完成", !exists);
    }
}
