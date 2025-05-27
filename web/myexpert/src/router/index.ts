import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '管理员登录' }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '数据统计', icon: 'DataAnalysis' }
      },
      {
        path: '/system',
        name: 'System',
        component: () => import('@/views/system/index.vue'),
        meta: { title: '系统配置', icon: 'Setting' }
      },
      {
        path: '/users',
        name: 'Users',
        component: () => import('@/views/users/index.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: '/experts',
        name: 'Experts',
        component: () => import('@/views/experts/index.vue'),
        meta: { title: '达人管理', icon: 'Avatar' }
      },
      {
        path: '/categories',
        name: 'Categories',
        component: () => import('@/views/categories/index.vue'),
        meta: { title: '分类管理', icon: 'Menu' }
      },
      {
        path: '/orders',
        name: 'Orders',
        component: () => import('@/views/orders/index.vue'),
        meta: { title: '订单管理', icon: 'Document' }
      },
      {
        path: '/reviews',
        name: 'Reviews',
        component: () => import('@/views/reviews/index.vue'),
        meta: { title: '评价管理', icon: 'Star' }
      },
      {
        path: '/banners',
        name: 'Banners',
        component: () => import('@/views/banners/index.vue'),
        meta: { title: '轮播图管理', icon: 'Picture' }
      },
      {
        path: '/announcements',
        name: 'Announcements',
        component: () => import('@/views/announcements/index.vue'),
        meta: { title: '通告管理', icon: 'Bell' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta?.title) {
    document.title = `${to.meta.title} - 达人接单管理系统`
  } else {
    document.title = '达人接单管理系统'
  }

  const token = localStorage.getItem('admin_token')

  if (to.path === '/login') {
    if (token) {
      next('/')
    } else {
      next()
    }
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

// 路由加载完成后的处理
router.afterEach((to, from) => {
  // 确保页面滚动到顶部
  window.scrollTo(0, 0)
})

export default router
