import { useEffect, useState } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import chartService from '../../services/chartService';
import type { BarChartData, ChartFilters } from '../../types/chart';

interface IncomeExpenseBarChartProps {
  filters?: ChartFilters;
  title?: string;
  height?: number;
}

const IncomeExpenseBarChart = ({
  filters,
  title = 'Monthly Income vs Expenses',
  height = 400
}: IncomeExpenseBarChartProps) => {
  const [chartData, setChartData] = useState<BarChartData[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    const fetchChartData = async () => {
      try {
        setLoading(true);
        setError('');

        const data = await chartService.getBarChartData(filters);

        // Data is already in the correct format from backend
        setChartData(data);
      } catch (err) {
        setError('Failed to load chart data. Please try again later.');
        console.error('Error fetching bar chart data:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchChartData();
  }, [filters]);

  const formatCurrency = (value: number) => {
    return `$${value.toFixed(2)}`;
  };

  const formatMonth = (month: string) => {
    // Convert yyyy-MM to more readable format (e.g., "2024-01" -> "Jan 2024")
    const [year, monthNum] = month.split('-');
    const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    const monthIndex = parseInt(monthNum, 10) - 1;
    return `${monthNames[monthIndex]} ${year}`;
  };

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-xl font-semibold text-gray-800 mb-4">{title}</h3>
        <div className="flex justify-center items-center" style={{ height }}>
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-xl font-semibold text-gray-800 mb-4">{title}</h3>
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      </div>
    );
  }

  if (chartData.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-xl font-semibold text-gray-800 mb-4">{title}</h3>
        <div className="flex justify-center items-center text-gray-500" style={{ height }}>
          No transaction data available for the selected period.
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow p-6">
      <h3 className="text-xl font-semibold text-gray-800 mb-4">{title}</h3>
      <ResponsiveContainer width="100%" height={height}>
        <BarChart
          data={chartData}
          margin={{ top: 20, right: 30, left: 20, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis
            dataKey="month"
            tickFormatter={formatMonth}
            angle={-45}
            textAnchor="end"
            height={80}
          />
          <YAxis tickFormatter={formatCurrency} />
          <Tooltip
            formatter={(value: number) => formatCurrency(value)}
            labelFormatter={formatMonth}
          />
          <Legend />
          <Bar dataKey="income" fill="#10b981" name="Income" />
          <Bar dataKey="expense" fill="#ef4444" name="Expenses" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default IncomeExpenseBarChart;
