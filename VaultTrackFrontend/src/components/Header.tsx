import { Link } from 'react-router-dom';

function Header() {
  return (
    <header className="bg-white border-b border-gray-200 shadow-sm">
      <div className="container mx-auto px-4 py-4 flex justify-between items-center">
        <h1 className="text-2xl font-semibold text-blue-600">VaultTrack</h1>
        <div className="flex gap-2">
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
        </div>
      </div>
    </header>
  );
}

export default Header;
