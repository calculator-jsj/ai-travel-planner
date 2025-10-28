<template>
  <div class="register-page">
    <el-card class="register-card">
      <h2>用户注册</h2>

      <el-input v-model="username" placeholder="用户名" class="mb-2" />
      <el-input v-model="email" placeholder="邮箱" class="mb-2" />
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
import axios from 'axios'

const router = useRouter()
const username = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')

const register = async () => {
  if (!username.value || !email.value || !password.value || !confirmPassword.value) {
    alert('请填写所有字段')
    return
  }
  if (password.value !== confirmPassword.value) {
    alert('两次密码不一致')
    return
  }

  try {
    const res = await axios.post('http://localhost:8080/api/user/register', {
      username: username.value,
      email: email.value,
      password: password.value
    })

    if (res.data.code === 1) {
      alert('注册成功！请登录')
      router.push('/login')
    } else {
      alert(res.data.msg || '注册失败')
    }
  } catch (error) {
    console.error(error)
    alert('服务器连接失败')
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
  background: linear-gradient(135deg, #bdb2ff, #ffc6ff);
}
.register-card {
  width: 320px;
  text-align: center;
}
.mb-2 { margin-bottom: 10px; }
</style>
