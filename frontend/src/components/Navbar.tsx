import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { ShoppingCart, LogOut, User as UserIcon, Search, Package, Sun, Moon, Heart } from 'lucide-react';

const Navbar: React.FC = () => {
  const { user, logout } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const [searchQuery, setSearchQuery] = React.useState('');
  const navigate = useNavigate();

  const handleSearch = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && searchQuery.trim()) {
      navigate(`/?q=${encodeURIComponent(searchQuery.trim())}`);
    }
  };

  return (
    <nav className="navbar-glass px-4 sm:px-8 py-4 flex items-center justify-between transition-colors duration-300">
      <Link to="/" className="flex items-center gap-2.5 text-primary font-bold text-2xl no-underline">
        <Package size={32} />
        <span className="tracking-tight italic">Cartify</span>
      </Link>

      <div className="flex-1 max-w-md mx-4 sm:mx-8 relative group hidden sm:block">
        <input 
          type="text" 
          placeholder="Search products..." 
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          onKeyDown={handleSearch}
          className="w-full bg-zinc-100 dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 rounded-lg py-2 pl-10 pr-4 text-sm text-zinc-900 dark:text-white placeholder-zinc-400 dark:placeholder-zinc-500 focus:border-primary transition-all outline-none"
        />
        <Search size={18} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-zinc-400 dark:text-zinc-500 group-focus-within:text-primary transition-colors" />
      </div>

      <div className="flex items-center gap-4 sm:gap-6">
        <button 
          onClick={toggleTheme}
          className="p-2.5 rounded-md bg-zinc-100 dark:bg-zinc-900 text-zinc-600 dark:text-zinc-400 hover:text-primary transition-all border border-zinc-200 dark:border-zinc-800"
          title={theme === 'dark' ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
        >
          {theme === 'dark' ? <Sun size={20} /> : <Moon size={20} />}
        </button>

        <Link to="/cart" className="relative text-zinc-600 dark:text-zinc-300 hover:text-primary transition-colors group">
          <ShoppingCart size={24} />
          {user && user.cart && user.cart.length > 0 && (
            <span className="absolute -top-2 -right-2 bg-red-600 text-white text-[10px] font-black w-5 h-5 rounded-full flex items-center justify-center shadow-lg border-2 border-white dark:border-zinc-950 animate-bounce">
              {user.cart.reduce((acc, item) => acc + item.quantity, 0)}
            </span>
          )}
        </Link>
        
        <Link to="/wishlist" className="relative text-zinc-600 dark:text-zinc-300 hover:text-primary transition-colors group">
          <Heart size={24} />
          {user && user.wishlist && user.wishlist.length > 0 && (
            <span className="absolute -top-2 -right-2 bg-primary text-white text-[10px] font-black w-5 h-5 rounded-full flex items-center justify-center shadow-lg border-2 border-white dark:border-zinc-950">
              {user.wishlist.length}
            </span>
          )}
        </Link>
        
        {user ? (
          <>
            <div className="h-8 w-px bg-zinc-800" />
            
            {(user.roles.includes('ROLE_ADMIN') || user.roles.includes('ROLE_SELLER')) && (
              <Link to={user.roles.includes('ROLE_ADMIN') ? "/admin" : "/seller"} className="text-sm font-bold text-primary hover:text-primary/70 transition-colors uppercase tracking-widest">
                Dashboard
              </Link>
            )}

            <Link to="/profile" className="flex items-center gap-3 pl-2 group/profile">
              <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center text-primary border border-primary/20 group-hover/profile:border-primary transition-all">
                <UserIcon size={18} />
              </div>
              <div className="flex flex-col hidden lg:flex">
                <span className="text-xs font-black text-zinc-900 dark:text-zinc-200 leading-none group-hover/profile:text-primary transition-colors uppercase tracking-widest">{user.username}</span>
                <span className="text-[8px] font-black text-primary mt-1 uppercase tracking-widest bg-primary/10 px-1.5 py-0.5 rounded-sm w-fit">{user.points || 0} pts</span>
              </div>
            </Link>

            <button onClick={logout} className="p-2.5 rounded-md bg-zinc-100 dark:bg-zinc-900 text-zinc-600 dark:text-zinc-400 hover:text-white dark:hover:text-white transition-all border border-zinc-200 dark:border-zinc-800">
              <LogOut size={20} />
            </button>
          </>
        ) : (
          <div className="flex items-center gap-4">
            <Link to="/login" className="text-zinc-600 dark:text-zinc-300 hover:text-primary dark:hover:text-white font-black uppercase tracking-widest text-[10px] transition-colors">Sign In</Link>
            <Link to="/register" className="premium-btn text-[10px] px-6">Join Now</Link>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
