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
    const response = await apiClient.post<AuthResponse>('/auth/register', data);

    if (response.data.token) {
      localStorage.setItem('authToken', response.data.token);
      // Store user data
      localStorage.setItem('user', JSON.stringify({
        email: response.data.email,
        firstName: response.data.firstName,
        lastName: response.data.lastName,
        userId: response.data.userId,
        role: response.data.role
      }));
    }

    return response.data;
  }

  /**
   * Login an existing user
   * @param data - Login data including email and password
   * @returns AuthResponse with token and user details
   */
  async login(data: LoginData): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>('/auth/login', data);

    if (response.data.token) {
      localStorage.setItem('authToken', response.data.token);
      // Store user data
      localStorage.setItem('user', JSON.stringify({
        email: response.data.email,
        firstName: response.data.firstName,
        lastName: response.data.lastName,
        userId: response.data.userId,
        role: response.data.role
      }));
    }

    return response.data;
  }

  /**
   * Logout the current user
   */
  logout(): void {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    return !!localStorage.getItem('authToken');
  }

  /**
   * Get the current user data
   * @returns User object or null if not authenticated
   */
  getUser(): { email: string; firstName: string; lastName: string; userId: string; role: string } | null {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }

  /**
   * Get the current user's role
   * @returns User role string or null if not authenticated
   */
  getUserRole(): string | null {
    const user = this.getUser();
    return user?.role || null;
  }

  /**
   * Check if the current user is an admin
   * @returns true if user has ADMIN role, false otherwise
   */
  isAdmin(): boolean {
    return this.getUserRole() === 'ADMIN';
  }
}

export default new AuthService();