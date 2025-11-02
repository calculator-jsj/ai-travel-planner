<template>
  <div class="plan-list-container">
    <!-- 顶部标题 -->
    <el-card class="header-card">
      <div class="header-content">
        <div class="title-wrapper">
          <el-icon :size="32" color="#409EFF"><FolderOpened /></el-icon>
          <h2>我的行程</h2>
        </div>
        <p class="subtitle">在这里查看、管理、或导出你创建的所有AI行程</p>
      </div>
    </el-card>

    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="filters" class="filter-form">
        <!-- 行程名称 -->
        <el-form-item label="行程名称">
          <el-input v-model="filters.planName" placeholder="输入行程名称" clearable />
        </el-form-item>

        <!-- 偏好 -->
        <el-form-item label="偏好">
          <el-select v-model="filters.preferences" multiple placeholder="选择偏好" style="width: 200px;" clearable>
            <el-option label="美食" value="美食" />
            <el-option label="文化" value="文化" />
            <el-option label="自然" value="自然" />
            <el-option label="亲子" value="亲子" />
            <el-option label="购物" value="购物" />
            <el-option label="动漫" value="动漫" />
          </el-select>
        </el-form-item>

        <!-- 预算范围 -->
        <el-form-item label="预算范围">
          <div style="display: flex; align-items: center;">
            <el-input v-model="filters.budgetMin" placeholder="最低预算" style="width: 100px;" clearable />
            <span style="margin: 0 5px;">~</span>
            <el-input v-model="filters.budgetMax" placeholder="最高预算" style="width: 100px;" clearable />
          </div>
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="filterPlans">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>


    <!-- 行程卡片 -->
    <el-row :gutter="20" class="plan-list">
      <el-col v-for="plan in filteredPlans" :key="plan.id" :xs="24" :sm="12" :md="8">
        <el-card class="plan-card" shadow="hover">
          <div class="card-header">
            <h3>{{ plan.title }}</h3>
            <p class="created-time">
              <el-icon><Clock /></el-icon>
              {{ formatDate(plan.createdAt) }}
            </p>
          </div>

          <div class="card-body">
            <div class="info-item">
              <el-icon><Calendar /></el-icon>
              <span>天数：{{ plan.days }} 天</span>
            </div>
            <div class="info-item">
              <el-icon><Money /></el-icon>
              <span>预算：¥{{ plan.budget.toLocaleString() }}</span>
            </div>

            <div class="tag-container">
              <span>偏好：</span>
              <div class="tags">
                <el-tag v-for="tag in parsePreferences(plan.preferences)" :key="tag" :type="tagColor(tag)"
                  effect="plain" size="small" class="tag-item">
                  {{ tag }}
                </el-tag>

              </div>
            </div>
          </div>

          <div class="card-footer">
            <div class="footer-left">
              <el-button size="small" @click="viewPlan(plan)" :icon="View">查看详情</el-button>
            </div>
            <div class="footer-right">
              <el-button size="small" type="success" text @click="exportPlan(plan)" :icon="Download">导出</el-button>
              <el-button size="small" type="danger" text @click="deletePlan(plan.id)" :icon="Delete">删除</el-button>
            </div>
          </div>

        </el-card>
      </el-col>
    </el-row>

    <!-- 详情弹窗 -->
    <el-dialog v-model="dialogVisible.view" title="行程详情" width="760px" class="detail-dialog">
      <div v-if="selectedPlan">
        <!-- 顶部信息 -->
        <div class="plan-summary">
          <h2>{{ selectedPlan.title }}</h2>
          <div class="meta">
            <span>
              <el-icon><Calendar /></el-icon>
              {{ selectedPlan.days }} 天
            </span>
            <span>
              <el-icon><Money /></el-icon>
              预算 ¥{{ selectedPlan.budget.toLocaleString() }}
            </span>
          </div>
          <div class="tags">
            <el-tag v-for="tag in parsePreferences(selectedPlan.preferences)" :key="tag" :type="tagColor(tag)"
              size="small" effect="plain">
              {{ tag }}
            </el-tag>
          </div>
        </div>

        <el-divider />

        <!-- 天数切换 -->
        <div class="day-tabs">
          <el-segmented v-model="activeDay" :options="planDetails.map(d => `第 ${d.day} 天`)" size="large" />
        </div>

        <!-- 固定高度滚动区 -->
        <div class="plan-details-scroll">
          <transition name="fade" mode="out-in">
            <div v-if="currentDayData" :key="activeDay" class="day-content">
              <el-empty v-if="!currentDayData.spots?.length" description="暂无景点数据" />
              <el-row v-else :gutter="12" class="spot-grid">
                <el-col v-for="spot in currentDayData.spots" :key="spot.id" :xs="24" :sm="12">
                  <el-card shadow="hover" class="spot-card">
                    <div class="spot-header">
                      <el-icon class="spot-icon"><LocationFilled /></el-icon>
                      <h4 class="spot-name">{{ spot.name }}</h4>
                    </div>
                    <p class="spot-type">
                      <el-icon><Collection /></el-icon>
                      {{ spot.type }}
                    </p>
                    <p class="spot-desc">{{ spot.description }}</p>
                  </el-card>
                </el-col>
              </el-row>
            </div>
          </transition>
        </div>
      </div>

      <template #footer>
        <el-button @click="dialogVisible.view = false">关闭</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  FolderOpened, 
  Clock, 
  Calendar, 
  Money, 
  View, 
  Download, 
  Delete,
  LocationFilled,
  Collection
} from '@element-plus/icons-vue'
import { getPlansByUserId, searchPlans, deletePlanById } from '@/api/plan'
import { getSpotsByPlanId } from '@/api/spot'

