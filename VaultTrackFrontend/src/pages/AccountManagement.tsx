import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Header from '../components/Header';
import accountService from '../services/accountService';
import budgetService from '../services/budgetService';
import { AccountType } from '../types/account';
import type { Account, CreateAccountData, UpdateAccountData } from '../types/account';
import type { CreateBudgetData, UpdateBudgetData, Budget } from '../types/budget';

function AccountManagement() {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [budgets, setBudgets] = useState<Map<string, Budget>>(new Map());
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>('');
  const [successMessage, setSuccessMessage] = useState<string>('');

  // Modal states
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [showBudgetModal, setShowBudgetModal] = useState(false);
  const [selectedAccount, setSelectedAccount] = useState<Account | null>(null);
  const [selectedBudget, setSelectedBudget] = useState<Budget | null>(null);

  // Form states
  const [formData, setFormData] = useState<CreateAccountData>({
    accountName: '',
    accountType: AccountType.CHECKING,
  });

  // Budget form states
  const [budgetFormData, setBudgetFormData] = useState<CreateBudgetData>({
    budgetAmount: 0,
    alertThreshold: 80,
    accountId: '',
  });

  useEffect(() => {
    fetchAccounts();
  }, []);

  const fetchAccounts = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await accountService.getAccounts();
      setAccounts(data);

      // Fetch budgets for all accounts
      const budgetMap = new Map<string, Budget>();
      await Promise.all(
        data.map(async (account) => {
          const budget = await budgetService.getBudgetByAccount(account.accountId);
          if (budget) {
            budgetMap.set(account.accountId, budget);
          }
        })
      );
      setBudgets(budgetMap);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch accounts');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setError('');
      const message = await accountService.createAccount(formData);
      setSuccessMessage(message);
      setShowCreateModal(false);
      setFormData({ accountName: '', accountType: AccountType.CHECKING });
      fetchAccounts();
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create account');
    }
  };

  const handleUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedAccount) return;

    try {
      setError('');
      const updateData: UpdateAccountData = {
        accountId: selectedAccount.accountId,
        accountName: formData.accountName,
        accountType: formData.accountType,
      };
      const message = await accountService.updateAccount(updateData);
      setSuccessMessage(message);
      setShowEditModal(false);
      setSelectedAccount(null);
      setFormData({ accountName: '', accountType: AccountType.CHECKING });
      fetchAccounts();
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update account');
    }
  };

  const handleDelete = async () => {
    if (!selectedAccount) return;

    try {
      setError('');
      const message = await accountService.deleteAccount(selectedAccount.accountId);
      setSuccessMessage(message);
      setShowDeleteModal(false);
      setSelectedAccount(null);
      fetchAccounts();
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete account');
    }
  };

  const handleBudgetSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedAccount) return;

    try {
      setError('');
      let message: string;

      if (selectedBudget) {
        // Update existing budget
        const updateData: UpdateBudgetData = {
          budgetId: selectedBudget.budgetId,
          budgetAmount: budgetFormData.budgetAmount,
          alertThreshold: budgetFormData.alertThreshold,
          isActive: true,
        };
        message = await budgetService.updateBudget(updateData);
      } else {
        // Create new budget
        message = await budgetService.createBudget(budgetFormData);
      }

      setSuccessMessage(message);
      setShowBudgetModal(false);
      setSelectedAccount(null);
      setSelectedBudget(null);
      resetBudgetForm();

      // Refresh the budget data to show updated values
      await fetchAccounts();

      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err: any) {
      // Extract error message properly from response
      let errorMessage = 'Failed to save budget';
      if (err.response?.data) {
        if (typeof err.response.data === 'string') {
          errorMessage = err.response.data;
        } else if (err.response.data.message) {
          errorMessage = err.response.data.message;
        } else if (err.response.data.error) {
          errorMessage = err.response.data.error;
        }
      } else if (err.message) {
        errorMessage = err.message;
      }

      // Handle duplicate budget error specifically
      if (errorMessage.includes('duplicate key') || errorMessage.includes('already exists')) {
        errorMessage = 'This account already has a budget. Please update the existing budget instead of creating a new one.';
      }

      setError(errorMessage);
    }
  };

  const openBudgetModal = async (account: Account) => {
    setSelectedAccount(account);
    setError('');

    try {
      // Fetch existing budget for this account
      const existingBudget = await budgetService.getBudgetByAccount(account.accountId);

      if (existingBudget) {
        // Budget exists - populate form for update
        setSelectedBudget(existingBudget);
        setBudgetFormData({
          budgetAmount: existingBudget.budgetAmount,
          alertThreshold: existingBudget.alertThreshold,
          accountId: account.accountId,
        });
      } else {
        // No budget exists - prepare for creation
        setSelectedBudget(null);
        setBudgetFormData({
          budgetAmount: 0,
          alertThreshold: 80,
          accountId: account.accountId,
        });
      }

      setShowBudgetModal(true);
    } catch (err: any) {
      setError('Failed to load budget information');
    }
  };

  const resetBudgetForm = () => {
    setBudgetFormData({
      budgetAmount: 0,
      alertThreshold: 80,
      accountId: '',
    });
  };

  const openEditModal = (account: Account) => {
    setSelectedAccount(account);
    setFormData({
      accountName: account.accountName,
      accountType: account.accountType,
    });
    setShowEditModal(true);
  };

  const openDeleteModal = (account: Account) => {
    setSelectedAccount(account);
    setShowDeleteModal(true);
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

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <main className="container mx-auto px-4 py-8">
        <div className="mb-8">
          <div className="flex justify-between items-center mb-6">
            <div>
              <h2 className="text-3xl font-bold text-gray-800">Account Management</h2>
              <Link to="/" className="text-blue-600 hover:text-blue-700 text-sm">
                Back to Dashboard
              </Link>
            </div>
            <button
              onClick={() => {
                setFormData({ accountName: '', accountType: AccountType.CHECKING });
                setShowCreateModal(true);
              }}
              className="px-6 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors"
            >
              Create New Account
            </button>
          </div>
        </div>

        {/* Success Message */}
        {successMessage && (
          <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded mb-4">
            {successMessage}
          </div>
        )}

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

        {/* Accounts Table */}
        {!loading && accounts.length === 0 && (
          <div className="text-center py-16 bg-white rounded-lg shadow">
            <p className="text-gray-600 mb-4">No accounts found</p>
            <button
              onClick={() => setShowCreateModal(true)}
              className="px-6 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors"
            >
              Create Your First Account
            </button>
          </div>
        )}

        {!loading && accounts.length > 0 && (
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Account Name
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Type
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Balance
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Budget
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Created
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {accounts.map((account) => (
                  <tr key={account.accountId}>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">{account.accountName}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-3 py-1 rounded-full text-xs font-medium ${getAccountTypeColor(account.accountType)}`}>
                        {account.accountType}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">${account.currentBalance.toFixed(2)}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {(() => {
                        const budget = budgets.get(account.accountId);
                        if (budget && budget.budgetAmount != null) {
                          return (
                            <div>
                              <div className="text-sm font-medium text-gray-900">
                                ${Number(budget.budgetAmount).toFixed(2)}/mo
                              </div>
                              <div className="text-xs text-gray-500">
                                Spent: ${Number(budget.currentSpent || 0).toFixed(2)}
                              </div>
                            </div>
                          );
                        }
                        return <span className="text-xs text-gray-400">No budget</span>;
                      })()}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-500">
                        {new Date(account.createdAt).toLocaleDateString()}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <button
                        onClick={() => openBudgetModal(account)}
                        className="text-green-600 hover:text-green-900 mr-4"
                      >
                        Set Budget
                      </button>
                      <button
                        onClick={() => openEditModal(account)}
                        className="text-blue-600 hover:text-blue-900 mr-4"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => openDeleteModal(account)}
                        className="text-red-600 hover:text-red-900"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </main>

      {/* Create Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-md w-full p-6">
            <h3 className="text-xl font-bold text-gray-800 mb-4">Create New Account</h3>
            <form onSubmit={handleCreate}>
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Account Name
                </label>
                <input
                  type="text"
                  required
                  value={formData.accountName}
                  onChange={(e) => setFormData({ ...formData, accountName: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="e.g., My Savings Account"
                />
              </div>
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Account Type
                </label>
                <select
                  value={formData.accountType}
                  onChange={(e) => setFormData({ ...formData, accountType: e.target.value as AccountType })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value={AccountType.SAVINGS}>Savings</option>
                  <option value={AccountType.CHECKING}>Checking</option>
                  <option value={AccountType.CREDIT}>Credit</option>
                  <option value={AccountType.INVESTMENT}>Investment</option>
                  <option value={AccountType.OTHER}>Other</option>
                </select>
              </div>
              <div className="flex gap-3">
                <button
                  type="button"
                  onClick={() => {
                    setShowCreateModal(false);
                    setFormData({ accountName: '', accountType: AccountType.CHECKING });
                  }}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded font-medium hover:bg-gray-50 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 px-4 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors"
                >
                  Create
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Edit Modal */}
      {showEditModal && selectedAccount && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-md w-full p-6">
            <h3 className="text-xl font-bold text-gray-800 mb-4">Edit Account</h3>
            <form onSubmit={handleUpdate}>
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Account Name
                </label>
                <input
                  type="text"
                  required
                  value={formData.accountName}
                  onChange={(e) => setFormData({ ...formData, accountName: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Account Type
                </label>
                <select
                  value={formData.accountType}
                  onChange={(e) => setFormData({ ...formData, accountType: e.target.value as AccountType })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value={AccountType.SAVINGS}>Savings</option>
                  <option value={AccountType.CHECKING}>Checking</option>
                  <option value={AccountType.CREDIT}>Credit</option>
                  <option value={AccountType.INVESTMENT}>Investment</option>
                  <option value={AccountType.OTHER}>Other</option>
                </select>
              </div>
              <div className="flex gap-3">
                <button
                  type="button"
                  onClick={() => {
                    setShowEditModal(false);
                    setSelectedAccount(null);
                    setFormData({ accountName: '', accountType: AccountType.CHECKING });
                  }}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded font-medium hover:bg-gray-50 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 px-4 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors"
                >
                  Update
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {showDeleteModal && selectedAccount && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-md w-full p-6">
            <h3 className="text-xl font-bold text-gray-800 mb-4">Delete Account</h3>
            <p className="text-gray-600 mb-6">
              Are you sure you want to delete <strong>{selectedAccount.accountName}</strong>? This action cannot be undone.
            </p>
            <div className="flex gap-3">
              <button
                onClick={() => {
                  setShowDeleteModal(false);
                  setSelectedAccount(null);
                }}
                className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded font-medium hover:bg-gray-50 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleDelete}
                className="flex-1 px-4 py-2 bg-red-600 text-white rounded font-medium hover:bg-red-700 transition-colors"
              >
                Delete
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Budget Modal */}
      {showBudgetModal && selectedAccount && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-md w-full p-6">
            <h3 className="text-xl font-bold text-gray-800 mb-4">
              {selectedBudget ? 'Update' : 'Set'} Budget for {selectedAccount.accountName}
            </h3>
            {selectedBudget && (
              <div className="mb-4 p-3 bg-green-50 border border-green-200 rounded-lg">
                <p className="text-sm text-green-800">
                  ✓ Existing budget found. Updating budget values.
                </p>
              </div>
            )}
            {!selectedBudget && (
              <div className="mb-4 p-3 bg-blue-50 border border-blue-200 rounded-lg">
                <p className="text-sm text-blue-800">
                  Creating a new budget for this account.
                </p>
              </div>
            )}
            <form onSubmit={handleBudgetSubmit}>
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Monthly Budget Amount ($)
                </label>
                <input
                  type="number"
                  step="0.01"
                  min="0"
                  required
                  value={budgetFormData.budgetAmount || ''}
                  onChange={(e) => setBudgetFormData({ ...budgetFormData, budgetAmount: parseFloat(e.target.value) || 0 })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="e.g., 1000.00"
                />
                <p className="text-xs text-gray-500 mt-1">
                  Set a spending limit for this account (monthly period)
                </p>
              </div>
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Alert Threshold (%)
                </label>
                <input
                  type="number"
                  min="0"
                  max="100"
                  required
                  value={budgetFormData.alertThreshold || ''}
                  onChange={(e) => setBudgetFormData({ ...budgetFormData, alertThreshold: parseInt(e.target.value) || 80 })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="80"
                />
                <p className="text-xs text-gray-500 mt-1">
                  You'll be alerted when spending reaches this percentage of your budget
                </p>
              </div>
              <div className="flex gap-3">
                <button
                  type="button"
                  onClick={() => {
                    setShowBudgetModal(false);
                    setSelectedAccount(null);
                    setSelectedBudget(null);
                    resetBudgetForm();
                  }}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded font-medium hover:bg-gray-50 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 px-4 py-2 bg-green-600 text-white rounded font-medium hover:bg-green-700 transition-colors"
                >
                  {selectedBudget ? 'Update' : 'Set'} Budget
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default AccountManagement;
