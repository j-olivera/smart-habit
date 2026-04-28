import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';
import { LoginRequest, RegisterRequest, TokenResponse, UserResponse, MessageResponse } from '../../models/auth/auth.model';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';

describe('AuthService', () => {
  let service: AuthService;
  let httpTestingController: HttpTestingController;
  const baseUrl = `${environment.apiUrl}/auth`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(AuthService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send a POST request on register', () => {
    const mockRequest: RegisterRequest = { name: 'Test', email: 'test@test.com', password: 'password' };
    const mockResponse: UserResponse = {
      id: 1,
      name: 'Test',
      email: 'test@test.com',
      role: 'USER',
      createdAt: new Date().toISOString(),
      active: true
    };

    service.register(mockRequest).subscribe(res => {
      expect(res).toEqual(mockResponse);
    });

    const req = httpTestingController.expectOne(`${baseUrl}/register`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockRequest);
    req.flush(mockResponse);
  });

  it('should send a POST request with credentials on login', () => {
    const mockRequest: LoginRequest = { email: 'test@test.com', password: 'password' };
    const mockResponse: TokenResponse = { accessToken: 'mock-jwt-token' };

    service.login(mockRequest).subscribe(res => {
      expect(res).toEqual(mockResponse);
    });

    const req = httpTestingController.expectOne(`${baseUrl}/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.withCredentials).toBe(true);
    expect(req.request.body).toEqual(mockRequest);
    req.flush(mockResponse);
  });

  it('should send a POST request with credentials on refresh', () => {
    const mockResponse: TokenResponse = { accessToken: 'new-mock-jwt-token' };

    service.refresh().subscribe(res => {
      expect(res).toEqual(mockResponse);
    });

    const req = httpTestingController.expectOne(`${baseUrl}/refresh`);
    expect(req.request.method).toBe('POST');
    expect(req.request.withCredentials).toBe(true);
    expect(req.request.body).toEqual({});
    req.flush(mockResponse);
  });

  it('should send a POST request with credentials on logout', () => {
    const mockResponse: MessageResponse = { message: 'Logged out' };

    service.logout().subscribe(res => {
      expect(res).toEqual(mockResponse);
    });

    const req = httpTestingController.expectOne(`${baseUrl}/logout`);
    expect(req.request.method).toBe('POST');
    expect(req.request.withCredentials).toBe(true);
    expect(req.request.body).toEqual({});
    req.flush(mockResponse);
  });
});
