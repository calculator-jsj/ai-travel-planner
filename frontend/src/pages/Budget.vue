<template>
  <div class="budget-container">
    <!-- 顶部标题 -->
    <el-card class="header-card">
      <div class="header-content">
        <h2>💰 旅行预算管理</h2>
        <p>管理你的每笔支出，实时掌握行程预算动态</p>
      </div>
    </el-card>

    <!-- 汇总信息 -->
    <el-card v-if="currentPlan" class="summary-card">
      <div class="summary-info">
        <div>
          <span>当前行程：</span>
          <strong>{{ currentPlan.title }}</strong>
        </div>
        <div>
          <span>总预算：</span>
          <strong>¥{{ currentPlan.budget }}</strong>
        </div>
        <div>
          <span>已支出：</span>
          <strong :style="{ color: budgetPercent > 100 ? 'red' : '#333' }">
            ¥{{ totalExpense }}
          </strong>
        </div>
        <div>
          <span>预算使用：</span>
          <el-progress :percentage="budgetPercent" :status="budgetPercent > 90 ? 'exception' : 'success'"
            style="width: 200px" />
        </div>
      </div>
    </el-card>

    <!-- 支出录入 -->
    <el-card class="form-card">
      <el-form :model="expenseForm" inline class="expense-form">
        <el-form-item label="关联行程">
          <el-select v-model.number="expenseForm.planId" placeholder="选择行程" @change="onPlanChange" style="width: 180px">
            <el-option v-for="plan in plans" :key="plan.id" :label="plan.title" :value="plan.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="类别">
          <el-select v-model="expenseForm.category" placeholder="类别" style="width: 120px">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>

        <el-form-item label="金额">
          <el-input-number v-model="expenseForm.amount" :min="0" style="width: 150px" />
        </el-form-item>

        <el-form-item label="日期">
          <el-date-picker v-model="expenseForm.date" type="date" placeholder="选择日期" format="YYYY-MM-DD"
            value-format="YYYY-MM-DD" style="width: 140px" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="expenseForm.remark" placeholder="例如：晚餐、出租车..." style="width: 180px" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="addExpense">添加</el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button @click="startVoiceInput">🎤 语音记账</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 支出记录 -->
    <el-card class="table-card">
      <h3>📘 支出记录</h3>
      <el-table :data="expenses" style="width: 100%">
        <el-table-column prop="category" label="类别" width="120" />
        <el-table-column label="金额 (¥)" width="120">
          <template #default="scope">
            {{ Number(scope.row.amount).toLocaleString() }}
          </template>
        </el-table-column>

        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="date" label="日期" width="140" />
        <el-table-column label="操作" width="100">
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
      <div class="chart-header">
        <h3>📊 支出分析</h3>
        <el-select v-model="chartType" size="small" style="width: 120px">
          <el-option label="类别占比" value="pie" />
          <el-option label="每日趋势" value="line" />
        </el-select>
      </div>
      <div ref="chartRef" style="height: 400px"></div>
    </el-card>

    <!-- AI 预算分析预留区 -->
    <el-card class="ai-card">
      <div class="ai-analysis">
        <h3>🤖 AI 智能分析（开发中）</h3>
        <p>AI 将为你分析消费趋势，并给出优化建议。</p>
        <el-button type="primary" plain disabled>即将上线</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { getPlansByUserId } from '@/api/plan'
import { addBudget, getBudgetByPlanId, deleteBudget } from '@/api/budget'

// 用户ID
const userId = 1 // TODO: 后续从登录信息获取
const plans = ref([])
const currentPlan = ref(null)

const expenseForm = ref({
  planId: '',
  category: '',
  amount: 0,
  remark: '',
  date: ''
})

const expenses = ref([])

const categories = ['餐饮', '住宿', '交通', '购物', '娱乐', '门票', '其他']

const chartRef = ref(null)
let chart = null
const chartType = ref('pie')

// ========== 生命周期 ==========
onMounted(async () => {
  await loadPlans()
  if (plans.value.length > 0) {
    expenseForm.value.planId = plans.value[0].id
    currentPlan.value = plans.value[0]
    await loadExpenses(plans.value[0].id)
  }
  chart = echarts.init(chartRef.value)
  updateChart()
})

