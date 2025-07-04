<template>
  <div class="profile-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>个人中心</h2>
      <p>管理您的个人信息和账户设置</p>
    </div>

    <div class="profile-content">
      <!-- 个人信息卡片 -->
      <el-card class="profile-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><User /></el-icon>
            <span>个人信息</span>
          </div>
        </template>

        <div class="profile-info">
          <!-- 头像部分 -->
          <div class="avatar-section">
            <el-avatar
              :size="100"
              :src="
                getAvatarUrl(
                  profileData.avatar,
                  0,
                  'default',
                  profileData.realName || profileData.username
                )
              "
              class="profile-avatar"
            >
              {{
                profileData.realName?.charAt(0) ||
                profileData.username?.charAt(0) ||
                "A"
              }}
            </el-avatar>
            <el-upload
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="handleAvatarUpload"
              accept="image/*"
              class="avatar-upload"
            >
              <el-button type="primary" size="small" :loading="avatarUploading">
                <el-icon><Camera /></el-icon>
                更换头像
              </el-button>
            </el-upload>
          </div>

          <!-- 信息表单 -->
          <div class="info-form">
            <el-form
              ref="profileFormRef"
              :model="profileForm"
              :rules="profileRules"
              label-width="100px"
              label-position="left"
            >
              <el-form-item label="用户名">
                <el-input v-model="profileData.username" disabled />
              </el-form-item>

              <el-form-item label="真实姓名" prop="realName">
                <el-input
                  v-model="profileForm.realName"
                  placeholder="请输入真实姓名"
                  maxlength="50"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="手机号" prop="phone">
                <el-input
                  v-model="profileForm.phone"
                  placeholder="请输入手机号"
                  maxlength="11"
                />
              </el-form-item>

              <el-form-item label="邮箱" prop="email">
                <el-input
                  v-model="profileForm.email"
                  placeholder="请输入邮箱地址"
                  maxlength="100"
                />
              </el-form-item>

              <el-form-item label="角色">
                <el-input v-model="profileData.roleName" disabled />
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  @click="handleUpdateProfile"
                  :loading="updating"
                >
                  保存修改
                </el-button>
                <el-button @click="resetProfileForm">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </el-card>

      <!-- 密码修改卡片 -->
      <el-card class="password-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><Lock /></el-icon>
            <span>修改密码</span>
          </div>
        </template>

        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-width="100px"
          label-position="left"
          class="password-form"
        >
          <el-form-item label="原密码" prop="oldPassword">
            <el-input
              v-model="passwordForm.oldPassword"
              type="password"
              placeholder="请输入原密码"
              show-password
            />
          </el-form-item>

          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码"
              show-password
            />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              @click="handleUpdatePassword"
              :loading="passwordUpdating"
            >
              修改密码
            </el-button>
            <el-button @click="resetPasswordForm">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import {
  ElMessage,
  ElMessageBox,
  type FormInstance,
  type FormRules,
  type UploadRequestOptions,
} from "element-plus";
import { User, Camera, Lock } from "@element-plus/icons-vue";
import {
  getProfile,
  updateProfile,
  updatePassword,
  uploadAvatar,
  type AdminProfile,
  type UpdateProfileParams,
  type UpdatePasswordParams,
} from "@/api/profile";
import { getAvatarUrl } from "@/utils/avatar";
import { useAuthStore } from "@/stores/auth";

// Store
const authStore = useAuthStore();

// 响应式数据
const profileData = ref<AdminProfile>({
  id: 0,
  username: "",
  realName: "",
  phone: "",
  email: "",
  avatar: "",
  role: "",
  roleName: "",
});

const profileForm = reactive<UpdateProfileParams>({
  realName: "",
  phone: "",
  email: "",
  avatar: "",
});

