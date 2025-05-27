<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>达人接单管理系统</h2>
        <p>管理员登录</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            {{ loading ? "登录中..." : "登录" }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { useAuthStore } from "@/stores/auth";
import type { LoginParams } from "@/api/auth";

const router = useRouter();
const authStore = useAuthStore();

// 表单引用
const loginFormRef = ref<FormInstance>();

// 加载状态
const loading = ref(false);

// 登录表单数据
const loginForm = reactive<LoginParams>({
  username: "",
  password: "",
});

// 表单验证规则
const loginRules: FormRules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    {
      min: 3,
      max: 20,
      message: "用户名长度在 3 到 20 个字符",
      trigger: "blur",
    },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 20, message: "密码长度在 6 到 20 个字符", trigger: "blur" },
  ],
};

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return;

  try {
    // 表单验证
    await loginFormRef.value.validate();

    loading.value = true;

    // 执行登录
    await authStore.login(loginForm);

    ElMessage.success("登录成功");

    // 跳转到首页
    router.push("/");
  } catch (error) {
    console.error("登录失败:", error);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  background: linear-gradient(
    135deg,
    var(--primary-600) 0%,
    var(--secondary-600) 100%
  );
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1;
  overflow: hidden;
}

.login-container::before {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(
    circle,
    rgba(255, 255, 255, 0.1) 1px,
    transparent 1px
  );
  background-size: 50px 50px;
  animation: float 20s ease-in-out infinite;
}

@keyframes float {
  0%,
  100% {
    transform: translate(0, 0) rotate(0deg);
  }
  33% {
    transform: translate(30px, -30px) rotate(120deg);
  }
  66% {
    transform: translate(-20px, 20px) rotate(240deg);
  }
}

.login-box {
  width: 420px;
  padding: var(--spacing-2xl);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-xl);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 2;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: var(--spacing-2xl);
  position: relative;
}

.login-header::after {
  content: "";
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: linear-gradient(
    90deg,
    var(--primary-600) 0%,
    var(--secondary-600) 100%
  );
  border-radius: var(--radius-base);
}

.login-header h2 {
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-base);
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  background: linear-gradient(
    135deg,
    var(--primary-600) 0%,
    var(--secondary-600) 100%
  );
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-header p {
  color: var(--color-text-secondary);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
}

.login-form {
  margin-top: var(--spacing-xl);
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  background: linear-gradient(
    135deg,
    var(--primary-600) 0%,
    var(--secondary-600) 100%
  );
  border: none;
  border-radius: var(--radius-lg);
  transition: all var(--transition-base);
  position: relative;
  overflow: hidden;
}

.login-btn::before {
  content: "";
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.2),
    transparent
  );
  transition: left 0.5s;
}

.login-btn:hover::before {
  left: 100%;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.login-btn:active {
  transform: translateY(0);
}

:deep(.el-input__wrapper) {
  padding: var(--spacing-base) var(--spacing-lg);
  border-radius: var(--radius-lg);
  border: 2px solid var(--color-border-light);
  transition: all var(--transition-fast);
  background-color: rgba(255, 255, 255, 0.8);
}

:deep(.el-input__wrapper:hover) {
  border-color: var(--color-border);
  background-color: rgba(255, 255, 255, 0.9);
}

:deep(.el-input__wrapper.is-focus) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px var(--primary-100);
  background-color: white;
}

:deep(.el-form-item) {
  margin-bottom: var(--spacing-xl);
}

:deep(.el-input__inner) {
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
}

:deep(.el-input__inner::placeholder) {
  color: var(--color-text-placeholder);
}

:deep(.el-input__prefix) {
  color: var(--color-text-muted);
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-box {
    width: 90%;
    padding: var(--spacing-xl);
    margin: var(--spacing-base);
  }

  .login-header h2 {
    font-size: var(--font-size-2xl);
  }
}
</style>
