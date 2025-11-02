<template>
  <div class="user-container">
    <el-card class="header-card">
      <div class="header-content">
        <div class="title-wrapper">
          <el-icon :size="32" color="#409EFF"><User /></el-icon>
          <h2>用户中心</h2>
        </div>
        <p class="subtitle">管理你的个人信息与AI推荐偏好</p>
      </div>
    </el-card>

    <el-card class="info-card">
      <div class="avatar-section">
        <el-avatar :size="100" :src="user.avatar || defaultAvatar">
          <el-icon :size="50"><UserFilled /></el-icon>
        </el-avatar>
        <div class="user-info">
          <h3>{{ user.username || '未登录用户' }}</h3>
          <div class="status-badge">
            <el-icon><CircleCheck /></el-icon>
            <span>已登录</span>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="settings-card">
      <el-form :model="user">
        <el-form-item label="AI推荐偏好">
          <template #label>
            <span class="label-text">
              <el-icon><Star /></el-icon>
              AI推荐偏好
            </span>
          </template>
          <el-select v-model="user.preferences" multiple placeholder="选择偏好" style="width: 100%">
            <el-option label="美食" value="美食" />
            <el-option label="文化" value="文化" />
            <el-option label="自然" value="自然" />
            <el-option label="购物" value="购物" />
            <el-option label="亲子" value="亲子" />
          </el-select>
        </el-form-item>

        <el-form-item label="旅行风格">
          <template #label>
            <span class="label-text">
              <el-icon><Guide /></el-icon>
              旅行风格
            </span>
          </template>
          <el-radio-group v-model="user.travelStyle">
            <el-radio label="轻松休闲" />
            <el-radio label="深度探索" />
            <el-radio label="亲子舒适" />
          </el-radio-group>
        </el-form-item>

        <div class="action-buttons">
          <el-button type="primary" @click="saveSettings" :icon="Check" size="large">保存设置</el-button>
          <el-button type="danger" @click="logout" :icon="SwitchButton" size="large">退出登录</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  User, 
  UserFilled, 
  CircleCheck, 
  Star, 
  Guide, 
  Check, 
  SwitchButton 
} from '@element-plus/icons-vue'
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
  max-width: 700px;
  margin: 0 auto;
  padding: 20px;
}

.header-card {
  margin-bottom: 24px;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.header-content {
  text-align: center;
  color: #fff;
}

.title-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 12px;
}

.title-wrapper h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #fff;
}

.subtitle {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
}

.info-card {
  margin: 20px 0;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 20px;
}

.user-info {
  flex: 1;
}

.user-info h3 {
  margin: 0 0 12px 0;
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #d1fae5;
  color: #059669;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.settings-card {
  text-align: left;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.label-text {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
  color: #374151;
}

.action-buttons {
  margin-top: 24px;
  display: flex;
  gap: 12px;
}

:deep(.settings-card .el-form-item) {
  margin-bottom: 24px;
}

:deep(.settings-card .el-card__body) {
  padding: 24px;
}
</style>
