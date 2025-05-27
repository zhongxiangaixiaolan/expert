<template>
  <div class="categories-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>分类管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增分类
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :model="searchForm" inline>
          <el-form-item label="分类名称">
            <el-input
              v-model="searchForm.name"
              placeholder="请输入分类名称"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              style="width: 120px"
            >
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图标" width="80">
          <template #default="{ row }">
            <div class="category-icon">
              <el-icon
                v-if="row.iconType === 'iconify'"
                :color="row.iconColor || '#409eff'"
                size="24"
              >
                <component :is="getIconComponent(row.icon)" />
              </el-icon>
              <span v-else-if="row.iconType === 'emoji'" class="emoji-icon">{{
                row.icon
              }}</span>
              <img
                v-else-if="row.icon"
                :src="row.icon"
                alt="图标"
                class="icon-image"
              />
              <el-icon v-else color="#ccc" size="24"><Picture /></el-icon>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="分类名称" min-width="120" />
        <el-table-column
          prop="description"
          label="描述"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? "启用" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              @click="handleToggleStatus(row)"
            >
              <el-icon><Switch /></el-icon>
              {{ row.status === 1 ? "禁用" : "启用" }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 批量操作 -->
      <div v-if="selectedRows.length > 0" class="batch-actions">
        <el-button type="danger" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>
          批量删除 ({{ selectedRows.length }})
        </el-button>
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
          />
        </el-form-item>
        <el-form-item label="图标类型" prop="iconType">
          <el-radio-group v-model="formData.iconType">
            <el-radio value="iconify">图标库</el-radio>
            <el-radio value="emoji">表情符号</el-radio>
            <el-radio value="url">图片链接</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <div v-if="formData.iconType === 'iconify'" class="icon-selector">
            <el-select
              v-model="formData.icon"
              placeholder="请选择图标"
              filterable
              style="width: 200px"
            >
              <el-option
                v-for="icon in iconOptions"
                :key="icon.value"
                :label="icon.label"
                :value="icon.value"
              >
                <div class="icon-option">
                  <el-icon><component :is="icon.component" /></el-icon>
                  <span>{{ icon.label }}</span>
                </div>
              </el-option>
            </el-select>
            <el-color-picker
              v-model="formData.iconColor"
              style="margin-left: 10px"
              show-alpha
            />
          </div>
          <el-input
            v-else-if="formData.iconType === 'emoji'"
            v-model="formData.icon"
            placeholder="请输入表情符号，如：🎨"
            style="width: 200px"
          />
          <el-input
            v-else-if="formData.iconType === 'url'"
            v-model="formData.icon"
            placeholder="请输入图片链接"
          />
        </el-form-item>
        <el-form-item label="排序权重" prop="sortOrder">
          <el-input-number
            v-model="formData.sortOrder"
            :min="0"
            :max="9999"
            placeholder="数值越小排序越靠前"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from "vue";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";
import {
  getAllCategories,
  saveCategory,
  deleteCategory,
  batchDeleteCategories,
  updateCategoryStatus,
  type Category,
  type CategorySaveParams,
} from "@/api/categories";

// 响应式数据
const loading = ref(false);
const submitting = ref(false);
const dialogVisible = ref(false);
const tableData = ref<Category[]>([]);
const selectedRows = ref<Category[]>([]);
const formRef = ref<FormInstance>();

// 搜索表单
const searchForm = reactive({
  name: "",
  status: undefined as number | undefined,
});

// 表单数据
const formData = reactive<CategorySaveParams>({
  id: undefined,
  name: "",
  description: "",
  icon: "",
  iconType: "iconify",
  iconColor: "#409eff",
  sortOrder: 0,
  status: 1,
});

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: "请输入分类名称", trigger: "blur" },
    {
      min: 1,
      max: 50,
      message: "分类名称长度在 1 到 50 个字符",
      trigger: "blur",
    },
  ],
  description: [
    { max: 255, message: "分类描述长度不能超过 255 个字符", trigger: "blur" },
  ],
  iconType: [{ required: true, message: "请选择图标类型", trigger: "change" }],
  sortOrder: [
    { required: true, message: "请输入排序权重", trigger: "blur" },
    {
      type: "number",
      min: 0,
      max: 9999,
      message: "排序权重范围为 0-9999",
      trigger: "blur",
    },
  ],
  status: [{ required: true, message: "请选择状态", trigger: "change" }],
};