// ====================== 数据定义 ======================
const plans = ref([])
const filteredPlans = ref([])
const selectedPlan = ref(null)
const dialogVisible = ref({ view: false })
const planDetails = ref([])

const filters = ref({
  planName: '',
  preferences: [],
  budgetMin: '',
  budgetMax: ''
})

// ====================== 工具函数 ======================
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

const parsePreferences = (pref) => {
  if (!pref) return []
  try {
    const parsed = JSON.parse(pref)
    return Array.isArray(parsed) ? parsed : [parsed]
  } catch {
    return typeof pref === 'string' ? pref.replace(/[\[\]"]/g, '').split(',').map(p => p.trim()) : []
  }
}

// ✅ 柔和标签颜色（低饱和版）
const tagColor = (tag) => {
  const map = {}
  return map[tag] || ''
}

// ====================== 查看详情 ======================
const viewPlan = async (plan) => {
  selectedPlan.value = plan
  dialogVisible.value.view = true

  try {
    const res = await getSpotsByPlanId(plan.id)
    if (res.data.code === 1) {
      const spots = res.data.data || []

      // ✅ 按天分组
      const grouped = {}
      spots.forEach(s => {
        if (!grouped[s.day]) grouped[s.day] = []
        grouped[s.day].push(s)
      })

      // ✅ 转为数组方便渲染
      planDetails.value = Object.keys(grouped).map(day => ({
        day,
        spots: grouped[day].sort((a, b) => a.orderIndex - b.orderIndex)
      }))
    } else {
      ElMessage.error(res.data.msg || '加载详情失败')
    }
  } catch (err) {
    console.error('❌ 加载详情失败:', err)
    ElMessage.error('服务器异常，无法加载详情')
  }
}


// ====================== 加载行程 ======================
const loadPlans = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) {
    ElMessage.warning('未检测到登录用户')
    return
  }
  const res = await getPlansByUserId(userId)
  if (res.data.code === 1) {
    plans.value = res.data.data
    filteredPlans.value = res.data.data
  } else {
    ElMessage.error(res.data.msg || '加载失败')
  }
}

// ====================== 搜索行程 ======================
const filterPlans = async () => {
  try {
    const userId = localStorage.getItem('userId')
    if (!userId) {
      ElMessage.warning('未检测到登录用户')
      return
    }

    const params = {
      userId,
      planName: filters.value.planName || '',
      preferences: filters.value.preferences.join(',') || '',
      budgetMin: filters.value.budgetMin || '',
      budgetMax: filters.value.budgetMax || ''
    }

    console.log('🔍 搜索参数:', params)
    const res = await searchPlans(params)

    if (res.data.code === 1) {
      filteredPlans.value = res.data.data
      ElMessage.success(`共找到 ${res.data.data.length} 个行程`)
    } else {
      ElMessage.error(res.data.msg || '搜索失败')
    }
  } catch (err) {
    console.error('❌ 搜索失败:', err)
    ElMessage.error('服务器异常，无法搜索行程')
  }
}

// ====================== 重置搜索 ======================
const resetSearch = async () => {
  filters.value = {
    planName: '',
    preferences: [],
    budgetMin: '',
    budgetMax: ''
  }
  await loadPlans()
  ElMessage.info('已重置筛选条件')
}

// 当前选中的天数标签
const activeDay = ref('第 1 天')

const currentDayData = computed(() => {
  if (!planDetails.value.length) return null
  const current = planDetails.value.find(d => `第 ${d.day} 天` === activeDay.value)
  return current || planDetails.value[0]
})

// ====================== 导出行程 ======================
const exportPlan = (plan) => {
  ElMessage.info('导出功能开发中，敬请期待')
}

// ====================== 删除行程 ======================
const deletePlan = async (planId) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个行程吗？删除后将无法恢复！',
      '⚠️ 删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const res = await deletePlanById(planId)
    if (res.data.code === 1) {
      ElMessage.success('行程已删除')
      await loadPlans() // 重新加载列表
    } else {
      ElMessage.error(res.data.msg || '删除失败')
    }
  } catch (err) {
    if (err !== 'cancel') {
      console.error('❌ 删除失败:', err)
      ElMessage.error('服务器异常，删除失败')
    } else {
      ElMessage.info('已取消删除')
    }
  }
}

// ====================== 页面初始化 ======================
onMounted(loadPlans)
</script>


