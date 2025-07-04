<template>
  <div class="orders-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
          <div class="header-actions">
            <el-button @click="handleExport">
              <el-icon><Download /></el-icon>
              导出数据
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :model="searchForm" inline>
          <el-form-item label="订单编号">
            <el-input
              v-model="searchForm.orderNo"
              placeholder="请输入订单编号"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="用户昵称">
            <el-input
              v-model="searchForm.userNickname"
              placeholder="请输入用户昵称"
              clearable
              style="width: 150px"
            />
          </el-form-item>
          <el-form-item label="达人昵称">
            <el-input
              v-model="searchForm.expertNickname"
              placeholder="请输入达人昵称"
              clearable
              style="width: 150px"
            />
          </el-form-item>
          <el-form-item label="订单状态">
            <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              style="width: 120px"
            >
              <el-option label="待接单" :value="1" />
              <el-option label="已接单" :value="2" />
              <el-option label="服务中" :value="3" />
              <el-option label="已完成" :value="4" />
              <el-option label="已取消" :value="5" />
              <el-option label="售后中" :value="6" />
            </el-select>
          </el-form-item>
          <el-form-item label="支付状态">
            <el-select
              v-model="searchForm.payStatus"
              placeholder="请选择支付状态"
              clearable
              style="width: 120px"
            >
              <el-option label="未支付" :value="0" />
              <el-option label="已支付" :value="1" />
              <el-option label="已退款" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="创建时间">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 240px"
            />
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
        <el-table-column prop="orderNo" label="订单编号" width="180" />
        <el-table-column label="用户信息" width="150">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="32" :src="row.userAvatar">
                {{ row.userNickname?.charAt(0) || "U" }}
              </el-avatar>
              <span class="user-name">{{
                row.userNickname || "未知用户"
              }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="达人信息" width="150">
          <template #default="{ row }">
            <div v-if="row.expertId" class="user-info">
              <el-avatar :size="32" :src="row.expertAvatar">
                {{ row.expertNickname?.charAt(0) || "E" }}
              </el-avatar>
              <span class="user-name">{{
                row.expertNickname || "未知达人"
              }}</span>
            </div>
            <span v-else class="text-placeholder">暂无</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="serviceName"
          label="服务名称"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column
          prop="title"
          label="订单标题"
          min-width="180"
          show-overflow-tooltip
        />
        <el-table-column label="金额" width="100">
          <template #default="{ row }">
            <span class="amount">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusTagType(row.status)">
              {{ getOrderStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="支付状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getPayStatusTagType(row.payStatus)">
              {{ getPayStatusText(row.payStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)">
              <el-icon><View /></el-icon>
              详情
            </el-button>
            <el-button
              v-if="row.status !== 5 && row.status !== 4"
              type="warning"
              link
              @click="handleCancel(row)"
            >
              <el-icon><Close /></el-icon>
              取消
            </el-button>
            <el-button
              v-if="row.payStatus === 1 && row.status !== 4"
              type="danger"
              link
              @click="handleRefund(row)"
            >
              <el-icon><RefreshLeft /></el-icon>
              退款
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="800px"
      @close="handleDetailDialogClose"
    >
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单编号">
            {{ currentOrder.orderNo }}
          </el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getOrderStatusTagType(currentOrder.status)">
              {{ getOrderStatusText(currentOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="支付状态">
            <el-tag :type="getPayStatusTagType(currentOrder.payStatus)">
              {{ getPayStatusText(currentOrder.payStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="订单金额">
            ¥{{ currentOrder.amount }}
          </el-descriptions-item>
          <el-descriptions-item label="用户昵称">
            {{ currentOrder.userNickname || "未知用户" }}
          </el-descriptions-item>
          <el-descriptions-item label="达人昵称">
            {{ currentOrder.expertNickname || "暂无" }}
          </el-descriptions-item>
          <el-descriptions-item label="服务名称" :span="2">
            {{ currentOrder.serviceName }}
          </el-descriptions-item>
          <el-descriptions-item label="订单标题" :span="2">
            {{ currentOrder.title || "无" }}
          </el-descriptions-item>
          <el-descriptions-item label="订单描述" :span="2">
            {{ currentOrder.description || "无" }}
          </el-descriptions-item>
          <el-descriptions-item label="用户备注" :span="2">
            {{ currentOrder.userRemark || "无" }}
          </el-descriptions-item>
          <el-descriptions-item label="达人备注" :span="2">
            {{ currentOrder.expertRemark || "无" }}
          </el-descriptions-item>
          <el-descriptions-item label="管理员备注" :span="2">
            {{ currentOrder.adminRemark || "无" }}
          </el-descriptions-item>
          <el-descriptions-item label="联系方式">
            {{ currentOrder.contactInfo || "无" }}
          </el-descriptions-item>
          <el-descriptions-item label="服务地址">
            {{ currentOrder.serviceAddress || "无" }}
          </el-descriptions-item>
          <el-descriptions-item label="预约时间">
            {{ currentOrder.appointmentTime || "无" }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ currentOrder.createTime }}
          </el-descriptions-item>
          <el-descriptions-item label="支付时间">
            {{ currentOrder.payTime || "未支付" }}
          </el-descriptions-item>
          <el-descriptions-item label="接单时间">
            {{ currentOrder.acceptTime || "未接单" }}
          </el-descriptions-item>
          <el-descriptions-item label="开始时间">
            {{ currentOrder.startTime || "未开始" }}
          </el-descriptions-item>
          <el-descriptions-item label="完成时间">
            {{ currentOrder.finishTime || "未完成" }}
          </el-descriptions-item>
          <el-descriptions-item label="取消时间">
            {{ currentOrder.cancelTime || "未取消" }}
          </el-descriptions-item>
          <el-descriptions-item label="取消原因" :span="2">
            {{ currentOrder.cancelReason || "无" }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 管理员备注编辑 -->
        <div class="admin-remark-section">
          <h4>管理员备注</h4>
          <el-input
            v-model="adminRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入管理员备注"
          />
          <div class="remark-actions">
            <el-button
              type="primary"
              @click="handleUpdateRemark"
              :loading="remarkLoading"
            >
              保存备注
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 取消订单对话框 -->
    <el-dialog v-model="cancelDialogVisible" title="取消订单" width="500px">
      <el-form :model="cancelForm" label-width="100px">
        <el-form-item label="取消原因" required>
          <el-input
            v-model="cancelForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入取消原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelDialogVisible = false">取消</el-button>
          <el-button
            type="danger"
            @click="handleConfirmCancel"
            :loading="cancelLoading"
          >
            确认取消
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 退款对话框 -->
    <el-dialog v-model="refundDialogVisible" title="订单退款" width="500px">
      <el-form :model="refundForm" label-width="100px">
        <el-form-item label="退款原因" required>
          <el-input
            v-model="refundForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入退款原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="refundDialogVisible = false">取消</el-button>
          <el-button
            type="danger"
            @click="handleConfirmRefund"
            :loading="refundLoading"
          >
            确认退款
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getOrderPage,
  getOrderDetail,
  updateOrder,
  cancelOrder,
  refundOrder,
  getOrderStatusText,
  getOrderStatusTagType,
  getPayStatusText,
  getPayStatusTagType,
  type Order,
  type OrderQueryParams,
} from "@/api/orders";

// 响应式数据
const loading = ref(false);
const tableData = ref<Order[]>([]);
const selectedRows = ref<Order[]>([]);
const detailDialogVisible = ref(false);
const cancelDialogVisible = ref(false);
const refundDialogVisible = ref(false);
const currentOrder = ref<Order | null>(null);
const adminRemark = ref("");
const remarkLoading = ref(false);
const cancelLoading = ref(false);
const refundLoading = ref(false);
const dateRange = ref<[string, string] | null>(null);

// 搜索表单
const searchForm = reactive<OrderQueryParams>({
  orderNo: "",
  userNickname: "",
  expertNickname: "",
  status: undefined,
  payStatus: undefined,
});

// 分页数据
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0,
});

// 取消订单表单
const cancelForm = reactive({
  orderId: 0,
  reason: "",
});

// 退款表单
const refundForm = reactive({
  orderId: 0,
  reason: "",
});

// 获取订单列表
const fetchOrders = async () => {
  try {
    loading.value = true;
    const params: OrderQueryParams = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
    };

    // 处理日期范围
    if (dateRange.value) {
      params.startTime = dateRange.value[0];
      params.endTime = dateRange.value[1];
    }

    const data = await getOrderPage(params);
    tableData.value = data.records;
    pagination.total = data.total;
  } catch (error) {
    ElMessage.error("获取订单列表失败");
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  pagination.current = 1;
  fetchOrders();
};

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    orderNo: "",
    userNickname: "",
    expertNickname: "",
    status: undefined,
    payStatus: undefined,
  });
  dateRange.value = null;
  pagination.current = 1;
  fetchOrders();
};

// 查看详情
const handleViewDetail = async (row: Order) => {
  try {
    const orderDetail = await getOrderDetail(row.id);
    currentOrder.value = orderDetail;
    adminRemark.value = orderDetail.adminRemark || "";
    detailDialogVisible.value = true;
  } catch (error) {
    ElMessage.error("获取订单详情失败");
  }
};

// 取消订单
const handleCancel = (row: Order) => {
  cancelForm.orderId = row.id;
  cancelForm.reason = "";
  cancelDialogVisible.value = true;
};

// 确认取消订单
const handleConfirmCancel = async () => {
  if (!cancelForm.reason.trim()) {
    ElMessage.warning("请输入取消原因");
    return;
  }

  try {
    cancelLoading.value = true;
    await cancelOrder(cancelForm.orderId, cancelForm.reason);
    ElMessage.success("订单取消成功");
    cancelDialogVisible.value = false;
    fetchOrders();
  } catch (error) {
    ElMessage.error("取消订单失败");
  } finally {
    cancelLoading.value = false;
  }
};

// 退款
const handleRefund = (row: Order) => {
  refundForm.orderId = row.id;
  refundForm.reason = "";
  refundDialogVisible.value = true;
};

// 确认退款
const handleConfirmRefund = async () => {
  if (!refundForm.reason.trim()) {
    ElMessage.warning("请输入退款原因");
    return;
  }

  try {
    refundLoading.value = true;
    await refundOrder(refundForm.orderId, refundForm.reason);
    ElMessage.success("退款处理成功");
    refundDialogVisible.value = false;
    fetchOrders();
  } catch (error) {
    ElMessage.error("退款处理失败");
  } finally {
    refundLoading.value = false;
  }
};

// 更新管理员备注
const handleUpdateRemark = async () => {
  if (!currentOrder.value) return;

  try {
    remarkLoading.value = true;
    await updateOrder({
      id: currentOrder.value.id,
      adminRemark: adminRemark.value,
    });
    ElMessage.success("备注更新成功");
    currentOrder.value.adminRemark = adminRemark.value;
  } catch (error) {
    ElMessage.error("备注更新失败");
  } finally {
    remarkLoading.value = false;
  }
};

// 导出数据
const handleExport = () => {
  ElMessage.info("导出功能开发中...");
};

// 表格选择变化
const handleSelectionChange = (selection: Order[]) => {
  selectedRows.value = selection;
};

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size;
  pagination.current = 1;
  fetchOrders();
};

// 当前页变化
const handleCurrentChange = (current: number) => {
  pagination.current = current;
  fetchOrders();
};

// 关闭详情对话框
const handleDetailDialogClose = () => {
  currentOrder.value = null;
  adminRemark.value = "";
};

// 页面加载时获取数据
onMounted(() => {
  fetchOrders();
});
</script>

<style scoped>
.orders-management {
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

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-size: 14px;
  color: #606266;
}

.text-placeholder {
  color: #c0c4cc;
  font-size: 14px;
}

.amount {
  font-weight: 600;
  color: #e6a23c;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.order-detail {
  max-height: 600px;
  overflow-y: auto;
}

.admin-remark-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.admin-remark-section h4 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 16px;
}

.remark-actions {
  margin-top: 10px;
  text-align: right;
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

:deep(.el-descriptions) {
  margin-bottom: 20px;
}

:deep(.el-descriptions__label) {
  font-weight: 600;
}
</style>