// 图标选项 - 使用Element Plus图标库中确实存在的图标
const iconOptions = [
  { label: "设计", value: "Brush", component: "Brush" },
  { label: "编程", value: "Monitor", component: "Monitor" },
  { label: "写作", value: "EditPen", component: "EditPen" },
  { label: "营销", value: "Promotion", component: "Promotion" },
  { label: "咨询", value: "ChatDotRound", component: "ChatDotRound" },
  { label: "教育", value: "Reading", component: "Reading" },
  { label: "生活", value: "House", component: "House" },
  { label: "商务", value: "Briefcase", component: "Briefcase" },
  { label: "技术", value: "Setting", component: "Setting" },
  { label: "创意", value: "MagicStick", component: "MagicStick" },
  { label: "摄影", value: "Camera", component: "Camera" },
  { label: "音乐", value: "Headphones", component: "Headphones" },
  { label: "视频", value: "VideoCamera", component: "VideoCamera" },
  { label: "翻译", value: "ChatLineRound", component: "ChatLineRound" },
  { label: "法律", value: "Scale", component: "Scale" },
  { label: "金融", value: "Money", component: "Money" },
  { label: "健康", value: "FirstAidKit", component: "FirstAidKit" },
  { label: "旅游", value: "MapLocation", component: "MapLocation" },
  { label: "美食", value: "Food", component: "Food" },
  { label: "运动", value: "Football", component: "Football" },
  { label: "工具", value: "Tools", component: "Tools" },
  { label: "购物", value: "ShoppingBag", component: "ShoppingBag" },
  { label: "汽车", value: "Car", component: "Car" },
  { label: "宠物", value: "Chicken", component: "Chicken" },
  { label: "其他", value: "More", component: "More" },
];

// 计算属性
const dialogTitle = computed(() => (formData.id ? "编辑分类" : "新增分类"));

// 获取图标组件 - 改进的图标映射逻辑
const getIconComponent = (iconName: string) => {
  if (!iconName) return "Picture";

  // 首先在预定义的图标选项中查找
  const icon = iconOptions.find((item) => item.value === iconName);
  if (icon) {
    return icon.component;
  }

  // 如果没找到，直接使用图标名称（假设它是有效的Element Plus图标）
  // 常见的Element Plus图标映射
  const iconMap: Record<string, string> = {
    Brush: "Brush",
    Monitor: "Monitor",
    EditPen: "EditPen",
    Promotion: "Promotion",
    ChatDotRound: "ChatDotRound",
    Reading: "Reading",
    House: "House",
    Briefcase: "Briefcase",
    Setting: "Setting",
    MagicStick: "MagicStick",
    Camera: "Camera",
    Headphones: "Headphones",
    VideoCamera: "VideoCamera",
    ChatLineRound: "ChatLineRound",
    Scale: "Scale",
    Money: "Money",
    FirstAidKit: "FirstAidKit",
    MapLocation: "MapLocation",
    Food: "Food",
    Football: "Football",
    Tools: "Tools",
    ShoppingBag: "ShoppingBag",
    Car: "Car",
    Chicken: "Chicken",
    More: "More",
  };

  return iconMap[iconName] || "Picture";
};

