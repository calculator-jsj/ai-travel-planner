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
            <el-button type="primary" @click="openSaveDialog">💾 保存行程</el-button>
            <el-button type="warning" @click="generatePlan">🔄 重新生成</el-button>
          </div>
        </el-card>
      </el-col>

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

              <el-tabs v-model="activeTab">
                <el-tab-pane label="景点信息" name="spot">
                  <p>{{ selectedSpot.description }}</p>
                  <p class="spot-type">🏷️ 类型：{{ selectedSpot.type }}</p>
                </el-tab-pane>

                <!-- 🚉 交通 -->
                <el-tab-pane label="🚉 交通" name="traffic">
                  <div class="nearby-cards">
                    <div v-for="(item, index) in nearbyInfo.traffic.slice(0, 6)" :key="index" class="nearby-card">
                      <img :src="item.photoUrl || `https://dummyimage.com/120x80/cccccc/ffffff&text=image not found`" />
                      <div class="info">
                        <h4>{{ item.name }}</h4>
                        <p>{{ item.address || '暂无地址信息' }}</p>
                      </div>
                    </div>
                    <p v-if="!nearbyInfo.traffic.length" class="no-data">暂无数据</p>
                  </div>
                </el-tab-pane>

                <!-- 🏨 住宿 -->
                <el-tab-pane label="🏨 住宿" name="hotel">
                  <div class="nearby-cards">
                    <div v-for="(item, index) in nearbyInfo.hotel.slice(0, 6)" :key="index" class="nearby-card">
                      <img :src="item.photoUrl || `https://dummyimage.com/120x80/cccccc/ffffff&text=image not found`"
                        alt="酒店" />
                      <div class="info">
                        <h4>{{ item.name }}</h4>
                        <p>{{ item.address || '暂无地址信息' }}</p>
                        <p class="tel">📞 {{ item.tel || '暂无电话' }}</p>
                      </div>
                    </div>
                    <p v-if="!nearbyInfo.hotel.length" class="no-data">暂无数据</p>
                  </div>
                </el-tab-pane>

                <!-- 🍽️ 餐饮 -->
                <el-tab-pane label="🍽️ 餐饮" name="food">
                  <div class="nearby-cards">
                    <div v-for="(item, index) in nearbyInfo.food.slice(0, 6)" :key="index" class="nearby-card">
                      <img :src="item.photoUrl || `https://dummyimage.com/120x80/cccccc/ffffff&text=image not found`"
                        alt="餐饮" />
                      <div class="info">
                        <h4>{{ item.name }}</h4>
                        <p>{{ item.address || '暂无地址信息' }}</p>
                        <p class="tel">📞 {{ item.tel || '暂无电话' }}</p>
                      </div>
                    </div>
                    <p v-if="!nearbyInfo.food.length" class="no-data">暂无数据</p>
                  </div>
                </el-tab-pane>

              </el-tabs>

              <el-button type="primary" text size="small" @click="selectedSpot = null">关闭</el-button>
            </div>

          </transition>
        </el-card>
      </el-col>

    </el-row>

    <!-- 保存行程对话框 -->
    <el-dialog title="保存行程" v-model="saveDialogVisible" width="400px" align-center>
      <el-form :model="saveForm" label-width="100px">
        <el-form-item label="行程名称">
          <el-input v-model="saveForm.planName" placeholder="请输入行程名称"></el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="saveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSavePlan">保存</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { generateAIPlan } from '@/api/ai'
import { getNearby } from '@/api/map'
import { savePlan } from '@/api/plan'

let map = null

const form = ref({
  destination: '',
  days: 5,
  budget: 10000,
  people: 2,
  preferences: []
})

// 附近信息（交通 / 餐饮 / 住宿）
const nearbyInfo = ref({
  traffic: [],
  hotel: [],
  food: []
})

// 当前选中的 Tab（spot / traffic / hotel / food）
const activeTab = ref('spot')
// const nearbyMarkers = ref([])  // 保存当前显示的附近 POI Marker

const userId = localStorage.getItem('userId')
const planResult = ref([])
const selectedDay = ref(null)
const activeDay = ref(0)
const loading = ref(false)
const listening = ref(false)
const selectedSpot = ref(null)
const saveDialogVisible = ref(false)
const saveForm = ref({
  planName: ''
})
const colors = ['#0078FF', '#28A745', '#FFC107', '#DC3545', '#6610F2', '#17A2B8']
const amapKey = import.meta.env.VITE_AMAP_KEY

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

