<template>
  <div class="pagination-wrapper">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="pageSizes"
      :layout="layout"
      :background="background"
      :small="small"
      :disabled="disabled"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";

// 定义组件属性
interface PaginationProps {
  current?: number;
  size?: number;
  total?: number;
  pageSizes?: number[];
  layout?: string;
  background?: boolean;
  small?: boolean;
  disabled?: boolean;
}

// 定义事件
interface PaginationEmits {
  (e: "update:current", value: number): void;
  (e: "update:size", value: number): void;
  (e: "change", current: number, size: number): void;
  (e: "size-change", size: number): void;
  (e: "current-change", current: number): void;
}

// 属性定义
const props = withDefaults(defineProps<PaginationProps>(), {
  current: 1,
  size: 20,
  total: 0,
  pageSizes: () => [10, 20, 50, 100],
  layout: "total, sizes, prev, pager, next, jumper",
  background: true,
  small: false,
  disabled: false,
});

// 事件定义
const emit = defineEmits<PaginationEmits>();

// 计算属性
const currentPage = computed({
  get: () => props.current,
  set: (value: number) => {
    emit("update:current", value);
  },
});

const pageSize = computed({
  get: () => props.size,
  set: (value: number) => {
    emit("update:size", value);
  },
});

// 事件处理
const handleSizeChange = (size: number) => {
  emit("size-change", size);
  emit("change", currentPage.value, size);
};

const handleCurrentChange = (current: number) => {
  emit("current-change", current);
  emit("change", current, pageSize.value);
};
</script>

<style scoped>
.pagination-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: var(--spacing-xl) 0;
  margin-top: var(--spacing-xl);
  background: linear-gradient(
    135deg,
    var(--color-background-soft) 0%,
    var(--primary-50) 100%
  );
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border-light);
  position: relative;
  overflow: hidden;
}

.pagination-wrapper::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(
    90deg,
    var(--primary-500) 0%,
    var(--secondary-500) 100%
  );
}

.pagination-wrapper :deep(.el-pagination) {
  justify-content: center;
  position: relative;
  z-index: 1;
}

.pagination-wrapper :deep(.el-pagination__total) {
  margin-right: var(--spacing-base);
  color: var(--color-text-secondary);
  font-weight: var(--font-weight-medium);
  font-size: var(--font-size-sm);
}

.pagination-wrapper :deep(.el-pagination__sizes) {
  margin-right: var(--spacing-base);
}

.pagination-wrapper :deep(.el-pagination__sizes .el-select) {
  border-radius: var(--radius-md);
}

.pagination-wrapper :deep(.el-pagination__jump) {
  margin-left: var(--spacing-base);
  color: var(--color-text-secondary);
  font-weight: var(--font-weight-medium);
  font-size: var(--font-size-sm);
}

.pagination-wrapper :deep(.el-pager) {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.pagination-wrapper :deep(.el-pager li) {
  background-color: var(--color-background);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  color: var(--color-text-secondary);
  font-weight: var(--font-weight-medium);
  transition: all var(--transition-fast);
  min-width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0;
}

.pagination-wrapper :deep(.el-pager li:hover) {
  background: linear-gradient(
    135deg,
    var(--primary-100) 0%,
    var(--secondary-100) 100%
  );
  border-color: var(--color-primary);
  color: var(--color-primary);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.pagination-wrapper :deep(.el-pager li.is-active) {
  background: linear-gradient(
    135deg,
    var(--primary-600) 0%,
    var(--primary-500) 100%
  );
  border-color: var(--primary-600);
  color: white;
  box-shadow: var(--shadow-md);
  transform: translateY(-1px);
}

.pagination-wrapper :deep(.btn-prev),
.pagination-wrapper :deep(.btn-next) {
  background-color: var(--color-background);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  color: var(--color-text-secondary);
  font-weight: var(--font-weight-medium);
  transition: all var(--transition-fast);
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pagination-wrapper :deep(.btn-prev:hover),
.pagination-wrapper :deep(.btn-next:hover) {
  background: linear-gradient(
    135deg,
    var(--primary-100) 0%,
    var(--secondary-100) 100%
  );
  border-color: var(--color-primary);
  color: var(--color-primary);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.pagination-wrapper :deep(.btn-prev:disabled),
.pagination-wrapper :deep(.btn-next:disabled) {
  background-color: var(--gray-100);
  border-color: var(--gray-200);
  color: var(--gray-400);
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.pagination-wrapper :deep(.el-pagination__jump input) {
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border-light);
  transition: all var(--transition-fast);
  width: 50px;
  height: 32px;
  text-align: center;
  font-weight: var(--font-weight-medium);
}

.pagination-wrapper :deep(.el-pagination__jump input:focus) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px var(--primary-100);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .pagination-wrapper {
    padding: var(--spacing-base) var(--spacing-sm);
  }

  .pagination-wrapper :deep(.el-pagination) {
    flex-wrap: wrap;
    gap: var(--spacing-sm);
  }

  .pagination-wrapper :deep(.el-pagination__total),
  .pagination-wrapper :deep(.el-pagination__jump) {
    order: 3;
    margin: var(--spacing-sm) 0 0 0;
    flex-basis: 100%;
    text-align: center;
  }

  .pagination-wrapper :deep(.el-pagination__sizes) {
    margin-right: var(--spacing-sm);
  }
}
</style>
