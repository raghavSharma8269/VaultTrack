import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Header from '../components/Header';
import accountService from '../services/accountService';
import transactionService from '../services/transactionService';
import type { Account } from '../types/account';
import { TransactionType, TransactionCategory } from '../types/transaction';
import type { CreateTransactionData } from '../types/transaction';

function TransactionManagement() {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>('');
  const [successMessage, setSuccessMessage] = useState<string>('');

  // Modal states
  const [showAddMoneyModal, setShowAddMoneyModal] = useState(false);
  const [showRemoveMoneyModal, setShowRemoveMoneyModal] = useState(false);
  const [showUploadCSVModal, setShowUploadCSVModal] = useState(false);

  // Form states
  const [selectedAccountId, setSelectedAccountId] = useState<string>('');
  const [amount, setAmount] = useState<string>('');
  const [transactionName, setTransactionName] = useState<string>('');
  const [category, setCategory] = useState<TransactionCategory>(TransactionCategory.MISCELLANEOUS);
  const [csvFile, setCsvFile] = useState<File | null>(null);
  const [csvAccountId, setCsvAccountId] = useState<string>('');

  useEffect(() => {
    fetchAccounts();
  }, []);

  const fetchAccounts = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await accountService.getAccounts();
      setAccounts(data);
      if (data.length > 0) {
        setSelectedAccountId(data[0].accountId);
        setCsvAccountId(data[0].accountId);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch accounts');
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setAmount('');
    setTransactionName('');
    setCategory(TransactionCategory.MISCELLANEOUS);
  };

  const handleAddMoney = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedAccountId) {
      setError('Please select an account');
      return;
    }

    try {
      setError('');
      const transactionData: CreateTransactionData = {
        amount: parseFloat(amount),
        transactionName: transactionName || undefined,
        transactionCategory: category,
        transactionType: TransactionType.INCOME,
        accountId: selectedAccountId,
      };
      const message = await transactionService.addMoney(transactionData);
      setSuccessMessage(message);
      setShowAddMoneyModal(false);
      resetForm();
      fetchAccounts(); // Refresh to show updated balance
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to add money');
    }
  };

  const handleRemoveMoney = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedAccountId) {
      setError('Please select an account');
      return;
    }

    try {
      setError('');
      const transactionData: CreateTransactionData = {
        amount: parseFloat(amount),
        transactionName: transactionName || undefined,
        transactionCategory: category,
        transactionType: TransactionType.EXPENSE,
        accountId: selectedAccountId,
      };
      const message = await transactionService.removeMoney(transactionData);
      setSuccessMessage(message);
      setShowRemoveMoneyModal(false);
      resetForm();
      fetchAccounts(); // Refresh to show updated balance
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to remove money');
    }
  };

  const handleUploadCSV = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!csvFile) {
      setError('Please select a CSV file');
      return;
    }
    if (!csvAccountId) {
      setError('Please select an account');
      return;
    }

    try {
      setError('');
      const message = await transactionService.uploadCSV(csvFile, csvAccountId);
      setSuccessMessage(message);
      setShowUploadCSVModal(false);
      setCsvFile(null);
      fetchAccounts(); // Refresh to show updated balance
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to upload CSV');
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setCsvFile(e.target.files[0]);
    }
  };

  const getCategoryColor = (cat: TransactionCategory) => {
    const colors: Record<TransactionCategory, string> = {
      [TransactionCategory.FOOD]: 'bg-orange-100 text-orange-800',
      [TransactionCategory.UTILITIES]: 'bg-yellow-100 text-yellow-800',
      [TransactionCategory.ENTERTAINMENT]: 'bg-pink-100 text-pink-800',
      [TransactionCategory.TRANSPORTATION]: 'bg-blue-100 text-blue-800',
      [TransactionCategory.HEALTHCARE]: 'bg-red-100 text-red-800',
      [TransactionCategory.EDUCATION]: 'bg-purple-100 text-purple-800',
      [TransactionCategory.GROCERIES]: 'bg-green-100 text-green-800',
      [TransactionCategory.RENT]: 'bg-indigo-100 text-indigo-800',
      [TransactionCategory.SALARY]: 'bg-teal-100 text-teal-800',
      [TransactionCategory.INVESTMENTS]: 'bg-cyan-100 text-cyan-800',
      [TransactionCategory.MISCELLANEOUS]: 'bg-gray-100 text-gray-800',
    };
    return colors[cat] || 'bg-gray-100 text-gray-800';
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <main className="container mx-auto px-4 py-8">
        <div className="mb-8">
          <div className="flex justify-between items-center mb-6">
            <div>
              <h2 className="text-3xl font-bold text-gray-800">Transaction Management</h2>
              <Link to="/" className="text-blue-600 hover:text-blue-700 text-sm">
                Back to Dashboard
              </Link>
            </div>
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

        {/* No Accounts State */}
        {!loading && accounts.length === 0 && (
          <div className="text-center py-16 bg-white rounded-lg shadow">
            <p className="text-gray-600 mb-4">No accounts found. Please create an account first.</p>
            <Link
              to="/accounts"
              className="inline-block px-6 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors"
            >
              Go to Account Management
            </Link>
          </div>
        )}

        {/* Action Cards */}
        {!loading && accounts.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            {/* Add Money Card */}
            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-xl font-bold text-gray-800">Add Money</h3>
                <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                </svg>
              </div>
              <p className="text-gray-600 mb-4">Add income or deposits to your account</p>
              <button
                onClick={() => setShowAddMoneyModal(true)}
                className="w-full px-4 py-2 bg-green-600 text-white rounded font-medium hover:bg-green-700 transition-colors"
              >
                Add Money
              </button>
            </div>

            {/* Remove Money Card */}
            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-xl font-bold text-gray-800">Remove Money</h3>
                <svg className="w-8 h-8 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 12H4" />
                </svg>
              </div>
              <p className="text-gray-600 mb-4">Record expenses or withdrawals from your account</p>
              <button
                onClick={() => setShowRemoveMoneyModal(true)}
                className="w-full px-4 py-2 bg-red-600 text-white rounded font-medium hover:bg-red-700 transition-colors"
              >
                Remove Money
              </button>
            </div>

            {/* Upload CSV Card */}
            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-xl font-bold text-gray-800">Upload CSV</h3>
                <svg className="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                </svg>
              </div>
              <p className="text-gray-600 mb-4">Import multiple transactions from a CSV file</p>
              <button
                onClick={() => setShowUploadCSVModal(true)}
                className="w-full px-4 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors"
              >
                Upload CSV
              </button>
            </div>
          </div>
        )}

        {/* Accounts Overview */}
        {!loading && accounts.length > 0 && (
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <div className="px-6 py-4 bg-gray-50 border-b border-gray-200">
              <h3 className="text-lg font-semibold text-gray-800">Your Accounts</h3>
            </div>
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
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {accounts.map((account) => (
                  <tr key={account.accountId}>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">{account.accountName}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">{account.accountType}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-semibold text-gray-900">${account.currentBalance.toFixed(2)}</div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </main>

      {/* Add Money Modal */}
      {showAddMoneyModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-md w-full p-6">
            <h3 className="text-xl font-bold text-gray-800 mb-4">Add Money (Income)</h3>
            <form onSubmit={handleAddMoney}>
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Select Account
                </label>
                <select
                  value={selectedAccountId}
                  onChange={(e) => setSelectedAccountId(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                  required
                >
                  {accounts.map((account) => (
                    <option key={account.accountId} value={account.accountId}>
                      {account.accountName} (${account.currentBalance.toFixed(2)})
                    </option>
                  ))}
                </select>
              </div>
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Amount
                </label>
                <input
                  type="number"
                  step="0.01"
                  min="0.01"
                  required
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="0.00"
                />
              </div>
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Description (Optional)
                </label>
                <input
                  type="text"
                  value={transactionName}
                  onChange={(e) => setTransactionName(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="e.g., Salary, Gift, Bonus"
                />
              </div>
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Category
                </label>
                <select
                  value={category}
                  onChange={(e) => setCategory(e.target.value as TransactionCategory)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                >
                  <option value={TransactionCategory.SALARY}>Salary</option>
                  <option value={TransactionCategory.INVESTMENTS}>Investments</option>
                  <option value={TransactionCategory.MISCELLANEOUS}>Miscellaneous</option>
                  <option value={TransactionCategory.FOOD}>Food</option>
                  <option value={TransactionCategory.UTILITIES}>Utilities</option>
                  <option value={TransactionCategory.ENTERTAINMENT}>Entertainment</option>
                  <option value={TransactionCategory.TRANSPORTATION}>Transportation</option>
                  <option value={TransactionCategory.HEALTHCARE}>Healthcare</option>
                  <option value={TransactionCategory.EDUCATION}>Education</option>
                  <option value={TransactionCategory.GROCERIES}>Groceries</option>
                  <option value={TransactionCategory.RENT}>Rent</option>
                </select>
              </div>
              <div className="flex gap-3">
                <button
                  type="button"
                  onClick={() => {
                    setShowAddMoneyModal(false);
                    resetForm();
                  }}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded font-medium hover:bg-gray-50 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 px-4 py-2 bg-green-600 text-white rounded font-medium hover:bg-green-700 transition-colors"
                >
                  Add Money
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Remove Money Modal */}
      {showRemoveMoneyModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-md w-full p-6">
            <h3 className="text-xl font-bold text-gray-800 mb-4">Remove Money (Expense)</h3>
            <form onSubmit={handleRemoveMoney}>
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Select Account
                </label>
                <select
                  value={selectedAccountId}
                  onChange={(e) => setSelectedAccountId(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                  required
                >
                  {accounts.map((account) => (
                    <option key={account.accountId} value={account.accountId}>
                      {account.accountName} (${account.currentBalance.toFixed(2)})
                    </option>
                  ))}
                </select>
              </div>
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Amount
                </label>
                <input
                  type="number"
                  step="0.01"
                  min="0.01"
                  required
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                  placeholder="0.00"
                />
              </div>
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Description (Optional)
                </label>
                <input
                  type="text"
                  value={transactionName}
                  onChange={(e) => setTransactionName(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                  placeholder="e.g., Groceries, Rent, Utilities"
                />
              </div>
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Category
                </label>
                <select
                  value={category}
                  onChange={(e) => setCategory(e.target.value as TransactionCategory)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                >
                  <option value={TransactionCategory.GROCERIES}>Groceries</option>
                  <option value={TransactionCategory.RENT}>Rent</option>
                  <option value={TransactionCategory.UTILITIES}>Utilities</option>
                  <option value={TransactionCategory.FOOD}>Food</option>
                  <option value={TransactionCategory.ENTERTAINMENT}>Entertainment</option>
                  <option value={TransactionCategory.TRANSPORTATION}>Transportation</option>
                  <option value={TransactionCategory.HEALTHCARE}>Healthcare</option>
                  <option value={TransactionCategory.EDUCATION}>Education</option>
                  <option value={TransactionCategory.MISCELLANEOUS}>Miscellaneous</option>
                  <option value={TransactionCategory.SALARY}>Salary</option>
                  <option value={TransactionCategory.INVESTMENTS}>Investments</option>
                </select>
              </div>
              <div className="flex gap-3">
                <button
                  type="button"
                  onClick={() => {
                    setShowRemoveMoneyModal(false);
                    resetForm();
                  }}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded font-medium hover:bg-gray-50 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 px-4 py-2 bg-red-600 text-white rounded font-medium hover:bg-red-700 transition-colors"
                >
                  Remove Money
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Upload CSV Modal */}
      {showUploadCSVModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-md w-full p-6">
            <h3 className="text-xl font-bold text-gray-800 mb-4">Upload CSV File</h3>
            <form onSubmit={handleUploadCSV}>
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Select Account
                </label>
                <select
                  value={csvAccountId}
                  onChange={(e) => setCsvAccountId(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                >
                  {accounts.map((account) => (
                    <option key={account.accountId} value={account.accountId}>
                      {account.accountName} (${account.currentBalance.toFixed(2)})
                    </option>
                  ))}
                </select>
              </div>
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  CSV File
                </label>
                <input
                  type="file"
                  accept=".csv"
                  onChange={handleFileChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
                {csvFile && (
                  <p className="mt-2 text-sm text-gray-600">Selected: {csvFile.name}</p>
                )}
              </div>
              <div className="mb-6 p-4 bg-blue-50 rounded-lg">
                <h4 className="text-sm font-semibold text-gray-800 mb-2">CSV Format:</h4>
                <p className="text-xs text-gray-600 mb-2">Your CSV should have the following columns:</p>
                <ul className="text-xs text-gray-600 list-disc list-inside space-y-1">
                  <li>Transaction Name (optional)</li>
                  <li>Amount (required)</li>
                  <li>Category (required)</li>
                  <li>Type (INCOME or EXPENSE)</li>
                  <li>Created At (ISO format: yyyy-MM-dd'T'HH:mm:ss)</li>
                </ul>
              </div>
              <div className="flex gap-3">
                <button
                  type="button"
                  onClick={() => {
                    setShowUploadCSVModal(false);
                    setCsvFile(null);
                  }}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded font-medium hover:bg-gray-50 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 px-4 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors"
                >
                  Upload
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default TransactionManagement;
