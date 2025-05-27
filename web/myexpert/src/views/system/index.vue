<template>
  <div class="system-config">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统配置管理</span>
          <div class="header-actions">
            <el-button
              type="primary"
              @click="refreshCache"
              :loading="refreshing"
            >
              <el-icon><Refresh /></el-icon>
              刷新缓存
            </el-button>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <!-- 微信配置 -->
        <el-tab-pane label="微信配置" name="wechat">
          <el-form
            ref="wechatFormRef"
            :model="wechatConfig"
            label-width="120px"
            class="config-form"
          >
            <el-form-item label="小程序AppID">
              <el-input
                v-model="wechatConfig.wx_appid"
                placeholder="请输入微信小程序AppID"
                clearable
              />
            </el-form-item>

            <el-form-item label="小程序AppSecret">
              <el-input
                v-model="wechatConfig.wx_appsecret"
                type="password"
                placeholder="请输入微信小程序AppSecret"
                show-password
                clearable
              />
            </el-form-item>

            <el-form-item label="商户号">
              <el-input
                v-model="wechatConfig.wx_mch_id"
                placeholder="请输入微信支付商户号"
                clearable
              />
            </el-form-item>

            <el-form-item label="支付密钥">
              <el-input
                v-model="wechatConfig.wx_api_key"
                type="password"
                placeholder="请输入微信支付API密钥"
                show-password
                clearable
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                @click="saveConfig('wechat')"
                :loading="saving"
              >
                保存配置
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 存储配置 -->
        <el-tab-pane label="存储配置" name="storage">
          <el-form
            ref="storageFormRef"
            :model="storageConfig"
            label-width="120px"
            class="config-form"
          >
            <el-form-item label="存储类型">
              <el-radio-group v-model="storageConfig.storage_type">
                <el-radio value="LOCAL">本地存储</el-radio>
                <el-radio value="OSS">阿里云OSS</el-radio>
              </el-radio-group>
            </el-form-item>

            <template v-if="storageConfig.storage_type === 'OSS'">
              <el-form-item label="OSS端点">
                <el-input
                  v-model="storageConfig.oss_endpoint"
                  placeholder="请输入OSS端点地址"
                  clearable
                />
              </el-form-item>

              <el-form-item label="AccessKey">
                <el-input
                  v-model="storageConfig.oss_access_key"
                  placeholder="请输入OSS AccessKey"
                  clearable
                />
              </el-form-item>

              <el-form-item label="SecretKey">
                <el-input
                  v-model="storageConfig.oss_secret_key"
                  type="password"
                  placeholder="请输入OSS SecretKey"
                  show-password
                  clearable
                />
              </el-form-item>

              <el-form-item label="存储桶">
                <el-input
                  v-model="storageConfig.oss_bucket"
                  placeholder="请输入OSS存储桶名称"
                  clearable
                />
              </el-form-item>
            </template>

            <el-form-item>
              <el-button
                type="primary"
                @click="saveConfig('storage')"
                :loading="saving"
              >
                保存配置
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 系统配置 -->
        <el-tab-pane label="系统配置" name="system">
          <el-form
            ref="systemFormRef"
            :model="systemConfig"
            label-width="120px"
            class="config-form"
          >
            <el-form-item label="系统名称">
              <el-input
                v-model="systemConfig.system_name"
                placeholder="请输入系统名称"
                clearable
              />
            </el-form-item>

            <el-form-item label="系统Logo">
              <el-input
                v-model="systemConfig.system_logo"
                placeholder="请输入系统Logo地址"
                clearable
              />
            </el-form-item>

            <el-form-item label="客服电话">
              <el-input
                v-model="systemConfig.contact_phone"
                placeholder="请输入客服电话"
                clearable
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                @click="saveConfig('system')"
                :loading="saving"
              >
                保存配置
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 业务配置 -->
        <el-tab-pane label="业务配置" name="business">
          <el-form
            ref="businessFormRef"
            :model="businessConfig"
            label-width="120px"
            class="config-form"
          >
            <el-form-item label="平台抽成比例">
              <el-input-number
                v-model="businessConfig.platform_commission_rate"
                :min="0"
                :max="100"
                :precision="2"
                controls-position="right"
              />
              <span class="input-suffix">%</span>
            </el-form-item>

            <el-form-item label="最小提现金额">
              <el-input-number
                v-model="businessConfig.min_withdraw_amount"
                :min="0"
                :precision="2"
                controls-position="right"
              />
              <span class="input-suffix">元</span>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                @click="saveConfig('business')"
                :loading="saving"
              >
                保存配置
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import {
  getAllConfigGroups,
  getConfigsByGroup,
  batchUpdateConfigs,
  refreshCache as refreshCacheApi,
} from "@/api/system";
import type { ConfigUpdateParams } from "@/api/system";

