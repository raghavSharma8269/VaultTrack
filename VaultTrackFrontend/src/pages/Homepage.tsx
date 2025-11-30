import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Header from '../components/Header';
import { ExpensePieChart, IncomeExpenseBarChart, BalanceTrendLineChart } from '../components/analytics';
import accountService from '../services/accountService';
import authService from '../services/authService';
import { AccountType } from '../types/account';
import type { Account } from '../types/account';

function Homepage() {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>('');
  const [filterType, setFilterType] = useState<AccountType | ''>('');
  const [selectedAccount, setSelectedAccount] = useState<Account | null>(null);

  useEffect(() => {
    fetchAccounts();
  }, [filterType]);

  const fetchAccounts = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await accountService.getAccounts(
        undefined,
        filterType || undefined
      );
      setAccounts(data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch accounts');
    } finally {
      setLoading(false);
    }
  };

  const getTotalBalance = () => {
    return accounts.reduce((sum, account) => sum + account.currentBalance, 0);
  };

  const getAccountTypeColor = (type: AccountType) => {
    switch (type) {
      case AccountType.SAVINGS:
        return 'bg-green-100 text-green-800';
      case AccountType.CHECKING:
        return 'bg-blue-100 text-blue-800';
      case AccountType.CREDIT:
        return 'bg-red-100 text-red-800';
      case AccountType.INVESTMENT:
        return 'bg-purple-100 text-purple-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const isAuthenticated = authService.isAuthenticated();

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Header />
        <main className="container mx-auto px-4 py-8">
          <div className="text-center py-16">
            <h2 className="text-3xl font-bold text-gray-800 mb-4">
              Welcome to VaultTrack
            </h2>
            <p className="text-gray-600 mb-8">
              Please login or sign up to manage your accounts
            </p>
            <div className="flex gap-4 justify-center">
              <Link to="/login">
                <button className="px-6 py-2 border border-blue-600 text-blue-600 rounded font-medium hover:bg-blue-50 transition-colors">
                  Login
                </button>
              </Link>
              <Link to="/signup">
                <button className="px-6 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors">
                  Sign Up
                </button>
              </Link>
            </div>
          </div>
        </main>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <main className="container mx-auto px-4 py-8">
        <div className="mb-8">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-3xl font-bold text-gray-800">Account Overview</h2>
            <div className="flex gap-3">
              <Link to="/transactions/manage">
                <button className="px-6 py-2 bg-green-600 text-white rounded font-medium hover:bg-green-700 transition-colors">
                  Manage Transactions
                </button>
              </Link>
              <Link to="/accounts/manage">
                <button className="px-6 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors">
                  Manage Accounts
                </button>
              </Link>
            </div>
          </div>

          {/* Total Balance Card */}
          <div className="bg-gradient-to-r from-blue-500 to-blue-600 rounded-lg shadow-lg p-8 text-white mb-6">
            <h3 className="text-lg font-medium opacity-90">Total Balance</h3>
            <p className="text-4xl font-bold mt-2">
              ${getTotalBalance().toFixed(2)}
            </p>
            <p className="text-sm opacity-75 mt-2">
              Across {accounts.length} account{accounts.length !== 1 ? 's' : ''}
            </p>
          </div>

          {/* Filter */}
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Filter by Account Type
            </label>
            <select
              value={filterType}
              onChange={(e) => setFilterType(e.target.value as AccountType | '')}
              className="w-full md:w-64 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">All Accounts</option>
              <option value={AccountType.SAVINGS}>Savings</option>
              <option value={AccountType.CHECKING}>Checking</option>
              <option value={AccountType.CREDIT}>Credit</option>
              <option value={AccountType.INVESTMENT}>Investment</option>
              <option value={AccountType.OTHER}>Other</option>
            </select>
          </div>
        </div>

        {/* Error Message */}
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        {/* Loading State */}
        {loading && (
          <div className="text-center py-8">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            <p className="text-gray-600 mt-4">Loading accounts...</p>
          </div>
        )}

        {/* Accounts Grid */}
        {!loading && accounts.length === 0 && (
          <div className="text-center py-16 bg-white rounded-lg shadow">
            <p className="text-gray-600 mb-4">No accounts found</p>
            <Link to="/accounts/manage">
              <button className="px-6 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors">
                Create Your First Account
              </button>
            </Link>
          </div>
        )}

        {!loading && accounts.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {accounts.map((account) => (
              <div
                key={account.accountId}
                className="bg-white rounded-lg shadow hover:shadow-md transition-shadow p-6 cursor-pointer"
                onClick={() => setSelectedAccount(account)}
              >
                <div className="flex justify-between items-start mb-4">
                  <h3 className="text-xl font-semibold text-gray-800">
                    {account.accountName}
                  </h3>
                  <span
                    className={`px-3 py-1 rounded-full text-xs font-medium ${getAccountTypeColor(
                      account.accountType
                    )}`}
                  >
                    {account.accountType}
                  </span>
                </div>
                <div className="mb-4">
                  <p className="text-sm text-gray-600">Current Balance</p>
                  <p className="text-3xl font-bold text-gray-900">
                    ${account.currentBalance.toFixed(2)}
                  </p>
                </div>
                <div className="text-xs text-gray-500">
                  <p>Created: {new Date(account.createdAt).toLocaleDateString()}</p>
                  <p>Updated: {new Date(account.updatedAt).toLocaleDateString()}</p>
                </div>
                <div className="mt-4 text-center">
                  <span className="text-sm text-blue-600 font-medium">
                    Click to view expense breakdown
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Account Details Modal with Charts */}
        {selectedAccount && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg shadow-xl max-w-6xl w-full max-h-[90vh] overflow-y-auto">
              <div className="p-6">
                <div className="flex justify-between items-start mb-6">
                  <div>
                    <h2 className="text-2xl font-bold text-gray-800">
                      {selectedAccount.accountName}
                    </h2>
                    <span
                      className={`inline-block mt-2 px-3 py-1 rounded-full text-xs font-medium ${getAccountTypeColor(
                        selectedAccount.accountType
                      )}`}
                    >
                      {selectedAccount.accountType}
                    </span>
                  </div>
                  <button
                    onClick={() => setSelectedAccount(null)}
                    className="text-gray-400 hover:text-gray-600 text-2xl font-bold"
                  >
                    &times;
                  </button>
                </div>

                <div className="mb-6 p-4 bg-gray-50 rounded-lg">
                  <p className="text-sm text-gray-600">Current Balance</p>
                  <p className="text-3xl font-bold text-gray-900">
                    ${selectedAccount.currentBalance.toFixed(2)}
                  </p>
                </div>

                <div className="space-y-6">
                  <BalanceTrendLineChart
                    filters={{ accountId: selectedAccount.accountId }}
                    title={`Balance Trend - ${selectedAccount.accountName}`}
                    height={400}
                  />

                  <IncomeExpenseBarChart
                    filters={{ accountId: selectedAccount.accountId }}
                    title={`Monthly Income vs Expenses - ${selectedAccount.accountName}`}
                    height={400}
                  />

                  <ExpensePieChart
                    filters={{ accountId: selectedAccount.accountId }}
                    title={`Expense Breakdown by Category - ${selectedAccount.accountName}`}
                    height={400}
                  />
                </div>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

export default Homepage;
