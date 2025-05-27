<template>
  <div class="announcements-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>通告管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增通告
          </el-button>
        </div>
      </template>

      <!-- 搜索筛选区域 -->
      <div class="search-section">
        <el-form :model="queryParams" inline>
          <el-form-item label="通告标题">
            <el-input
              v-model="queryParams.title"
              placeholder="请输入通告标题"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="通告类型">
            <el-select
              v-model="queryParams.type"
              placeholder="请选择通告类型"
              clearable
              style="width: 150px"
            >
              <el-option
                v-for="item in AnnouncementTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="滚动显示">
            <el-select
              v-model="queryParams.isScroll"
              placeholder="请选择"
              clearable
              style="width: 120px"
            >
              <el-option label="是" :value="1" />
              <el-option label="否" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select
              v-model="queryParams.status"
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

      <!-- 通告列表 -->
      <el-table
        v-loading="loading"
        :data="announcementList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column
          prop="title"
          label="通告标题"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column prop="type" label="通告类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">
              {{ getTypeLabel(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isScroll" label="滚动显示" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isScroll ? 'success' : 'info'">
              {{ row.isScroll ? "是" : "否" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序权重" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="{ row }">
            {{ row.startTime ? formatDateTime(row.startTime) : "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180">
          <template #default="{ row }">
            {{ row.endTime ? formatDateTime(row.endTime) : "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑通告对话框 -->
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
            <el-form-item label="通告标题" prop="title">
              <el-input
                v-model="formData.title"
                placeholder="请输入通告标题"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="通告类型" prop="type">
              <el-select
                v-model="formData.type"
                placeholder="请选择通告类型"
                style="width: 100%"
              >
                <el-option
                  v-for="item in AnnouncementTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="滚动显示" prop="isScroll">
              <el-radio-group v-model="formData.isScroll">
                <el-radio :label="1">是</el-radio>
                <el-radio :label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序权重" prop="sortOrder">
              <el-input-number
                v-model="formData.sortOrder"
                :min="0"
                :max="9999"
                placeholder="请输入排序权重"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-date-picker
                v-model="formData.startTime"
                type="datetime"
                placeholder="请选择开始时间"
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
                placeholder="请选择结束时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="通告内容">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="6"
            placeholder="请输入通告内容"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="handleSubmit"
            :loading="submitLoading"
          >
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
import { Plus, Search, Refresh } from "@element-plus/icons-vue";
import {
  getAnnouncementPage,
  saveAnnouncement,
  deleteAnnouncement,
  updateAnnouncementStatus,
  AnnouncementTypeOptions,
  type Announcement,
  type AnnouncementQueryParams,
  type AnnouncementSaveParams,
} from "@/api/announcements";

// 响应式数据
const loading = ref(false);
const submitLoading = ref(false);
const announcementList = ref<Announcement[]>([]);
const total = ref(0);
const selectedRows = ref<Announcement[]>([]);

// 查询参数
const queryParams = reactive<AnnouncementQueryParams>({
  pageNum: 1,
  pageSize: 10,
  title: "",
  type: "",
  isScroll: undefined,
  status: undefined,
});

// 对话框相关
const dialogVisible = ref(false);
const dialogTitle = computed(() => (formData.id ? "编辑通告" : "新增通告"));
const formRef = ref<FormInstance>();

// 表单数据
const formData = reactive<AnnouncementSaveParams>({
  id: undefined,
  title: "",
  content: "",
  type: "NOTICE",
  isScroll: 1,
  sortOrder: 0,
  status: 1,
  startTime: "",
  endTime: "",
});

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: "请输入通告标题", trigger: "blur" },
    {
      min: 1,
      max: 100,
      message: "标题长度在 1 到 100 个字符",
      trigger: "blur",
    },
  ],
  type: [{ required: true, message: "请选择通告类型", trigger: "change" }],
  isScroll: [
    { required: true, message: "请选择是否滚动显示", trigger: "change" },
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
};

// 获取通告类型标签样式
const getTypeTagType = (type: string) => {
  const typeMap: Record<string, string> = {
    NOTICE: "primary",
    ACTIVITY: "success",
    SYSTEM: "warning",
  };
  return typeMap[type] || "primary";
};

// 获取通告类型标签文本
const getTypeLabel = (type: string) => {
  const option = AnnouncementTypeOptions.find((item) => item.value === type);
  return option?.label || type;
};

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return "-";
  return new Date(dateTime).toLocaleString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  });
};

// 获取通告列表
const getAnnouncementList = async () => {
  try {
    loading.value = true;
    const response = await getAnnouncementPage(queryParams);
    announcementList.value = response.records;
    total.value = response.total;
  } catch (error) {
    console.error("获取通告列表失败:", error);
    ElMessage.error("获取通告列表失败");
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1;
  getAnnouncementList();
};

// 重置搜索
const handleReset = () => {
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    title: "",
    type: "",
    isScroll: undefined,
    status: undefined,
  });
  getAnnouncementList();
};

// 分页大小改变
const handleSizeChange = (size: number) => {
  queryParams.pageSize = size;
  queryParams.pageNum = 1;
  getAnnouncementList();
};

// 当前页改变
const handleCurrentChange = (page: number) => {
  queryParams.pageNum = page;
  getAnnouncementList();
};

// 选择改变
const handleSelectionChange = (selection: Announcement[]) => {
  selectedRows.value = selection;
};

// 状态改变
const handleStatusChange = async (row: Announcement) => {
  try {
    await updateAnnouncementStatus(row.id, row.status);
    ElMessage.success("状态更新成功");
    getAnnouncementList();
  } catch (error) {
    console.error("状态更新失败:", error);
    ElMessage.error("状态更新失败");
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1;
  }
};

// 新增通告
const handleAdd = () => {
  resetFormData();
  dialogVisible.value = true;
};

// 编辑通告
const handleEdit = (row: Announcement) => {
  Object.assign(formData, {
    id: row.id,
    title: row.title,
    content: row.content || "",
    type: row.type,
    isScroll: row.isScroll,
    sortOrder: row.sortOrder,
    status: row.status,
    startTime: row.startTime || "",
    endTime: row.endTime || "",
  });
  dialogVisible.value = true;
};

// 删除通告
const handleDelete = async (row: Announcement) => {
  try {
    await ElMessageBox.confirm(`确定要删除通告"${row.title}"吗？`, "删除确认", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteAnnouncement(row.id);
    ElMessage.success("删除成功");
    getAnnouncementList();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除失败:", error);
      ElMessage.error("删除失败");
    }
  }
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    submitLoading.value = true;

    // 处理时间格式
    const submitData = { ...formData };
    if (!submitData.startTime) {
      submitData.startTime = undefined;
    }
    if (!submitData.endTime) {
      submitData.endTime = undefined;
    }

    await saveAnnouncement(submitData);
    ElMessage.success(formData.id ? "更新成功" : "新增成功");
    dialogVisible.value = false;
    getAnnouncementList();
  } catch (error) {
    console.error("保存失败:", error);
    ElMessage.error("保存失败");
  } finally {
    submitLoading.value = false;
  }
};

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields();
  resetFormData();
};

// 重置表单数据
const resetFormData = () => {
  Object.assign(formData, {
    id: undefined,
    title: "",
    content: "",
    type: "NOTICE",
    isScroll: 1,
    sortOrder: 0,
    status: 1,
    startTime: "",
    endTime: "",
  });
};

// 生命周期
onMounted(() => {
  getAnnouncementList();
});
</script>

<style scoped>
.announcements-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 6px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.dialog-footer {
  text-align: right;
}

.el-table {
  margin-top: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-tag {
  font-size: 12px;
}

.el-button + .el-button {
  margin-left: 8px;
}
</style>