// 当前激活的标签页
const activeTab = ref("wechat");

// 加载和保存状态
const loading = ref(false);
const saving = ref(false);
const refreshing = ref(false);

// 各配置分组数据
const wechatConfig = reactive({
  wx_appid: "",
  wx_appsecret: "",
  wx_mch_id: "",
  wx_api_key: "",
});

const storageConfig = reactive({
  storage_type: "LOCAL",
  oss_endpoint: "",
  oss_access_key: "",
  oss_secret_key: "",
  oss_bucket: "",
});

const systemConfig = reactive({
  system_name: "",
  system_logo: "",
  contact_phone: "",
});

const businessConfig = reactive({
  platform_commission_rate: 0,
  min_withdraw_amount: 0,
});

// 处理标签页切换
const handleTabChange = (tabName: string) => {
  loadConfigByGroup(tabName);
};

// 根据分组加载配置
const loadConfigByGroup = async (group: string) => {
  try {
    loading.value = true;
    const configs = await getConfigsByGroup(group);

    // 根据分组更新对应的配置对象
    switch (group) {
      case "wechat":
        Object.assign(wechatConfig, configs || {});
        break;
      case "storage":
        Object.assign(storageConfig, configs || {});
        break;
      case "system":
        Object.assign(systemConfig, configs || {});
        break;
      case "business":
        const businessData = configs || {};
        Object.assign(businessConfig, {
          platform_commission_rate:
            parseFloat(businessData.platform_commission_rate) || 0,
          min_withdraw_amount:
            parseFloat(businessData.min_withdraw_amount) || 0,
        });
        break;
    }
  } catch (error) {
    console.error("加载配置失败:", error);
    ElMessage.error(`加载${group}配置失败`);
  } finally {
    loading.value = false;
  }
};

// 保存配置
const saveConfig = async (group: string) => {
  try {
    saving.value = true;

    let configData: Record<string, any> = {};

    switch (group) {
      case "wechat":
        configData = wechatConfig;
        break;
      case "storage":
        configData = storageConfig;
        break;
      case "system":
        configData = systemConfig;
        break;
      case "business":
        configData = businessConfig;
        break;
    }

    // 构建更新参数
    const updateParams: ConfigUpdateParams[] = Object.entries(configData).map(
      ([key, value]) => ({
        configKey: key,
        configValue: String(value),
      })
    );

    await batchUpdateConfigs(updateParams);
    ElMessage.success("配置保存成功");
  } catch (error) {
    console.error("保存配置失败:", error);
  } finally {
    saving.value = false;
  }
};

// 刷新缓存
const refreshCache = async () => {
  try {
    refreshing.value = true;
    await refreshCacheApi();
    ElMessage.success("缓存刷新成功");
  } catch (error) {
    console.error("刷新缓存失败:", error);
  } finally {
    refreshing.value = false;
  }
};

// 加载所有配置
const loadAllConfigs = async () => {
  try {
    loading.value = true;
    const allConfigs = await getAllConfigGroups();

    // 安全地分配配置，处理可能为空的情况
    Object.assign(wechatConfig, allConfigs.wechat || {});
    Object.assign(storageConfig, allConfigs.storage || {});
    Object.assign(systemConfig, allConfigs.system || {});

    // 处理业务配置，提供默认值
    const businessData = allConfigs.business || {};
    Object.assign(businessConfig, {
      platform_commission_rate:
        parseFloat(businessData.platform_commission_rate) || 0,
      min_withdraw_amount: parseFloat(businessData.min_withdraw_amount) || 0,
    });
  } catch (error) {
    console.error("加载配置失败:", error);
    ElMessage.error("加载配置失败，请检查网络连接");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadAllConfigs();
});
</script>

<style scoped>
.system-config {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.config-form {
  max-width: 600px;
  margin-top: 20px;
}

.input-suffix {
  margin-left: 8px;
  color: #909399;
  font-size: 14px;
}

:deep(.el-tabs__content) {
  padding-top: 0;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input-number) {
  width: 200px;
}
</style>
