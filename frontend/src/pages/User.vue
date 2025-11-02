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
      <el-tabs v-model="activeTab">
        <!-- 个人偏好设置 -->
        <el-tab-pane label="偏好设置" name="preference">
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
            </div>
          </el-form>
        </el-tab-pane>

        <!-- API Key 配置 -->
        <el-tab-pane label="API配置" name="api">
          <el-alert
            title="API Key 说明"
            type="info"
            :closable="false"
            style="margin-bottom: 20px;"
          >
            <template #default>
              <div style="line-height: 1.8;">
                <p style="margin: 0 0 8px 0;">为确保数据安全，API Key 仅存储在浏览器本地，不会上传到服务器。</p>
                <p style="margin: 0;"><strong>注意：</strong>配置的 API Key 仅用于本地开发环境，生产环境建议通过环境变量配置。</p>
              </div>
            </template>
          </el-alert>

          <el-form :model="apiKeys" label-width="180px">
            <el-form-item label="阿里云API Key">
              <template #label>
                <span class="label-text">
                  <el-icon><Key /></el-icon>
                  阿里云API Key
                </span>
              </template>
              <el-input
                v-model="apiKeys.aliyunApiKey"
                type="password"
                show-password
                placeholder="请输入阿里云百炼平台 API Key"
                style="width: 100%"
              >
                <template #append>
                  <el-button @click="saveApiKey('aliyun')" :icon="Check">保存</el-button>
                </template>
              </el-input>
              <div class="form-tip">用于 AI 行程规划和预算分析（阿里云百炼平台）</div>
            </el-form-item>

            <el-form-item label="高德地图 Key">
              <template #label>
                <span class="label-text">
                  <el-icon><LocationInformation /></el-icon>
                  高德地图 Key
                </span>
              </template>
              <el-input
                v-model="apiKeys.gaodeMapKey"
                type="password"
                show-password
                placeholder="请输入高德地图 API Key"
                style="width: 100%"
              >
                <template #append>
                  <el-button @click="saveApiKey('gaode')" :icon="Check">保存</el-button>
                </template>
              </el-input>
              <div class="form-tip">用于地图展示和地理位置服务（高德地图开放平台）</div>
            </el-form-item>

            <el-form-item>
              <el-button type="warning" @click="clearApiKeys" :icon="Delete">清除所有 Key</el-button>
              <el-button type="info" @click="loadApiKeys" :icon="Refresh">刷新显示</el-button>
            </el-form-item>
          </el-form>

          <el-divider />

          <div class="api-help">
            <h4>如何获取 API Key：</h4>
            <ul>
              <li><strong>阿里云百炼平台：</strong>访问 <a href="https://bailian.console.aliyun.com/" target="_blank">阿里云百炼控制台</a>，创建 API Key</li>
              <li><strong>高德地图：</strong>访问 <a href="https://console.amap.com/" target="_blank">高德开放平台</a>，创建应用并获取 Key</li>
            </ul>
          </div>
        </el-tab-pane>
      </el-tabs>

      <el-divider />

      <div class="action-buttons">
        <el-button type="danger" @click="logout" :icon="SwitchButton" size="large">退出登录</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  User, 
  UserFilled, 
  CircleCheck, 
  Star, 
  Guide, 
  Check, 
  SwitchButton,
  Key,
  LocationInformation,
  Delete,
  Refresh
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

const activeTab = ref('preference')

const apiKeys = ref({
  aliyunApiKey: '',
  gaodeMapKey: ''
})

// 页面加载时动态获取用户偏好和旅行风格
onMounted(async () => {
  // 加载 API Keys
  loadApiKeys()
  
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

// API Key 管理
const saveApiKey = (type) => {
  const key = type === 'aliyun' ? apiKeys.value.aliyunApiKey : apiKeys.value.gaodeMapKey
  const keyName = type === 'aliyun' ? 'aliyun_api_key' : 'gaode_map_key'
  
  if (!key || key.trim() === '') {
    ElMessage.warning('请输入 API Key')
    return
  }
  
  localStorage.setItem(keyName, key)
  ElMessage.success(`${type === 'aliyun' ? '阿里云' : '高德地图'} API Key 已保存到本地`)
}

const loadApiKeys = () => {
  apiKeys.value.aliyunApiKey = localStorage.getItem('aliyun_api_key') || ''
  apiKeys.value.gaodeMapKey = localStorage.getItem('gaode_map_key') || ''
  ElMessage.success('已刷新 API Key 显示')
}

const clearApiKeys = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清除所有本地存储的 API Key 吗？此操作不可恢复。',
      '确认清除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    localStorage.removeItem('aliyun_api_key')
    localStorage.removeItem('gaode_map_key')
    apiKeys.value.aliyunApiKey = ''
    apiKeys.value.gaodeMapKey = ''
    ElMessage.success('已清除所有 API Key')
  } catch {
    // 用户取消
  }
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

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
  line-height: 1.5;
}

.api-help {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 8px;
  margin-top: 20px;
}

.api-help h4 {
  margin: 0 0 12px 0;
  color: #1f2937;
  font-size: 16px;
}

.api-help ul {
  margin: 0;
  padding-left: 20px;
  color: #6b7280;
  line-height: 1.8;
}

.api-help li {
  margin-bottom: 8px;
}

.api-help a {
  color: #409eff;
  text-decoration: none;
}

.api-help a:hover {
  text-decoration: underline;
}

.user-container {
  max-width: 900px;
}

@media (max-width: 768px) {
  .user-container {
    padding: 10px;
  }
  
  .avatar-section {
    flex-direction: column;
    text-align: center;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .action-buttons .el-button {
    width: 100%;
  }
}
</style>
