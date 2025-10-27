<template>
  <div class="register-page">
    <el-card class="register-card">
      <h2>用户注册</h2>

      <el-input v-model="username" placeholder="用户名" class="mb-2" />
      <el-input v-model="password" placeholder="密码" show-password class="mb-2" />
      <el-input v-model="confirmPassword" placeholder="确认密码" show-password class="mb-2" />

      <el-button type="success" @click="register" class="mb-2">注册</el-button>
      <el-button type="info" @click="goLogin">返回登录</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const password = ref('')
const confirmPassword = ref('')

const register = () => {
  if (!username.value || !password.value || !confirmPassword.value) {
    alert('请填写所有字段')
    return
  }
  if (password.value !== confirmPassword.value) {
    alert('两次密码不一致')
    return
  }
  alert(`注册成功！欢迎你，${username.value}`)
  router.push('/login')
}

const goLogin = () => router.push('/login')
</script>

<style scoped>
.register-page {
  display: flex;
  height: 100vh;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #bdb2ff, #ffc6ff);
}
.register-card {
  width: 300px;
  text-align: center;
}
.mb-2 { margin-bottom: 10px; }
</style>
