<template>
  <div class="photo-manager">
    <!-- 照片预览区域 -->
    <div class="preview-section" v-if="photos.length > 0">
      <h3 class="section-title">照片预览</h3>
      <PhotoCarousel3D
        :photos="photos"
        :auto-play="false"
        width="100%"
        height="400px"
        ref="carouselRef"
      />
    </div>

    <!-- 照片管理区域 -->
    <div class="management-section">
      <div class="section-header">
        <h3 class="section-title">照片管理</h3>
        <el-button
          type="primary"
          :icon="Plus"
          @click="showUploadDialog = true"
          :disabled="uploading"
        >
          添加照片
        </el-button>
      </div>

      <!-- 照片列表 -->
      <div class="photo-list" v-if="photos.length > 0">
        <draggable
          v-model="photos"
          @end="handleSortChange"
          item-key="id"
          class="photo-grid"
        >
          <template #item="{ element: photo, index }">
            <div class="photo-item">
              <div class="photo-preview">
                <img
                  :src="getPhotoUrl(photo)"
                  :alt="photo.photoTitle || `照片 ${index + 1}`"
                  @error="handleImageError"
                />
                <div class="photo-overlay">
                  <el-button
                    type="primary"
                    :icon="View"
                    circle
                    size="small"
                    @click="previewPhoto(index)"
                  />
                  <el-button
                    type="warning"
                    :icon="Edit"
                    circle
                    size="small"
                    @click="editPhoto(photo)"
                  />
                  <el-button
                    type="danger"
                    :icon="Delete"
                    circle
                    size="small"
                    @click="deletePhoto(photo)"
                  />
                </div>
              </div>
              <div class="photo-info">
                <div class="photo-title">
                  {{ photo.photoTitle || `照片 ${index + 1}` }}
                </div>
                <div class="photo-description" v-if="photo.photoDescription">
                  {{ photo.photoDescription }}
                </div>
                <div class="photo-meta">
                  <span class="sort-order"
                    >排序: {{ photo.sortOrder || index + 1 }}</span
                  >
                  <span class="file-size" v-if="photo.fileSize">
                    {{ formatFileSize(photo.fileSize) }}
                  </span>
                </div>
              </div>
            </div>
          </template>
        </draggable>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-else>
        <el-empty description="暂无照片">
          <el-button
            type="primary"
            @click="showUploadDialog = true"
            :disabled="uploading"
          >
            添加第一张照片
          </el-button>
        </el-empty>
      </div>
    </div>

    <!-- 上传对话框 -->
    <el-dialog
      v-model="showUploadDialog"
      title="上传照片"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="uploadForm" label-width="80px">
        <el-form-item label="选择照片" required>
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :show-file-list="true"
            :limit="1"
            accept="image/*"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
          >
            <el-button type="primary" :icon="Upload">选择文件</el-button>
            <template #tip>
              <div class="upload-tip">
                支持 JPG、PNG、GIF 格式，建议尺寸 3:4，文件大小不超过 10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="照片标题">
          <el-input
            v-model="uploadForm.photoTitle"
            placeholder="请输入照片标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="照片描述">
          <el-input
            v-model="uploadForm.photoDescription"
            type="textarea"
            placeholder="请输入照片描述"
            maxlength="500"
            show-word-limit
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showUploadDialog = false" :disabled="uploading">
          取消
        </el-button>
        <el-button
          type="primary"
          @click="handleUpload"
          :loading="uploading"
          :disabled="!uploadForm.file"
        >
          上传
        </el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑照片信息"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="照片标题">
          <el-input
            v-model="editForm.photoTitle"
            placeholder="请输入照片标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="照片描述">
          <el-input
            v-model="editForm.photoDescription"
            type="textarea"
            placeholder="请输入照片描述"
            maxlength="500"
            show-word-limit
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false" :disabled="updating">
          取消
        </el-button>
        <el-button type="primary" @click="handleUpdate" :loading="updating">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Upload, View, Edit, Delete } from "@element-plus/icons-vue";
import draggable from "vuedraggable";
import PhotoCarousel3D from "./PhotoCarousel3D.vue";

// 照片接口定义
interface Photo {
  id: number;
  expertId: number;
  photoName: string;
  photoTitle?: string;
  photoDescription?: string;
  sortOrder: number;
  fileSize?: number;
  width?: number;
  height?: number;
  createTime?: string;
  updateTime?: string;
}

// Props
interface Props {
  expertId: number;
  photos: Photo[];
}

const props = defineProps<Props>();

// Emits
const emit = defineEmits<{
  "photos-updated": [photos: Photo[]];
  "upload-photo": [
    data: { file: File; photoTitle?: string; photoDescription?: string }
  ];
  "update-photo": [
    data: { photoId: number; photoTitle?: string; photoDescription?: string }
  ];
  "delete-photo": [photoId: number];
  "sort-photos": [photoIds: number[]];
}>();

