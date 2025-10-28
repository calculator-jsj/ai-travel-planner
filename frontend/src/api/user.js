// src/api/user.js
import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api'

export function loginApi(data) {
  return axios.post(`${BASE_URL}/user/login`, null, {
    params: data
  })
}

export function registerApi(data) {
  return axios.post(`${BASE_URL}/user/register`, data)
}

export function getUserProfile() {
  return axios.get(`${BASE_URL}/user/preference`, {
    params: {
      userId: localStorage.getItem('userId')
    }
  })
}

export function saveUserPreferences(preferences, travelStyle) {
  const userId = localStorage.getItem('userId')
  return axios.post(`${BASE_URL}/user/preferences/save`, {
    userId,
    preferences,
    travelStyle
  })
}