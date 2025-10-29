import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api/plan'

// 保存行程
export const savePlan = (payload) => {
  return axios.post(`${BASE_URL}/save`, payload)
}