// 获取分类列表
const fetchCategories = async () => {
  try {
    loading.value = true;
    const data = await getAllCategories();
    tableData.value = data.filter((item) => {
      if (searchForm.name && !item.name.includes(searchForm.name)) {
        return false;
      }
      if (
        searchForm.status !== undefined &&
        item.status !== searchForm.status
      ) {
        return false;
      }
      return true;
    });
  } catch (error) {
    ElMessage.error("获取分类列表失败");
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  fetchCategories();
};

// 重置搜索
const handleReset = () => {
  searchForm.name = "";
  searchForm.status = undefined;
  fetchCategories();
};

// 新增分类
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    name: "",
    description: "",
    icon: "",
    iconType: "iconify",
    iconColor: "#409eff",
    sortOrder: 0,
    status: 1,
  });
  dialogVisible.value = true;
};

// 编辑分类
const handleEdit = (row: Category) => {
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    description: row.description || "",
    icon: row.icon || "",
    iconType: row.iconType || "iconify",
    iconColor: row.iconColor || "#409eff",
    sortOrder: row.sortOrder || 0,
    status: row.status,
  });
  dialogVisible.value = true;
};

// 删除分类
const handleDelete = async (row: Category) => {
  try {
    await ElMessageBox.confirm(`确定要删除分类"${row.name}"吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteCategory(row.id);
    ElMessage.success("删除成功");
    fetchCategories();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

// 切换状态
const handleToggleStatus = async (row: Category) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1;
    await updateCategoryStatus(row.id, newStatus);
    ElMessage.success(`${newStatus === 1 ? "启用" : "禁用"}成功`);
    fetchCategories();
  } catch (error) {
    ElMessage.error("操作失败");
  }
};

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个分类吗？`,
      "提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    const ids = selectedRows.value.map((row) => row.id);
    await batchDeleteCategories(ids);
    ElMessage.success("批量删除成功");
    selectedRows.value = [];
    fetchCategories();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("批量删除失败");
    }
  }
};

// 表格选择变化
const handleSelectionChange = (selection: Category[]) => {
  selectedRows.value = selection;
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    submitting.value = true;

    await saveCategory(formData);
    ElMessage.success(formData.id ? "更新成功" : "新增成功");
    dialogVisible.value = false;
    fetchCategories();
  } catch (error) {
    if (error !== false) {
      ElMessage.error("操作失败");
    }
  } finally {
    submitting.value = false;
  }
};

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields();
};

// 页面加载时获取数据
onMounted(() => {
  fetchCategories();
});
</script>

<style scoped>
.categories-management {
  padding: 0;
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
}

.card-header span {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  background: linear-gradient(
    135deg,
    var(--primary-600) 0%,
    var(--secondary-600) 100%
  );
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-actions .el-button {
  background: linear-gradient(
    135deg,
    var(--primary-600) 0%,
    var(--primary-500) 100%
  );
  border: none;
  border-radius: var(--radius-lg);
  padding: var(--spacing-sm) var(--spacing-lg);
  font-weight: var(--font-weight-semibold);
  transition: all var(--transition-fast);
  box-shadow: var(--shadow-sm);
}

.header-actions .el-button:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
  background: linear-gradient(
    135deg,
    var(--primary-700) 0%,
    var(--primary-600) 100%
  );
}

.search-bar {
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-xl);
  background: linear-gradient(
    135deg,
    var(--color-background-soft) 0%,
    var(--primary-50) 100%
  );
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border-light);
  box-shadow: var(--shadow-sm);
  position: relative;
  overflow: hidden;
}

.search-bar::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(
    90deg,
    var(--primary-500) 0%,
    var(--secondary-500) 100%
  );
}

.search-bar .el-form {
  position: relative;
  z-index: 1;
}

.search-bar .el-button--primary {
  background: linear-gradient(
    135deg,
    var(--primary-600) 0%,
    var(--primary-500) 100%
  );
  border: none;
  border-radius: var(--radius-md);
  font-weight: var(--font-weight-medium);
  transition: all var(--transition-fast);
}

.search-bar .el-button--primary:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.search-bar .el-button:not(.el-button--primary) {
  background: var(--color-background);
  border: 1px solid var(--color-border);
  color: var(--color-text-secondary);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.search-bar .el-button:not(.el-button--primary):hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: var(--primary-50);
}

