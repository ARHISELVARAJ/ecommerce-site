import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';
import { User } from 'lucide-react';

const Register: React.FC = () => {
  const [role, setRole] = useState<'buyer' | 'seller'>('buyer');
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    businessName: '',
    phoneNumber: ''
  });
  const [step, setStep] = useState(1); // 1: Details, 2: OTP
  const [otp, setOtp] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { register, sendOTP, verifyOTP, login } = useAuth();
  const navigate = useNavigate();

  const validateEmail = (email: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  const validatePhone = (phone: string) => /^\d{10}$/.test(phone.replace(/\s+/g, ''));

  const handleSendOTP = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    
    // Explicit K6 Validations
    if (formData.username.length < 3) {
      setError('Username must be at least 3 characters');
      return;
    }
    if (!validateEmail(formData.email)) {
      setError('Please enter a valid email address (e.g. user@example.com)');
      return;
    }
    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters for security');
      return;
    }
    if (role === 'seller' && !validatePhone(formData.phoneNumber)) {
      setError('Please enter a valid 10-digit phone number');
      return;
    }

    setLoading(true);
    const res = await sendOTP(formData.email);
    setLoading(false);
    if (res.success) {
      setStep(2);
    } else {
      setError(res.message || 'Failed to send OTP');
    }
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    
    if (otp.length !== 6 || !/^\d+$/.test(otp)) {
      setError('OTP must be 6 digits');
      return;
    }

    setLoading(true);
    const verifyRes = await verifyOTP(formData.email, otp);
    if (verifyRes.success) {
      const endpoint = role === 'buyer' ? '/register/buyer' : '/register/seller';
      const result = await register(formData, endpoint);
      if (result.success) {
        const loginRes = await login(formData.username, formData.password);
        if (loginRes.success) {
          navigate('/');
        } else {
          navigate('/login');
        }
      } else {
        // Handle global conflict errors from the backend
        const msg = typeof result.message === 'string' ? result.message : 'Registration failed. This username/email might be taken.';
        setError(msg);
      }
    } else {
      setError('Invalid or expired OTP');
    }
    setLoading(false);
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-[90vh] px-8 py-12 transition-colors duration-300">
      <div className="glass-card fade-in w-full max-w-lg bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 p-8 rounded-2xl shadow-2xl relative overflow-hidden">
        <div className="absolute top-0 right-0 w-32 h-32 bg-primary/5 rounded-full -mr-16 -mt-16 blur-3xl" />
        
        <div className="flex flex-col items-center mb-10 relative z-10">
          <div className="w-16 h-16 rounded-md bg-primary/10 flex items-center justify-center text-primary mb-6 border border-primary/20">
            <User size={32} />
          </div>
          <h2 className="text-3xl font-black text-zinc-900 dark:text-white tracking-tight italic uppercase">Cartify</h2>
          <p className="text-zinc-500 mt-2 font-black uppercase tracking-[0.2em] text-[10px]">Join our community</p>
        </div>

        {error && (
          <div className="bg-red-500/10 border border-red-500/20 text-red-500 text-sm py-3 px-4 rounded-md mb-6 text-center font-medium relative z-10">
            {error}
          </div>
        )}

        {step === 1 ? (
          <form onSubmit={handleSendOTP} className="space-y-6 relative z-10">
            <div className="flex bg-zinc-100 dark:bg-zinc-950 p-1.5 rounded-xl border border-zinc-200 dark:border-zinc-800 mb-8">
              <button 
                type="button"
                onClick={() => setRole('buyer')}
                className={`flex-1 py-2.5 text-[10px] font-black uppercase tracking-widest rounded-lg transition-all ${role === 'buyer' ? 'bg-primary text-black shadow-lg shadow-primary/20' : 'text-zinc-400 dark:text-zinc-500 hover:text-zinc-900 dark:hover:text-white'}`}
              >
                Buyer
              </button>
              <button 
                type="button"
                onClick={() => setRole('seller')}
                className={`flex-1 py-2.5 text-[10px] font-black uppercase tracking-widest rounded-lg transition-all ${role === 'seller' ? 'bg-primary text-black shadow-lg shadow-primary/20' : 'text-zinc-400 dark:text-zinc-500 hover:text-zinc-900 dark:hover:text-white'}`}
              >
                Seller
              </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-1.5">
                <label className="text-[10px] font-black uppercase tracking-[0.2em] text-zinc-400 dark:text-zinc-500 ml-1">Username</label>
                <input 
                  type="text" 
                  className="input-field" 
                  placeholder="arhi"
                  value={formData.username} 
                  onChange={(e) => setFormData({...formData, username: e.target.value})} 
                  required 
                />
              </div>
              <div className="space-y-1.5">
                <label className="text-[10px] font-black uppercase tracking-[0.2em] text-zinc-400 dark:text-zinc-500 ml-1">Email Address</label>
                <input 
                  type="email" 
                  className="input-field" 
                  placeholder="user@example.com"
                  value={formData.email} 
                  onChange={(e) => setFormData({...formData, email: e.target.value})} 
                  required 
                />
              </div>
            </div>

            <div className="space-y-1.5">
              <label className="text-[10px] font-black uppercase tracking-[0.2em] text-zinc-400 dark:text-zinc-500 ml-1">Password</label>
              <input 
                type="password" 
                className="input-field" 
                placeholder="••••••"
                value={formData.password} 
                onChange={(e) => setFormData({...formData, password: e.target.value})} 
                required 
              />
            </div>

            {role === 'seller' && (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pt-4 border-t border-zinc-100 dark:border-zinc-800">
                <div className="space-y-1.5">
                  <label className="text-[10px] font-black uppercase tracking-[0.2em] text-zinc-400 dark:text-zinc-500 ml-1">Business Name</label>
                  <input 
                    type="text" 
                    className="input-field" 
                    placeholder="Tech Corp"
                    value={formData.businessName} 
                    onChange={(e) => setFormData({...formData, businessName: e.target.value})} 
                    required={role === 'seller'} 
                  />
                </div>
                <div className="space-y-1.5">
                  <label className="text-[10px] font-black uppercase tracking-[0.2em] text-zinc-400 dark:text-zinc-500 ml-1">Phone Number</label>
                  <input 
                    type="text" 
                    className="input-field" 
                    placeholder="+91 9876543210"
                    value={formData.phoneNumber} 
                    onChange={(e) => setFormData({...formData, phoneNumber: e.target.value})} 
                    required={role === 'seller'} 
                  />
                </div>
              </div>
            )}

            <button type="submit" disabled={loading} className="premium-btn w-full mt-4 py-4 text-xs tracking-[0.3em] font-black uppercase">
              {loading ? 'Sending OTP...' : 'Verify Email'}
            </button>
          </form>
        ) : (
          <form onSubmit={handleRegister} className="space-y-6 relative z-10">
            <div className="space-y-1.5">
              <label className="text-[10px] font-black uppercase tracking-[0.2em] text-zinc-400 dark:text-zinc-500 ml-1">Enter 6-Digit OTP SENT TO YOUR MAIL</label>
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
                Check your email address for the OTP code.<br/>
                <span className="text-zinc-300 dark:text-zinc-600 text-[8px] italic mt-1 block">Sent to {formData.email}</span>
              </p>
            </div>
            <button type="submit" disabled={loading} className="premium-btn w-full mt-4 py-4 text-xs tracking-[0.3em] font-black uppercase">
              {loading ? 'Registering...' : 'Create Account'}
            </button>
            <button type="button" onClick={() => setStep(1)} className="text-zinc-400 dark:text-zinc-600 text-[10px] font-black uppercase tracking-widest w-full hover:text-primary transition-colors text-center mt-2">Back to Details</button>
          </form>
        )}

        <p className="mt-10 text-center text-zinc-500 text-[10px] font-black uppercase tracking-widest relative z-10">
          Already have an account? <Link to="/login" className="text-primary hover:underline ml-1">Sign In</Link>
        </p>
      </div>
    </div>
  );
};

export default Register;
