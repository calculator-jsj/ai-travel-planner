<template>
  <el-container style="height:100vh">
    <el-aside width="200px" style="background:#f5f7fa">
      <el-menu :default-active="active" @select="handleSelect">
        <el-menu-item index="create">创建行程</el-menu-item>
        <el-menu-item index="list">行程管理</el-menu-item>
        <el-menu-item index="budget">预算管理</el-menu-item>
        <el-menu-item index="user">用户中心</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="display:flex;justify-content:space-between;align-items:center;">
        <h2>AI 旅行规划师</h2>
        <div>欢迎您，{{ username || '游客' }}</div>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const active = ref('create')
const username = ref('')

// 菜单选择
const handleSelect = (key) => {
  active.value = key
  router.push(`/home/${key}`)
}

// 组件加载时从 localStorage 读取登录信息
onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    const user = JSON.parse(userStr)
    username.value = user.username
  }
})
</script>
