import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api/budget'

// 新增预算支出
export const addBudget = (data) => {
  return axios.post(`${BASE_URL}/add`, data)
}

// 根据行程ID查询预算支出
export const getBudgetByPlanId = (planId) => {
  return axios.get(`${BASE_URL}/list/${planId}`)
}

// 删除预算支出
export const deleteBudget = (id) => {
  return axios.delete(`${BASE_URL}/delete/${id}`)
}
