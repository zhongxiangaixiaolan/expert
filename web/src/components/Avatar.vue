<template>
  <el-avatar
    :size="size"
    :src="avatarUrl"
    :class="avatarClass"
    @error="handleError"
  >
    {{ fallbackText }}
  </el-avatar>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { getAvatarUrl, AVATAR_CONFIG } from "@/utils/avatar";

// 定义组件属性
interface AvatarProps {
  avatar?: string | null;
  nickname?: string;
  gender?: number;
  size?: number | "small" | "medium" | "large" | "xlarge";
  fallbackType?: "default" | "letter";
  shape?: "circle" | "square";
}

// 属性定义
const props = withDefaults(defineProps<AvatarProps>(), {
  avatar: null,
  nickname: "",
  gender: 0,
  size: "medium",
  fallbackType: "letter",
  shape: "circle",
});

// 状态
const imageError = ref(false);

// 计算属性
const size = computed(() => {
  if (typeof props.size === "number") {
    return props.size;
  }
  return (
    AVATAR_CONFIG.displaySizes[props.size] || AVATAR_CONFIG.displaySizes.medium
  );
});

const avatarUrl = computed(() => {
  if (imageError.value) {
    // 图片加载失败时使用默认头像
    return getAvatarUrl(null, props.gender, "default", props.nickname);
  }
  return getAvatarUrl(
    props.avatar,
    props.gender,
    props.fallbackType,
    props.nickname
  );
});

const fallbackText = computed(() => {
  if (props.nickname) {
    return props.nickname.charAt(0).toUpperCase();
  }
  return "U";
});

const avatarClass = computed(() => {
  return {
    "avatar-circle": props.shape === "circle",
    "avatar-square": props.shape === "square",
  };
});

// 处理图片加载错误
const handleError = () => {
  imageError.value = true;
};
</script>

<style scoped>
.avatar-circle {
  border-radius: 50%;
}

.avatar-square {
  border-radius: 4px;
}

:deep(.el-avatar) {
  background-color: #f0f0f0;
  color: #666;
  font-weight: 500;
}
</style>
