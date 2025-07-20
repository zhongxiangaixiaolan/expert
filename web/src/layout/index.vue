<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
      <div class="logo">
        <img v-if="!isCollapse" src="/favicon.ico" alt="Logo" />
        <span v-if="!isCollapse" class="logo-text">达人接单</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :unique-opened="true"
        class="sidebar-menu"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>数据统计</template>
        </el-menu-item>

        <el-menu-item index="/system">
          <el-icon><Setting /></el-icon>
          <template #title>系统配置</template>
        </el-menu-item>

        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>

        <el-menu-item index="/experts">
          <el-icon><UserFilled /></el-icon>
          <template #title>达人管理</template>
        </el-menu-item>

        <el-menu-item index="/categories">
          <el-icon><Menu /></el-icon>
          <template #title>分类管理</template>
        </el-menu-item>

        <el-menu-item index="/orders">
          <el-icon><Document /></el-icon>
          <template #title>订单管理</template>
        </el-menu-item>

        <el-menu-item index="/reviews">
          <el-icon><Star /></el-icon>
          <template #title>评价管理</template>
        </el-menu-item>

        <el-menu-item index="/banners">
          <el-icon><Picture /></el-icon>
          <template #title>轮播图管理</template>
        </el-menu-item>

        <el-menu-item index="/announcements">
          <el-icon><Bell /></el-icon>
          <template #title>通告管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-button link @click="toggleCollapse">
            <el-icon size="20">
              <Expand v-if="isCollapse" />
              <Fold v-else />
            </el-icon>
          </el-button>

          <el-breadcrumb separator="/">
            <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar
                :size="32"
                :src="
                  getAvatarUrl(
                    adminInfo?.avatar,
                    0,
                    'default',
                    adminInfo?.realName || adminInfo?.username
                  )
                "
              >
                {{
                  adminInfo?.realName?.charAt(0) ||
                  adminInfo?.username?.charAt(0) ||
                  "A"
                }}
              </el-avatar>
              <span class="username">{{
                adminInfo?.realName || adminInfo?.username
              }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容 -->
      <el-main class="main-content">
        <RouterView />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessageBox, ElMessage } from "element-plus";
import { useAuthStore } from "@/stores/auth";
import { getAvatarUrl } from "@/utils/avatar";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

// 侧边栏折叠状态
const isCollapse = ref(false);

// 当前激活的菜单
const activeMenu = computed(() => route.path);

// 当前页面标题
const currentPageTitle = computed(() => route.meta?.title || "首页");

// 管理员信息
const adminInfo = computed(() => authStore.adminInfo);

// 切换侧边栏折叠状态
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value;
};

// 处理下拉菜单命令
const handleCommand = async (command: string) => {
  switch (command) {
    case "profile":
      router.push("/profile");
      break;
    case "logout":
      await handleLogout();
      break;
  }
};

// 处理退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm("确定要退出登录吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await authStore.logout();
    ElMessage.success("退出登录成功");
    router.push("/login");
  } catch (error) {
    // 用户取消操作
  }
};

// 组件挂载时检查登录状态
onMounted(() => {
  if (!authStore.isLoggedIn) {
    router.push("/login");
  }
});
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  width: 100vw;
  background-color: var(--color-background-page);
  position: relative;
}

.sidebar {
  background: linear-gradient(180deg, var(--gray-900) 0%, var(--gray-800) 100%);
  transition: width var(--transition-base);
  box-shadow: var(--shadow-lg);
  border-right: 1px solid var(--gray-700);
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 64px;
  background: linear-gradient(
    135deg,
    var(--primary-700) 0%,
    var(--primary-600) 100%
  );
  color: white;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  position: relative;
  overflow: hidden;
}

.logo::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    45deg,
    transparent 30%,
    rgba(255, 255, 255, 0.1) 50%,
    transparent 70%
  );
  transform: translateX(-100%);
  transition: transform 0.6s;
}

