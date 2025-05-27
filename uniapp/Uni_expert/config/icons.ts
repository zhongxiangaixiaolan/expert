/**
 * 图标配置文件
 * 支持动态配置分类图标和颜色
 */

// 图标库映射配置
export const ICON_LIBRARY = {
  // Material Design Icons
  mdi: 'mdi',
  // Heroicons
  heroicons: 'heroicons',
  // Feather Icons
  feather: 'feather',
  // Font Awesome
  fa: 'fa',
  // Tabler Icons
  tabler: 'tabler'
}

// 默认分类图标配置
export const DEFAULT_CATEGORY_ICONS = {
  // 设计类
  design: {
    icon: 'mdi:palette',
    color: '#ff6b35',
    fallback: '🎨'
  },
  // 编程类
  programming: {
    icon: 'mdi:code-tags',
    color: '#007aff',
    fallback: '💻'
  },
  // 写作类
  writing: {
    icon: 'mdi:pencil',
    color: '#4cd964',
    fallback: '✍️'
  },
  // 营销类
  marketing: {
    icon: 'mdi:bullhorn',
    color: '#f0ad4e',
    fallback: '📢'
  },
  // 咨询类
  consulting: {
    icon: 'mdi:lightbulb-on',
    color: '#5bc0de',
    fallback: '💡'
  },
  // 教育类
  education: {
    icon: 'mdi:school',
    color: '#9c27b0',
    fallback: '🎓'
  },
  // 生活服务
  lifestyle: {
    icon: 'mdi:home-heart',
    color: '#e91e63',
    fallback: '🏠'
  },
  // 商务服务
  business: {
    icon: 'mdi:briefcase',
    color: '#795548',
    fallback: '💼'
  },
  // 技术服务
  technology: {
    icon: 'mdi:cog',
    color: '#607d8b',
    fallback: '⚙️'
  },
  // 创意服务
  creative: {
    icon: 'mdi:creation',
    color: '#ff5722',
    fallback: '🎭'
  }
}

// 功能图标配置
export const FUNCTION_ICONS = {
  // 搜索
  search: {
    icon: 'mdi:magnify',
    color: '#007aff'
  },
  // 筛选
  filter: {
    icon: 'mdi:filter-variant',
    color: '#007aff'
  },
  // 收藏
  favorite: {
    icon: 'mdi:heart-outline',
    activeIcon: 'mdi:heart',
    color: '#999999',
    activeColor: '#ff6b35'
  },
  // 消息
  message: {
    icon: 'mdi:message-outline',
    color: '#007aff'
  },
  // 电话
  phone: {
    icon: 'mdi:phone',
    color: '#4cd964'
  },
  // 位置
  location: {
    icon: 'mdi:map-marker',
    color: '#666666'
  },
  // 相机
  camera: {
    icon: 'mdi:camera',
    color: '#007aff'
  },
  // 上传
  upload: {
    icon: 'mdi:upload',
    color: '#007aff'
  },
  // 编辑
  edit: {
    icon: 'mdi:pencil',
    color: '#666666'
  },
  // 删除
  delete: {
    icon: 'mdi:delete',
    color: '#dd524d'
  },
  // 添加
  add: {
    icon: 'mdi:plus',
    color: '#007aff'
  },
  // 关闭
  close: {
    icon: 'mdi:close',
    color: '#666666'
  },
  // 箭头
  arrowRight: {
    icon: 'mdi:chevron-right',
    color: '#999999'
  },
  arrowLeft: {
    icon: 'mdi:chevron-left',
    color: '#999999'
  },
  // 确认
  check: {
    icon: 'mdi:check',
    color: '#4cd964'
  },
  // 警告
  warning: {
    icon: 'mdi:alert',
    color: '#f0ad4e'
  },
  // 信息
  info: {
    icon: 'mdi:information',
    color: '#5bc0de'
  }
}

// 状态图标配置
export const STATUS_ICONS = {
  empty: {
    icon: 'mdi:inbox',
    color: '#cccccc'
  },
  error: {
    icon: 'mdi:alert-circle',
    color: '#dd524d'
  },
  loading: {
    icon: 'mdi:loading',
    color: '#007aff'
  },
  success: {
    icon: 'mdi:check-circle',
    color: '#4cd964'
  }
}

// 根据分类名称获取图标配置
export const getCategoryIcon = (categoryName: string, customIcon?: string, customColor?: string) => {
  // 优先使用自定义配置
  if (customIcon) {
    return {
      icon: customIcon,
      color: customColor || '#007aff',
      fallback: DEFAULT_CATEGORY_ICONS[categoryName]?.fallback || '📋'
    }
  }
  
  // 使用默认配置
  const defaultConfig = DEFAULT_CATEGORY_ICONS[categoryName]
  if (defaultConfig) {
    return defaultConfig
  }
  
  // 兜底配置
  return {
    icon: 'mdi:folder',
    color: '#666666',
    fallback: '📁'
  }
}

// 根据功能名称获取图标配置
export const getFunctionIcon = (functionName: string) => {
  return FUNCTION_ICONS[functionName] || {
    icon: 'mdi:help-circle',
    color: '#666666'
  }
}

// 根据状态获取图标配置
export const getStatusIcon = (status: string) => {
  return STATUS_ICONS[status] || {
    icon: 'mdi:help-circle',
    color: '#666666'
  }
}
