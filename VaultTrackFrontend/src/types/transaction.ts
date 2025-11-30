export enum TransactionType {
  INCOME = 'INCOME',
  EXPENSE = 'EXPENSE'
}

export enum TransactionCategory {
  FOOD = 'FOOD',
  UTILITIES = 'UTILITIES',
  ENTERTAINMENT = 'ENTERTAINMENT',
  TRANSPORTATION = 'TRANSPORTATION',
  HEALTHCARE = 'HEALTHCARE',
  EDUCATION = 'EDUCATION',
  GROCERIES = 'GROCERIES',
  RENT = 'RENT',
  SALARY = 'SALARY',
  INVESTMENTS = 'INVESTMENTS',
  MISCELLANEOUS = 'MISCELLANEOUS'
}

export interface CreateTransactionData {
  amount: number;
  transactionName?: string;
  transactionCategory: TransactionCategory;
  transactionType: TransactionType;
  accountId: string;
}

export interface Transaction {
  transactionId: string;
  transactionName: string;
  amount: number;
  transactionCategory: TransactionCategory;
  transactionType: TransactionType;
  createdAt: string;
  accountId: string;
  accountName: string;
}

export interface TransactionFilters {
  start?: string;
  end?: string;
  transactionCategory?: TransactionCategory;
  transactionType?: TransactionType;
  transactionName?: string;
  accountId?: string;
}
