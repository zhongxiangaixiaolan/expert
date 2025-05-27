<template>
  <div class="reviews-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>评价管理</span>
        </div>
      </template>
      
      <div class="placeholder">
        <el-icon size="64"><Star /></el-icon>
        <h3>评价管理功能开发中...</h3>
        <p>此页面将包含评价查询、回复管理、隐藏显示等功能</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
// 评价管理页面 - 开发中
</script>

<style scoped>
.reviews-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.placeholder {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}

.placeholder h3 {
  margin: 20px 0 10px;
  color: #606266;
}

.placeholder p {
  font-size: 14px;
}
</style>
