import apiClient from '../api/client';
import type { CreateTransactionData } from '../types/transaction';

class TransactionService {
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
