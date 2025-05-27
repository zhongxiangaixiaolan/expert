import App from './App'

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
  ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
import pinia from './store'

export function createApp() {
  const app = createSSRApp(App)

  // 全局错误处理
  app.config.errorHandler = (err, vm, info) => {
    console.error('Vue应用错误:', err, info)
  }

  // 使用Pinia状态管理
  app.use(pinia)

  return {
    app
  }
}
// #endif