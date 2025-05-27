<template>
  <div class="banners-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>轮播图管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增轮播图
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :model="searchForm" inline>
          <el-form-item label="标题">
            <el-input
              v-model="searchForm.title"
              placeholder="请输入轮播图标题"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="链接类型">
            <el-select
              v-model="searchForm.linkType"
              placeholder="请选择链接类型"
              clearable
              style="width: 150px"
            >
              <el-option
                v-for="option in linkTypeOptions"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
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
        <el-table-column label="轮播图" width="120">
          <template #default="{ row }">
            <div class="banner-image">
              <el-image
                :src="row.imageUrl"
                :alt="row.title"
                fit="cover"
                style="width: 80px; height: 45px; border-radius: 4px"
                :preview-src-list="[row.imageUrl]"
                preview-teleported
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="title"
          label="标题"
          min-width="150"
          show-overflow-tooltip
        />
        <el-table-column label="链接类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getLinkTypeTagType(row.linkType)">
              {{ getLinkTypeLabel(row.linkType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="linkUrl"
          label="跳转链接"
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
        <el-table-column label="时间范围" width="180">
          <template #default="{ row }">
            <div v-if="row.startTime || row.endTime" class="time-range">
              <div v-if="row.startTime" class="time-item">
                开始：{{ formatDateTime(row.startTime) }}
              </div>
              <div v-if="row.endTime" class="time-item">
                结束：{{ formatDateTime(row.endTime) }}
              </div>
            </div>
            <span v-else class="text-muted">永久有效</span>
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

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

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
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="轮播图标题" prop="title">
              <el-input
                v-model="formData.title"
                placeholder="请输入轮播图标题"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序权重" prop="sortOrder">
              <el-input-number
                v-model="formData.sortOrder"
                :min="0"
                :max="9999"
                placeholder="数值越小排序越靠前"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="轮播图片" prop="imageUrl">
          <div class="image-upload">
            <el-input
              v-model="formData.imageUrl"
              placeholder="请输入图片URL或上传图片"
              style="margin-bottom: 10px"
            />
            <div v-if="formData.imageUrl" class="image-preview">
              <el-image
                :src="formData.imageUrl"
                fit="cover"
                style="width: 200px; height: 112px; border-radius: 4px"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                    <div>图片加载失败</div>
                  </div>
                </template>
              </el-image>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="链接类型" prop="linkType">
          <el-radio-group v-model="formData.linkType">
            <el-radio
              v-for="option in linkTypeOptions"
              :key="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item
          v-if="formData.linkType === 'URL'"
          label="跳转链接"
          prop="linkUrl"
        >
          <el-input
            v-model="formData.linkUrl"
            placeholder="请输入完整的URL地址，如：https://example.com"
          />
        </el-form-item>

        <el-form-item
          v-if="formData.linkType === 'CATEGORY'"
          label="关联分类"
          prop="linkId"
        >
          <el-select
            v-model="formData.linkId"
            placeholder="请选择分类"
            style="width: 100%"
          >
            <el-option
              v-for="category in categoryOptions"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-date-picker
                v-model="formData.startTime"
                type="datetime"
                placeholder="选择开始时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间">
              <el-date-picker
                v-model="formData.endTime"
                type="datetime"
                placeholder="选择结束时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>

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
import dayjs from "dayjs";
import {
  getBannerPage,
  saveBanner,
  deleteBanner,
  updateBannerStatus,
  batchDeleteBanners,
  linkTypeOptions,
  type Banner,
  type BannerSaveParams,
  type BannerQueryParams,
  type PageResult,
} from "@/api/banners";
import { getEnabledCategories, type Category } from "@/api/categories";

// 响应式数据
const loading = ref(false);
const submitting = ref(false);
const dialogVisible = ref(false);
const tableData = ref<Banner[]>([]);
const selectedRows = ref<Banner[]>([]);
const categoryOptions = ref<Category[]>([]);
const formRef = ref<FormInstance>();

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
});

// 搜索表单
const searchForm = reactive<BannerQueryParams>({
  title: "",
  linkType: undefined,
  status: undefined,
});

// 表单数据
const formData = reactive<BannerSaveParams>({
  id: undefined,
  title: "",
  imageUrl: "",
  linkUrl: "",
  linkType: "NONE",
  linkId: undefined,
  sortOrder: 0,
  status: 1,
  startTime: undefined,
  endTime: undefined,
});

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: "请输入轮播图标题", trigger: "blur" },
    {
      min: 1,
      max: 100,
      message: "轮播图标题长度在 1 到 100 个字符",
      trigger: "blur",
    },
  ],
  imageUrl: [
    { required: true, message: "请输入图片URL", trigger: "blur" },
    {
      pattern: /^https?:\/\/.+/,
      message: "请输入有效的图片URL",
      trigger: "blur",
    },
  ],
  linkType: [{ required: true, message: "请选择链接类型", trigger: "change" }],
  linkUrl: [
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (formData.linkType === "URL") {
          if (!value) {
            callback(new Error("请输入跳转链接"));
          } else if (!/^https?:\/\/.+/.test(value)) {
            callback(new Error("请输入有效的URL地址"));
          } else {
            callback();
          }
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  linkId: [
    {
      validator: (rule: any, value: number, callback: Function) => {
        if (formData.linkType === "CATEGORY" && !value) {
          callback(new Error("请选择关联分类"));
        } else {
          callback();
        }
      },
      trigger: "change",
    },
  ],
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

// 计算属性
const dialogTitle = computed(() => (formData.id ? "编辑轮播图" : "新增轮播图"));

// 获取链接类型标签样式
const getLinkTypeTagType = (linkType: string) => {
  const typeMap: Record<string, string> = {
    NONE: "info",
    URL: "primary",
    SERVICE: "success",
    CATEGORY: "warning",
  };
  return typeMap[linkType] || "info";
};

// 获取链接类型标签文本
const getLinkTypeLabel = (linkType: string) => {
  const option = linkTypeOptions.find((item) => item.value === linkType);
  return option?.label || "未知";
};

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  return dayjs(dateTime).format("MM-DD HH:mm");
};

// 获取轮播图列表
const fetchBanners = async () => {
  try {
    loading.value = true;
    const params: BannerQueryParams = {
      pageNum: pagination.current,
      pageSize: pagination.size,
      ...searchForm,
    };
    const result: PageResult<Banner> = await getBannerPage(params);
    tableData.value = result.records;
    pagination.total = result.total;
  } catch (error) {
    ElMessage.error("获取轮播图列表失败");
  } finally {
    loading.value = false;
  }
};

// 获取分类选项
const fetchCategories = async () => {
  try {
    const categories = await getEnabledCategories();
    categoryOptions.value = categories;
  } catch (error) {
    console.error("获取分类列表失败:", error);
  }
};

// 搜索
const handleSearch = () => {
  pagination.current = 1;
  fetchBanners();
};

// 重置搜索
const handleReset = () => {
  searchForm.title = "";
  searchForm.linkType = undefined;
  searchForm.status = undefined;
  pagination.current = 1;
  fetchBanners();
};

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size;
  pagination.current = 1;
  fetchBanners();
};

