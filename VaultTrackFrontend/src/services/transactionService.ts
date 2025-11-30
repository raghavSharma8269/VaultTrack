import apiClient from '../api/client';
import type { CreateTransactionData, Transaction, TransactionFilters } from '../types/transaction';

class TransactionService {
  /**
   * Get all transactions for the authenticated user with optional filters
   * @param filters - Optional filters (start, end, transactionCategory, transactionType, transactionName, accountId)
   * @returns List of transactions matching the filters
   */
  async getTransactions(filters?: TransactionFilters): Promise<Transaction[]> {
    // Build query parameters
    const params = new URLSearchParams();
    if (filters?.start) params.append('start', filters.start);
    if (filters?.end) params.append('end', filters.end);
    if (filters?.transactionCategory) params.append('transactionCategory', filters.transactionCategory);
    if (filters?.transactionType) params.append('transactionType', filters.transactionType);
    if (filters?.transactionName) params.append('transactionName', filters.transactionName);
    if (filters?.accountId) params.append('accountId', filters.accountId);

    const response = await apiClient.get<Transaction[]>('/transactions', {
      params,
    });
    return response.data;
  }

  /**
   * Delete a transaction
   * @param transactionId - ID of the transaction to delete
   * @returns Success message
   */
  async deleteTransaction(transactionId: string): Promise<string> {
    const response = await apiClient.delete<string>(`/transactions/${transactionId}`);
    return response.data;
  }

  /**
   * Add money to an account (create INCOME transaction)
   * @param data - Transaction data with INCOME type
   * @returns Success message
   */
  async addMoney(data: CreateTransactionData): Promise<string> {
    const response = await apiClient.post<string>('/transactions', data);
    return response.data;
  }

  /**
   * Remove money from an account (create EXPENSE transaction)
   * @param data - Transaction data with EXPENSE type
   * @returns Success message
   */
  async removeMoney(data: CreateTransactionData): Promise<string> {
    const response = await apiClient.post<string>('/transactions', data);
    return response.data;
  }

  /**
   * Upload CSV file with transactions to import into an account
   * @param file - CSV file to upload
   * @param accountId - Account ID to import transactions into
   * @returns Success message with count of imported transactions
   */
  async uploadCSV(file: File, accountId: string): Promise<string> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('accountId', accountId);

    const response = await apiClient.post<string>('/transactions/import/csv', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  }

  /**
   * Export transactions to CSV file
   * @param filters - Optional filters for the export (start, end, transactionCategory, transactionType, transactionName, accountId)
   * @returns void - Downloads the CSV file
   */
  async exportTransactionsToCsv(filters?: TransactionFilters): Promise<void> {
    // Build query parameters
    const params = new URLSearchParams();
    if (filters?.start) params.append('start', filters.start);
    if (filters?.end) params.append('end', filters.end);
    if (filters?.transactionCategory) params.append('transactionCategory', filters.transactionCategory);
    if (filters?.transactionType) params.append('transactionType', filters.transactionType);
    if (filters?.transactionName) params.append('transactionName', filters.transactionName);
    if (filters?.accountId) params.append('accountId', filters.accountId);

    const response = await apiClient.get('/transactions/export/csv', {
      params,
      responseType: 'blob', // Important for binary data
    });

    // Create a blob from the response
    const blob = new Blob([response.data], { type: 'text/csv' });

    // Create a temporary URL for the blob
    const url = window.URL.createObjectURL(blob);

    // Create a temporary anchor element and trigger download
    const link = document.createElement('a');
    link.href = url;

    // Extract filename from Content-Disposition header if available, otherwise use default
    const contentDisposition = response.headers['content-disposition'];
    let filename = 'transactions.csv';
    if (contentDisposition) {
      const filenameMatch = contentDisposition.match(/filename="?(.+)"?/);
      if (filenameMatch) {
        filename = filenameMatch[1];
      }
    }

    link.download = filename;
    document.body.appendChild(link);
    link.click();

    // Cleanup
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  }
}

export default new TransactionService();
