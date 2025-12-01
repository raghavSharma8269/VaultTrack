import { Link, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import authService from '../services/authService';

function Header() {
  const navigate = useNavigate();
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    // Check authentication status on mount and when localStorage changes
    const checkAuth = () => {
      setIsAuthenticated(authService.isAuthenticated());
      setIsAdmin(authService.getUserRole() === 'ADMIN');
    };

    checkAuth();

    // Listen for storage changes (e.g., login/logout in another tab)
    window.addEventListener('storage', checkAuth);

    // Custom event for same-tab auth changes
    window.addEventListener('authChange', checkAuth);

    return () => {
      window.removeEventListener('storage', checkAuth);
      window.removeEventListener('authChange', checkAuth);
    };
  }, []);

  const handleLogout = () => {
    authService.logout();
    setIsAuthenticated(false);
    setIsAdmin(false);
    window.dispatchEvent(new Event('authChange'));
    navigate('/');
  };

  return (
      <header className="bg-white border-b border-gray-200 shadow-sm">
        <div className="container mx-auto px-4 py-4 flex justify-between items-center">
          <Link to="/">
            <h1 className="text-2xl font-semibold text-blue-600 cursor-pointer">VaultTrack</h1>
          </Link>
          <div className="flex gap-2">
            {isAuthenticated ? (
                <>
                  {isAdmin && (
                      <Link to="/admin/dashboard">
                        <button className="px-6 py-2 bg-purple-600 text-white rounded font-medium hover:bg-purple-700 transition-colors min-w-[100px]">
                          Admin Dashboard
                        </button>
                      </Link>
                  )}
                  <button
                      onClick={handleLogout}
                      className="px-6 py-2 bg-red-600 text-white rounded font-medium hover:bg-red-700 transition-colors min-w-[100px]"
                  >
                    Logout
                  </button>
                </>
            ) : (
                <>
                  <Link to="/login">
                    <button className="px-6 py-2 border border-blue-600 text-blue-600 rounded font-medium hover:bg-blue-50 transition-colors min-w-[100px]">
                      Login
                    </button>
                  </Link>
                  <Link to="/signup">
                    <button className="px-6 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors min-w-[100px]">
                      Sign Up
                    </button>
                  </Link>
                </>
            )}
          </div>
        </div>
      </header>
  );
}

export default Header;