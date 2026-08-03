import { api } from './api';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthData {
  token: string;
  employeeId: number;
  email: string;
  role: string;
  region: string;
}

export interface AuthResponse {
  success: boolean;
  message: string;
  data: AuthData;
  timestamp: string | null;
}

export const authService = {
  login: async (credentials: LoginRequest): Promise<AuthData> => {
    const response = await api.post<AuthResponse>('/auth/login', credentials);
    return response.data.data;
  },

  createCredential: async (data: any): Promise<void> => {
    await api.post('/auth/create', data);
  },

  changePassword: async (data: any): Promise<void> => {
    await api.post('/auth/change-password', data);
  },

  validateToken: async (token: string): Promise<boolean> => {
    // Assuming backend takes token in Authorization header
    const response = await api.get('/auth/validate', {
      headers: { Authorization: `Bearer ${token}` }
    });
    return response.data.data;
  },

  getMe: async (): Promise<AuthData> => {
    const response = await api.get('/auth/me');
    return response.data.data;
  }
};