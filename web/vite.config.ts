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
    host: 'localhost',
    port: 3000,
    fs: {
      strict: false
    },
    proxy: {
      // 代理 /api 请求到后端服务器
      // 前端请求 /api/xxx -> 后端 http://localhost:9090/api/xxx
      // 注意：后端配置了 context-path: /api，所以不需要重写路径
      '/api': {
        target: 'http://localhost:9090',
        changeOrigin: true,
        // 不需要重写路径，直接转发到后端的 /api 路径
        configure: (proxy) => {
          proxy.on('proxyReq', (_, req) => {
            console.log('代理请求:', req.method, req.url, '-> http://localhost:9090' + req.url);
          });
          proxy.on('proxyRes', (proxyRes, req) => {
            console.log('代理响应:', req.url, '状态码:', proxyRes.statusCode);
          });
        }
      }
    }
  }
})
