<template>
  <div class="users-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
        </div>
      </template>

      <!-- 搜索筛选 -->
      <div class="search-section">
        <el-form :model="searchForm" inline>
          <el-form-item label="关键词">
            <el-input
              v-model="searchForm.keyword"
              placeholder="请输入昵称、手机号或真实姓名"
              clearable
              style="width: 250px"
            />
          </el-form-item>

          <el-form-item label="状态">
            <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              style="width: 120px"
            >
              <el-option label="正常" :value="1" />
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

      <!-- 用户列表 -->
      <el-table
        v-loading="loading"
        :data="userList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />

        <el-table-column prop="id" label="ID" width="80" />

        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <Avatar
              :avatar="row.avatar"
              :nickname="row.nickname"
              :gender="row.gender"
              size="medium"
            />
          </template>
        </el-table-column>

        <el-table-column prop="nickname" label="昵称" />

        <el-table-column label="性别" width="80">
          <template #default="{ row }">
            <el-tag :type="getGenderTagType(row.gender)" size="small">
              {{ getGenderText(row.gender) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="phone" label="手机号" />

        <el-table-column prop="balance" label="余额">
          <template #default="{ row }"> ¥{{ row.balance || 0 }} </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? "正常" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="registerTime" label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.registerTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? "禁用" : "启用" }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <Pagination
        v-model:current="pagination.current"
        v-model:size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 用户详情弹窗 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="600px">
      <div v-if="currentUser" class="user-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">
            {{ currentUser.id }}
          </el-descriptions-item>
          <el-descriptions-item label="昵称">
            {{ currentUser.nickname }}
          </el-descriptions-item>
          <el-descriptions-item label="性别">
            <el-tag :type="getGenderTagType(currentUser.gender)" size="small">
              {{ getGenderText(currentUser.gender) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="手机号">
            {{ currentUser.phone || "未绑定" }}
          </el-descriptions-item>
          <el-descriptions-item label="余额">
            ¥{{ currentUser.balance || 0 }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
              {{ currentUser.status === 1 ? "正常" : "禁用" }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">
            {{ formatDate(currentUser.registerTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from "dayjs";
import {
  getUserPage,
  getUserDetail,
  updateUserStatus,
  batchUpdateUserStatus,
  type User,
  type UserQueryParams,
} from "@/api/users";
import Pagination from "@/components/Pagination.vue";
import Avatar from "@/components/Avatar.vue";

// 搜索表单
const searchForm = reactive<UserQueryParams>({
  keyword: "",
  status: undefined as number | undefined,
});

// 分页信息
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0,
});

// 状态
const loading = ref(false);
const detailVisible = ref(false);
const selectedUsers = ref<User[]>([]);
const currentUser = ref<User | null>(null);

// 用户列表
const userList = ref<User[]>([]);

// 格式化日期
const formatDate = (date: string) => {
  return dayjs(date).format("YYYY-MM-DD HH:mm:ss");
};

// 获取性别文本
const getGenderText = (gender?: number) => {
  switch (gender) {
    case 1:
      return "男";
    case 2:
      return "女";
    default:
      return "未知";
  }
};

// 获取性别标签类型
const getGenderTagType = (gender?: number) => {
  switch (gender) {
    case 1:
      return "primary";
    case 2:
      return "danger";
    default:
      return "info";
  }
};

// 搜索
const handleSearch = () => {
  pagination.current = 1;
  loadUserList();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: "",
    status: undefined,
  });
  pagination.current = 1;
  loadUserList();
};

// 查看用户详情
const handleView = async (user: User) => {
  try {
    const userDetail = await getUserDetail(user.id);
    currentUser.value = userDetail;
    detailVisible.value = true;
  } catch (error) {
    ElMessage.error("获取用户详情失败");
  }
};

// 切换用户状态
const handleToggleStatus = async (user: User) => {
  const action = user.status === 1 ? "禁用" : "启用";
  const newStatus = user.status === 1 ? 0 : 1;

  try {
    await ElMessageBox.confirm(
      `确定要${action}用户"${user.nickname}"吗？`,
      "提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await updateUserStatus(user.id, newStatus);
    user.status = newStatus;
    ElMessage.success(`${action}成功`);
  } catch (error) {
    // 用户取消操作或API调用失败
    if (error !== "cancel") {
      ElMessage.error(`${action}失败`);
    }
  }
};

// 选择变化
const handleSelectionChange = (selection: User[]) => {
  selectedUsers.value = selection;
};

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size;
  loadUserList();
};

// 当前页变化
const handleCurrentChange = (current: number) => {
  pagination.current = current;
  loadUserList();
};

// 加载用户列表
const loadUserList = async () => {
  try {
    loading.value = true;

    const response = await getUserPage({
      ...searchForm,
      current: pagination.current,
      size: pagination.size,
    });

    userList.value = response.records;
    pagination.total = response.total;
  } catch (error) {
    console.error("加载用户列表失败:", error);
    ElMessage.error("加载用户列表失败");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadUserList();
});
</script>

<style scoped>
.users-management {
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
  border-radius: 4px;
}

.user-detail {
  padding: 20px 0;
}
</style>
