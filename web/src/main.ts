import type { UserModule } from './types'
import { ViteSSG } from 'vite-ssg'
import { createApp } from 'vue'

import { routes } from 'vue-router/auto-routes'

import LoginApp from '~/LoginApp.vue'
import App from './App.vue'

import '~/styles/index.scss'
import 'uno.css'
import 'element-plus/theme-chalk/src/message.scss'
import 'element-plus/theme-chalk/src/message-box.scss'
import 'element-plus/theme-chalk/src/overlay.scss'

export const app = ViteSSG(
  App,
  {
    routes,
    base: import.meta.env.BASE_URL,
  },
  (ctx) => {
    Object.values(import.meta.glob<{ install: UserModule }>('./modules/*.ts', { eager: true }))
      .forEach(i => i.install?.(ctx))
  },
)

export const loginAPP = createApp(LoginApp).mount('#loginApp')
