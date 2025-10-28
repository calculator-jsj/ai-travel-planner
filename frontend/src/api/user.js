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
  return axios.get(`${BASE_URL}/preference`, {
    params: {
      userId: localStorage.getItem('userId')
    }
  })
}

export function saveUserPreferences(preferences, travelStyle) {
  const userId = localStorage.getItem('userId')
  return axios.post(`${BASE_URL}/preferences/save`, {
    userId,
    preferences,
    travelStyle
  })
}

