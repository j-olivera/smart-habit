export interface RegisterRequest {
  name: string;
  email: string;
  password?: string;
}

export interface LoginRequest {
  email: string;
  password?: string;
}

export interface UserResponse {
  id: number;
  name: string;
  email: string;
  role: string;
  createdAt: string; 
  active: boolean;
}

export interface TokenResponse {
  accessToken: string;
}

export interface MessageResponse {
  message: string;
}
