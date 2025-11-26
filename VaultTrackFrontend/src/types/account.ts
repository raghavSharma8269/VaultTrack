export enum AccountType {
  SAVINGS = 'SAVINGS',
  CHECKING = 'CHECKING',
  CREDIT = 'CREDIT',
  INVESTMENT = 'INVESTMENT',
  OTHER = 'OTHER'
}

export interface Account {
  accountId: string;
  accountName: string;
  accountType: AccountType;
  currentBalance: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateAccountData {
  accountName: string;
  accountType: AccountType;
  budget?: any;
}

export interface UpdateAccountData {
  accountId: string;
  accountName?: string;
  accountType?: string;
  budget?: any;
}
