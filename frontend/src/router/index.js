import { createRouter, createWebHistory } from 'vue-router'
import Login from '../pages/Login.vue'
import Register from '../pages/Register.vue'
import Home from '../pages/Home.vue'
import CreatePlan from '../pages/CreatePlan.vue'
import PlanList from '../pages/PlanList.vue'
import Budget from '../pages/Budget.vue'
import User from '../pages/User.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  {
    path: '/home',
    component: Home,
    children: [
      { path: 'create', component: CreatePlan },
      { path: 'list', component: PlanList },
      { path: 'budget', component: Budget },
      { path: 'user', component: User },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
