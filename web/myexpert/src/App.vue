<script setup lang="ts">
import { RouterView } from "vue-router";
import { onMounted, nextTick } from "vue";

// 应用挂载完成后隐藏加载屏
onMounted(async () => {
  await nextTick();
  // 延迟一点时间确保所有组件都已渲染
  setTimeout(() => {
    document.body.classList.add("app-loaded");
  }, 100);
});
</script>

<template>
  <div id="app">
    <RouterView v-slot="{ Component, route }">
      <Transition name="fade" mode="out-in">
        <component :is="Component" :key="route.path" />
      </Transition>
    </RouterView>
  </div>
</template>

<style>
#app {
  height: 100vh;
  width: 100vw;
  background-color: #f0f2f5;
  overflow: hidden;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB",
    "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
  background-color: #f0f2f5;
}

/* 路由切换过渡效果 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 确保路由组件占满容器 */
.fade-enter-active,
.fade-leave-active {
  position: absolute;
  width: 100%;
  height: 100%;
}
</style>
