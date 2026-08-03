import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { authService } from '../services/auth.service';
import { useAuthStore } from '../store/useAuthStore';
import { jwtDecode } from 'jwt-decode';
import { toast } from 'react-toastify';
import { motion } from 'framer-motion';

const loginSchema = z.object({
  email: z.string().email('Invalid email address'),
  password: z.string().min(1, 'Password is required'),
});

type LoginFormValues = z.infer<typeof loginSchema>;

interface JwtPayload {
  sub: string;
  employeeId: number;
  role: string;
  region: string;
}

export const Login = () => {
  const [isLoading, setIsLoading] = useState(false);
  const setAuth = useAuthStore((state) => state.setAuth);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormValues) => {
    setIsLoading(true);
    try {
      const response = await authService.login(data);
      const decoded = jwtDecode<JwtPayload>(response.token);
      
      setAuth({
        token: response.token,
        employeeId: decoded.employeeId,
        email: decoded.sub,
        role: decoded.role,
        region: decoded.region,
      });
      
      toast.success('Successfully logged in!');
      // Routing is automatically handled by ProtectedRoute / PublicRoute redirecting if authenticated
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Login failed. Please check your credentials.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="bg-white rounded-xl p-8 w-full border border-gray-200 shadow-sm">
      <div className="text-center mb-8">
        <div className="mx-auto mb-6 flex items-center justify-center">
          <img src="/Virtusa-Neural-Hackathon-2025-Software-Developer-Interview-Prep.webp" alt="Pulse AI Logo" className="h-12 w-auto object-contain" />
        </div>
        <h2 className="text-3xl font-bold text-gray-900 tracking-tight">Welcome to Pulse AI</h2>
        <p className="text-sm text-gray-600 mt-2">Sign in to your account to continue</p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        <div>
          <label htmlFor="loginEmail" className="block text-sm font-medium text-gray-700 mb-1.5">Email Address</label>
          <input
            id="loginEmail"
            autoComplete="username"
            {...register('email')}
            type="email"
            placeholder="name@company.com"
            className="w-full glass-input px-4 py-2.5 outline-none transition-all"
          />
          {errors.email && <p className="text-red-400 text-xs mt-1.5">{errors.email.message}</p>}
        </div>

        <div>
          <div className="flex items-center justify-between mb-1.5">
            <label htmlFor="loginPassword" className="block text-sm font-medium text-gray-700">Password</label>
            {/* <a href="#" className="text-xs text-primary hover:text-purple-400 transition-colors">Forgot password?</a> */}
          </div>
          <input
            id="loginPassword"
            autoComplete="current-password"
            {...register('password')}
            type="password"
            placeholder="••••••••"
            className="w-full glass-input px-4 py-2.5 outline-none transition-all"
          />
          {errors.password && <p className="text-red-400 text-xs mt-1.5">{errors.password.message}</p>}
        </div>

        <button
          type="submit"
          disabled={isLoading}
          className="w-full glass-button py-2.5 rounded-lg flex items-center justify-center space-x-2 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isLoading ? (
            <>
              <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <span>Signing In...</span>
            </>
          ) : (
            <span>Sign In</span>
          )}
        </button>
      </form>
      
      <div className="mt-8 pt-6 border-t border-white/10 text-center">
        <p className="text-xs text-gray-500">
          Pulse AI is for authorized employees only.
        </p>
      </div>
    </div>
  );
};

