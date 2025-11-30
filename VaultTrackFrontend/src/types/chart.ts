export interface PieChartData {
  totalAmount: number;
  foodAmount: number;
  utilitiesAmount: number;
  entertainmentAmount: number;
  transportationAmount: number;
  healthcareAmount: number;
  educationAmount: number;
  groceriesAmount: number;
  rentAmount: number;
  salaryAmount: number;
  investmentsAmount: number;
  miscellaneousAmount: number;
}

export interface ChartFilters {
  transactionName?: string;
  transactionType?: 'INCOME' | 'EXPENSE';
  transactionCategory?: string;
  start?: string;
  end?: string;
  accountId?: string;
}

export interface PieChartEntry {
  name: string;
  value: number;
  color: string;
  [key: string]: string | number;
}

export interface BarChartData {
  month: string;
  income: number;
  expense: number;
}

export interface LineChartData {
  date: string;
  balance: number;
}

export interface LineChartFilters {
  accountId: string;
  start?: string;
  end?: string;
}
