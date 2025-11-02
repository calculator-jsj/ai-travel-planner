<template>
  <div class="budget-container">
    <!-- 顶部标题 -->
    <el-card class="header-card">
      <div class="header-content">
        <div class="title-wrapper">
          <el-icon :size="32" color="#409EFF"><Money /></el-icon>
          <h2>旅行预算管理</h2>
        </div>
        <p class="subtitle">管理你的每笔支出，实时掌握行程预算动态</p>
      </div>
    </el-card>

    <!-- 左右分栏布局 -->
    <el-row :gutter="20">
      <!-- 左侧：原有内容 -->
      <el-col :span="16">

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
          <el-button type="primary" @click="addExpense" :icon="Plus">添加</el-button>
          <el-button @click="resetForm" :icon="Refresh">重置</el-button>
          <el-button @click="startVoiceInput" :icon="Microphone">语音记账</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 支出记录 -->
    <el-card class="table-card">
      <div class="table-header">
        <el-icon :size="24" color="#409EFF"><List /></el-icon>
        <h3>支出记录</h3>
      </div>
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
        <div class="chart-title">
          <el-icon :size="24" color="#409EFF"><DataAnalysis /></el-icon>
          <h3>支出分析</h3>
        </div>
        <el-select v-model="chartType" size="small" style="width: 120px">
          <el-option label="类别占比" value="pie" />
          <el-option label="每日趋势" value="line" />
        </el-select>
      </div>
      <div ref="chartRef" style="height: 400px"></div>
    </el-card>

      </el-col>

      <!-- 右侧：AI智能分析 -->
      <el-col :span="8">
        <el-card class="ai-card">
          <div class="ai-header">
            <el-icon :size="24" color="#409EFF"><Connection /></el-icon>
            <h3>AI 智能分析</h3>
          </div>
          
          <div v-if="!aiAnalysis" class="ai-empty">
            <el-icon :size="48" color="#909399"><MagicStick /></el-icon>
            <p>点击开始分析，AI将为你分析消费趋势并给出优化建议</p>
            <el-button 
              type="primary" 
              :icon="MagicStick" 
              :loading="analyzing"
              :disabled="!currentPlan || expenses.length === 0"
              @click="startAnalysis"
            >
              开始分析
            </el-button>
            <p v-if="!currentPlan || expenses.length === 0" class="hint-text">
              请先选择行程并添加支出记录
            </p>
          </div>

          <div v-else class="ai-content">
            <div class="analysis-section">
              <div class="section-title">
                <el-icon><DataLine /></el-icon>
                <span>消费趋势</span>
              </div>
              <div class="section-content">{{ aiAnalysis.consumptionTrend }}</div>
            </div>

            <div class="analysis-section">
              <div class="section-title">
                <el-icon><InfoFilled /></el-icon>
                <span>优化建议</span>
              </div>
              <ul class="suggestions-list">
                <li v-for="(suggestion, index) in aiAnalysis.suggestions" :key="index">
                  {{ suggestion }}
                </li>
              </ul>
            </div>

            <div class="analysis-section">
              <div class="section-title">
                <el-icon><Document /></el-icon>
                <span>预算总结</span>
              </div>
              <div class="section-content">{{ aiAnalysis.budgetSummary }}</div>
            </div>

            <div v-if="aiAnalysis.riskWarning && aiAnalysis.riskWarning !== '暂无风险'" class="analysis-section risk-section">
              <div class="section-title">
                <el-icon><Warning /></el-icon>
                <span>风险提示</span>
              </div>
              <div class="section-content risk-content">{{ aiAnalysis.riskWarning }}</div>
            </div>

            <el-button 
              type="primary" 
              :icon="Refresh" 
              :loading="analyzing"
              @click="startAnalysis"
              style="width: 100%; margin-top: 16px;"
            >
              重新分析
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { 
  Money, 
  Plus, 
  Refresh, 
  Microphone, 
  List, 
  DataAnalysis, 
  Connection, 
  MagicStick,
  DataLine,
  InfoFilled,
  Document,
  Warning
} from '@element-plus/icons-vue'
import { getPlansByUserId } from '@/api/plan'
import { addBudget, getBudgetByPlanId, deleteBudget } from '@/api/budget'
import { analyzeBudget } from '@/api/ai'

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

// AI分析相关
const analyzing = ref(false)
const aiAnalysis = ref(null)