// ========== 方法 ==========
const loadPlans = async () => {
  try {
    const res = await getPlansByUserId(userId)
    if (res.data.code === 1) {
      plans.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('获取行程失败')
  }
}

// 添加支出（调用接口）
const addExpense = async () => {
  if (!expenseForm.value.planId || !expenseForm.value.category || !expenseForm.value.amount || !expenseForm.value.date) {
    ElMessage.warning('请填写完整信息')
    return
  }

  try {
    const payload = {
      planId: expenseForm.value.planId,
      category: expenseForm.value.category,
      amount: expenseForm.value.amount,
      remark: expenseForm.value.remark,
      expenseDate: expenseForm.value.date
    }

    const res = await addBudget(payload)
    if (res.data.code === 1) {
      ElMessage.success('添加成功')
      // 刷新当前行程支出列表
      await loadExpenses(expenseForm.value.planId)
      resetForm()
    } else {
      ElMessage.error(res.data.msg || '添加失败')
    }
  } catch (err) {
    console.error('❌ 添加失败', err)
    ElMessage.error('服务器异常，添加失败')
  }
}

// 加载行程支出
const loadExpenses = async (planId) => {
  try {
    const res = await getBudgetByPlanId(planId)
    if (res.data.code === 1) {
      expenses.value = res.data.data.map(e => ({
        ...e,
        date: e.expenseDate // 保证字段统一
      }))
      updateChart()
    } else {
      expenses.value = []
      updateChart()
      ElMessage.error(res.data.msg || '加载支出失败')
    }
  } catch (err) {
    console.error('❌ 加载支出失败', err)
    expenses.value = []
    updateChart()
    ElMessage.error('服务器异常，无法加载支出')
  }
}

// 删除支出
const deleteExpense = async (index) => {
  const item = expenses.value[index]
  if (!item.id) return
  try {
    const res = await deleteBudget(item.id)
    if (res.data.code === 1) {
      expenses.value.splice(index, 1)
      updateChart()
      ElMessage.success('删除成功')
    } else {
      ElMessage.error(res.data.msg || '删除失败')
    }
  } catch (err) {
    console.error('❌ 删除失败', err)
    ElMessage.error('服务器异常，删除失败')
  }
}

// 选择行程时加载支出
const onPlanChange = async (planId) => {
  currentPlan.value = plans.value.find(p => p.id === planId) || null
  await loadExpenses(planId)
}


// 重置表单
const resetForm = () => {
  expenseForm.value = {
    // planId: '',
    category: '',
    amount: 0,
    remark: '',
    date: ''
  }
  // currentPlan.value = null
}

// 语音输入
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
    const match = text.match(/(\d+)\s*元/)
    if (match) {
      expenseForm.value.amount = parseInt(match[1])
    }
  }
}

const updateChart = () => {
  if (!chart) return
  chart.clear() // 👈 清空旧配置
  if (chartType.value === 'pie') {
    const summary = {}
    expenses.value.forEach(e => {
      summary[e.category] = (summary[e.category] || 0) + e.amount
    })
    const data = Object.entries(summary).map(([name, value]) => ({ name, value }))
    chart.setOption({
      tooltip: { trigger: 'item' },
      series: [{ type: 'pie', radius: '60%', data }]
    })
  } else {
    // 按日期排序
    const daily = {}
    expenses.value.forEach(e => {
      daily[e.date] = (daily[e.date] || 0) + e.amount
    })
    const sortedDates = Object.keys(daily).sort((a, b) => new Date(a) - new Date(b))
    const data = sortedDates.map(date => daily[date])
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: sortedDates },
      yAxis: { type: 'value' },
      series: [{ type: 'line', data }]
    })
  }
}


watch(chartType, () => updateChart())
watch(expenses, () => updateChart(), { deep: true })

// ========== 计算属性 ==========
const totalExpense = computed(() =>
  expenses.value.reduce((sum, e) => sum + Number(e.amount || 0), 0)
)

const budgetPercent = computed(() => {
  if (!currentPlan.value?.budget) return 0
  return Math.min(((totalExpense.value / currentPlan.value.budget) * 100).toFixed(1), 100)
})
</script>

<style scoped>
.budget-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 20px;
}

.header-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #dff1ff, #f7faff);
}

.header-content h2 {
  margin: 0;
}

.summary-card {
  margin-bottom: 20px;
}

.summary-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-card {
  margin-bottom: 20px;
}

.expense-form .el-form-item {
  margin-right: 10px;
}

.table-card {
  margin-bottom: 20px;
}

.chart-card {
  margin-top: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.ai-card {
  margin-top: 20px;
  text-align: center;
  color: #888;
}
</style>
