import apiClient from '../api/client';
import type {
  RecurringTransaction,
  CreateRecurringTransactionData,
  UpdateRecurringTransactionData,
} from '../types/recurringTransaction';

class RecurringTransactionService {
  /**
   * Get all recurring transactions for the authenticated user
   * @returns List of recurring transactions
   */
  async getRecurringTransactions(): Promise<RecurringTransaction[]> {
    const response = await apiClient.get<RecurringTransaction[]>('/recurring-transactions');
    return response.data;
  }

  /**
   * Create a new recurring transaction
   * @param data - Recurring transaction creation data
   * @returns Success message
   */
  async createRecurringTransaction(data: CreateRecurringTransactionData): Promise<string> {
    const response = await apiClient.post<string>('/recurring-transactions', data);
    return response.data;
  }

  /**
   * Update an existing recurring transaction
   * @param recurringTransactionId - ID of the recurring transaction to update
   * @param data - Recurring transaction update data
   * @returns Success message
   */
  async updateRecurringTransaction(
    recurringTransactionId: string,
    data: UpdateRecurringTransactionData
  ): Promise<string> {
    const response = await apiClient.put<string>(
      `/recurring-transactions/${recurringTransactionId}`,
      data
    );
    return response.data;
  }

  /**
   * Delete a recurring transaction
   * @param recurringTransactionId - ID of the recurring transaction to delete
   * @returns Success message
   */
  async deleteRecurringTransaction(recurringTransactionId: string): Promise<string> {
    const response = await apiClient.delete<string>(
      `/recurring-transactions/${recurringTransactionId}`
    );
    return response.data;
  }

  /**
   * Pause a recurring transaction
   * @param recurringTransactionId - ID of the recurring transaction to pause
   * @returns Success message
   */
  async pauseRecurringTransaction(recurringTransactionId: string): Promise<string> {
    const response = await apiClient.patch<string>(
      `/recurring-transactions/${recurringTransactionId}/pause`
    );
    return response.data;
  }

  /**
   * Resume a recurring transaction
   * @param recurringTransactionId - ID of the recurring transaction to resume
   * @returns Success message
   */
  async resumeRecurringTransaction(recurringTransactionId: string): Promise<string> {
    const response = await apiClient.patch<string>(
      `/recurring-transactions/${recurringTransactionId}/resume`
    );
    return response.data;
  }
}

export default new RecurringTransactionService();
