<template>
  <div class="login-page">
    <el-card class="login-card">
      <h2>AI 旅行规划师</h2>

      <el-input v-model="username" placeholder="用户名" class="mb-2" />
      <el-input v-model="password" placeholder="密码" show-password class="mb-2" />

      <el-button type="primary" @click="login" class="mb-2">登录</el-button>
      <el-button type="success" @click="goRegister">注册</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref } from 'vue'

const router = useRouter()
const username = ref('')
const password = ref('')

const login = () => {
  if (username.value && password.value) {
    router.push('/home/create')
  } else {
    alert('请输入用户名和密码')
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
  background: linear-gradient(135deg, #a0c4ff, #bdb2ff);
}
.login-card {
  width: 300px;
  text-align: center;
}
.mb-2 { margin-bottom: 10px; }
</style>
