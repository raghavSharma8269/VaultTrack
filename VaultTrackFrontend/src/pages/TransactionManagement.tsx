import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Header from '../components/Header';
import accountService from '../services/accountService';
import transactionService from '../services/transactionService';
import recurringTransactionService from '../services/recurringTransactionService';
import type { Account } from '../types/account';
import { TransactionType, TransactionCategory } from '../types/transaction';
import type { CreateTransactionData, Transaction } from '../types/transaction';
import type { RecurringTransaction, CreateRecurringTransactionData } from '../types/recurringTransaction';
import { RecurringFrequency } from '../types/recurringTransaction';

type UnifiedTransaction = {
  id: string;
  name: string;
  amount: number;
  category: TransactionCategory;
  type: TransactionType;
  accountId: string;
  accountName: string;
  createdAt: string;
  isRecurring: boolean;
  // Recurring-specific fields
  frequency?: RecurringFrequency;
  nextExecutionDate?: string;
  isActive?: boolean;
};

function TransactionManagement() {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [recurringTransactions, setRecurringTransactions] = useState<RecurringTransaction[]>([]);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>('');
  const [successMessage, setSuccessMessage] = useState<string>('');

  // Filter states
  const [filterType, setFilterType] = useState<'all' | 'one-time' | 'recurring'>('all');

  // Modal states
  const [showAddMoneyModal, setShowAddMoneyModal] = useState(false);
  const [showRemoveMoneyModal, setShowRemoveMoneyModal] = useState(false);
  const [showRecurringModal, setShowRecurringModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedTransaction, setSelectedTransaction] = useState<UnifiedTransaction | null>(null);

  // Form states for regular transactions
  const [selectedAccountId, setSelectedAccountId] = useState<string>('');
  const [amount, setAmount] = useState<string>('');
  const [transactionName, setTransactionName] = useState<string>('');
  const [category, setCategory] = useState<TransactionCategory>(TransactionCategory.MISCELLANEOUS);

  // Form states for recurring transactions
  const [recurringFormData, setRecurringFormData] = useState<CreateRecurringTransactionData>({
    transactionName: '',
    amount: 0,
    transactionCategory: TransactionCategory.MISCELLANEOUS,
    transactionType: TransactionType.EXPENSE,
    recurringFrequency: RecurringFrequency.MONTHLY,
    nextExecutionDate: new Date().toISOString().split('T')[0],
    accountId: '',
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      setError('');
      const [transactionsData, recurringData, accountsData] = await Promise.all([
        transactionService.getTransactions(),
        recurringTransactionService.getRecurringTransactions(),
        accountService.getAccounts(),
      ]);
      setTransactions(transactionsData);
      setRecurringTransactions(recurringData);
      setAccounts(accountsData);
      if (accountsData.length > 0 && !selectedAccountId) {
        setSelectedAccountId(accountsData[0].accountId);
        setRecurringFormData(prev => ({ ...prev, accountId: accountsData[0].accountId }));
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch data');
    } finally {
      setLoading(false);
    }
  };

  const getUnifiedTransactions = (): UnifiedTransaction[] => {
    const regularTx: UnifiedTransaction[] = transactions.map(tx => ({
      id: tx.transactionId,
      name: tx.transactionName || 'Transaction',
      amount: tx.amount,
      category: tx.transactionCategory,
      type: tx.transactionType,
      accountId: tx.accountId,
      accountName: tx.accountName,
      createdAt: tx.createdAt,
      isRecurring: false,
    }));

    const recurringTx: UnifiedTransaction[] = recurringTransactions.map(rtx => ({
      id: rtx.recurringTransactionId,
      name: rtx.transactionName,
      amount: rtx.amount,
      category: rtx.transactionCategory,
      type: rtx.transactionType,
      accountId: rtx.accountId,
      accountName: rtx.accountName,
      createdAt: rtx.createdAt,
      isRecurring: true,
      frequency: rtx.recurringFrequency,
      nextExecutionDate: rtx.nextExecutionDate,
      isActive: rtx.isActive,
    }));

    let combined = [...regularTx, ...recurringTx];

    // Apply filter
    if (filterType === 'one-time') {
      combined = combined.filter(tx => !tx.isRecurring);
    } else if (filterType === 'recurring') {
      combined = combined.filter(tx => tx.isRecurring);
    }

    // Sort by date, newest first
    return combined.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  };

  const resetForm = () => {
    setAmount('');
    setTransactionName('');
    setCategory(TransactionCategory.MISCELLANEOUS);
  };

  const resetRecurringForm = () => {
    setRecurringFormData({
      transactionName: '',
      amount: 0,
      transactionCategory: TransactionCategory.MISCELLANEOUS,
      transactionType: TransactionType.EXPENSE,
      recurringFrequency: RecurringFrequency.MONTHLY,
      nextExecutionDate: new Date().toISOString().split('T')[0],
      accountId: accounts.length > 0 ? accounts[0].accountId : '',
    });
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
      fetchData();
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
      fetchData();
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to remove money');
    }
  };

  const handleCreateRecurring = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setError('');
      const message = await recurringTransactionService.createRecurringTransaction(recurringFormData);
      setSuccessMessage(message);
      setShowRecurringModal(false);
      resetRecurringForm();
      fetchData();
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create recurring transaction');
    }
  };

  const handleDeleteTransaction = async () => {
    if (!selectedTransaction) return;

    try {
      setError('');
      let message: string;
      if (selectedTransaction.isRecurring) {
        message = await recurringTransactionService.deleteRecurringTransaction(selectedTransaction.id);
      } else {
        message = await transactionService.deleteTransaction(selectedTransaction.id);
      }
      setSuccessMessage(message);
      setShowDeleteModal(false);
      setSelectedTransaction(null);
      fetchData();
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete transaction');
    }
  };

  const handleToggleRecurringActive = async (transaction: UnifiedTransaction) => {
    if (!transaction.isRecurring) return;

    try {
      setError('');
      const message = transaction.isActive
        ? await recurringTransactionService.pauseRecurringTransaction(transaction.id)
        : await recurringTransactionService.resumeRecurringTransaction(transaction.id);
      setSuccessMessage(message);
      fetchData();
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to toggle recurring transaction');
    }
  };

  const handleExportToCsv = async () => {
    try {
      setError('');
      await transactionService.exportTransactionsToCsv();
      setSuccessMessage('Transactions exported successfully!');
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to export transactions');
    }
  };

  const openDeleteModal = (transaction: UnifiedTransaction) => {
    setSelectedTransaction(transaction);
    setShowDeleteModal(true);
  };

  const getTypeColor = (type: TransactionType) => {
    return type === TransactionType.INCOME
      ? 'bg-green-100 text-green-800'
      : 'bg-red-100 text-red-800';
  };

  const getFrequencyColor = (frequency?: RecurringFrequency) => {
    if (!frequency) return 'bg-gray-100 text-gray-800';
    switch (frequency) {
      case RecurringFrequency.DAILY:
        return 'bg-red-100 text-red-800';
      case RecurringFrequency.WEEKLY:
        return 'bg-orange-100 text-orange-800';
      case RecurringFrequency.MONTHLY:
        return 'bg-blue-100 text-blue-800';
      case RecurringFrequency.YEARLY:
        return 'bg-green-100 text-green-800';
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
            <p className="text-gray-600 mt-4">Loading transactions...</p>
          </div>
        )}

        {/* No Accounts State */}
        {!loading && accounts.length === 0 && (
          <div className="text-center py-16 bg-white rounded-lg shadow">
            <p className="text-gray-600 mb-4">No accounts found. Please create an account first.</p>
            <Link
              to="/accounts/manage"
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
              <p className="text-gray-600 mb-4">Record expenses or withdrawals</p>
              <button
                onClick={() => setShowRemoveMoneyModal(true)}
                className="w-full px-4 py-2 bg-red-600 text-white rounded font-medium hover:bg-red-700 transition-colors"
              >
                Remove Money
              </button>
            </div>

            {/* Create Recurring Transaction Card */}
            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-xl font-bold text-gray-800">Recurring</h3>
                <svg className="w-8 h-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
              </div>
              <p className="text-gray-600 mb-4">Set up automatic recurring transactions</p>
              <button
                onClick={() => {
                  resetRecurringForm();
                  setShowRecurringModal(true);
                }}
                className="w-full px-4 py-2 bg-purple-600 text-white rounded font-medium hover:bg-purple-700 transition-colors"
              >
                Create Recurring
              </button>
            </div>
          </div>
        )}

        {/* Transactions Table */}
        {!loading && accounts.length > 0 && (
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <div className="px-6 py-4 bg-gray-50 border-b border-gray-200 flex justify-between items-center">
              <h3 className="text-lg font-semibold text-gray-800">All Transactions</h3>
              <div className="flex gap-2">
                <button
                  onClick={() => setFilterType('all')}
                  className={`px-4 py-1 rounded text-sm font-medium ${filterType === 'all' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700'}`}
                >
                  All
                </button>
                <button
                  onClick={() => setFilterType('one-time')}
                  className={`px-4 py-1 rounded text-sm font-medium ${filterType === 'one-time' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700'}`}
                >
                  One-Time
                </button>
                <button
                  onClick={() => setFilterType('recurring')}
                  className={`px-4 py-1 rounded text-sm font-medium ${filterType === 'recurring' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700'}`}
                >
                  Recurring
                </button>
                <button
                  onClick={handleExportToCsv}
                  className="px-4 py-1 rounded text-sm font-medium bg-emerald-600 text-white hover:bg-emerald-700 transition-colors flex items-center gap-2"
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  Export to CSV
                </button>
              </div>
            </div>

            {getUnifiedTransactions().length === 0 ? (
              <div className="text-center py-16">
                <p className="text-gray-600">No transactions found</p>
              </div>
            ) : (
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Name
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Amount
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Type
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Category
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Account
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Recurring
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Date
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {getUnifiedTransactions().map((transaction) => (
                      <tr key={transaction.id}>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="text-sm font-medium text-gray-900">{transaction.name}</div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className={`text-sm font-semibold ${transaction.type === TransactionType.INCOME ? 'text-green-600' : 'text-red-600'}`}>
                            {transaction.type === TransactionType.INCOME ? '+' : '-'}${transaction.amount.toFixed(2)}
                          </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span className={`px-3 py-1 rounded-full text-xs font-medium ${getTypeColor(transaction.type)}`}>
                            {transaction.type}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="text-sm text-gray-900">{transaction.category}</div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="text-sm text-gray-900">{transaction.accountName}</div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          {transaction.isRecurring ? (
                            <div>
                              <span className={`px-3 py-1 rounded-full text-xs font-medium ${getFrequencyColor(transaction.frequency)}`}>
                                {transaction.frequency}
                              </span>
                              {transaction.isActive !== undefined && (
                                <div className="mt-1">
                                  <span className={`text-xs ${transaction.isActive ? 'text-green-600' : 'text-gray-400'}`}>
                                    {transaction.isActive ? 'Active' : 'Paused'}
                                  </span>
                                </div>
                              )}
                            </div>
                          ) : (
                            <span className="text-xs text-gray-400">One-time</span>
                          )}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="text-sm text-gray-500">
                            {transaction.isRecurring && transaction.nextExecutionDate
                              ? new Date(transaction.nextExecutionDate).toLocaleDateString()
                              : new Date(transaction.createdAt).toLocaleDateString()}
                          </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                          {transaction.isRecurring && (
                            <button
                              onClick={() => handleToggleRecurringActive(transaction)}
                              className="text-yellow-600 hover:text-yellow-900 mr-4"
                            >
                              {transaction.isActive ? 'Pause' : 'Resume'}
                            </button>
                          )}
                          <button
                            onClick={() => openDeleteModal(transaction)}
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
          </div>
        )}
      </main>

      {/* Add Money Modal - keeping existing */}
      {showAddMoneyModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-md w-full p-6">
            <h3 className="text-xl font-bold text-gray-800 mb-4">Add Money (Income)</h3>
            <form onSubmit={handleAddMoney}>
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">Select Account</label>
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
                <label className="block text-sm font-medium text-gray-700 mb-2">Amount</label>
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
                <label className="block text-sm font-medium text-gray-700 mb-2">Description (Optional)</label>
                <input
                  type="text"
                  value={transactionName}
                  onChange={(e) => setTransactionName(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="e.g., Salary, Gift"
                />
              </div>
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">Category</label>
                <select
                  value={category}
                  onChange={(e) => setCategory(e.target.value as TransactionCategory)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                >
                  {Object.values(TransactionCategory).map((cat) => (
                    <option key={cat} value={cat}>{cat}</option>
                  ))}
                </select>
              </div>
              <div className="flex gap-3">
                <button
                  type="button"
                  onClick={() => { setShowAddMoneyModal(false); resetForm(); }}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded font-medium hover:bg-gray-50"
                >
                  Cancel
                </button>
                <button type="submit" className="flex-1 px-4 py-2 bg-green-600 text-white rounded font-medium hover:bg-green-700">
                  Add Money
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Remove Money Modal - keeping existing */}
      {showRemoveMoneyModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-md w-full p-6">
            <h3 className="text-xl font-bold text-gray-800 mb-4">Remove Money (Expense)</h3>
            <form onSubmit={handleRemoveMoney}>
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">Select Account</label>
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
                <label className="block text-sm font-medium text-gray-700 mb-2">Amount</label>
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
                <label className="block text-sm font-medium text-gray-700 mb-2">Description (Optional)</label>
                <input
                  type="text"
                  value={transactionName}
                  onChange={(e) => setTransactionName(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                  placeholder="e.g., Groceries, Rent"
                />
              </div>
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">Category</label>
                <select
                  value={category}
                  onChange={(e) => setCategory(e.target.value as TransactionCategory)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                >
                  {Object.values(TransactionCategory).map((cat) => (
                    <option key={cat} value={cat}>{cat}</option>
                  ))}
                </select>
              </div>
              <div className="flex gap-3">
                <button
                  type="button"
                  onClick={() => { setShowRemoveMoneyModal(false); resetForm(); }}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded font-medium hover:bg-gray-50"
                >
                  Cancel
                </button>
                <button type="submit" className="flex-1 px-4 py-2 bg-red-600 text-white rounded font-medium hover:bg-red-700">
                  Remove Money
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Create Recurring Transaction Modal */}
      {showRecurringModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full p-6 max-h-[90vh] overflow-y-auto">
            <h3 className="text-xl font-bold text-gray-800 mb-4">Create Recurring Transaction</h3>
            <form onSubmit={handleCreateRecurring}>
              <div className="grid grid-cols-2 gap-4">
                <div className="col-span-2">
                  <label className="block text-sm font-medium text-gray-700 mb-2">Transaction Name</label>
                  <input
                    type="text"
                    required
                    value={recurringFormData.transactionName}
                    onChange={(e) => setRecurringFormData({ ...recurringFormData, transactionName: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                    placeholder="e.g., Monthly Rent"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Amount ($)</label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={recurringFormData.amount || ''}
                    onChange={(e) => setRecurringFormData({ ...recurringFormData, amount: parseFloat(e.target.value) || 0 })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                    placeholder="0.00"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Transaction Type</label>
                  <select
                    value={recurringFormData.transactionType}
                    onChange={(e) => setRecurringFormData({ ...recurringFormData, transactionType: e.target.value as TransactionType })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                  >
                    <option value={TransactionType.INCOME}>Income</option>
                    <option value={TransactionType.EXPENSE}>Expense</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Category</label>
                  <select
                    value={recurringFormData.transactionCategory}
                    onChange={(e) => setRecurringFormData({ ...recurringFormData, transactionCategory: e.target.value as TransactionCategory })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                  >
                    {Object.values(TransactionCategory).map((category) => (
                      <option key={category} value={category}>{category}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Frequency</label>
                  <select
                    value={recurringFormData.recurringFrequency}
                    onChange={(e) => setRecurringFormData({ ...recurringFormData, recurringFrequency: e.target.value as RecurringFrequency })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                  >
                    <option value={RecurringFrequency.DAILY}>Daily</option>
                    <option value={RecurringFrequency.WEEKLY}>Weekly</option>
                    <option value={RecurringFrequency.MONTHLY}>Monthly</option>
                    <option value={RecurringFrequency.YEARLY}>Yearly</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Next Execution Date</label>
                  <input
                    type="date"
                    required
                    value={recurringFormData.nextExecutionDate}
                    onChange={(e) => setRecurringFormData({ ...recurringFormData, nextExecutionDate: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                  />
                </div>
                <div className="col-span-2">
                  <label className="block text-sm font-medium text-gray-700 mb-2">Account</label>
                  <select
                    value={recurringFormData.accountId}
                    onChange={(e) => setRecurringFormData({ ...recurringFormData, accountId: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                    required
                  >
                    {accounts.map((account) => (
                      <option key={account.accountId} value={account.accountId}>
                        {account.accountName} - ${account.currentBalance.toFixed(2)}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="flex gap-3 mt-6">
                <button
                  type="button"
                  onClick={() => { setShowRecurringModal(false); resetRecurringForm(); }}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded font-medium hover:bg-gray-50"
                >
                  Cancel
                </button>
                <button type="submit" className="flex-1 px-4 py-2 bg-purple-600 text-white rounded font-medium hover:bg-purple-700">
                  Create
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {showDeleteModal && selectedTransaction && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-md w-full p-6">
            <h3 className="text-xl font-bold text-gray-800 mb-4">Delete Transaction</h3>
            <p className="text-gray-600 mb-6">
              Are you sure you want to delete <strong>{selectedTransaction.name}</strong>?
              {selectedTransaction.isRecurring && ' This will stop all future automatic transactions.'}
            </p>
            <div className="flex gap-3">
              <button
                onClick={() => { setShowDeleteModal(false); setSelectedTransaction(null); }}
                className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded font-medium hover:bg-gray-50"
              >
                Cancel
              </button>
              <button
                onClick={handleDeleteTransaction}
                className="flex-1 px-4 py-2 bg-red-600 text-white rounded font-medium hover:bg-red-700"
              >
                Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default TransactionManagement;
