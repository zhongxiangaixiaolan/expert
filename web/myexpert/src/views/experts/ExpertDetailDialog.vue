<template>
  <el-dialog
    v-model="visible"
    title="达人详情"
    width="800px"
    :before-close="handleClose"
  >
    <div v-loading="loading" class="expert-detail">
      <div v-if="expert" class="expert-info">
        <!-- 基本信息 -->
        <div class="info-section">
          <h3 class="section-title">基本信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">达人头像：</span>
              <Avatar
                :avatar="expert.avatar"
                :nickname="expert.expertName"
                size="large"
              />
            </div>
            <div class="info-item">
              <span class="label">达人名称：</span>
              <span class="value">{{ expert.expertName }}</span>
            </div>
            <div class="info-item">
              <span class="label">所属分类：</span>
              <el-tag v-if="expert.category" type="primary">
                {{ expert.category.name }}
              </el-tag>
              <span v-else class="text-gray">-</span>
            </div>
            <div class="info-item">
              <span class="label">价格区间：</span>
              <span class="value">
                ¥{{ expert.priceMin || 0 }} - ¥{{ expert.priceMax || 0 }}
              </span>
            </div>
            <div class="info-item">
              <span class="label">评分：</span>
              <div class="rating-display">
                <el-rate
                  v-model="expert.rating"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value}"
                />
              </div>
            </div>
            <div class="info-item">
              <span class="label">状态：</span>
              <el-tag :type="getStatusTagType(expert.status)">
                {{ getStatusText(expert.status) }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">审核状态：</span>
              <el-tag :type="getAuditStatusTagType(expert.auditStatus)">
                {{ getAuditStatusText(expert.auditStatus) }}
              </el-tag>
            </div>
          </div>
        </div>

        <!-- 用户信息 -->
        <div v-if="expert.user" class="info-section">
          <h3 class="section-title">用户信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">用户昵称：</span>
              <span class="value">{{ expert.user.nickname }}</span>
            </div>
            <div class="info-item">
              <span class="label">真实姓名：</span>
              <span class="value">{{ expert.user.realName || "-" }}</span>
            </div>
            <div class="info-item">
              <span class="label">手机号：</span>
              <span class="value">{{ expert.user.phone || "-" }}</span>
            </div>
            <div class="info-item">
              <span class="label">用户状态：</span>
              <el-tag :type="expert.user.status === 1 ? 'success' : 'danger'">
                {{ expert.user.status === 1 ? "正常" : "禁用" }}
              </el-tag>
            </div>
          </div>
        </div>

        <!-- 统计信息 -->
        <div class="info-section">
          <h3 class="section-title">统计信息</h3>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-number">{{ expert.orderCount || 0 }}</div>
              <div class="stat-label">接单数量</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ expert.completeCount || 0 }}</div>
              <div class="stat-label">完成数量</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">
                {{ expert.completeRate?.toFixed(1) || "0.0" }}%
              </div>
              <div class="stat-label">完成率</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">
                {{ expert.rating?.toFixed(1) || "0.0" }}
              </div>
              <div class="stat-label">平均评分</div>
            </div>
          </div>
        </div>

        <!-- 达人描述 -->
        <div v-if="expert.description" class="info-section">
          <h3 class="section-title">达人描述</h3>
          <div class="description">
            {{ expert.description }}
          </div>
        </div>

        <!-- 审核信息 -->
        <div v-if="expert.auditRemark" class="info-section">
          <h3 class="section-title">审核信息</h3>
          <div class="audit-info">
            <div class="audit-remark">
              <span class="label">审核备注：</span>
              <span class="value">{{ expert.auditRemark }}</span>
            </div>
          </div>
        </div>

        <!-- 时间信息 -->
        <div class="info-section">
          <h3 class="section-title">时间信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">创建时间：</span>
              <span class="value">{{ formatDateTime(expert.createTime) }}</span>
            </div>
            <div class="info-item">
              <span class="label">更新时间：</span>
              <span class="value">{{ formatDateTime(expert.updateTime) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handleEdit">编辑</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import dayjs from "dayjs";
import {
  getExpertDetail,
  getStatusText,
  getAuditStatusText,
  getStatusTagType,
  getAuditStatusTagType,
  type Expert,
} from "@/api/experts";
import Avatar from "@/components/Avatar.vue";

// 定义组件属性
interface ExpertDetailDialogProps {
  visible: boolean;
  expertId?: number;
}

// 定义事件
interface ExpertDetailDialogEmits {
  (e: "update:visible", value: boolean): void;
  (e: "edit", expert: Expert): void;
}

// 属性和事件
const props = defineProps<ExpertDetailDialogProps>();
const emit = defineEmits<ExpertDetailDialogEmits>();

// 状态
const loading = ref(false);
const expert = ref<Expert | null>(null);

// 计算属性
const visible = computed({
  get: () => props.visible,
  set: (value: boolean) => {
    emit("update:visible", value);
  },
});

// 格式化日期时间
const formatDateTime = (dateTime: string | undefined): string => {
  if (!dateTime) return "-";
  return dayjs(dateTime).format("YYYY-MM-DD HH:mm:ss");
};

// 加载达人详情
const loadExpertDetail = async () => {
  if (!props.expertId) return;

  try {
    loading.value = true;
    expert.value = await getExpertDetail(props.expertId);
  } catch (error) {
    console.error("加载达人详情失败:", error);
    expert.value = null;
  } finally {
    loading.value = false;
  }
};

// 关闭弹窗
const handleClose = () => {
  visible.value = false;
  expert.value = null;
};

// 编辑达人
const handleEdit = () => {
  if (expert.value) {
    emit("edit", expert.value);
  }
};

// 监听弹窗显示状态
watch(
  () => props.visible,
  (newVisible) => {
    if (newVisible && props.expertId) {
      loadExpertDetail();
    }
  }
);
</script>

<style scoped>
.expert-detail {
  max-height: 600px;
  overflow-y: auto;
}

.info-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
}

.label {
  font-weight: 500;
  color: #606266;
  min-width: 80px;
  margin-right: 8px;
}

.value {
  color: #303133;
}

.text-gray {
  color: #c0c4cc;
}

.rating-display {
  display: flex;
  align-items: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.description {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  color: #606266;
  line-height: 1.6;
}

.audit-info {
  padding: 16px;
  background: #fff7e6;
  border-radius: 8px;
  border-left: 4px solid #e6a23c;
}

.audit-remark {
  display: flex;
  align-items: flex-start;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
