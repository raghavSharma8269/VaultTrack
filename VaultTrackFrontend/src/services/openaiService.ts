import apiClient from '../api/client';

interface AiFeedbackRequest {
  userPrompt: string;
}

interface AiFeedbackFilters {
  start?: string;
  end?: string;
  transactionCategory?: string;
  transactionType?: string;
  transactionName?: string;
  accountId?: string;
}

class OpenAIService {
  /**
   * Get AI feedback on financial transactions
   * @param userPrompt - The user's question or feedback request
   * @param filters - Optional filters for transactions to analyze
   * @returns AI-generated financial advice
   */
  async getAiFeedback(
    userPrompt: string,
    filters?: AiFeedbackFilters
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