const passwordForm = reactive<UpdatePasswordParams>({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

// 表单引用
const profileFormRef = ref<FormInstance>();
const passwordFormRef = ref<FormInstance>();

// 加载状态
const updating = ref(false);
const passwordUpdating = ref(false);
const avatarUploading = ref(false);

// 表单验证规则
const profileRules: FormRules = {
  realName: [
    { max: 50, message: "真实姓名长度不能超过50个字符", trigger: "blur" },
  ],
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  email: [
    { type: "email", message: "请输入正确的邮箱地址", trigger: "blur" },
    { max: 100, message: "邮箱长度不能超过100个字符", trigger: "blur" },
  ],
};

const passwordRules: FormRules = {
  oldPassword: [{ required: true, message: "请输入原密码", trigger: "blur" }],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    {
      min: 6,
      max: 20,
      message: "密码长度必须在6-20个字符之间",
      trigger: "blur",
    },
  ],
  confirmPassword: [
    { required: true, message: "请再次输入新密码", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error("两次输入的密码不一致"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
};

// 获取个人信息
const fetchProfile = async () => {
  try {
    const data = await getProfile();
    profileData.value = data;

    // 同步到表单
    profileForm.realName = data.realName || "";
    profileForm.phone = data.phone || "";
    profileForm.email = data.email || "";
    profileForm.avatar = data.avatar || "";
  } catch (error) {
    console.error("获取个人信息失败:", error);
    ElMessage.error("获取个人信息失败");
  }
};

// 更新个人信息
const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return;

  try {
    await profileFormRef.value.validate();
    updating.value = true;

    await updateProfile(profileForm);
    ElMessage.success("个人信息更新成功");

    // 重新获取数据
    await fetchProfile();

    // 同步更新authStore中的adminInfo
    if (authStore.adminInfo) {
      authStore.adminInfo.realName =
        profileForm.realName || authStore.adminInfo.realName;
      authStore.adminInfo.avatar =
        profileForm.avatar || authStore.adminInfo.avatar;
      // 更新localStorage中的管理员信息
      localStorage.setItem("admin_info", JSON.stringify(authStore.adminInfo));
    }
  } catch (error) {
    console.error("更新个人信息失败:", error);
    ElMessage.error("更新个人信息失败");
  } finally {
    updating.value = false;
  }
};

// 重置个人信息表单
const resetProfileForm = () => {
  profileForm.realName = profileData.value.realName || "";
  profileForm.phone = profileData.value.phone || "";
  profileForm.email = profileData.value.email || "";
  profileForm.avatar = profileData.value.avatar || "";
  profileFormRef.value?.clearValidate();
};

// 修改密码
const handleUpdatePassword = async () => {
  if (!passwordFormRef.value) return;

  try {
    await passwordFormRef.value.validate();

    await ElMessageBox.confirm(
      "确定要修改密码吗？修改后需要重新登录。",
      "确认修改",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    passwordUpdating.value = true;
    await updatePassword(passwordForm);

    ElMessage.success("密码修改成功，请重新登录");

    // 清空表单
    resetPasswordForm();

    // 可以在这里添加跳转到登录页的逻辑
    // router.push('/login')
  } catch (error) {
    if (error !== "cancel") {
      console.error("修改密码失败:", error);
      ElMessage.error("修改密码失败");
    }
  } finally {
    passwordUpdating.value = false;
  }
};

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.oldPassword = "";
  passwordForm.newPassword = "";
  passwordForm.confirmPassword = "";
  passwordFormRef.value?.clearValidate();
};

// 头像上传前验证
const beforeAvatarUpload = (file: File) => {
  const isImage = file.type.startsWith("image/");
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error("只能上传图片文件!");
    return false;
  }
  if (!isLt2M) {
    ElMessage.error("图片大小不能超过 2MB!");
    return false;
  }
  return true;
};

// 处理头像上传
const handleAvatarUpload = async (options: UploadRequestOptions) => {
  try {
    avatarUploading.value = true;
    const response = await uploadAvatar(options.file as File);

    // 更新头像（只保存文件名）
    profileForm.avatar = response.url;
    profileData.value.avatar = response.url;

    // 保存到后端（只保存文件名）
    await updateProfile({ avatar: response.url });

    // 同步更新authStore中的adminInfo头像
    if (authStore.adminInfo) {
      authStore.adminInfo.avatar = response.url;
      // 更新localStorage中的管理员信息
      localStorage.setItem("admin_info", JSON.stringify(authStore.adminInfo));
    }

    ElMessage.success("头像上传成功");
  } catch (error) {
    console.error("头像上传失败:", error);
    ElMessage.error("头像上传失败");
  } finally {
    avatarUploading.value = false;
  }
};

// 页面加载时获取数据
onMounted(() => {
  fetchProfile();
});
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.profile-content {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
}

@media (min-width: 768px) {
  .profile-content {
    grid-template-columns: 2fr 1fr;
  }
}

.profile-card,
.password-card {
  border-radius: 12px;
  border: 1px solid #e4e7ed;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

@media (min-width: 768px) {
  .profile-info {
    flex-direction: row;
    align-items: flex-start;
  }
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  min-width: 140px;
}

.profile-avatar {
  border: 3px solid #f0f2f5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.avatar-upload {
  width: 100%;
}

.info-form {
  flex: 1;
  min-width: 0;
}

.password-form {
  max-width: 400px;
}

/* 响应式调整 */
@media (max-width: 767px) {
  .profile-container {
    padding: 16px;
  }

  .profile-content {
    grid-template-columns: 1fr;
  }

  .profile-info {
    flex-direction: column;
    text-align: center;
  }

  .info-form {
    margin-top: 16px;
  }
}

/* 表单样式优化 */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
}

/* 卡片悬停效果 */
.profile-card:hover,
.password-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s ease;
}
</style>
