<template>
  <div class="experts-management">
    <!-- 统计卡片 -->
    <div class="statistics-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon total">
                <el-icon><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ statistics.totalCount }}</div>
                <div class="stat-label">总达人数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon online">
                <el-icon><CircleCheck /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ statistics.onlineCount }}</div>
                <div class="stat-label">在线达人</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon pending">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ statistics.pendingCount }}</div>
                <div class="stat-label">待审核</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon rating">
                <el-icon><Star /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">
                  {{ statistics.avgRating?.toFixed(1) || "0.0" }}
                </div>
                <div class="stat-label">平均评分</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 主要内容 -->
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">达人管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd" :icon="Plus">
              新增达人
            </el-button>
            <el-button
              type="danger"
              :disabled="selectedExperts.length === 0"
              @click="handleBatchDelete"
              :icon="Delete"
            >
              批量删除
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索表单 -->
      <div class="search-form">
        <el-form :model="searchForm" inline>
          <el-form-item label="关键词">
            <el-input
              v-model="searchForm.keyword"
              placeholder="达人名称、用户昵称、手机号"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="分类">
            <el-select
              v-model="searchForm.categoryId"
              placeholder="请选择分类"
              clearable
              style="width: 150px"
            >
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
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
              <el-option label="下线" :value="0" />
              <el-option label="在线" :value="1" />
              <el-option label="忙碌" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="审核状态">
            <el-select
              v-model="searchForm.auditStatus"
              placeholder="请选择审核状态"
              clearable
              style="width: 120px"
            >
              <el-option label="待审核" :value="0" />
              <el-option label="已通过" :value="1" />
              <el-option label="已拒绝" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch" :icon="Search">
              搜索
            </el-button>
            <el-button @click="handleReset" :icon="Refresh"> 重置 </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 达人列表 -->
      <el-table
        v-loading="loading"
        :data="expertList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        stripe
      >
        <el-table-column type="selection" width="55" />

        <el-table-column prop="id" label="ID" width="80" />

        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <Avatar
              :avatar="row.avatar"
              :nickname="row.expertName"
              :gender="row.user?.gender"
              size="medium"
            />
          </template>
        </el-table-column>

        <el-table-column prop="expertName" label="达人名称" min-width="120" />

        <el-table-column label="用户信息" min-width="150">
          <template #default="{ row }">
            <div v-if="row.user">
              <div class="user-info">
                <span class="nickname">{{ row.user.nickname }}</span>
                <span class="phone" v-if="row.user.phone">{{
                  row.user.phone
                }}</span>
              </div>
            </div>
            <span v-else class="text-gray">-</span>
          </template>
        </el-table-column>

        <el-table-column label="分类" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.category" size="small">
              {{ row.category.name }}
            </el-tag>
            <span v-else class="text-gray">-</span>
          </template>
        </el-table-column>

        <el-table-column label="价格区间" width="120">
          <template #default="{ row }">
            <span v-if="row.priceMin || row.priceMax">
              ¥{{ row.priceMin || 0 }} - ¥{{ row.priceMax || 0 }}
            </span>
            <span v-else class="text-gray">-</span>
          </template>
        </el-table-column>

        <el-table-column label="评分" width="100">
          <template #default="{ row }">
            <div class="rating-info">
              <el-rate
                v-model="row.rating"
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value}"
                size="small"
              />
            </div>
          </template>
        </el-table-column>

        <el-table-column label="订单统计" width="120">
          <template #default="{ row }">
            <div class="order-stats">
              <div>接单: {{ row.orderCount || 0 }}</div>
              <div>完成: {{ row.completeCount || 0 }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getAuditStatusTagType(row.auditStatus)" size="small">
              {{ getAuditStatusText(row.auditStatus) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                type="primary"
                size="small"
                @click="handleView(row)"
                :icon="View"
              >
                查看
              </el-button>
              <el-button
                type="warning"
                size="small"
                @click="handleEdit(row)"
                :icon="Edit"
              >
                编辑
              </el-button>
              <el-dropdown
                @command="(command) => handleDropdownCommand(command, row)"
              >
                <el-button size="small" :icon="More" />
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item
                      v-if="row.auditStatus === 0"
                      command="audit"
                      :icon="Check"
                    >
                      审核
                    </el-dropdown-item>
                    <el-dropdown-item command="status" :icon="Switch">
                      {{ row.status === 1 ? "下线" : "上线" }}
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" :icon="Delete" divided>
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <Pagination
          v-model:current="pagination.current"
          v-model:size="pagination.size"
          :total="pagination.total"
          @change="loadExpertList"
        />
      </div>
    </el-card>

    <!-- 达人详情弹窗 -->
    <ExpertDetailDialog
      v-model:visible="detailDialogVisible"
      :expert-id="selectedExpertId"
      @edit="handleEditFromDetail"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Plus,
  Delete,
  Search,
  Refresh,
  View,
  Edit,
  More,
  Check,
  Switch,
  User,
  CircleCheck,
  Clock,
  Star,
} from "@element-plus/icons-vue";
import dayjs from "dayjs";
import {
  getExpertPage,
  getExpertStatistics,
  deleteExpert,
  batchDeleteExperts,
  updateExpertStatus,
  auditExpert,
  getStatusText,
  getAuditStatusText,
  getStatusTagType,
  getAuditStatusTagType,
  type Expert,
  type ExpertQueryParams,
  type ExpertStatistics,
} from "@/api/experts";
import { getEnabledCategories, type Category } from "@/api/categories";
import Pagination from "@/components/Pagination.vue";
import Avatar from "@/components/Avatar.vue";
import ExpertDetailDialog from "./ExpertDetailDialog.vue";

// 搜索表单
const searchForm = reactive<ExpertQueryParams>({
  keyword: "",
  categoryId: undefined,
  status: undefined,
  auditStatus: undefined,
});

// 分页信息
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0,
});

