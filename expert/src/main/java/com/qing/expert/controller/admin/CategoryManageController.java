package com.qing.expert.controller.admin;

import com.qing.expert.common.result.Result;
import com.qing.expert.dto.CategorySaveDTO;
import com.qing.expert.entity.Category;
import com.qing.expert.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "分类管理", description = "服务分类的增删改查接口")
public class CategoryManageController {

    private final CategoryService categoryService;

    @Operation(summary = "获取所有分类列表", description = "获取所有分类列表，包含禁用的分类")
    @GetMapping("/list")
    public Result<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return Result.success("获取成功", categories);
    }

    @Operation(summary = "获取启用的分类列表", description = "获取启用状态的分类列表")
    @GetMapping("/enabled")
    public Result<List<Category>> getEnabledCategories() {
        List<Category> categories = categoryService.getEnabledCategories();
        return Result.success("获取成功", categories);
    }

    @Operation(summary = "获取分类详情", description = "根据分类ID获取分类详细信息")
    @GetMapping("/{categoryId}")
    public Result<Category> getCategoryDetail(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        Category category = categoryService.getCategoryDetail(categoryId);
        return Result.success("获取成功", category);
    }

    @Operation(summary = "保存分类", description = "新增或更新分类信息")
    @PostMapping("/save")
    public Result<Void> saveCategory(@Validated @RequestBody CategorySaveDTO saveDTO) {
        boolean success = categoryService.saveCategory(saveDTO);
        if (success) {
            return Result.success(saveDTO.getId() != null ? "更新成功" : "新增成功");
        } else {
            return Result.error(saveDTO.getId() != null ? "更新失败" : "新增失败");
        }
    }

    @Operation(summary = "更新分类状态", description = "启用或禁用分类")
    @PutMapping("/{categoryId}/status")
    public Result<Void> updateCategoryStatus(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @Parameter(description = "状态：0-禁用，1-启用") @RequestParam Integer status) {
        boolean success = categoryService.updateCategoryStatus(categoryId, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }

    @Operation(summary = "更新分类排序", description = "更新分类的排序权重")
    @PutMapping("/{categoryId}/sort")
    public Result<Void> updateCategorySort(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @Parameter(description = "排序权重") @RequestParam Integer sortOrder) {
        boolean success = categoryService.updateCategorySort(categoryId, sortOrder);
        if (success) {
            return Result.success("排序更新成功");
        } else {
            return Result.error("排序更新失败");
        }
    }

    @Operation(summary = "删除分类", description = "删除指定分类")
    @DeleteMapping("/{categoryId}")
    public Result<Void> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        boolean success = categoryService.deleteCategory(categoryId);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @Operation(summary = "批量删除分类", description = "批量删除分类")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteCategories(
            @Parameter(description = "分类ID列表") @RequestBody List<Long> categoryIds) {
        boolean success = categoryService.batchDeleteCategories(categoryIds);
        if (success) {
            return Result.success("批量删除成功");
        } else {
            return Result.error("批量删除失败");
        }
    }

    @Operation(summary = "检查分类名称", description = "检查分类名称是否已存在")
    @GetMapping("/check-name")
    public Result<Boolean> checkCategoryName(
            @Parameter(description = "分类名称") @RequestParam String name,
            @Parameter(description = "排除的分类ID") @RequestParam(required = false) Long excludeId) {
        boolean exists = categoryService.checkNameExists(name, excludeId);
        return Result.success("检查完成", !exists);
    }
}
