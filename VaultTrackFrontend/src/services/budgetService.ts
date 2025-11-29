import apiClient from '../api/client';
import type { Budget, CreateBudgetData, UpdateBudgetData } from '../types/budget';

class BudgetService {
  /**
   * Get budget by account ID
   * @param accountId - Account ID
   * @returns Budget or null if not found
   */
  async getBudgetByAccount(accountId: string): Promise<Budget | null> {
    try {
      const response = await apiClient.get<Budget>(`/budgets/account/${accountId}`);
      return response.data;
    } catch (err: any) {
      if (err.response?.status === 404) {
        return null;
      }
      throw err;
    }
  }

  /**
   * Create a new budget for an account
   * @param data - Budget creation data
   * @returns Success message
   */
  async createBudget(data: CreateBudgetData): Promise<string> {
    const response = await apiClient.post<string>('/budgets', data);
    return response.data;
  }

  /**
   * Update an existing budget
   * @param data - Budget update data
   * @returns Success message
   */
  async updateBudget(data: UpdateBudgetData): Promise<string> {
    const response = await apiClient.put<string>('/budgets', data);
    return response.data;
  }
}

export default new BudgetService();
