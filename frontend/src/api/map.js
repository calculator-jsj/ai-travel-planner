import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api/map'

export function getNearby(lng, lat, type = 'restaurant') {
  return axios.get(`${BASE_URL}/nearby`, {
    params: { lng, lat, type }
  })
}