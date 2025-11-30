import { useEffect, useState } from 'react';
import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts';
import chartService from '../../services/chartService';
import type { ChartFilters, PieChartEntry } from '../../types/chart';

interface ExpensePieChartProps {
  filters?: ChartFilters;
  title?: string;
  height?: number;
}

const COLORS: Record<string, string> = {
  Food: '#ef4444',
  Utilities: '#f59e0b',
  Entertainment: '#ec4899',
  Transportation: '#8b5cf6',
  Healthcare: '#06b6d4',
  Education: '#3b82f6',
  Groceries: '#10b981',
  Rent: '#6366f1',
  Salary: '#14b8a6',
  Investments: '#a855f7',
  Miscellaneous: '#6b7280',
};

const ExpensePieChart = ({
  filters,
  title = 'Expense Breakdown by Category',
  height = 400
}: ExpensePieChartProps) => {
  const [chartData, setChartData] = useState<PieChartEntry[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    const fetchChartData = async () => {
      try {
        setLoading(true);
        setError('');

        const data = await chartService.getPieChartData(filters);

        // Transform backend data into recharts format
        const transformedData: PieChartEntry[] = [];

        if (data.foodAmount > 0) {
          transformedData.push({
            name: 'Food',
            value: data.foodAmount,
            color: COLORS.Food
          });
        }
        if (data.utilitiesAmount > 0) {
          transformedData.push({
            name: 'Utilities',
            value: data.utilitiesAmount,
            color: COLORS.Utilities
          });
        }
        if (data.entertainmentAmount > 0) {
          transformedData.push({
            name: 'Entertainment',
            value: data.entertainmentAmount,
            color: COLORS.Entertainment
          });
        }
        if (data.transportationAmount > 0) {
          transformedData.push({
            name: 'Transportation',
            value: data.transportationAmount,
            color: COLORS.Transportation
          });
        }
        if (data.healthcareAmount > 0) {
          transformedData.push({
            name: 'Healthcare',
            value: data.healthcareAmount,
            color: COLORS.Healthcare
          });
        }
        if (data.educationAmount > 0) {
          transformedData.push({
            name: 'Education',
            value: data.educationAmount,
            color: COLORS.Education
          });
        }
        if (data.groceriesAmount > 0) {
          transformedData.push({
            name: 'Groceries',
            value: data.groceriesAmount,
            color: COLORS.Groceries
          });
        }
        if (data.rentAmount > 0) {
          transformedData.push({
            name: 'Rent',
            value: data.rentAmount,
            color: COLORS.Rent
          });
        }
        if (data.salaryAmount > 0) {
          transformedData.push({
            name: 'Salary',
            value: data.salaryAmount,
            color: COLORS.Salary
          });
        }
        if (data.investmentsAmount > 0) {
          transformedData.push({
            name: 'Investments',
            value: data.investmentsAmount,
            color: COLORS.Investments
          });
        }
        if (data.miscellaneousAmount > 0) {
          transformedData.push({
            name: 'Miscellaneous',
            value: data.miscellaneousAmount,
            color: COLORS.Miscellaneous
          });
        }

        setChartData(transformedData);
      } catch (err) {
        setError('Failed to load chart data. Please try again later.');
        console.error('Error fetching chart data:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchChartData();
  }, [filters]);

  const formatCurrency = (value: number) => {
    return `$${value.toFixed(2)}`;
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
          No expense data available for the selected period.
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow p-6">
      <h3 className="text-xl font-semibold text-gray-800 mb-4">{title}</h3>
      <ResponsiveContainer width="100%" height={height}>
        <PieChart>
          <Pie
            data={chartData}
            cx="50%"
            cy="50%"
            labelLine={false}
            label={(entry) => `${entry.name}: ${formatCurrency(entry.value)}`}
            outerRadius={120}
            fill="#8884d8"
            dataKey="value"
          >
            {chartData.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={entry.color} />
            ))}
          </Pie>
          <Tooltip formatter={(value: number) => formatCurrency(value)} />
          <Legend />
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
};

export default ExpensePieChart;
