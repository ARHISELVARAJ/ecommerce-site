import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';
import { LogIn } from 'lucide-react';

const Login: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [step, setStep] = useState(1); // 1: Login, 2: OTP
  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { login, sendOTP, verifyOTP, getEmailByUsername } = useAuth();
  const navigate = useNavigate();

  const handleStep1 = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    
    let email = username;
    if (!username.includes('@')) {
      const res = await getEmailByUsername(username);
      if (res.success && res.email) {
        email = res.email;
      } else {
        setError(res.message || 'User not found');
        setLoading(false);
        return;
      }
    }

    const otpRes = await sendOTP(email);
    setLoading(false);
    if (otpRes.success) {
      setStep(2);
    } else {
      setError(otpRes.message || 'Failed to send OTP');
    }
  };

  const handleFinalLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    
    let email = username;
    if (!username.includes('@')) {
      const res = await getEmailByUsername(username);
      if (res.success && res.email) {
        email = res.email;
      }
    }

    const verifyRes = await verifyOTP(email, otp);
    if (verifyRes.success) {
      const result = await login(username, password);
      if (result.success) {
        navigate('/');
      } else {
        setError(result.message || 'Login failed');
      }
    } else {
      setError('Invalid or expired OTP');
    }
    setLoading(false);
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-[85vh] px-8 py-12 transition-colors duration-300">
      <div className="glass-card fade-in w-full max-w-md p-8 border border-zinc-200 dark:border-zinc-800 rounded-2xl shadow-2xl bg-white dark:bg-zinc-900">
        <div className="flex flex-col items-center mb-10">
          <div className="w-16 h-16 rounded-md bg-primary/10 flex items-center justify-center text-primary mb-6 border border-primary/20">
            <LogIn size={32} />
          </div>
          <h2 className="text-3xl font-black text-zinc-900 dark:text-white tracking-tight italic">Cartify</h2>
          <p className="text-zinc-500 mt-2 text-center uppercase tracking-widest text-[10px] font-black">Secure Access</p>
        </div>

        {error && (
          <div className="bg-red-500/10 border border-red-500/20 text-red-500 text-sm py-3 px-4 rounded-md mb-6 text-center font-medium">
            {error}
          </div>
        )}

        {step === 1 ? (
          <form onSubmit={handleStep1} className="space-y-6">
            <div className="space-y-1.5">
              <label className="text-[10px] font-black uppercase tracking-[0.2em] text-zinc-400 dark:text-zinc-500 ml-1">Username / Email</label>
              <input 
                type="text" 
                className="input-field" 
                placeholder="arhi"
                value={username} 
                onChange={(e) => setUsername(e.target.value)} 
                required 
              />
            </div>
            <div className="space-y-1.5">
              <label className="text-[10px] font-black uppercase tracking-[0.2em] text-zinc-400 dark:text-zinc-500 ml-1">Password</label>
              <input 
                type="password" 
                className="input-field" 
                placeholder="••••••••"
                value={password} 
                onChange={(e) => setPassword(e.target.value)} 
                required 
              />
            </div>
            <button type="submit" disabled={loading} className="premium-btn w-full mt-4 py-4 text-xs tracking-[0.3em] font-black uppercase">
              {loading ? 'Processing...' : 'Continue'}
            </button>
          </form>
        ) : (
          <form onSubmit={handleFinalLogin} className="space-y-6">
             <div className="space-y-1.5">
              <label className="text-[10px] font-black uppercase tracking-[0.2em] text-zinc-400 dark:text-zinc-500 ml-1">Enter OTP SENT TO YOUR MAIL</label>
              <input 
                type="text" 
                className="input-field text-center tracking-[1em] text-2xl font-black focus:border-primary transition-all pr-0 shadow-inner" 
                placeholder="000000"
                maxLength={6}
                value={otp} 
                onChange={(e) => setOtp(e.target.value)} 
                required 
              />
              <p className="text-[10px] text-zinc-500 text-center mt-6 uppercase tracking-widest leading-relaxed">
                A 6-digit code has been sent to your email address.
              </p>
            </div>
            <button type="submit" disabled={loading} className="premium-btn w-full mt-4 py-4 text-xs tracking-[0.3em] font-black uppercase">
              {loading ? 'Verifying...' : 'Sign In'}
            </button>
            <button type="button" onClick={() => setStep(1)} className="text-zinc-400 dark:text-zinc-600 text-[10px] font-black uppercase tracking-widest w-full hover:text-primary transition-colors mt-2 text-center">Back</button>
          </form>
        )}

        <p className="mt-10 text-center text-zinc-500 text-[10px] font-black uppercase tracking-widest">
          New to Cartify? <Link to="/register" className="text-primary hover:underline ml-1">Create Account</Link>
        </p>
      </div>
    </div>
  );
};

export default Login;