// 初始化地图
onMounted(() => {
  const script = document.createElement('script')
  script.src = `https://webapi.amap.com/maps?v=2.0&key=${amapKey}&plugin=AMap.ToolBar,AMap.Scale,AMap.Geocoder,AMap.PlaceSearch`
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
        activeTab.value = 'spot' // 默认显示景点信息
        fetchNearbyInfo(spot)    // 获取周边数据
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

// 使用后端代理获取附近数据并展示
const fetchNearbyInfo = async (spot) => {
  if (!spot || !spot.lng || !spot.lat) return

  // 清空旧数据（显示加载状态可以按需添加）
  nearbyInfo.value.traffic = []
  nearbyInfo.value.hotel = []
  nearbyInfo.value.food = []

  try {
    // 交通
    const resTraffic = await getNearby(spot.lng, spot.lat, 'traffic')
    if (resTraffic && resTraffic.data) {
      nearbyInfo.value.traffic = resTraffic.data || []
    }

    // 酒店
    const resHotel = await getNearby(spot.lng, spot.lat, 'hotel')
    if (resHotel && resHotel.data) {
      nearbyInfo.value.hotel = resHotel.data || []
    }

    // 餐饮
    const resFood = await getNearby(spot.lng, spot.lat, 'restaurant')
    if (resFood && resFood.data) {
      nearbyInfo.value.food = resFood.data || []
    }

    // （可选）在控制台查看返回结构，方便调试
    console.log('nearby traffic', nearbyInfo.value.traffic)
    console.log('nearby hotel', nearbyInfo.value.hotel)
    console.log('nearby food', nearbyInfo.value.food)
  } catch (err) {
    console.error('fetchNearbyInfo error', err)
  }
}

/// 点击“保存行程”按钮，弹出对话框
const openSaveDialog = () => {
  console.log('按钮点击了')

  if (!form.value.destination || !planResult.value.length) {
    ElMessage.warning('请先生成行程并填写必要信息')
    return
  }
  saveForm.value.planName = '' // 重置表单
  saveDialogVisible.value = true
}

// 提交表单
const submitSavePlan = async () => {
  if (!saveForm.value.planName) {
    ElMessage.warning('请输入行程名称')
    return
  }

  const simplifiedPlan = planResult.value.map(day => ({
    day: day.day,
    spots: day.spots.map(spot => ({
      name: spot.name,
      type: spot.type,
      description: spot.description
    }))
  }))

  const payload = {
    // userId: localStorage.getItem('userId'),
    planName: saveForm.value.planName,
    destination: form.value.destination,
    days: form.value.days,
    budget: form.value.budget,
    people: form.value.people,
    preferences: form.value.preferences,
    plan: simplifiedPlan
  }

  console.log('要发送的行程数据:', JSON.stringify(payload, null, 2))

  try {
    console.log('用户ID:', userId)
    const res = await savePlan(payload, userId)
    if (res.data.code === 1) {
      ElMessage.success('行程已保存！')
      saveDialogVisible.value = false
    } else {
      ElMessage.error(res.data.msg || '保存失败')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('服务器异常，保存失败')
  }
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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

.spot-card {
  margin-top: 10px;
  background: #f9fafb;
  border-radius: 12px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  max-height: 600px;
  overflow-y: auto;
}

.nearby-cards {
  display: flex;
  gap: 12px;
  margin-top: 10px;
  flex-wrap: wrap;
  justify-content: space-between;
}

.nearby-card {
  flex: 1;
  min-width: 30%;
  max-width: 32%;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease;
  cursor: pointer;
}

.nearby-card:hover {
  transform: translateY(-4px);
}

.nearby-card img {
  width: 100%;
  height: 80px;
  object-fit: cover;
  border-bottom: 1px solid #eee;
}

.nearby-card .info {
  padding: 8px 10px;
}

.nearby-card h4 {
  font-size: 14px;
  color: #333;
  margin: 0;
  margin-bottom: 4px;
}

.nearby-card p {
  font-size: 12px;
  color: #666;
  margin: 0;
  line-height: 1.4;
}

.nearby-card .tel {
  color: #409eff;
  font-weight: 500;
  margin-top: 4px;
}

.no-data {
  width: 100%;
  text-align: center;
  color: #999;
  margin-top: 20px;
}
</style>