.logo:hover::before {
  transform: translateX(100%);
}

.logo img {
  width: 36px;
  height: 36px;
  margin-right: var(--spacing-sm);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}

.logo-text {
  white-space: nowrap;
  font-weight: var(--font-weight-bold);
  letter-spacing: 0.5px;
}

.sidebar-menu {
  border: none;
  background: transparent;
  padding: var(--spacing-base) 0;
}

:deep(.el-menu-item) {
  color: var(--gray-300);
  margin: var(--spacing-xs) var(--spacing-base);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
  position: relative;
  overflow: hidden;
}

:deep(.el-menu-item::before) {
  content: "";
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: var(--color-primary);
  transform: scaleY(0);
  transition: transform var(--transition-fast);
}

:deep(.el-menu-item:hover) {
  background: linear-gradient(
    135deg,
    var(--primary-600) 0%,
    var(--primary-500) 100%
  );
  color: white;
  transform: translateX(4px);
  box-shadow: var(--shadow-md);
}

:deep(.el-menu-item:hover::before) {
  transform: scaleY(1);
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(
    135deg,
    var(--primary-600) 0%,
    var(--primary-500) 100%
  );
  color: white;
  box-shadow: var(--shadow-md);
}

:deep(.el-menu-item.is-active::before) {
  transform: scaleY(1);
}

:deep(.el-menu-item .el-icon) {
  margin-right: var(--spacing-sm);
  font-size: var(--font-size-lg);
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: var(--color-background-page);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 var(--spacing-xl);
  height: 64px;
  background: var(--color-background);
  border-bottom: 1px solid var(--color-border-light);
  box-shadow: var(--shadow-sm);
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 10;
}

.header::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    var(--primary-200) 50%,
    transparent 100%
  );
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
}

.header-left .el-button {
  border: none;
  background: transparent;
  color: var(--color-text-secondary);
  padding: var(--spacing-sm);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.header-left .el-button:hover {
  background-color: var(--color-background-soft);
  color: var(--color-primary);
  transform: scale(1.05);
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  padding: var(--spacing-sm) var(--spacing-base);
  border-radius: var(--radius-lg);
  transition: all var(--transition-fast);
  border: 1px solid transparent;
}

.user-info:hover {
  background: linear-gradient(
    135deg,
    var(--primary-50) 0%,
    var(--secondary-50) 100%
  );
  border-color: var(--primary-200);
  box-shadow: var(--shadow-sm);
  transform: translateY(-1px);
}

.user-info .el-avatar {
  border: 2px solid var(--primary-200);
  transition: all var(--transition-fast);
}

.user-info:hover .el-avatar {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px var(--primary-100);
}

.username {
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
  font-weight: var(--font-weight-medium);
  margin: 0 var(--spacing-xs);
}

.main-content {
  background-color: var(--color-background-page);
  padding: var(--spacing-xl);
  overflow-y: auto;
  flex: 1;
  position: relative;
}

/* 面包屑样式优化 */
:deep(.el-breadcrumb) {
  font-weight: var(--font-weight-medium);
}

:deep(.el-breadcrumb__item) {
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: var(--color-primary);
  font-weight: var(--font-weight-semibold);
}

/* 下拉菜单样式优化 */
:deep(.el-dropdown-menu) {
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--color-border-light);
  padding: var(--spacing-sm);
}

:deep(.el-dropdown-menu__item) {
  border-radius: var(--radius-md);
  margin: var(--spacing-xs) 0;
  transition: all var(--transition-fast);
  color: var(--color-text-primary);
}

:deep(.el-dropdown-menu__item:hover) {
  background-color: var(--error-50);
  color: var(--error-600);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header {
    padding: 0 var(--spacing-base);
  }

  .header-left {
    gap: var(--spacing-base);
  }

  .main-content {
    padding: var(--spacing-base);
  }

  .username {
    display: none;
  }
}
</style>
