import { createRouter, createWebHistory } from 'vue-router'
import ConnectionTest from "@/components/ConnectionTest.vue";

const routes = [
  {
    path: '/test-connection',
    name: 'ConnectionTest',
    component: ConnectionTest
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes, // ← This was missing!
})

export default router
