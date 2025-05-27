<template>
	<view class="dynamic-icon" :style="iconStyle">
		<!-- 优先显示图标库图标 -->
		<text 
			v-if="iconConfig.icon && !iconError" 
			class="icon-font"
			:class="iconClass"
			:style="{ color: currentColor, fontSize: size + 'rpx' }"
		>
			{{ getIconContent(iconConfig.icon) }}
		</text>
		
		<!-- 兜底显示Emoji -->
		<text 
			v-else-if="iconConfig.fallback"
			class="icon-emoji"
			:style="{ fontSize: size + 'rpx' }"
		>
			{{ iconConfig.fallback }}
		</text>
		
		<!-- 最终兜底显示默认图标 -->
		<text 
			v-else
			class="icon-default"
			:style="{ color: currentColor, fontSize: size + 'rpx' }"
		>
			📋
		</text>
	</view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getCategoryIcon, getFunctionIcon, getStatusIcon } from '@/config/icons'

// Props定义
interface Props {
  // 图标类型：category | function | status | custom
  type: 'category' | 'function' | 'status' | 'custom'
  // 图标名称或自定义图标代码
  name: string
  // 自定义颜色
  color?: string
  // 图标大小（rpx）
  size?: number
  // 自定义图标（优先级最高）
  customIcon?: string
  // 是否激活状态（用于收藏等）
  active?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'function',
  name: '',
  color: '',
  size: 48,
  customIcon: '',
  active: false
})

// 状态
const iconError = ref(false)

// 计算属性
const iconConfig = computed(() => {
  switch (props.type) {
    case 'category':
      return getCategoryIcon(props.name, props.customIcon, props.color)
    case 'function':
      return getFunctionIcon(props.name)
    case 'status':
      return getStatusIcon(props.name)
    case 'custom':
      return {
        icon: props.customIcon || props.name,
        color: props.color || '#666666',
        fallback: '📋'
      }
    default:
      return {
        icon: 'mdi:help-circle',
        color: '#666666',
        fallback: '❓'
      }
  }
})

const currentColor = computed(() => {
  // 如果是收藏类图标且处于激活状态
  if (props.name === 'favorite' && props.active) {
    const functionIcon = getFunctionIcon(props.name)
    return functionIcon.activeColor || functionIcon.color
  }
  
  return props.color || iconConfig.value.color
})

const iconClass = computed(() => {
  const baseClass = 'icon-base'
  const iconName = iconConfig.value.icon
  
  if (iconName.startsWith('mdi:')) {
    return `${baseClass} mdi mdi-${iconName.replace('mdi:', '')}`
  } else if (iconName.startsWith('fa:')) {
    return `${baseClass} fa fa-${iconName.replace('fa:', '')}`
  } else if (iconName.startsWith('feather:')) {
    return `${baseClass} feather feather-${iconName.replace('feather:', '')}`
  }
  
  return baseClass
})

const iconStyle = computed(() => {
  return {
    width: props.size + 'rpx',
    height: props.size + 'rpx'
  }
})

// 方法
const getIconContent = (iconName: string) => {
  // 这里可以实现图标内容的获取逻辑
  // 由于UniApp的限制，可能需要使用字体图标或者预定义的映射
  
  // 简化版本：使用Emoji映射
  const iconMap: Record<string, string> = {
    'mdi:palette': '🎨',
    'mdi:code-tags': '💻',
    'mdi:pencil': '✏️',
    'mdi:bullhorn': '📢',
    'mdi:lightbulb-on': '💡',
    'mdi:school': '🎓',
    'mdi:magnify': '🔍',
    'mdi:filter-variant': '⚙️',
    'mdi:heart-outline': '🤍',
    'mdi:heart': '❤️',
    'mdi:message-outline': '💬',
    'mdi:phone': '📞',
    'mdi:map-marker': '📍',
    'mdi:camera': '📷',
    'mdi:upload': '⬆️',
    'mdi:delete': '🗑️',
    'mdi:plus': '➕',
    'mdi:close': '✕',
    'mdi:chevron-right': '›',
    'mdi:chevron-left': '‹',
    'mdi:check': '✓',
    'mdi:alert': '⚠️',
    'mdi:information': 'ℹ️',
    'mdi:inbox': '📥',
    'mdi:alert-circle': '❌',
    'mdi:loading': '⏳',
    'mdi:check-circle': '✅'
  }
  
  return iconMap[iconName] || '📋'
}

// 生命周期
onMounted(() => {
  // 可以在这里预加载图标字体
})
</script>

<style lang="scss" scoped>
.dynamic-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  
  .icon-font,
  .icon-emoji,
  .icon-default {
    display: flex;
    align-items: center;
    justify-content: center;
    line-height: 1;
  }
  
  .icon-font {
    font-family: 'Material Design Icons', sans-serif;
  }
  
  .icon-emoji {
    font-family: 'Apple Color Emoji', 'Segoe UI Emoji', sans-serif;
  }
}

// 图标字体样式
.mdi {
  font-family: 'Material Design Icons';
  font-weight: normal;
  font-style: normal;
  font-size: inherit;
  line-height: 1;
  letter-spacing: normal;
  text-transform: none;
  display: inline-block;
  white-space: nowrap;
  word-wrap: normal;
  direction: ltr;
}
</style>
