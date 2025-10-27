<template>
  <div class="user-container">
    <el-card class="header-card">
      <h2>👤 用户中心</h2>
      <p>管理你的个人信息与AI推荐偏好</p>
    </el-card>

    <el-card class="info-card">
      <el-avatar :size="80" src="https://avatars.githubusercontent.com/u/583231?v=4" />
      <h3>{{ user.name }}</h3>
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

        <el-form-item label="默认预算">
          <el-input-number v-model="user.defaultBudget" :min="0" />
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
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const user = ref({
  name: '测试用户',
  preferences: ['美食', '文化'],
  defaultBudget: 8000,
  travelStyle: '轻松休闲'
})

const saveSettings = () => {
  ElMessage.success('用户设置已保存')
}

const logout = () => {
  ElMessage.info('已退出登录')
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
