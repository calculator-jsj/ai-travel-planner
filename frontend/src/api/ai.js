import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api'

// 调用后端AI行程生成接口
export function generateAIPlan(data) {
  return axios.post(`${BASE_URL}/ai/generate`, data)
}

/**
 * AI预算分析
 * @param {Object} data - 包含行程信息和支出记录的payload
 * @returns {Promise}
 */
export function analyzeBudget(data) {
  return axios.post(`${BASE_URL}/ai/budget/analyze`, data)
}