.category-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  background: var(--color-background-soft);
  transition: all var(--transition-fast);
}

.category-icon:hover {
  transform: scale(1.1);
  box-shadow: var(--shadow-sm);
}

.emoji-icon {
  font-size: 24px;
  line-height: 1;
}

.icon-image {
  width: 28px;
  height: 28px;
  object-fit: cover;
  border-radius: var(--radius-base);
  box-shadow: var(--shadow-sm);
}

.batch-actions {
  margin-top: var(--spacing-xl);
  padding: var(--spacing-lg);
  background: linear-gradient(
    135deg,
    var(--error-50) 0%,
    var(--warning-50) 100%
  );
  border: 1px solid var(--error-200);
  border-radius: var(--radius-lg);
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.batch-actions .el-button {
  background: linear-gradient(
    135deg,
    var(--error-600) 0%,
    var(--error-500) 100%
  );
  border: none;
  border-radius: var(--radius-md);
  font-weight: var(--font-weight-medium);
  transition: all var(--transition-fast);
}

.batch-actions .el-button:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.icon-selector {
  display: flex;
  align-items: center;
  gap: var(--spacing-base);
}

.icon-option {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-base);
  transition: all var(--transition-fast);
}

.icon-option:hover {
  background-color: var(--primary-50);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-base);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--color-border-light);
}

.dialog-footer .el-button {
  border-radius: var(--radius-md);
  font-weight: var(--font-weight-medium);
  padding: var(--spacing-sm) var(--spacing-lg);
  transition: all var(--transition-fast);
}

.dialog-footer .el-button--primary {
  background: linear-gradient(
    135deg,
    var(--primary-600) 0%,
    var(--primary-500) 100%
  );
  border: none;
}

.dialog-footer .el-button--primary:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

:deep(.el-table) {
  margin-top: var(--spacing-xl);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-base);
}

:deep(.el-table__row) {
  transition: all var(--transition-fast);
}

:deep(.el-table__row:hover) {
  background-color: var(--primary-50);
}

:deep(.el-table .el-button--link) {
  font-weight: var(--font-weight-medium);
  border-radius: var(--radius-base);
  padding: var(--spacing-xs) var(--spacing-sm);
  transition: all var(--transition-fast);
}

:deep(.el-table .el-button--link:hover) {
  background-color: var(--primary-100);
  transform: translateY(-1px);
}

:deep(.el-form-item) {
  margin-bottom: var(--spacing-xl);
}

:deep(.el-form-item__label) {
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

:deep(.el-dialog) {
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-xl);
}

:deep(.el-dialog__header) {
  background: linear-gradient(
    135deg,
    var(--color-background-soft) 0%,
    var(--primary-50) 100%
  );
  border-bottom: 1px solid var(--color-border-light);
  border-radius: var(--radius-2xl) var(--radius-2xl) 0 0;
}

:deep(.el-dialog__title) {
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

:deep(.el-dialog__body) {
  padding: var(--spacing-xl);
}

/* 标签样式优化 */
:deep(.el-tag) {
  border-radius: var(--radius-base);
  font-weight: var(--font-weight-medium);
  padding: var(--spacing-xs) var(--spacing-sm);
  border: none;
}

:deep(.el-tag--success) {
  background: linear-gradient(
    135deg,
    var(--success-500) 0%,
    var(--success-400) 100%
  );
  color: white;
}

:deep(.el-tag--danger) {
  background: linear-gradient(
    135deg,
    var(--error-500) 0%,
    var(--error-400) 100%
  );
  color: white;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-bar {
    padding: var(--spacing-base);
  }

  .search-bar .el-form--inline .el-form-item {
    display: block;
    margin-bottom: var(--spacing-base);
  }

  .card-header {
    flex-direction: column;
    gap: var(--spacing-base);
    align-items: stretch;
  }
}
</style>
