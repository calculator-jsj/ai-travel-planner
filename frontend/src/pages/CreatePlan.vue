<template>
  <div class="create-plan-container">
    <!-- 标题栏 -->
    <el-card class="header-card">
      <h2>✈️ 创建智能行程</h2>
      <p>输入或语音描述你的旅行计划，让 AI 帮你生成完整路线</p>
    </el-card>

    <el-row :gutter="20">
      <!-- 左侧表单 + AI行程结果 -->
      <el-col :span="10">
        <!-- 表单区域 -->
        <el-card class="form-card">
          <el-form :model="form" label-width="100px">
            <el-form-item label="目的地">
              <el-input v-model="form.destination" placeholder="如：日本东京" />
            </el-form-item>

            <el-form-item label="天数">
              <el-input-number v-model="form.days" :min="1" />
            </el-form-item>

            <el-form-item label="预算 (元)">
              <el-input-number v-model="form.budget" :min="0" step="500" />
            </el-form-item>

            <el-form-item label="人数">
              <el-input-number v-model="form.people" :min="1" />
            </el-form-item>

            <el-form-item label="偏好">
              <el-select v-model="form.preferences" multiple placeholder="选择偏好">
                <el-option label="美食" value="美食" />
                <el-option label="文化" value="文化" />
                <el-option label="自然" value="自然" />
                <el-option label="亲子" value="亲子" />
                <el-option label="购物" value="购物" />
                <el-option label="动漫" value="动漫" />
              </el-select>
            </el-form-item>

            <!-- 语音输入按钮 -->
            <div class="voice-section">
              <el-button type="primary" plain @click="startVoiceInput">
                🎤 语音输入
              </el-button>
              <span v-if="listening" class="listening-text">正在聆听中...</span>
            </div>

            <!-- 提交按钮 -->
            <el-button type="success" class="generate-btn" :loading="loading" @click="generatePlan">
              生成 AI 行程
            </el-button>
          </el-form>
        </el-card>

        <el-card v-if="planResult.length > 0" class="result-card">
          <h3>🧭 AI 生成的旅行计划</h3>
          <el-collapse v-model="activeDay">
            <el-collapse-item v-for="(day, index) in planResult" :key="index" :title="'第 ' + (index + 1) + ' 天'"
              :name="index">
              <li v-for="(spot, i) in day.spots" :key="i">
                <strong>{{ spot.name }}</strong>
                <p class="spot-desc">{{ spot.description }}</p>
              </li>
            </el-collapse-item>
          </el-collapse>

          <div class="action-btns">
            <el-button type="primary">💾 保存行程</el-button>
            <el-button type="warning" @click="generatePlan">🔄 重新生成</el-button>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧地图 -->
      <!-- 右侧地图 -->
      <el-col :span="14">
        <el-card>
          <div class="map-header">
            <el-select v-model="selectedDay" placeholder="选择天数" size="medium" @change="renderPlanOnMap">
              <el-option v-for="(day, index) in planResult" :key="index" :label="'第 ' + (index + 1) + ' 天'"
                :value="index" />
            </el-select>
          </div>

          <div id="mapContainer" class="map"></div>

          <!-- ✅ 点击景点后显示的卡片 -->
          <transition name="fade">
            <div v-if="selectedSpot" class="spot-card">
              <h3>{{ selectedSpot.name }}</h3>
              <p>{{ selectedSpot.description }}</p>
              <p class="spot-type">🏷️ 类型：{{ selectedSpot.type }}</p>
              <el-button type="primary" text size="small" @click="selectedSpot = null">关闭</el-button>
            </div>
          </transition>
        </el-card>
      </el-col>

    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { generateAIPlan } from '@/api/ai'

let map = null

const form = ref({
  destination: '',
  days: 5,
  budget: 10000,
  people: 2,
  preferences: []
})

const planResult = ref([])
const selectedDay = ref(null)
const activeDay = ref(0)
const loading = ref(false)
const listening = ref(false)
const selectedSpot = ref(null)
const colors = ['#0078FF', '#28A745', '#FFC107', '#DC3545', '#6610F2', '#17A2B8']