// 数据状态
const loading = ref(false);
const expertList = ref<Expert[]>([]);
const selectedExperts = ref<Expert[]>([]);
const categories = ref<Category[]>([]);

// 弹窗状态
const detailDialogVisible = ref(false);
const selectedExpertId = ref<number>();
const statistics = ref<ExpertStatistics>({
  totalCount: 0,
  onlineCount: 0,
  busyCount: 0,
  offlineCount: 0,
  pendingCount: 0,
  approvedCount: 0,
  rejectedCount: 0,
  todayNewCount: 0,
  weekNewCount: 0,
  monthNewCount: 0,
  avgRating: 0,
  totalOrderCount: 0,
  totalCompleteCount: 0,
  avgCompleteRate: 0,
});

// 格式化日期时间
const formatDateTime = (dateTime: string | undefined): string => {
  if (!dateTime) return "-";
  return dayjs(dateTime).format("YYYY-MM-DD HH:mm:ss");
};

// 加载达人列表
const loadExpertList = async () => {
  try {
    loading.value = true;
    const params = {
      ...searchForm,
      current: pagination.current,
      size: pagination.size,
    };

    const response = await getExpertPage(params);
    expertList.value = response.records || [];
    pagination.total = response.total || 0;
  } catch (error) {
    console.error("加载达人列表失败:", error);
    ElMessage.error("加载达人列表失败");
  } finally {
    loading.value = false;
  }
};

// 加载统计信息
const loadStatistics = async () => {
  try {
    statistics.value = await getExpertStatistics();
  } catch (error) {
    console.error("加载统计信息失败:", error);
  }
};

// 加载分类列表
const loadCategories = async () => {
  try {
    categories.value = await getEnabledCategories();
  } catch (error) {
    console.error("加载分类列表失败:", error);
  }
};

// 搜索
const handleSearch = () => {
  pagination.current = 1;
  loadExpertList();
};

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: "",
    categoryId: undefined,
    status: undefined,
    auditStatus: undefined,
  });
  pagination.current = 1;
  loadExpertList();
};

// 选择变化
const handleSelectionChange = (selection: Expert[]) => {
  selectedExperts.value = selection;
};

// 新增达人
const handleAdd = () => {
  ElMessage.info("新增达人功能开发中...");
};

// 查看达人
const handleView = (expert: Expert) => {
  selectedExpertId.value = expert.id;
  detailDialogVisible.value = true;
};

// 编辑达人
const handleEdit = (expert: Expert) => {
  ElMessage.info(`编辑达人: ${expert.expertName}`);
};

// 从详情弹窗编辑
const handleEditFromDetail = (expert: Expert) => {
  detailDialogVisible.value = false;
  handleEdit(expert);
};

// 删除达人
const handleDelete = async (expert: Expert) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除达人 "${expert.expertName}" 吗？`,
      "确认删除",
      {
        type: "warning",
      }
    );

    await deleteExpert(expert.id);
    ElMessage.success("删除成功");
    loadExpertList();
    loadStatistics();
  } catch (error: any) {
    if (error !== "cancel") {
      console.error("删除达人失败:", error);
      ElMessage.error("删除失败");
    }
  }
};

// 批量删除
const handleBatchDelete = async () => {
  if (selectedExperts.value.length === 0) {
    ElMessage.warning("请选择要删除的达人");
    return;
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedExperts.value.length} 个达人吗？`,
      "确认批量删除",
      {
        type: "warning",
      }
    );

    const expertIds = selectedExperts.value.map((expert) => expert.id);
    await batchDeleteExperts(expertIds);
    ElMessage.success("批量删除成功");
    loadExpertList();
    loadStatistics();
  } catch (error: any) {
    if (error !== "cancel") {
      console.error("批量删除失败:", error);
      ElMessage.error("批量删除失败");
    }
  }
};

