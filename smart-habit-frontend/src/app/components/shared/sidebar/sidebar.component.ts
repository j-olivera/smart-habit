import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html'
})
export class SidebarComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  navItems = [
    { label: 'Dashboard', icon: 'dashboard', route: '/app/dashboard' },
    { label: 'Reports', icon: 'bar_chart', route: '/app/reports' }
  ];

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
