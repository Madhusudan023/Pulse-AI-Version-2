import axios from 'axios';
import { useAuthStore } from '../store/useAuthStore';

// Assuming API Gateway is running on 8080
const API_URL = (import.meta.env.VITE_API_URL || 'http://localhost:8080') + '/api/v1';

export const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token;
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

api.interceptors.response.use((response) => {
  return response;
}, (error) => {
  if (error.response?.status === 401) {
    // Global handle 401 Unauthorized
    useAuthStore.getState().clearAuth();
    window.location.href = '/login';
  }
  return Promise.reject(error);
});
