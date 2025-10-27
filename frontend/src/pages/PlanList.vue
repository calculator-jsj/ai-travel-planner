<template>
  <div class="plan-list-container">
    <!-- 顶部标题 -->
    <el-card class="header-card">
      <h2>🗂 我的行程</h2>
      <p>在这里查看、管理、或导出你创建的所有AI行程</p>
    </el-card>

    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="filters">
        <el-form-item label="目的地">
          <el-input v-model="filters.destination" placeholder="输入目的地" />
        </el-form-item>

        <el-form-item label="出发时间">
          <el-date-picker
            v-model="filters.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="filterPlans">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 行程卡片展示 -->
    <el-row :gutter="20" class="plan-list">
      <el-col
        v-for="plan in filteredPlans"
        :key="plan.id"
        :xs="24" :sm="12" :md="8"
      >
        <el-card class="plan-card" shadow="hover">
          <div class="card-header">
            <h3>{{ plan.destination }}</h3>
            <span>{{ plan.startDate }} ~ {{ plan.endDate }}</span>
          </div>
          <div class="card-body">
            <p>天数：{{ plan.days }} 天</p>
            <p>预算：¥{{ plan.budget }}</p>
            <p>同行人数：{{ plan.people }} 人</p>
            <p>偏好：{{ plan.preferences.join('、') }}</p>
          </div>

          <div class="card-footer">
            <el-button size="small" @click="viewPlan(plan)">查看详情</el-button>
            <el-button size="small" type="primary" @click="editPlan(plan)">编辑</el-button>
            <el-button size="small" type="danger" @click="deletePlan(plan.id)">删除</el-button>
            <el-button size="small" type="success" @click="exportPlan(plan)">导出</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详情弹窗 -->
    <el-dialog v-model="dialogVisible.view" title="行程详情" width="600px">
      <div v-if="selectedPlan">
        <h3>{{ selectedPlan.destination }}（{{ selectedPlan.days }}天行程）</h3>
        <el-divider></el-divider>
        <el-timeline>
          <el-timeline-item
            v-for="(day, index) in selectedPlan.details"
            :key="index"
            :timestamp="'第 ' + (index + 1) + ' 天'"
          >
            {{ day }}
          </el-timeline-item>
        </el-timeline>
      </div>
      <template #footer>
        <el-button @click="dialogVisible.view = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible.edit" title="编辑行程" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="目的地">
          <el-input v-model="editForm.destination" />
        </el-form-item>
        <el-form-item label="预算">
          <el-input-number v-model="editForm.budget" :min="0" step="500" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="editForm.remark" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible.edit = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 模拟数据（未来你可用后端接口替换）
const plans = ref([
  {
    id: 1,
    destination: '日本东京',
    startDate: '2025-10-10',
    endDate: '2025-10-15',
    days: 5,
    budget: 10000,
    people: 2,
    preferences: ['美食', '动漫'],
    details: [
      '抵达东京，入住酒店，晚餐推荐涉谷美食街。',
      '游览秋叶原动漫街区与浅草寺。',
      '自由行活动，可前往新宿御苑散步。',
      '购物日：银座百货与秋叶原周边。',
      '返程准备，总结行程拍照留念。'
    ]
  },
  {
    id: 2,
    destination: '云南大理',
    startDate: '2025-09-20',
    endDate: '2025-09-25',
    days: 5,
    budget: 8000,
    people: 3,
    preferences: ['自然', '文化'],
    details: [
      '抵达大理古城，入住客栈。',
      '洱海环湖骑行。',
      '游览苍山与蝴蝶泉。',
      '参观白族文化村与手工体验。',
      '返程准备，购买纪念品。'
    ]
  }
])

// 搜索与过滤
const filters = ref({
  destination: '',
  dateRange: []
})

const filteredPlans = ref(plans.value)

const filterPlans = () => {
  const { destination, dateRange } = filters.value
  filteredPlans.value = plans.value.filter((plan) => {
    const matchDestination =
      !destination || plan.destination.includes(destination)
    const matchDate =
      !dateRange.length ||
      (plan.startDate >= dateRange[0] && plan.endDate <= dateRange[1])
    return matchDestination && matchDate
  })
}

const resetFilters = () => {
  filters.value = { destination: '', dateRange: [] }
  filteredPlans.value = plans.value
}

// 弹窗状态管理
const dialogVisible = ref({
  view: false,
  edit: false
})

const selectedPlan = ref(null)
const editForm = ref({})

// 查看行程详情
const viewPlan = (plan) => {
  selectedPlan.value = plan
  dialogVisible.value.view = true
}

// 编辑行程
const editPlan = (plan) => {
  editForm.value = { ...plan }
  dialogVisible.value.edit = true
}

const saveEdit = () => {
  const index = plans.value.findIndex(p => p.id === editForm.value.id)
  if (index !== -1) {
    plans.value[index] = { ...editForm.value }
    ElMessage.success('行程更新成功')
  }
  dialogVisible.value.edit = false
}

// 删除行程
const deletePlan = (id) => {
  ElMessageBox.confirm('确定要删除此行程吗？', '提示', {
    type: 'warning'
  })
    .then(() => {
      plans.value = plans.value.filter((p) => p.id !== id)
      filteredPlans.value = plans.value
      ElMessage.success('删除成功')
    })
    .catch(() => {})
}

// 导出功能（JSON导出示例）
const exportPlan = (plan) => {
  const blob = new Blob([JSON.stringify(plan, null, 2)], { type: 'application/json' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `${plan.destination}_行程.json`
  link.click()
  ElMessage.success('行程已导出')
}
</script>

<style scoped>
.plan-list-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 20px;
}

.header-card {
  text-align: center;
  margin-bottom: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.plan-list {
  margin-top: 10px;
}

.plan-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-body p {
  margin: 3px 0;
}

.card-footer {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
}
</style>
