import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { BehaviorSubject, tap, catchError, of } from 'rxjs';
import { UserView } from '../models/user-view.model';
import { LoginRequest } from '../models/login-request.model';
import { RegisterRequest } from '../models/register-request.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private apiBaseUrl = 'http://localhost:8080/api';

  private currentUserSource = new BehaviorSubject<UserView | null>(null);
  currentUser$ = this.currentUserSource.asObservable();

  constructor() {
    this.checkAuthStatus().subscribe();
  }

  checkAuthStatus() {
    return this.http.get<UserView>(`${this.apiBaseUrl}/auth/me`, { observe: 'response' }).pipe(
      tap((response: HttpResponse<UserView>) => {
        // 204 No Content means user is not logged in
        if (response.status === 204 || response.body === null) {
          this.currentUserSource.next(null);
        } else {
          this.currentUserSource.next(response.body);
        }
      }),
      catchError((error) => {
        // Any error (404, 401, etc.) means user is not logged in
        this.currentUserSource.next(null);
        return of(null);
      }),
    );
  }

  login(request: LoginRequest) {
    return this.http
      .post<UserView>(`${this.apiBaseUrl}/auth/login`, request)
      .pipe(tap((user) => this.currentUserSource.next(user)));
  }

  register(request: RegisterRequest) {
    return this.http
      .post<UserView>(`${this.apiBaseUrl}/auth/register`, request)
      .pipe(tap((user) => this.currentUserSource.next(user)));
  }

  logout() {
    return this.http
      .post(`${this.apiBaseUrl}/auth/logout`, {})
      .pipe(tap(() => this.currentUserSource.next(null)));
  }
}