// ========== 生命周期 ==========
onMounted(async () => {
  await loadPlans()
  if (plans.value.length > 0) {
    expenseForm.value.planId = plans.value[0].id
    currentPlan.value = plans.value[0]
    await loadExpenses(plans.value[0].id)
  }
  // 确保DOM已经渲染后再初始化图表
  await nextTick()
  if (chartRef.value) {
    chart = echarts.init(chartRef.value)
    updateChart()
  }
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
      // 添加支出后重置AI分析，让用户重新分析
      aiAnalysis.value = null
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
      // 删除支出后重置AI分析
      aiAnalysis.value = null
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
  // 切换行程时重置AI分析
  aiAnalysis.value = null
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

// ========== AI分析功能 ==========
const startAnalysis = async () => {
  if (!currentPlan.value || expenses.value.length === 0) {
    ElMessage.warning('请先选择行程并添加支出记录')
    return
  }

  analyzing.value = true
  aiAnalysis.value = null

  try {
    // 构建分析数据
    const payload = {
      planId: currentPlan.value.id,
      planTitle: currentPlan.value.title,
      totalBudget: currentPlan.value.budget,
      totalExpense: totalExpense.value,
      budgetUsage: budgetPercent.value,
      days: currentPlan.value.days,
      expenses: expenses.value.map(e => ({
        category: e.category,
        amount: Number(e.amount),
        date: e.date,
        remark: e.remark
      }))
    }

    const res = await analyzeBudget(payload)
    if (res.data.code === 1) {
      aiAnalysis.value = res.data.data
      ElMessage.success('AI分析完成')
    } else {
      ElMessage.error(res.data.msg || '分析失败')
    }
  } catch (err) {
    console.error('AI分析失败:', err)
    ElMessage.error('服务器异常，分析失败')
  } finally {
    analyzing.value = false
  }
}
</script>

<style scoped>
.budget-container {
  max-width: 1600px;
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

.summary-card {
  margin-bottom: 20px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.summary-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.summary-info > div {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #f9fafb;
  border-radius: 12px;
  font-size: 14px;
}

.summary-info strong {
  color: #1f2937;
  font-weight: 600;
}

.form-card {
  margin-bottom: 20px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.expense-form .el-form-item {
  margin-right: 10px;
}

.table-card {
  margin-bottom: 20px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.table-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.table-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
}

.chart-card {
  margin-top: 20px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.chart-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chart-title h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
}

.ai-card {
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 20px;
}

.ai-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.ai-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
}

.ai-empty {
  text-align: center;
  padding: 40px 20px;
  color: #9ca3af;
}

.ai-empty p {
  margin: 16px 0 24px 0;
  color: #6b7280;
  line-height: 1.6;
}

.hint-text {
  margin-top: 12px;
  font-size: 12px;
  color: #9ca3af;
}

.ai-content {
  max-height: calc(100vh - 200px);
  overflow-y: auto;
}

.ai-content::-webkit-scrollbar {
  width: 6px;
}

.ai-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.ai-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.analysis-section {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f3f4f6;
}

.analysis-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 12px;
}

.section-title .el-icon {
  color: #667eea;
  font-size: 18px;
}

.section-content {
  color: #6b7280;
  line-height: 1.8;
  font-size: 14px;
  text-align: justify;
}

.suggestions-list {
  margin: 0;
  padding-left: 20px;
  color: #6b7280;
  line-height: 1.8;
  font-size: 14px;
}

.suggestions-list li {
  margin-bottom: 10px;
}

.suggestions-list li:last-child {
  margin-bottom: 0;
}

.risk-section {
  background: #fef2f2;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #fecaca;
}

.risk-section .section-title {
  color: #dc2626;
}

.risk-section .section-title .el-icon {
  color: #dc2626;
}

.risk-content {
  color: #991b1b;
  font-weight: 500;
}

@media (max-width: 1024px) {
  .budget-container {
    padding: 10px;
  }
  
  :deep(.el-row) {
    flex-direction: column;
  }
  
  :deep(.el-col) {
    width: 100% !important;
  }
  
  .ai-card {
    position: relative;
    top: 0;
    margin-top: 20px;
  }
}

@media (max-width: 768px) {
  .budget-container {
    padding: 8px;
  }
  
  .summary-info {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .expense-form {
    flex-direction: column;
  }
  
  :deep(.expense-form .el-form-item) {
    width: 100%;
    margin-right: 0;
  }
  
  .chart-card {
    margin-top: 16px;
  }
  
  #chartRef {
    height: 300px !important;
  }
}
</style>
