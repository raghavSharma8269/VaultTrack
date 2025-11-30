import apiClient from '../api/client';
import type { PieChartData, BarChartData, LineChartData, ChartFilters, LineChartFilters } from '../types/chart';

class ChartService {
  /**
   * Get pie chart data showing expense breakdown by category
   * @param filters - Optional filters for transactions
   * @returns Pie chart data with amounts per category
   */
  async getPieChartData(filters?: ChartFilters): Promise<PieChartData> {
    const params = new URLSearchParams();

    if (filters?.transactionName) params.append('transactionName', filters.transactionName);
    if (filters?.transactionType) params.append('transactionType', filters.transactionType);
    if (filters?.transactionCategory) params.append('transactionCategory', filters.transactionCategory);
    if (filters?.start) params.append('start', filters.start);
    if (filters?.end) params.append('end', filters.end);
    if (filters?.accountId) params.append('accountId', filters.accountId);

    const queryString = params.toString();
    const url = `/charts/pie-chart${queryString ? `?${queryString}` : ''}`;

    const response = await apiClient.get<PieChartData>(url);
    return response.data;
  }

  /**
   * Get bar chart data showing monthly income vs expenses
   * @param filters - Optional filters for transactions
   * @returns List of monthly data with income and expense amounts
   */
  async getBarChartData(filters?: ChartFilters): Promise<BarChartData[]> {
    const params = new URLSearchParams();

    if (filters?.transactionName) params.append('transactionName', filters.transactionName);
    if (filters?.transactionType) params.append('transactionType', filters.transactionType);
    if (filters?.transactionCategory) params.append('transactionCategory', filters.transactionCategory);
    if (filters?.start) params.append('start', filters.start);
    if (filters?.end) params.append('end', filters.end);
    if (filters?.accountId) params.append('accountId', filters.accountId);

    const queryString = params.toString();
    const url = `/charts/bar-chart${queryString ? `?${queryString}` : ''}`;

    const response = await apiClient.get<BarChartData[]>(url);
    return response.data;
  }

  /**
   * Get line chart data showing account balance trend over time
   * @param filters - Filters with required accountId and optional date range
   * @returns List of daily balance data points
   */
  async getLineChartData(filters: LineChartFilters): Promise<LineChartData[]> {
    const params = new URLSearchParams();

    // accountId is required for line chart
    params.append('accountId', filters.accountId);

    if (filters?.start) params.append('start', filters.start);
    if (filters?.end) params.append('end', filters.end);

    const queryString = params.toString();
    const url = `/charts/line-chart?${queryString}`;

    const response = await apiClient.get<LineChartData[]>(url);
    return response.data;
  }
}

export default new ChartService();
