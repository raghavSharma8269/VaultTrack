import { TransactionCategory, TransactionType } from './transaction';

export enum RecurringFrequency {
  DAILY = 'DAILY',
  WEEKLY = 'WEEKLY',
  MONTHLY = 'MONTHLY',
  YEARLY = 'YEARLY'
}

export interface RecurringTransaction {
  recurringTransactionId: string;
  transactionName: string;
  amount: number;
  transactionCategory: TransactionCategory;
  transactionType: TransactionType;
  recurringFrequency: RecurringFrequency;
  nextExecutionDate: string; // YYYY-MM-DD format
  isActive: boolean;
  accountId: string;
  accountName: string;
  createdAt: string;
}

export interface CreateRecurringTransactionData {
  transactionName: string;
  amount: number;
  transactionCategory: TransactionCategory;
  transactionType: TransactionType;
  recurringFrequency: RecurringFrequency;
  nextExecutionDate: string; // YYYY-MM-DD format
  accountId: string;
}

export interface UpdateRecurringTransactionData {
  transactionName: string;
  amount: number;
  transactionCategory: TransactionCategory;
  transactionType: TransactionType;
  recurringFrequency: RecurringFrequency;
  nextExecutionDate: string; // YYYY-MM-DD format
  accountId: string;
}
