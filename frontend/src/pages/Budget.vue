<template>
  <div class="budget-container">
    <el-card class="header-card">
      <h2>💰 旅行预算管理</h2>
      <p>记录每笔支出，查看消费趋势，AI帮你合理规划预算</p>
    </el-card>

    <!-- 新增支出 -->
    <el-card class="form-card">
      <el-form :model="expenseForm" inline>
        <el-form-item label="关联行程">
          <el-select v-model="expenseForm.plan" placeholder="选择行程">
            <el-option label="日本东京" value="日本东京" />
            <el-option label="云南大理" value="云南大理" />
          </el-select>
        </el-form-item>

        <el-form-item label="类别">
          <el-select v-model="expenseForm.category" placeholder="类别">
            <el-option label="餐饮" value="餐饮" />
            <el-option label="住宿" value="住宿" />
            <el-option label="交通" value="交通" />
            <el-option label="购物" value="购物" />
            <el-option label="娱乐" value="娱乐" />
          </el-select>
        </el-form-item>

        <el-form-item label="金额">
          <el-input-number v-model="expenseForm.amount" :min="0" />
        </el-form-item>

        <el-button type="primary" @click="addExpense">添加</el-button>
        <el-button @click="startVoiceInput">🎤 语音记账</el-button>
      </el-form>
    </el-card>

    <!-- 支出列表 -->
    <el-card>
      <el-table :data="expenses" style="width: 100%">
        <el-table-column prop="plan" label="行程" width="120" />
        <el-table-column prop="category" label="类别" width="120" />
        <el-table-column prop="amount" label="金额 (¥)" width="120" />
        <el-table-column prop="date" label="日期" width="180" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" type="danger" @click="deleteExpense(scope.$index)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 图表分析 -->
    <el-card class="chart-card">
      <div ref="chartRef" style="height:400px;"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

const expenseForm = ref({
  plan: '',
  category: '',
  amount: 0
})

const expenses = ref([
  { plan: '日本东京', category: '餐饮', amount: 300, date: '2025-10-10' },
  { plan: '日本东京', category: '住宿', amount: 800, date: '2025-10-11' },
  { plan: '云南大理', category: '交通', amount: 200, date: '2025-09-22' }
])

// 添加支出
const addExpense = () => {
  if (!expenseForm.value.plan || !expenseForm.value.category) {
    ElMessage.warning('请填写完整信息')
    return
  }
  expenses.value.push({
    ...expenseForm.value,
    date: new Date().toISOString().split('T')[0]
  })
  updateChart()
  ElMessage.success('添加成功')
}

// 删除支出
const deleteExpense = (index) => {
  expenses.value.splice(index, 1)
  updateChart()
  ElMessage.success('删除成功')
}

// 🎤 语音记账功能（示例）
const startVoiceInput = () => {
  if (!('webkitSpeechRecognition' in window)) {
    ElMessage.error('当前浏览器不支持语音识别')
    return
  }
  const recognition = new webkitSpeechRecognition()
  recognition.lang = 'zh-CN'
  recognition.start()
  recognition.onresult = (event) => {
    const text = event.results[0][0].transcript
    ElMessage.success('识别成功：' + text)
    // 简单识别语音中金额
    const match = text.match(/(\d+)\s*元/)
    if (match) {
      expenseForm.value.amount = parseInt(match[1])
    }
  }
}

// 图表
const chartRef = ref(null)
let chart = null

const updateChart = () => {
  const summary = {}
  expenses.value.forEach((e) => {
    summary[e.category] = (summary[e.category] || 0) + e.amount
  })
  const data = Object.entries(summary).map(([k, v]) => ({ name: k, value: v }))
  chart.setOption({
    tooltip: {},
    series: [{ type: 'pie', data }]
  })
}

onMounted(() => {
  chart = echarts.init(chartRef.value)
  updateChart()
})
</script>

<style scoped>
.budget-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}
.form-card {
  margin-bottom: 20px;
}
.chart-card {
  margin-top: 20px;
}
</style>
