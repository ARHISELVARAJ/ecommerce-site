import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Cart from './pages/Cart';
import AdminDashboard from './pages/AdminDashboard';
import './index.css';

import Profile from './pages/Profile';
import ProductDetails from './pages/ProductDetails';
import Wishlist from './pages/Wishlist';

import { ThemeProvider } from './context/ThemeContext';

const ProtectedRoute: React.FC<{ children: React.ReactElement; role?: string }> = ({ children, role }) => {
  const { user, loading } = useAuth();
  if (loading) return <div className="min-h-screen bg-black dark:bg-black light:bg-zinc-50 flex items-center justify-center text-zinc-500 font-medium tracking-widest uppercase text-xs">Loading Cartify...</div>;
  if (!user) return <Navigate to="/login" />;
  if (role && !user.roles.includes(role)) return <Navigate to="/" />;
  return children;
};

const AppContextWrapper: React.FC = () => {
  const { loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen bg-white dark:bg-black flex flex-col items-center justify-center gap-6 animate-in fade-in duration-500">
         <div className="relative">
            <div className="w-16 h-16 border-4 border-primary/20 border-t-primary rounded-full animate-spin shadow-[0_0_20px_rgba(249,115,22,0.15)]" />
            <div className="absolute inset-0 flex items-center justify-center font-black text-[10px] text-primary uppercase tracking-tighter">CF</div>
         </div>
         <div className="flex flex-col items-center gap-1">
            <h1 className="text-xl font-black text-zinc-900 dark:text-white tracking-widest uppercase italic">Cartify</h1>
            <p className="text-[10px] font-black text-zinc-400 dark:text-zinc-600 uppercase tracking-[0.4em] animate-pulse">Syncing Session</p>
         </div>
      </div>
    );
  }

  return (
    <Router>
      <div className="min-h-screen bg-zinc-50 dark:bg-zinc-950 text-zinc-900 dark:text-zinc-100 flex flex-col selection:bg-primary/30 selection:text-primary transition-colors duration-500 ease-in-out">
        <Navbar />
        <main className="flex-1">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/product/:id" element={<ProductDetails />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />
            <Route path="/cart" element={<ProtectedRoute><Cart /></ProtectedRoute>} />
            <Route path="/wishlist" element={<ProtectedRoute><Wishlist /></ProtectedRoute>} />
            <Route path="/admin" element={<ProtectedRoute role="ROLE_ADMIN"><AdminDashboard /></ProtectedRoute>} />
            <Route path="/seller" element={<ProtectedRoute role="ROLE_SELLER"><AdminDashboard /></ProtectedRoute>} />
            <Route path="*" element={<Navigate to="/" />} />
          </Routes>
        </main>
        <footer className="py-12 border-t border-zinc-900 bg-zinc-950 mt-24">
           <div className="max-w-7xl mx-auto px-8 flex justify-between items-center text-zinc-500 text-sm font-medium">
              <p>&copy; 2026 Cartify Premium. All Rights Reserved.</p>
              <div className="flex gap-8">
                <a href="#" className="hover:text-primary transition-colors">Privacy</a>
                <a href="#" className="hover:text-primary transition-colors">Terms</a>
                <a href="#" className="hover:text-primary transition-colors">Support</a>
              </div>
           </div>
        </footer>
      </div>
    </Router>
  );
}

const App: React.FC = () => {
  return (
    <ThemeProvider>
      <AuthProvider>
        <AppContextWrapper />
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