<style scoped>
.plan-list-container {
  max-width: 1100px;
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

.search-card {
  margin-bottom: 20px;
}

.budget-range {
  display: flex;
  align-items: center;
  gap: 5px;
}

.plan-card {
  margin-bottom: 20px;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid #e5e7eb;
}

.plan-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #667eea;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.created-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #9ca3af;
}

.card-body {
  margin: 16px 0;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 10px 0;
  color: #6b7280;
  font-size: 14px;
}

.info-item .el-icon {
  color: #667eea;
  font-size: 16px;
}

.tag-container {
  margin-top: 8px;
  display: flex;
  align-items: flex-start;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-left: 5px;
}

/* 搜索栏布局更紧凑 */
.filter-form .el-form-item {
  margin-right: 20px;
  margin-bottom: 0;
}

/* 卡片底部操作区域优化 */
.card-footer {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-right {
  display: flex;
  gap: 8px;
}

.footer-left {
  flex-grow: 1;
}

/* 弹窗样式优化 */
.detail-dialog .el-dialog__body {
  padding-top: 10px;
  padding-bottom: 10px;
}

/* 顶部摘要 */
.plan-summary {
  text-align: center;
}

.plan-summary h3 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
}

.plan-summary .meta {
  display: flex;
  justify-content: center;
  gap: 16px;
  font-size: 14px;
  color: #666;
  margin-bottom: 6px;
}

/* 偏好标签 */
.plan-summary .tags {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 6px;
}

/* 滚动区 */
.plan-details-scroll {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 8px;
}

/* 折叠卡片内的景点 */
.spot-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 8px;
}

.spot-card {
  border-radius: 12px;
  padding: 16px;
  background: #fafafa;
  transition: all 0.3s ease;
  height: 100%;
  border: 1px solid #e5e7eb;
}

.spot-card:hover {
  background: #fff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: #667eea;
}

.spot-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.spot-icon {
  color: #667eea;
  font-size: 18px;
}

.spot-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.spot-type {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #9ca3af;
  margin-bottom: 8px;
}

.spot-desc {
  font-size: 13px;
  color: #555;
  line-height: 1.5;
}

/* 滚动条优化 */
.plan-details-scroll::-webkit-scrollbar {
  width: 6px;
}

.plan-details-scroll::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.15);
  border-radius: 4px;
}

.plan-details-scroll::-webkit-scrollbar-thumb:hover {
  background-color: rgba(0, 0, 0, 0.25);
}

.plan-detail-dialog .el-dialog__body {
  padding: 0;
}

/* ===== 顶部标题区 ===== */
.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 22px;
  background: #f8faff;
  border-bottom: 1px solid #e8ecf3;
}

.dialog-title h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.dialog-title p {
  margin: 0;
  color: #888;
  font-size: 13px;
}

/* ===== 内容区 ===== */
.plan-content {
  max-height: 65vh;
  overflow-y: auto;
  padding: 20px 24px;
  background: #fff;
}

/* ===== 元信息 ===== */
.plan-meta {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  font-size: 14px;
  color: #555;
}

.pref-tag {
  margin-left: 4px;
}

/* ===== 每天的模块 ===== */
.days-wrapper {
  margin-top: 10px;
}

.day-block {
  background: #f9fafb;
  border-radius: 16px;
  padding: 18px 20px;
  margin-bottom: 18px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.day-block:hover {
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.06);
}

.day-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.day-number {
  background: #409EFF;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 13px;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.2);
}

.detail-dialog .el-dialog__body {
  padding-top: 10px;
  padding-bottom: 10px;
}

/* 顶部摘要区 */
.plan-summary {
  text-align: center;
  margin-bottom: 8px;
}

.plan-summary h2 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 6px;
}

.plan-summary .meta {
  display: flex;
  justify-content: center;
  gap: 24px;
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 8px;
}

.plan-summary .meta span {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #f3f4f6;
  border-radius: 8px;
}

.plan-summary .meta .el-icon {
  color: #667eea;
}

.plan-summary .tags {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 6px;
}

/* Tabs 区域 */
.day-tabs {
  display: flex;
  justify-content: center;
  margin-bottom: 10px;
}

/* 内容滚动区 */
.plan-details-scroll {
  max-height: 60vh;
  overflow-y: auto;
  padding: 4px 8px;
}

/* 景点卡片 */
.spot-card {
  background: #f9fafb;
  border-radius: 12px;
  transition: all 0.2s ease;
  padding: 10px 14px;
  height: 100%;
}

.spot-card:hover {
  background: #f5f7fa;
  transform: translateY(-2px);
}

.spot-name {
  font-weight: 600;
  font-size: 15px;
  margin-bottom: 4px;
}

.spot-type {
  font-size: 13px;
  color: #777;
  margin-bottom: 6px;
}

.spot-desc {
  font-size: 13px;
  color: #555;
  line-height: 1.5;
}

/* 渐隐切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 滚动条优化 */
.plan-details-scroll::-webkit-scrollbar {
  width: 6px;
}

.plan-details-scroll::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.15);
  border-radius: 4px;
}

.plan-details-scroll::-webkit-scrollbar-thumb:hover {
  background-color: rgba(0, 0, 0, 0.25);
}
</style>
