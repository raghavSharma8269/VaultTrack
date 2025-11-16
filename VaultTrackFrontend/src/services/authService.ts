import apiClient from '../api/client';

export interface SignupData {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface LoginData {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  firstName: string;
  lastName: string;
  userId: string;
  role: string;
}

class AuthService {
  /**
   * Register a new user
   * @param data - Signup data including email, password, firstName, lastName
   * @returns AuthResponse with token and user details
   */
  async signup(data: SignupData): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>('/api/auth/register', data);

    if (response.data.token) {
      localStorage.setItem('authToken', response.data.token);
    }

    return response.data;
  }

  /**
   * Login an existing user
   * @param data - Login data including email and password
   * @returns AuthResponse with token and user details
   */
  async login(data: LoginData): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>('/api/auth/login', data);

    if (response.data.token) {
      localStorage.setItem('authToken', response.data.token);
    }

    return response.data;
  }

  /**
   * Logout the current user
   */
  logout(): void {
    localStorage.removeItem('authToken');
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    return !!localStorage.getItem('authToken');
  }
}

export default new AuthService();
