import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      imports: ['vue', 'vue-router', 'pinia'],
      resolvers: [ElementPlusResolver()],
      dts: true,
    }),
    Components({
      resolvers: [ElementPlusResolver()],
      dts: true,
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  optimizeDeps: {
    include: [
      'element-plus/es',
      'element-plus/es/components/base/style/css',
      'element-plus/es/components/loading/style/css',
      'element-plus/es/components/dialog/style/css',
      'element-plus/es/components/descriptions/style/css',
      'element-plus/es/components/descriptions-item/style/css',
      'element-plus/es/components/form/style/css',
      'element-plus/es/components/select/style/css',
      'element-plus/es/components/option/style/css',
      'element-plus/es/components/form-item/style/css',
      'element-plus/es/components/input/style/css',
      'element-plus/es/components/pagination/style/css',
      'element-plus/es/components/upload/style/css',
      'element-plus/es/components/button/style/css',
      'element-plus/es/components/icon/style/css',
      'element-plus/es/components/image/style/css',
      'element-plus/es/components/table/style/css',
      'element-plus/es/components/table-column/style/css',
      'element-plus/es/components/radio/style/css',
      'element-plus/es/components/radio-group/style/css',
      'element-plus/es/components/date-picker/style/css',
      'element-plus/es/components/input-number/style/css',
      'element-plus/es/components/card/style/css',
      'element-plus/es/components/row/style/css',
      'element-plus/es/components/col/style/css'
    ]
  },
  server: {
    port: 3030,
    fs: {
      strict: false
    },
    proxy: {
      '/api': {
        target: process.env.VITE_API_BASE_URL || 'http://localhost:8080',
        changeOrigin: true,
        // 不需要重写路径，因为后端已经配置了context-path: /api
        configure: (proxy) => {
          const targetUrl = process.env.VITE_API_BASE_URL || 'http://localhost:8080'
          proxy.on('proxyReq', (_, req) => {
            console.log('代理请求:', req.method, req.url, '->', targetUrl + req.url);
          });
          proxy.on('proxyRes', (proxyRes, req) => {
            console.log('代理响应:', req.url, '状态码:', proxyRes.statusCode);
          });
        }
      }
    }
  }
})
