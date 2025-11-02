<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-card">
        <div class="logo-section">
          <div class="logo-icon">
            <el-icon :size="48" color="#67C23A"><UserFilled /></el-icon>
          </div>
          <h1 class="title">创建账号</h1>
          <p class="subtitle">加入我们，开启智能旅行之旅</p>
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
                  <el-icon><Message /></el-icon> 邮箱
                </span>
              </template>
              <el-input 
                v-model="email" 
                placeholder="请输入邮箱地址" 
                size="large"
                :prefix-icon="Message"
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
              />
            </el-form-item>

            <el-form-item>
              <template #label>
                <span class="label-text">
                  <el-icon><Lock /></el-icon> 确认密码
                </span>
              </template>
              <el-input 
                v-model="confirmPassword" 
                placeholder="请再次输入密码" 
                type="password"
                show-password 
                size="large"
                :prefix-icon="Lock"
                @keyup.enter="register"
              />
            </el-form-item>

            <el-button 
              type="success" 
              @click="register" 
              class="register-btn"
              size="large"
              :loading="loading"
            >
              <el-icon class="mr-1"><Check /></el-icon>
              立即注册
            </el-button>
          </el-form>

          <div class="divider">
            <span>已有账号？</span>
          </div>

          <el-button 
            @click="goLogin" 
            class="login-btn"
            size="large"
            plain
          >
            <el-icon class="mr-1"><ArrowLeft /></el-icon>
            返回登录
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, UserFilled, Check, ArrowLeft } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const username = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)

const register = async () => {
  if (!username.value || !email.value || !password.value || !confirmPassword.value) {
    ElMessage.warning('请填写所有字段')
    return
  }
  if (password.value !== confirmPassword.value) {
    ElMessage.error('两次密码不一致')
    return
  }

  loading.value = true
  try {
    const res = await axios.post('http://localhost:8080/api/user/register', {
      username: username.value,
      email: email.value,
      password: password.value
    })

    if (res.data.code === 1) {
      ElMessage.success('注册成功！请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.data.msg || '注册失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('服务器连接失败')
  } finally {
    loading.value = false
  }
}

const goLogin = () => router.push('/login')
</script>

<style scoped>
.register-page {
  display: flex;
  height: 100vh;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  position: relative;
  overflow: hidden;
}

.register-page::before {
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

.register-container {
  position: relative;
  z-index: 1;
}

.register-card {
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
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border-radius: 16px;
  box-shadow: 0 8px 16px rgba(245, 87, 108, 0.4);
}

.title {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px 0;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
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

.register-btn {
  width: 100%;
  margin-top: 8px;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border: none;
  transition: all 0.3s;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(245, 87, 108, 0.4);
}

.login-btn {
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
