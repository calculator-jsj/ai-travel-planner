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
        <div>欢迎，用户</div>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()
const active = ref('create')

const handleSelect = (key) => {
  active.value = key
  router.push(`/home/${key}`)
}
</script>
