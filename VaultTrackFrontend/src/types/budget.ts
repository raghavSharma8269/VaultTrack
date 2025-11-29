export enum BudgetPeriod {
  WEEKLY = 'WEEKLY',
  MONTHLY = 'MONTHLY'
}

export interface Budget {
  budgetId: string;
  budgetAmount: number;
  periodType: BudgetPeriod;
  currentSpent: number;
  alertThreshold: number; // Percentage (e.g., 80 for 80%)
  lastResetDate: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateBudgetData {
  budgetAmount: number;
  alertThreshold?: number; // Optional, defaults to 80% on backend
  accountId: string;
}

export interface UpdateBudgetData {
  budgetId: string;
  budgetAmount?: number;
  alertThreshold?: number;
  isActive?: boolean;
}
