import apiClient from '../api/client';
import type { TransactionFilters } from '../types/transaction';

interface AiFeedbackRequest {
  userPrompt: string;
}

class OpenAIService {
  /**
   * Get AI feedback on financial transactions
   * @param userPrompt - The user's question or feedback request
   * @param filters - Optional filters for transactions to analyze (start, end, transactionCategory, transactionType, transactionName, accountId)
   * @returns AI-generated financial advice
   */
  async getAiFeedback(
    userPrompt: string,
    filters?: TransactionFilters
  ): Promise<string> {
    const params = new URLSearchParams();

    if (filters?.start) params.append('start', filters.start);
    if (filters?.end) params.append('end', filters.end);
    if (filters?.transactionCategory) params.append('transactionCategory', filters.transactionCategory);
    if (filters?.transactionType) params.append('transactionType', filters.transactionType);
    if (filters?.transactionName) params.append('transactionName', filters.transactionName);
    if (filters?.accountId) params.append('accountId', filters.accountId);

    const requestBody: AiFeedbackRequest = {
      userPrompt
    };

    const response = await apiClient.post<string>('/ai/feedback', requestBody, {
      params
    });

    return response.data;
  }
}

export default new OpenAIService();
