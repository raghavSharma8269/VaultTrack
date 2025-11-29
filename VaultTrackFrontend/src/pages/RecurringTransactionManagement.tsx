import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Header from '../components/Header';
import recurringTransactionService from '../services/recurringTransactionService';
import accountService from '../services/accountService';
import { RecurringFrequency } from '../types/recurringTransaction';
import { TransactionCategory, TransactionType } from '../types/transaction';
import type { RecurringTransaction, CreateRecurringTransactionData } from '../types/recurringTransaction';
import type { Account } from '../types/account';

function RecurringTransactionManagement() {
  const [recurringTransactions, setRecurringTransactions] = useState<RecurringTransaction[]>([]);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>('');
  const [successMessage, setSuccessMessage] = useState<string>('');

  // Modal states
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedTransaction, setSelectedTransaction] = useState<RecurringTransaction | null>(null);

  // Form states
  const [formData, setFormData] = useState<CreateRecurringTransactionData>({
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
      const [transactionsData, accountsData] = await Promise.all([
        recurringTransactionService.getRecurringTransactions(),
        accountService.getAccounts(),
      ]);
      setRecurringTransactions(transactionsData);
      setAccounts(accountsData);
      if (accountsData.length > 0 && !formData.accountId) {
        setFormData(prev => ({ ...prev, accountId: accountsData[0].accountId }));
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch data');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setError('');
      const message = await recurringTransactionService.createRecurringTransaction(formData);
      setSuccessMessage(message);
      setShowCreateModal(false);
      resetForm();
      fetchData();
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create recurring transaction');
    }
  };

  const handleUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedTransaction) return;

    try {
      setError('');
      const message = await recurringTransactionService.updateRecurringTransaction(
        selectedTransaction.recurringTransactionId,
        formData
      );
      setSuccessMessage(message);
      setShowEditModal(false);
      setSelectedTransaction(null);
      resetForm();
      fetchData();
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update recurring transaction');
    }
  };

  const handleDelete = async () => {
    if (!selectedTransaction) return;

    try {
      setError('');
      const message = await recurringTransactionService.deleteRecurringTransaction(
        selectedTransaction.recurringTransactionId
      );
      setSuccessMessage(message);
      setShowDeleteModal(false);
      setSelectedTransaction(null);
      fetchData();
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete recurring transaction');
    }
  };

  const handleToggleActive = async (transaction: RecurringTransaction) => {
    try {
      setError('');
      const message = transaction.isActive
        ? await recurringTransactionService.pauseRecurringTransaction(transaction.recurringTransactionId)
        : await recurringTransactionService.resumeRecurringTransaction(transaction.recurringTransactionId);
      setSuccessMessage(message);
      fetchData();
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to toggle recurring transaction status');
    }
  };

  const openEditModal = (transaction: RecurringTransaction) => {
    setSelectedTransaction(transaction);
    setFormData({
      transactionName: transaction.transactionName,
      amount: transaction.amount,
      transactionCategory: transaction.transactionCategory,
      transactionType: transaction.transactionType,
      recurringFrequency: transaction.recurringFrequency,
      nextExecutionDate: transaction.nextExecutionDate,
      accountId: transaction.accountId,
    });
    setShowEditModal(true);
  };

  const openDeleteModal = (transaction: RecurringTransaction) => {
    setSelectedTransaction(transaction);
    setShowDeleteModal(true);
  };

  const resetForm = () => {
    setFormData({
      transactionName: '',
      amount: 0,
      transactionCategory: TransactionCategory.MISCELLANEOUS,
      transactionType: TransactionType.EXPENSE,
      recurringFrequency: RecurringFrequency.MONTHLY,
      nextExecutionDate: new Date().toISOString().split('T')[0],
      accountId: accounts.length > 0 ? accounts[0].accountId : '',
    });
  };

  const getFrequencyColor = (frequency: RecurringFrequency) => {
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

  const getTypeColor = (type: TransactionType) => {
    return type === TransactionType.INCOME
      ? 'bg-green-100 text-green-800'
      : 'bg-red-100 text-red-800';
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <main className="container mx-auto px-4 py-8">
        <div className="mb-8">
          <div className="flex justify-between items-center mb-6">
            <div>
              <h2 className="text-3xl font-bold text-gray-800">Recurring Transactions</h2>
              <Link to="/" className="text-blue-600 hover:text-blue-700 text-sm">
                Back to Dashboard
              </Link>
            </div>
            <button
              onClick={() => {
                resetForm();
                setShowCreateModal(true);
              }}
              className="px-6 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors"
            >
              Create Recurring Transaction
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
            <p className="text-gray-600 mt-4">Loading recurring transactions...</p>
          </div>
        )}

        {/* Recurring Transactions Table */}
        {!loading && recurringTransactions.length === 0 && (
          <div className="text-center py-16 bg-white rounded-lg shadow">
            <p className="text-gray-600 mb-4">No recurring transactions found</p>
            <button
              onClick={() => setShowCreateModal(true)}
              className="px-6 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors"
            >
              Create Your First Recurring Transaction
            </button>
          </div>
        )}

        {!loading && recurringTransactions.length > 0 && (
          <div className="bg-white rounded-lg shadow overflow-hidden">
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
                    Frequency
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Next Execution
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Account
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {recurringTransactions.map((transaction) => (
                  <tr key={transaction.recurringTransactionId}>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">{transaction.transactionName}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">${transaction.amount.toFixed(2)}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-3 py-1 rounded-full text-xs font-medium ${getTypeColor(transaction.transactionType)}`}>
                        {transaction.transactionType}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">{transaction.transactionCategory}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-3 py-1 rounded-full text-xs font-medium ${getFrequencyColor(transaction.recurringFrequency)}`}>
                        {transaction.recurringFrequency}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-500">
                        {new Date(transaction.nextExecutionDate).toLocaleDateString()}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">{transaction.accountName}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                        transaction.isActive ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                      }`}>
                        {transaction.isActive ? 'Active' : 'Paused'}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <button
                        onClick={() => handleToggleActive(transaction)}
                        className="text-yellow-600 hover:text-yellow-900 mr-4"
                      >
                        {transaction.isActive ? 'Pause' : 'Resume'}
                      </button>
                      <button
                        onClick={() => openEditModal(transaction)}
                        className="text-blue-600 hover:text-blue-900 mr-4"
                      >
                        Edit
                      </button>
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
      </main>

      {/* Create/Edit Modal */}
      {(showCreateModal || showEditModal) && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full p-6 max-h-[90vh] overflow-y-auto">
            <h3 className="text-xl font-bold text-gray-800 mb-4">
              {showCreateModal ? 'Create Recurring Transaction' : 'Edit Recurring Transaction'}
            </h3>
            <form onSubmit={showCreateModal ? handleCreate : handleUpdate}>
              <div className="grid grid-cols-2 gap-4">
                <div className="col-span-2">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Transaction Name
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.transactionName}
                    onChange={(e) => setFormData({ ...formData, transactionName: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="e.g., Monthly Rent"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Amount ($)
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={formData.amount}
                    onChange={(e) => setFormData({ ...formData, amount: parseFloat(e.target.value) })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="0.00"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Transaction Type
                  </label>
                  <select
                    value={formData.transactionType}
                    onChange={(e) => setFormData({ ...formData, transactionType: e.target.value as TransactionType })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    <option value={TransactionType.INCOME}>Income</option>
                    <option value={TransactionType.EXPENSE}>Expense</option>
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Category
                  </label>
                  <select
                    value={formData.transactionCategory}
                    onChange={(e) => setFormData({ ...formData, transactionCategory: e.target.value as TransactionCategory })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    {Object.values(TransactionCategory).map((category) => (
                      <option key={category} value={category}>
                        {category}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Frequency
                  </label>
                  <select
                    value={formData.recurringFrequency}
                    onChange={(e) => setFormData({ ...formData, recurringFrequency: e.target.value as RecurringFrequency })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    <option value={RecurringFrequency.DAILY}>Daily</option>
                    <option value={RecurringFrequency.WEEKLY}>Weekly</option>
                    <option value={RecurringFrequency.MONTHLY}>Monthly</option>
                    <option value={RecurringFrequency.YEARLY}>Yearly</option>
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Next Execution Date
                  </label>
                  <input
                    type="date"
                    required
                    value={formData.nextExecutionDate}
                    onChange={(e) => setFormData({ ...formData, nextExecutionDate: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>

                <div className="col-span-2">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Account
                  </label>
                  <select
                    value={formData.accountId}
                    onChange={(e) => setFormData({ ...formData, accountId: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                  >
                    {accounts.length === 0 && <option value="">No accounts available</option>}
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
                  onClick={() => {
                    if (showCreateModal) setShowCreateModal(false);
                    if (showEditModal) setShowEditModal(false);
                    setSelectedTransaction(null);
                    resetForm();
                  }}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded font-medium hover:bg-gray-50 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 px-4 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700 transition-colors"
                >
                  {showCreateModal ? 'Create' : 'Update'}
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
            <h3 className="text-xl font-bold text-gray-800 mb-4">Delete Recurring Transaction</h3>
            <p className="text-gray-600 mb-6">
              Are you sure you want to delete <strong>{selectedTransaction.transactionName}</strong>? This action cannot be undone.
            </p>
            <div className="flex gap-3">
              <button
                onClick={() => {
                  setShowDeleteModal(false);
                  setSelectedTransaction(null);
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
    </div>
  );
}

export default RecurringTransactionManagement;
