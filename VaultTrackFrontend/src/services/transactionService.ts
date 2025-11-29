import apiClient from '../api/client';
import type { CreateTransactionData, Transaction } from '../types/transaction';

class TransactionService {
  /**
   * Get all transactions for the authenticated user
   * @returns List of transactions
   */
  async getTransactions(): Promise<Transaction[]> {
    const response = await apiClient.get<Transaction[]>('/transactions');
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
}

export default new TransactionService();
