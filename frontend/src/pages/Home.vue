<template>
  <el-container class="home-container">
    <el-aside width="240px" class="sidebar">
      <div class="sidebar-header">
        <div class="logo-wrapper">
          <el-icon :size="32" color="#409EFF"><MapLocation /></el-icon>
          <span class="logo-text">AI 旅行规划师</span>
        </div>
      </div>
      
      <el-menu 
        :default-active="active" 
        @select="handleSelect"
        class="sidebar-menu"
        :collapse="false"
      >
        <el-menu-item index="create">
          <el-icon><DocumentAdd /></el-icon>
          <span>创建行程</span>
        </el-menu-item>
        <el-menu-item index="list">
          <el-icon><FolderOpened /></el-icon>
          <span>行程管理</span>
        </el-menu-item>
        <el-menu-item index="budget">
          <el-icon><Money /></el-icon>
          <span>预算管理</span>
        </el-menu-item>
        <el-menu-item index="user">
          <el-icon><User /></el-icon>
          <span>用户中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <h2 class="page-title">
            <el-icon><LocationInformation /></el-icon>
            <span>AI 旅行规划师</span>
          </h2>
        </div>
        <div class="header-right">
          <div class="user-info">
            <el-icon class="user-icon"><Avatar /></el-icon>
            <span class="username">欢迎您，{{ username || '游客' }}</span>
          </div>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
  MapLocation, 
  DocumentAdd, 
  FolderOpened, 
  Money, 
  User, 
  LocationInformation,
  Avatar
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const active = ref('create')
const username = ref('')

// 菜单选择
const handleSelect = (key) => {
  active.value = key
  router.push(`/home/${key}`)
}

// 根据路由设置活动菜单项
onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    const user = JSON.parse(userStr)
    username.value = user.username
  }
  
  // 根据当前路由设置active
  const path = route.path
  if (path.includes('/create')) active.value = 'create'
  else if (path.includes('/list')) active.value = 'list'
  else if (path.includes('/budget')) active.value = 'budget'
  else if (path.includes('/user')) active.value = 'user'
})
</script>

<style scoped>
.home-container {
  height: 100vh;
  background: #f5f7fa;
}

.sidebar {
  background: linear-gradient(180deg, #1f2937 0%, #111827 100%);
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.sidebar-header {
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #fff;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.sidebar-menu {
  border: none;
  background: transparent;
  padding: 16px 0;
}

:deep(.sidebar-menu .el-menu-item) {
  color: rgba(255, 255, 255, 0.7);
  margin: 4px 12px;
  border-radius: 12px;
  height: 48px;
  line-height: 48px;
  transition: all 0.3s;
}

:deep(.sidebar-menu .el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

:deep(.sidebar-menu .el-menu-item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

:deep(.sidebar-menu .el-menu-item .el-icon) {
  font-size: 20px;
  margin-right: 12px;
}

.main-container {
  display: flex;
  flex-direction: column;
}

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 32px;
  height: 64px;
  border-bottom: 1px solid #e5e7eb;
}

.header-left {
  display: flex;
  align-items: center;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #f9fafb;
  border-radius: 20px;
  transition: all 0.3s;
}

.user-info:hover {
  background: #f3f4f6;
}

.user-icon {
  font-size: 20px;
  color: #667eea;
}

.username {
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}

.main-content {
  padding: 24px;
  background: #f5f7fa;
  overflow-y: auto;
}

:deep(.main-content::-webkit-scrollbar) {
  width: 8px;
}

:deep(.main-content::-webkit-scrollbar-track) {
  background: #f1f1f1;
  border-radius: 4px;
}

:deep(.main-content::-webkit-scrollbar-thumb) {
  background: #c1c1c1;
  border-radius: 4px;
}

:deep(.main-content::-webkit-scrollbar-thumb:hover) {
  background: #a8a8a8;
}
</style>
