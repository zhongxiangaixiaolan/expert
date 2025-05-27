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
    force: true,
    include: ['element-plus/es', 'element-plus/es/components/base/style/css']
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
