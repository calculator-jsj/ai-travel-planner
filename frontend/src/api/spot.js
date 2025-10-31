import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api/spot'

// 按 planId 查询景点列表
export const getSpotsByPlanId = (planId) => {
  return axios.get(`${BASE_URL}/list/${planId}`)
}
