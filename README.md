# VaultTrack 

A full-stack financial management application for tracking accounts, transactions, budgets, and recurring payments with AI-powered insights.

## 🛠 Tech Stack

### Backend
- Java 21
- Spring Boot 3.5.7
- PostgreSQL
- Spring Security + JWT
- Maven

### Frontend
- React 19.1.1
- TypeScript 5.9.3
- Tailwind CSS 4.1.17
- Axios 1.13.2
- Recharts 3.5.1
- Vite 7.1.7

## 📦 Requirements

- Java 21+
- Node.js 18+
- PostgreSQL 12+
- Maven 3.6+ (included)

## 🚀 Installation & Setup

### 1. Database Setup

```sql
-- Create database
CREATE DATABASE vaulttrack;
CREATE USER vaulttrack_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE vaulttrack TO vaulttrack_user;
```

### 2. Backend Setup

```bash
cd VaultTrackBackend

# Create .env file
cat > .env << EOF
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/vaulttrack
SPRING_DATASOURCE_USERNAME=vaulttrack_user
SPRING_DATASOURCE_PASSWORD=your_password
JWT_SECRET=your-long-secret-key-at-least-256-bits
JWT_EXPIRATION=86400000
EOF

# Copy application properties
cp src/main/resources/application.properties.example src/main/resources/application.properties

# Build and run
./mvnw clean install
./mvnw spring-boot:run
```

Backend runs on: `http://localhost:8080`

### 3. Frontend Setup

```bash
cd VaultTrackFrontend

# Install dependencies
npm install

# Run dev server
npm run dev
```

Frontend runs on: `http://localhost:5173`

## 📁 Project Structure

### Backend
```
VaultTrackBackend/
├── src/main/java/com/example/VaultTrackBackend/
│   ├── config/              # App configuration
│   ├── controller/          # REST endpoints
│   ├── dto/                 # Data transfer objects
│   ├── model/
│   │   ├── entity/          # Database entities
│   │   └── enums/           # Enums (AccountType, TransactionType, etc.)
│   ├── repository/          # Database access
│   ├── security/            # JWT & security config
│   └── service/             # Business logic
│       ├── account/
│       ├── admin/
│       ├── auth/
│       ├── budget/
│       ├── charts/
│       ├── recurringTransaction/
│       └── transaction/
└── src/main/resources/
    ├── application.properties
    └── templates/emails/    # Email templates
```

### Frontend
```
VaultTrackFrontend/
├── src/
│   ├── api/                 # API client config
│   ├── components/          # Reusable components
│   │   ├── Header.tsx
│   │   └── analytics/       # Chart components
│   ├── pages/               # Main pages
│   │   ├── AccountManagement.tsx
│   │   ├── AdminDashboard.tsx
│   │   ├── Homepage.tsx
│   │   ├── Login.tsx
│   │   ├── Signup.tsx
│   │   ├── TransactionManagement.tsx
│   │   └── RecurringTransactionManagement.tsx
│   ├── services/            # API service functions
│   │   ├── authService.ts
│   │   ├── accountService.ts
│   │   ├── transactionService.ts
│   │   ├── budgetService.ts
│   │   └── chartService.ts
│   └── types/               # TypeScript interfaces
```

## 📖 How to Use

### 1. Sign Up / Login
- Go to `http://localhost:5173`
- Click "Sign Up" to create account
- Or "Login" if you have an account

### 2. Manage Accounts
- **Create**: Add checking, savings, credit card, investment, or loan accounts
- **View**: See all accounts with balances
- **Search**: Filter by name or type
- **Edit**: Update account details
- **Delete**: Remove account (deletes all transactions)

### 3. Manage Transactions
- **Create**: Add income, expense, or transfer transactions
- **Categories**: Food, Transportation, Shopping, Entertainment, Bills, etc.
- **Filter**: By type, category, account, date, or description
- **Export/Import**: Download or upload CSV files

### 4. Recurring Transactions
- **Create**: Set up automatic recurring payments/income
- **Frequency**: Daily, Weekly, Monthly, or Yearly
- **Auto-process**: System automatically creates transactions when due

### 5. Budgets
- **Create**: Set spending limits per category and time period
- **Track**: See progress with visual bars
- **Alerts**: Get email when reaching 80% or exceeding budget
- **Auto-reset**: Budgets reset automatically each period

### 6. Analytics
- **Pie Chart**: Expense breakdown by category
- **Bar Chart**: Monthly income vs expense comparison
- **Line Chart**: Account balance trends over time
- **AI Insights**: Get personalized financial advice

### 7. Admin Features (ADMIN role only)
- View all users
- Search by email
- Filter by role (USER/ADMIN)
- Update user roles

## 🔑 Default Accounts

After signup, you have a **USER** role. To become an admin:

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'your@email.com';
```

## 🔧 Common Issues

**Database connection failed**
- Check PostgreSQL is running
- Verify credentials in `.env`

**Port already in use**
- Backend: Change port in `.env` or kill process on 8080
- Frontend: Change port or kill process on 5173

**Frontend can't reach backend**
- Verify backend is running on `http://localhost:8080`
- Check `src/api/client.ts` for correct baseURL

## 📝 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login user |

### Accounts
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/accounts?query={name}&accountType={type}` | Get all accounts (with filters) |
| POST | `/accounts` | Create account |
| PUT | `/accounts` | Update account |
| DELETE | `/accounts/{accountId}` | Delete account |

### Transactions
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/transactions?transactionName={name}&transactionType={type}&transactionCategory={category}&accountId={id}&start={date}&end={date}` | Get transactions (with filters) |
| POST | `/transactions` | Create transaction |
| DELETE | `/transactions/{transactionId}` | Delete transaction |
| GET | `/transactions/export/csv` | Export transactions to CSV |
| POST | `/transactions/import/csv` | Import transactions from CSV |

### Recurring Transactions
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/recurring-transactions` | Get all recurring transactions |
| POST | `/recurring-transactions` | Create recurring transaction |
| PUT | `/recurring-transactions/{id}` | Update recurring transaction |
| DELETE | `/recurring-transactions/{id}` | Delete recurring transaction |
| PATCH | `/recurring-transactions/{id}/pause` | Pause recurring transaction |
| PATCH | `/recurring-transactions/{id}/resume` | Resume recurring transaction |

### Budgets
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/budgets/account/{accountId}` | Get budgets by account |
| POST | `/budgets` | Create budget |
| PUT | `/budgets` | Update budget |

### Charts & Analytics
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/charts/pie-chart?accountId={id}&start={date}&end={date}` | Get pie chart data (expenses by category) |
| GET | `/charts/bar-chart?accountId={id}&start={date}&end={date}` | Get bar chart data (income vs expense) |
| GET | `/charts/line-chart?accountId={id}&start={date}&end={date}` | Get line chart data (balance trends) |

### AI Insights
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/ai/feedback?start={date}&end={date}&accountId={id}` | Get AI financial feedback |

### Admin (Requires ADMIN role)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin?email={email}&role={role}` | Get all users (with filters) |
| PATCH | `/admin/{userId}?role={ROLE}` | Update user role |
