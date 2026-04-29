import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, RegisterRequest, TokenResponse, UserResponse, MessageResponse } from '../../models/auth/auth.model';
import { HandleError } from '../../models/error/handleError.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/auth`;

  // Signal para manejar el estado del usuario logueado en caso de querer mostrarlo globalmente
  readonly currentUser = signal<UserResponse | null>(null);

  register(request: RegisterRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.baseUrl}/register`, request)
      .pipe(catchError(this.handleError));
  }

  login(request: LoginRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.baseUrl}/login`, request, {
      withCredentials: true
    }).pipe(catchError(this.handleError));
  }

  refresh(): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.baseUrl}/refresh`, {}, {
      withCredentials: true
    }).pipe(catchError(this.handleError));
  }

  logout(): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.baseUrl}/logout`, {}, {
      withCredentials: true
    }).pipe(catchError(this.handleError));
  }
  private handleError(error: HttpErrorResponse): Observable<never> {
    let appError: HandleError = { code: 'UNKNOW_ERROR' };

    if (error.status === 0) {
      appError.code = 'SERVER_DOWN';
    } else if (error.status === 400) {
      appError.code = 'INSUFFICIENT_DATA';
      appError.details = error.error?.details || error.error?.errors;
    } else if (error.status === 401) {
      // Dependiendo del error del backend puede ser credentials o token expirado
      appError.code = error.error?.message?.toLowerCase().includes('credential')
        ? 'INVALID_CREDENTIALS'
        : 'UNAUTHORIZED';
    } else if (error.status === 404) {
      appError.code = 'NOT_FOUND';
    } else if (error.status === 409) {
      appError.code = 'EMAIL_TAKEN';
    }

    // Retornamos el error formateado para que los componentes lo consuman limpiamente
    return throwError(() => appError);
  }
}
