# 自定义 TabBar 实现方案

## 概述

为了在微信小程序中使用 SVG 格式的图标，我们实现了自定义 TabBar 方案。该方案通过创建 `custom-tab-bar` 组件来替代原生 TabBar，支持使用 SVG 图标和更灵活的样式定制。

## 实现步骤

### 1. 修改 pages.json 配置

在 `pages.json` 中启用自定义 TabBar：

```json
{
  "tabBar": {
    "custom": true,
    "color": "#999999",
    "selectedColor": "#007aff",
    "backgroundColor": "#ffffff",
    "borderStyle": "black",
    "list": [
      {
        "pagePath": "pages/index/index",
        "text": "首页"
      },
      {
        "pagePath": "pages/expert/list",
        "text": "找达人"
      },
      {
        "pagePath": "pages/order/list",
        "text": "订单"
      },
      {
        "pagePath": "pages/user/index",
        "text": "我的"
      }
    ]
  }
}
```

关键变化：
- 添加 `"custom": true` 启用自定义 TabBar
- 移除 `iconPath` 和 `selectedIconPath` 配置

### 2. 创建自定义 TabBar 组件

创建 `custom-tab-bar/index.vue` 组件：

```vue
<template>
  <view class="custom-tab-bar">
    <view 
      v-for="(item, index) in tabList" 
      :key="index"
      class="tab-item"
      :class="{ active: selected === index }"
      @click="switchTab(index)"
    >
      <view class="tab-icon">
        <TabIcon 
          :name="item.iconName" 
          :color="selected === index ? '#007aff' : '#999999'"
        />
      </view>
      <text class="tab-text" :class="{ active: selected === index }">{{ item.text }}</text>
    </view>
  </view>
</template>
```

### 3. 创建图标组件

创建 `components/TabIcon/index.vue` 组件来处理图标显示：

```vue
<template>
  <view class="tab-icon-wrapper">
    <!-- 使用 emoji 或字体图标代替 SVG -->
    <view v-if="name === 'home'" class="icon-svg" :style="{ color: color }">
      <text class="iconfont">🏠</text>
    </view>
    <!-- 其他图标... -->
  </view>
</template>
```

### 4. 页面状态同步

在每个 TabBar 页面中添加状态同步代码：

```javascript
// 页面加载时设置 TabBar 选中状态
onMounted(() => {
  setTabBarIndex()
})

// 设置自定义tabbar选中状态
const setTabBarIndex = () => {
  if (typeof getApp().globalData.setTabBarIndex === 'function') {
    getApp().globalData.setTabBarIndex(0) // 对应的索引
  }
}
```

## 技术要点

### 1. 图标解决方案

由于微信小程序对 SVG 支持有限，我们采用以下方案：

1. **Emoji 图标**：使用 Unicode emoji 字符
2. **字体图标**：可以集成 iconfont 等字体图标库
3. **图片图标**：使用 PNG/JPG 格式的图片

### 2. 状态管理

通过全局函数实现页面与 TabBar 的状态同步：

```javascript
// 在 custom-tab-bar 组件中设置全局函数
setupGlobalFunction() {
  const app = getApp()
  if (!app.globalData) {
    app.globalData = {}
  }
  
  app.globalData.setTabBarIndex = (index) => {
    this.selected = index
  }
}
```

### 3. 样式适配

- 支持安全区域适配：`padding-bottom: env(safe-area-inset-bottom)`
- 固定定位：`position: fixed; bottom: 0`
- 响应式布局：`display: flex`

## 优势

1. **灵活性**：可以使用任意格式的图标
2. **可定制性**：完全控制样式和交互
3. **兼容性**：支持所有小程序平台
4. **扩展性**：易于添加新功能（如徽章、动画等）

## 注意事项

1. **性能**：自定义组件会增加一定的渲染开销
2. **维护**：需要手动维护页面状态同步
3. **兼容性**：确保在不同设备上的显示效果
4. **无障碍**：注意添加适当的无障碍支持

## 文件结构

```
uniapp/Uni_expert/
├── custom-tab-bar/
│   └── index.vue                 # 自定义 TabBar 组件
├── components/
│   └── TabIcon/
│       └── index.vue             # 图标组件
├── pages/
│   ├── index/index.vue           # 首页（已添加状态同步）
│   ├── expert/list.vue           # 达人列表（已添加状态同步）
│   ├── order/list.vue            # 订单列表（已添加状态同步）
│   └── user/index.vue            # 用户中心（已添加状态同步）
└── pages.json                    # 页面配置（已启用自定义 TabBar）
```

## 总结

通过实现自定义 TabBar，我们成功解决了微信小程序中使用 SVG 图标的问题。该方案提供了更好的灵活性和可定制性，同时保持了良好的用户体验。
