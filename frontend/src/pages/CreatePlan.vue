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
            <el-button
              type="success"
              class="generate-btn"
              :loading="loading"
              @click="generatePlan"
            >
              生成 AI 行程
            </el-button>
          </el-form>
        </el-card>

        <!-- AI生成结果 -->
        <el-card v-if="planResult.length > 0" class="result-card">
          <h3>🧭 AI 生成的旅行计划</h3>
          <el-collapse v-model="activeDay">
            <el-collapse-item
              v-for="(day, index) in planResult"
              :key="index"
              :title="'第 ' + (index + 1) + ' 天'"
              :name="index"
            >
              <ul>
                <li v-for="spot in day.spots" :key="spot.name">{{ spot.name }}</li>
              </ul>
            </el-collapse-item>
          </el-collapse>

          <div class="action-btns">
            <el-button type="primary">💾 保存行程</el-button>
            <el-button type="warning" @click="generatePlan">🔄 重新生成</el-button>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧地图 -->
      <el-col :span="14">
        <el-card>
          <div id="mapContainer" class="map"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'

const form = ref({
  destination: '',
  days: 5,
  budget: 10000,
  people: 2,
  preferences: []
})

// AI生成结果结构，每天包含景点数组
const planResult = ref([])

const loading = ref(false)
const listening = ref(false)
const activeDay = ref(0)

let map = null
let markers = []
let polyline = null

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

// 模拟 AI 生成行程数据
const generatePlan = async () => {
  if (!form.value.destination) {
    ElMessage.warning('请先输入目的地或使用语音输入')
    return
  }

  loading.value = true
  planResult.value = []
  markers.forEach(m => m.setMap(null)) // 清空原有marker
  polyline && polyline.setMap(null)

  setTimeout(() => {
    // 模拟数据，每天包含景点
    planResult.value = [
      {
        spots: [
          { name: '东京塔', lnglat: [139.745433, 35.658581] },
          { name: '涩谷', lnglat: [139.703549, 35.659108] },
          { name: '银座', lnglat: [139.764936, 35.674915] }
        ]
      },
      {
        spots: [
          { name: '秋叶原', lnglat: [139.770102, 35.702069] },
          { name: '上野公园', lnglat: [139.7745, 35.7138] }
        ]
      }
    ]

    // 地图上展示景点
    updateMap()
    loading.value = false
    ElMessage.success('AI 行程生成完成')
  }, 2000)
}

// 初始化地图
onMounted(() => {
  const script = document.createElement('script')
  script.src =
    'https://webapi.amap.com/maps?v=2.0&key=你的高德Key&plugin=AMap.Driving'
  document.head.appendChild(script)

  script.onload = () => {
    map = new AMap.Map('mapContainer', {
      zoom: 12,
      center: [139.767052, 35.681167] // 默认东京站
    })
  }
})

// 更新地图显示
const updateMap = () => {
  if (!map) return

  markers.forEach(m => m.setMap(null))
  polyline && polyline.setMap(null)
  markers = []

  const path = []

  planResult.value.forEach(day => {
    day.spots.forEach(spot => {
      const marker = new AMap.Marker({
        position: spot.lnglat,
        map,
        title: spot.name
      })
      marker.on('click', () => {
        const infoWindow = new AMap.InfoWindow({
          content: `<strong>${spot.name}</strong>`,
          offset: new AMap.Pixel(0, -30)
        })
        infoWindow.open(map, marker.getPosition())
      })
      markers.push(marker)
      path.push(spot.lnglat)
    })
  })

  polyline = new AMap.Polyline({
    path,
    strokeColor: '#409EFF',
    strokeWeight: 4
  })
  polyline.setMap(map)
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
</style>