// 🎤 语音输入
const startVoiceInput = () => {
  if (!('webkitSpeechRecognition' in window)) {
    ElMessage.error('当前浏览器不支持语音识别')
    return
  }
  const recognition = new webkitSpeechRecognition()
  recognition.lang = 'zh-CN'
  recognition.start()
  listening.value = true

  recognition.onresult = (event) => {
    const text = event.results[0][0].transcript
    form.value.destination = text
    listening.value = false
    ElMessage.success('识别成功: ' + text)
  }
  recognition.onerror = () => {
    listening.value = false
    ElMessage.error('语音识别失败')
  }
}

//生成AI行程
const generatePlan = async () => {
  if (!form.value.destination) {
    ElMessage.warning('请先输入目的地或使用语音输入')
    return
  }

  loading.value = true
  planResult.value = []

  try {
    const res = await generateAIPlan(form.value)
    if (res.data.code === 1 && res.data.data) {
      planResult.value = res.data.data.plan
      ElMessage.success('AI 行程生成完成')
      renderPlanOnMap()
    } else {
      ElMessage.error(res.data.msg || '生成失败，请稍后再试')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('服务器连接失败，请检查后端服务')
  } finally {
    loading.value = false
  }
}

const amapKey = import.meta.env.VITE_AMAP_KEY

// 初始化地图
onMounted(() => {
  const script = document.createElement('script')
  script.src = `https://webapi.amap.com/maps?v=2.0&key=${amapKey}&plugin=AMap.ToolBar,AMap.Scale,AMap.Geocoder`
  document.head.appendChild(script)
  script.onload = () => {
    map = new AMap.Map('mapContainer', {
      zoom: 12,
      center: [116.397, 39.908],
    })
  }
})

// 在地图上渲染AI路线
const renderPlanOnMap = () => {
  if (!map || planResult.value.length === 0) return
  map.clearMap()

  const daysToRender =
    selectedDay.value !== null ? [planResult.value[selectedDay.value]] : planResult.value

  daysToRender.forEach((day, i) => {
    const dayIndex = selectedDay.value !== null ? selectedDay.value : i
    const path = []

    day.spots.forEach((spot, index) => {
      const marker = new AMap.Marker({
        position: [spot.lng, spot.lat],
        map: map,
        title: spot.name,
        label: {
          content: `D${day.day}-${index + 1}`,
          direction: 'top',
          offset: new AMap.Pixel(0, -25)
        }
      })

      marker.on('click', () => {
        selectedSpot.value = spot
      })

      path.push([spot.lng, spot.lat])
    })

    if (path.length > 1) {
      const polyline = new AMap.Polyline({
        path: path,
        strokeColor: colors[dayIndex % colors.length],
        strokeWeight: 5,
        strokeOpacity: 0.9,
        showDir: true,
        isOutline: true,
        outlineColor: '#ffffff'
      })
      polyline.setMap(map)
    }
  })

  map.setFitView()
}
</script>

<style scoped>
.create-plan-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header-card {
  margin-bottom: 20px;
  text-align: center;
}

.form-card {
  margin-bottom: 20px;
}

.generate-btn {
  width: 100%;
  margin-top: 10px;
}

.result-card {
  margin-top: 20px;
}

.action-btns {
  margin-top: 15px;
  display: flex;
  gap: 10px;
}

.voice-section {
  margin: 10px 0;
}

.listening-text {
  margin-left: 10px;
  color: #409eff;
  font-weight: bold;
}

.map {
  width: 100%;
  height: 500px;
}

.map-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 10px;
}

.spot-card {
  margin-top: 10px;
  background: #f9fafb;
  border-radius: 12px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.spot-card h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
  color: #333;
}

.spot-card p {
  margin: 5px 0;
  color: #555;
}

.spot-type {
  font-style: italic;
  color: #888;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

</style>
