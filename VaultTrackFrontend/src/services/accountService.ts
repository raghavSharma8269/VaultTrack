import apiClient from '../api/client';
import type { Account, AccountType, CreateAccountData, UpdateAccountData } from '../types/account';

class AccountService {
  /**
   * Get all accounts with optional filtering
   * @param query - Optional search query
   * @param accountType - Optional account type filter
   * @returns List of accounts
   */
  async getAccounts(query?: string, accountType?: AccountType): Promise<Account[]> {
    const params = new URLSearchParams();
    if (query) params.append('query', query);
    if (accountType) params.append('accountType', accountType);

    const response = await apiClient.get<Account[]>(`/accounts?${params.toString()}`);
    return response.data;
  }

  /**
   * Create a new account
   * @param data - Account creation data
   * @returns Success message
   */
  async createAccount(data: CreateAccountData): Promise<string> {
    const response = await apiClient.post<string>('/accounts', data);
    return response.data;
  }

  /**
   * Update an existing account
   * @param data - Account update data
   * @returns Success message
   */
  async updateAccount(data: UpdateAccountData): Promise<string> {
    const response = await apiClient.put<string>('/accounts', data);
    return response.data;
  }

  /**
   * Delete an account
   * @param accountId - ID of account to delete
   * @returns Success message
   */
  async deleteAccount(accountId: string): Promise<string> {
    const response = await apiClient.delete<string>(`/accounts/${accountId}`);
    return response.data;
  }
}

export default new AccountService();
