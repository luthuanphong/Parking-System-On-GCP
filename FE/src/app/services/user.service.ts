import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { LoginRequest, SignupRequest, DepositRequest } from '../models/requests.model';
import { LoginResponse } from '../models/responses.model';
import { User } from '../models/user.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;
  private tokenKey = 'auth_token';
  private currentUser: LoginResponse | null = null;

  constructor(private http: HttpClient) {}

  /**
   * Login user
   */
  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response => {
        if (response.token) {
          this.setToken(response.token);
          this.currentUser = response; // Store current user data
        }
      })
    );
  }

  /**
   * Sign up new user
   */
  signup(request: SignupRequest): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/signup`, request);
  }

  /**
   * Deposit money to user account
   */
  deposit(userId: number, request: DepositRequest): Observable<User> {
    return this.http.patch<User>(`${this.apiUrl}/${userId}/deposit`, request);
  }

  /**
   * Store authentication token
   */
  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  /**
   * Get authentication token
   */
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  /**
   * Remove authentication token
   */
  removeToken(): void {
    localStorage.removeItem(this.tokenKey);
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  /**
   * Logout user
   */
  logout(): void {
    this.removeToken();
    this.currentUser = null; // Clear current user data
  }

  /**
   * Get current user data
   */
  getCurrentUser(): LoginResponse | null {
    return this.currentUser;
  }
}
