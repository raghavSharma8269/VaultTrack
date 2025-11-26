import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Homepage from './pages/Homepage';
import Login from './pages/Login';
import Signup from './pages/Signup';
import AccountManagement from './pages/AccountManagement';
import TransactionManagement from './pages/TransactionManagement';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Homepage />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/accounts/manage" element={<AccountManagement />} />
        <Route path="/transactions/manage" element={<TransactionManagement />} />
      </Routes>
    </Router>
  );
}

export default App;
