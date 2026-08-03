import { create } from 'zustand';

interface AuthState {
  token: string | null;
  employeeId: number | null;
  email: string | null;
  role: string | null;
  region: string | null;
  setAuth: (data: Partial<AuthState>) => void;
  clearAuth: () => void;
  isAuthenticated: () => boolean;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  token: localStorage.getItem('token'),
  employeeId: localStorage.getItem('employeeId') ? Number(localStorage.getItem('employeeId')) : null,
  email: localStorage.getItem('email'),
  role: localStorage.getItem('role'),
  region: localStorage.getItem('region'),

  setAuth: (data) => {
    if (data.token) localStorage.setItem('token', data.token);
    if (data.employeeId) localStorage.setItem('employeeId', data.employeeId.toString());
    if (data.email) localStorage.setItem('email', data.email);
    if (data.role) localStorage.setItem('role', data.role);
    if (data.region) localStorage.setItem('region', data.region);
    
    set((state) => ({ ...state, ...data }));
  },

  clearAuth: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('employeeId');
    localStorage.removeItem('email');
    localStorage.removeItem('role');
    localStorage.removeItem('region');
    
    set({ token: null, employeeId: null, email: null, role: null, region: null });
  },

  isAuthenticated: () => {
    return !!get().token;
  },
}));
