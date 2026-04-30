import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/home/home.component').then((m) => m.HomeComponent),
  },
  {
    path: 'home',
    loadComponent: () => import('./components/home/home.component').then((m) => m.HomeComponent),
  },
  {
    path: 'register',
    loadComponent: () => import('./components/auth/register/register.component').then((m) => m.RegisterComponent),
  },
  {
    path: 'login',
    loadComponent: () => import('./components/auth/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'app',
    canActivate: [authGuard], // <-- Blindaje activado
    loadComponent: () => import('./components/layout/dashboard-layout/dashboard-layout.component').then((m) => m.DashboardLayoutComponent),
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        loadComponent: () => import('./components/dashboard/dashboard.component').then((m) => m.DashboardComponent),
      },
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];