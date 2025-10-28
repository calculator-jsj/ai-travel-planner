<template>
  <div class="user-container">
    <el-card class="header-card">
      <h2>👤 用户中心</h2>
      <p>管理你的个人信息与AI推荐偏好</p>
    </el-card>

    <el-card class="info-card">
      <el-avatar :size="80" :src="user.avatar || defaultAvatar" />
      <h3>{{ user.username || '未登录用户' }}</h3>
      <p>当前状态：<strong>已登录</strong></p>
    </el-card>

    <el-card class="settings-card">
      <el-form :model="user">
        <el-form-item label="AI推荐偏好">
          <el-select v-model="user.preferences" multiple placeholder="选择偏好">
            <el-option label="美食" value="美食" />
            <el-option label="文化" value="文化" />
            <el-option label="自然" value="自然" />
            <el-option label="购物" value="购物" />
            <el-option label="亲子" value="亲子" />
          </el-select>
        </el-form-item>

        <el-form-item label="旅行风格">
          <el-radio-group v-model="user.travelStyle">
            <el-radio label="轻松休闲" />
            <el-radio label="深度探索" />
            <el-radio label="亲子舒适" />
          </el-radio-group>
        </el-form-item>

        <el-button type="primary" @click="saveSettings">保存设置</el-button>
        <el-button type="danger" @click="logout">退出登录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserProfile } from '@/api/user'
import { saveUserPreferences } from '@/api/user'

const defaultAvatar = 'https://avatars.githubusercontent.com/u/583231?v=4'

const user = ref({
  username: '',
  avatar: '',
  preferences: [],
  travelStyle: ''
})

// 页面加载时动态获取用户偏好和旅行风格
onMounted(async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) {
    ElMessage.warning('未获取到用户ID，请先登录')
    return
  }
  try {
    const res = await getUserProfile()
    console.log(res.data)
    if (res.data.code === 1) {
      user.value.preferences = res.data.data.preferences || []
      user.value.travelStyle = res.data.data.travelStyle || ''
      // 可选：如果后端返回了用户名/头像，也可以绑定
      user.value.username = res.data.data.username || JSON.parse(localStorage.getItem('user')).username
      user.value.avatar = res.data.data.avatar || ''
    } else {
      ElMessage.error(res.data.msg || '获取用户信息失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('服务器连接失败')
  }
})

const saveSettings = async () => {
  try {
    await saveUserPreferences(user.value.preferences, user.value.travelStyle)
    ElMessage.success('用户设置已保存')
  } catch (err) {
    console.error(err)
    ElMessage.error('保存失败，请稍后重试')
  }
}

// 退出登录
const logout = () => {
  localStorage.removeItem('userId')
  localStorage.removeItem('user')
  ElMessage.success('已退出登录')
  window.location.href = '/login'
}
</script>

<style scoped>
.user-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  text-align: center;
}
.info-card {
  margin: 20px 0;
}
.settings-card {
  text-align: left;
}
</style>