// 状态
const carouselRef = ref();
const uploadRef = ref();
const showUploadDialog = ref(false);
const showEditDialog = ref(false);
const uploading = ref(false);
const updating = ref(false);

// 表单数据
const uploadForm = ref({
  file: null as File | null,
  photoTitle: "",
  photoDescription: "",
});

const editForm = ref({
  photoId: 0,
  photoTitle: "",
  photoDescription: "",
});

// 计算属性
const photos = computed({
  get: () => props.photos,
  set: (value) => emit("photos-updated", value),
});

// 获取照片URL
const getPhotoUrl = (photo: Photo): string => {
  if (!photo.photoName) return "";
  return `/api/photos/${photo.photoName}`;
};

// 格式化文件大小
const formatFileSize = (size: number): string => {
  if (size < 1024) return `${size} B`;
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`;
  return `${(size / (1024 * 1024)).toFixed(1)} MB`;
};

// 处理图片加载错误
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement;
  // 使用一个简单的SVG占位符
  img.src =
    "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjE1MCIgdmlld0JveD0iMCAwIDIwMCAxNTAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIyMDAiIGhlaWdodD0iMTUwIiBmaWxsPSIjRjVGNUY1Ii8+CjxwYXRoIGQ9Ik04MCA2MEg4MFYxMDBIMTIwVjYwSDgwWiIgZmlsbD0iI0NDQyIvPgo8Y2lyY2xlIGN4PSIxMDAiIGN5PSI4MCIgcj0iMTAiIGZpbGw9IiNDQ0MiLz4KPHR5cGUgeD0iNzAiIHk9IjEyMCIgZmlsbD0iIzk5OSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjEyIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIj7ml6Dms5XliqDovb3lm77niYc8L3R5cGU+Cjwvc3ZnPgo=";
};

// 文件选择处理
const handleFileChange = (file: any) => {
  uploadForm.value.file = file.raw;
};

const handleFileRemove = () => {
  uploadForm.value.file = null;
};

// 上传照片
const handleUpload = async () => {
  if (!uploadForm.value.file) {
    ElMessage.warning("请选择要上传的照片");
    return;
  }

  try {
    uploading.value = true;
    emit("upload-photo", {
      file: uploadForm.value.file,
      photoTitle: uploadForm.value.photoTitle,
      photoDescription: uploadForm.value.photoDescription,
    });

    // 重置表单
    uploadForm.value = {
      file: null,
      photoTitle: "",
      photoDescription: "",
    };
    uploadRef.value?.clearFiles();
    showUploadDialog.value = false;
  } catch (error) {
    console.error("上传照片失败:", error);
  } finally {
    uploading.value = false;
  }
};

// 编辑照片
const editPhoto = (photo: Photo) => {
  editForm.value = {
    photoId: photo.id,
    photoTitle: photo.photoTitle || "",
    photoDescription: photo.photoDescription || "",
  };
  showEditDialog.value = true;
};

// 更新照片信息
const handleUpdate = async () => {
  try {
    updating.value = true;
    emit("update-photo", {
      photoId: editForm.value.photoId,
      photoTitle: editForm.value.photoTitle,
      photoDescription: editForm.value.photoDescription,
    });
    showEditDialog.value = false;
  } catch (error) {
    console.error("更新照片信息失败:", error);
  } finally {
    updating.value = false;
  }
};

// 删除照片
const deletePhoto = async (photo: Photo) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除照片"${photo.photoTitle || "未命名"}"吗？`,
      "确认删除",
      {
        type: "warning",
        confirmButtonText: "确定",
        cancelButtonText: "取消",
      }
    );

    emit("delete-photo", photo.id);
  } catch (error) {
    // 用户取消删除
  }
};

// 预览照片
const previewPhoto = (index: number) => {
  carouselRef.value?.setActiveIndex(index);
  // 滚动到预览区域
  document.querySelector(".preview-section")?.scrollIntoView({
    behavior: "smooth",
  });
};

// 排序变化处理
const handleSortChange = () => {
  const photoIds = photos.value.map((photo) => photo.id);
  emit("sort-photos", photoIds);
};
</script>

<style scoped>
.photo-manager {
  padding: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #303133;
}

.preview-section {
  margin-bottom: 32px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.management-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
}

.photo-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: move;
}

.photo-item:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.photo-preview {
  position: relative;
  width: 100%;
  height: 120px;
  overflow: hidden;
  aspect-ratio: 3/4; /* 设置3:4比例 */
}

.photo-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.photo-item:hover .photo-preview img {
  transform: scale(1.05);
}

.photo-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.photo-item:hover .photo-overlay {
  opacity: 1;
}

.photo-info {
  padding: 8px;
}

.photo-title {
  font-weight: 600;
  margin-bottom: 4px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.photo-description {
  font-size: 12px;
  color: #606266;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.photo-meta {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #909399;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .photo-grid {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 8px;
  }

  .photo-preview {
    height: 100px;
  }

  .section-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
}
</style>
