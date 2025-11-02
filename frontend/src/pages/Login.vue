<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <div class="logo-section">
          <div class="logo-icon">
            <el-icon :size="48" color="#409EFF"><MapLocation /></el-icon>
          </div>
          <h1 class="title">AI 旅行规划师</h1>
          <p class="subtitle">让 AI 帮你规划完美的旅行</p>
        </div>

        <div class="form-section">
          <el-form :model="form" label-position="top">
            <el-form-item>
              <template #label>
                <span class="label-text">
                  <el-icon><User /></el-icon> 用户名
                </span>
              </template>
              <el-input 
                v-model="username" 
                placeholder="请输入用户名" 
                size="large"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>

            <el-form-item>
              <template #label>
                <span class="label-text">
                  <el-icon><Lock /></el-icon> 密码
                </span>
              </template>
              <el-input 
                v-model="password" 
                placeholder="请输入密码" 
                type="password"
                show-password 
                size="large"
                :prefix-icon="Lock"
                @keyup.enter="login"
              />
            </el-form-item>

            <el-button 
              type="primary" 
              @click="login" 
              class="login-btn"
              size="large"
              :loading="loading"
            >
              <el-icon class="mr-1"><Right /></el-icon>
              登录
            </el-button>
          </el-form>

          <div class="divider">
            <span>还没有账号？</span>
          </div>

          <el-button 
            type="success" 
            @click="goRegister" 
            class="register-btn"
            size="large"
            plain
          >
            <el-icon class="mr-1"><UserFilled /></el-icon>
            立即注册
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock, Right, UserFilled, MapLocation } from '@element-plus/icons-vue'
import { loginApi } from '@/api/user'

const router = useRouter()
const username = ref('')
const password = ref('')
const loading = ref(false)

const login = async () => {
  if (!username.value || !password.value) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    const res = await loginApi({
      username: username.value,
      password: password.value
    })

    if (res.data.code === 1) {
      ElMessage.success('登录成功')
      localStorage.setItem('user', JSON.stringify(res.data.data))
      localStorage.setItem('userId', res.data.data.id)
      router.push('/home/create')
    } else {
      ElMessage.error(res.data.msg || '登录失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('服务器连接失败')
  } finally {
    loading.value = false
  }
}

const goRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-page {
  display: flex;
  height: 100vh;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.login-page::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: move 20s linear infinite;
}

@keyframes move {
  0% { transform: translate(0, 0); }
  100% { transform: translate(50px, 50px); }
}

.login-container {
  position: relative;
  z-index: 1;
}

.login-card {
  width: 420px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  padding: 48px 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.5s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.logo-section {
  text-align: center;
  margin-bottom: 40px;
}

.logo-icon {
  margin-bottom: 16px;
  display: inline-flex;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.4);
}

.title {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.form-section {
  margin-top: 32px;
}

.label-text {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
  color: #374151;
}

:deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.login-btn {
  width: 100%;
  margin-top: 8px;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.register-btn {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
}

.divider {
  text-align: center;
  margin: 24px 0;
  color: #9ca3af;
  font-size: 14px;
}

.mr-1 {
  margin-right: 6px;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.el-form-item__label) {
  padding-bottom: 8px;
}
</style>
