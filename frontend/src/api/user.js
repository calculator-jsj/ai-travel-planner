// src/api/user.js
import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api/user'

export function loginApi(data) {
  return axios.post(`${BASE_URL}/login`, null, {
    params: data
  })
}

export function registerApi(data) {
  return axios.post(`${BASE_URL}/register`, data)
}

export function getUserProfile() {
  return axios.get(`${BASE_URL}/profile`)
}
