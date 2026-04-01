import React, { createContext, useState, useContext, useEffect, ReactNode } from 'react';
import axios from 'axios';
import { User } from '../types';

interface AuthContextType {
  user: User | null;
  login: (username: string, password: string) => Promise<{ success: boolean; message?: string }>;
  register: (userData: any, endpoint?: string) => Promise<{ success: boolean; message?: string }>;
  logout: () => void;
  loading: boolean;
  token: string | null;
  sendOTP: (email: string) => Promise<{ success: boolean; message?: string }>;
  verifyOTP: (email: string, code: string) => Promise<{ success: boolean; message?: string }>;
  getEmailByUsername: (username: string) => Promise<{ success: boolean; email?: string; message?: string }>;
  refreshUser: () => Promise<void>;
}

// Use relative path for unified hosting
const API_BASE_URL = '/api';

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
};

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      const savedUser = localStorage.getItem('user');
      const savedToken = localStorage.getItem('token');
      
      if (savedUser && savedToken) {
        try {
          const parsedUser = JSON.parse(savedUser);
          setToken(savedToken);
          axios.defaults.headers.common['Authorization'] = `Bearer ${savedToken}`;
          
          const res = await axios.get(`${API_BASE_URL}/auth/profile/${parsedUser.username}`);
          setUser(res.data);
          localStorage.setItem('user', JSON.stringify(res.data));
        } catch (err) {
          console.error("Auth session expired", err);
          logout();
        }
      }
      setLoading(false);
    };
    initAuth();
  }, []);

  const login = async (username: string, password: string) => {
    try {
      const response = await axios.post(`${API_BASE_URL}/auth/login`, { username, password });
      const { token, ...userData } = response.data;
      
      setUser(userData as User);
      setToken(token);
      localStorage.setItem('user', JSON.stringify(userData));
      localStorage.setItem('token', token);
      
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      
      return { success: true };
    } catch (error: any) {
      return { success: false, message: error.response?.data?.message || 'Login failed' };
    }
  };

  const register = async (userData: any, endpoint: string = '/register') => {
    try {
      await axios.post(`${API_BASE_URL}/auth${endpoint}`, userData);
      return { success: true };
    } catch (error: any) {
      return { success: false, message: error.response?.data || 'Registration failed' };
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    delete axios.defaults.headers.common['Authorization'];
  };

  const sendOTP = async (email: string) => {
    try {
      await axios.post(`${API_BASE_URL}/auth/send-otp?email=${email}`);
      return { success: true };
    } catch (err) {
      return { success: false, message: 'Failed to send OTP' };
    }
  };

  const verifyOTP = async (email: string, code: string) => {
    try {
      await axios.post(`${API_BASE_URL}/auth/verify-otp?email=${email}&code=${code}`);
      return { success: true };
    } catch (err) {
      return { success: false, message: 'Invalid OTP' };
    }
  };

  const getEmailByUsername = async (username: string) => {
    try {
      const res = await axios.get(`${API_BASE_URL}/auth/email/${username}`);
      return { success: true, email: res.data.email };
    } catch (err) {
      return { success: false, message: 'User not found' };
    }
  };

  const refreshUser = async () => {
    if (user) {
      try {
        const res = await axios.get(`${API_BASE_URL}/auth/profile/${user.username}`);
        setUser(res.data);
        localStorage.setItem('user', JSON.stringify(res.data));
      } catch (err) {
        console.error("Failed to refresh user", err);
      }
    }
  };

  return (
    <AuthContext.Provider value={{ user, token, login, register, logout, loading, sendOTP, verifyOTP, getEmailByUsername, refreshUser }}>
        {children}
    </AuthContext.Provider>
  );
};
