import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api/plan'

// 保存行程（需要带上 userId）
export const savePlan = (payload,userId) => {
  return axios.post(`${BASE_URL}/save?userId=${userId}`, payload)
}

// 获取用户所有行程
export const getPlansByUserId = (userId) => {
  return axios.get(`${BASE_URL}/list/${userId}`)
}

// 按条件搜索行程
export const searchPlans = (params) => {
  // params 应该包含 userId、planName、preferences、budgetMin、budgetMax
  return axios.get(`${BASE_URL}/search`, { params })
}

// 删除行程
export const deletePlanById = (planId) => {
  return axios.delete(`${BASE_URL}/delete/${planId}`)
}