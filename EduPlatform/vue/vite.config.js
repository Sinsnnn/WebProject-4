import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import ElementPlus from 'unplugin-element-plus/vite'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // 按需定制主题配置
    ElementPlus({
      useSource: true,
    }),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
  // 👇 添加以下 server 配置段落（包含代理）
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:9090',   // 后端地址
        changeOrigin: true,                // 是否改变请求源
        rewrite: (path) => path.replace(/^\/api/, '')  // 去掉 /api 前缀
      }
    },
    port: 5173                             // 确保是当前使用的端口
  },
  // 👆 上面这部分是新增的

  // 预加载项目必需的组件
  optimizeDeps: {
    include: [
      "vue",
      "vue-router",
      "axios",
      "path-browserify",
      "element-plus/es/components/base/style/css",
      // ...其他省略
    ],
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `
          @use "@/assets/css/index.scss" as *;
        `,
      }
    }
  },
})