// 更新达人状态
const handleUpdateStatus = async (expert: Expert) => {
  const newStatus = expert.status === 1 ? 0 : 1;
  const statusText = newStatus === 1 ? "上线" : "下线";

  try {
    await ElMessageBox.confirm(
      `确定要将达人 "${expert.expertName}" ${statusText}吗？`,
      "确认操作",
      {
        type: "warning",
      }
    );

    await updateExpertStatus(expert.id, newStatus);
    ElMessage.success(`${statusText}成功`);
    loadExpertList();
    loadStatistics();
  } catch (error: any) {
    if (error !== "cancel") {
      console.error("更新状态失败:", error);
      ElMessage.error("更新状态失败");
    }
  }
};

// 审核达人
const handleAudit = async (expert: Expert) => {
  try {
    // 先选择审核结果
    const auditStatus = await ElMessageBox.confirm(
      "请选择审核结果",
      "达人审核",
      {
        distinguishCancelAndClose: true,
        confirmButtonText: "通过",
        cancelButtonText: "拒绝",
        type: "warning",
      }
    )
      .then(() => 1)
      .catch(() => 2);

    // 再输入审核备注
    const { value: auditRemark } = await ElMessageBox.prompt(
      `审核结果：${auditStatus === 1 ? "通过" : "拒绝"}\n请填写审核备注：`,
      "填写审核备注",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        inputPlaceholder: "请输入审核备注（必填）",
        inputValidator: (value) => {
          if (!value || value.trim() === "") {
            return "审核备注不能为空";
          }
          if (value.length > 255) {
            return "审核备注长度不能超过255个字符";
          }
          return true;
        },
      }
    );

    if (!auditRemark || auditRemark.trim() === "") {
      ElMessage.warning("审核备注不能为空");
      return;
    }

    await auditExpert({
      expertId: expert.id,
      auditStatus,
      auditRemark: auditRemark.trim(),
    });

    ElMessage.success("审核成功");
    loadExpertList();
    loadStatistics();
  } catch (error: any) {
    if (error !== "cancel") {
      console.error("审核操作失败:", error);
      ElMessage.error("审核失败");
    }
  }
};

// 下拉菜单命令处理
const handleDropdownCommand = (command: string, expert: Expert) => {
  switch (command) {
    case "audit":
      handleAudit(expert);
      break;
    case "status":
      handleUpdateStatus(expert);
      break;
    case "delete":
      handleDelete(expert);
      break;
  }
};

// 初始化
onMounted(() => {
  loadExpertList();
  loadStatistics();
  loadCategories();
});
</script>

<style scoped>
.experts-management {
  padding: 0;
}

/* 统计卡片样式 */
.statistics-cards {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.online {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.pending {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.rating {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

/* 主卡片样式 */
.main-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 搜索表单样式 */
.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

/* 表格样式 */
.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nickname {
  font-weight: 500;
  color: #303133;
}

.phone {
  font-size: 12px;
  color: #909399;
}

.rating-info {
  display: flex;
  align-items: center;
}

.order-stats {
  font-size: 12px;
  line-height: 1.4;
}

.order-stats div {
  color: #606266;
}

.text-gray {
  color: #c0c4cc;
}

.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

/* 分页样式 */
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stat-number {
    font-size: 24px;
  }

  .stat-icon {
    width: 50px;
    height: 50px;
    font-size: 20px;
  }
}

@media (max-width: 768px) {
  .statistics-cards .el-col {
    margin-bottom: 16px;
  }

  .search-form .el-form {
    flex-direction: column;
  }

  .search-form .el-form-item {
    margin-right: 0;
    margin-bottom: 16px;
  }

  .header-actions {
    flex-direction: column;
    gap: 8px;
  }

  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }
}

/* 表格行悬停效果 */
:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

/* 自定义标签样式 */
:deep(.el-tag) {
  border-radius: 6px;
  font-weight: 500;
}

/* 自定义按钮样式 */
:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-1px);
}

/* 自定义输入框样式 */
:deep(.el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 8px;
}
</style>