// 当前页变化
const handleCurrentChange = (current: number) => {
  pagination.current = current;
  fetchBanners();
};

// 新增轮播图
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    title: "",
    imageUrl: "",
    linkUrl: "",
    linkType: "NONE",
    linkId: undefined,
    sortOrder: 0,
    status: 1,
    startTime: undefined,
    endTime: undefined,
  });
  dialogVisible.value = true;
};

// 编辑轮播图
const handleEdit = (row: Banner) => {
  Object.assign(formData, {
    id: row.id,
    title: row.title,
    imageUrl: row.imageUrl,
    linkUrl: row.linkUrl || "",
    linkType: row.linkType,
    linkId: row.linkId,
    sortOrder: row.sortOrder,
    status: row.status,
    startTime: row.startTime,
    endTime: row.endTime,
  });
  dialogVisible.value = true;
};

// 删除轮播图
const handleDelete = async (row: Banner) => {
  try {
    await ElMessageBox.confirm(`确定要删除轮播图"${row.title}"吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteBanner(row.id);
    ElMessage.success("删除成功");
    fetchBanners();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

// 切换状态
const handleToggleStatus = async (row: Banner) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1;
    await updateBannerStatus(row.id, newStatus);
    ElMessage.success(`${newStatus === 1 ? "启用" : "禁用"}成功`);
    fetchBanners();
  } catch (error) {
    ElMessage.error("操作失败");
  }
};

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个轮播图吗？`,
      "提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    const ids = selectedRows.value.map((row) => row.id);
    await batchDeleteBanners(ids);
    ElMessage.success("批量删除成功");
    selectedRows.value = [];
    fetchBanners();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("批量删除失败");
    }
  }
};

// 表格选择变化
const handleSelectionChange = (selection: Banner[]) => {
  selectedRows.value = selection;
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();

    // 验证时间范围
    if (formData.startTime && formData.endTime) {
      if (dayjs(formData.startTime).isAfter(dayjs(formData.endTime))) {
        ElMessage.error("开始时间不能晚于结束时间");
        return;
      }
    }

    submitting.value = true;
    await saveBanner(formData);
    ElMessage.success(formData.id ? "更新成功" : "新增成功");
    dialogVisible.value = false;
    fetchBanners();
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
  fetchBanners();
  fetchCategories();
});
</script>

<style scoped>
.banners-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.banner-image {
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 45px;
  background-color: #f5f7fa;
  border-radius: 4px;
  color: #909399;
  font-size: 12px;
}

.time-range {
  font-size: 12px;
  line-height: 1.4;
}

.time-item {
  margin-bottom: 2px;
}

.text-muted {
  color: #909399;
  font-size: 12px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.batch-actions {
  margin-top: 20px;
  padding: 15px;
  background-color: #f0f9ff;
  border: 1px solid #bfdbfe;
  border-radius: 4px;
}

.image-upload {
  width: 100%;
}

.image-preview {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.image-preview .image-error {
  width: 200px;
  height: 112px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  color: #909399;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-table) {
  margin-top: 20px;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-image__error) {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 12px;
}
</style>
