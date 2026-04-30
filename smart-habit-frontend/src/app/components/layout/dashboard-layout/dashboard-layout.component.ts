import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';

@Component({
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, SidebarComponent],
  template: `
    <div class="flex h-screen bg-zinc-950 text-zinc-100 overflow-hidden">
      <!-- Sidebar -->
      <app-sidebar class="hidden md:block"></app-sidebar>
      
      <!-- Main Content Area -->
      <main class="flex-grow md:ml-64 relative flex flex-col h-screen overflow-y-auto overflow-x-hidden">
        <!-- Background Glow Effects -->
        <div class="absolute top-0 left-1/2 -translate-x-1/2 w-[800px] h-[500px] bg-primary opacity-[0.03] blur-[100px] rounded-full pointer-events-none z-0"></div>
        <div class="absolute bottom-0 right-0 w-[500px] h-[500px] bg-violet-500 opacity-[0.02] blur-[100px] rounded-full pointer-events-none z-0"></div>
        
        <!-- Router Outlet Content -->
        <div class="relative z-10 p-6 md:p-10 w-full max-w-7xl mx-auto flex-grow">
          <router-outlet></router-outlet>
        </div>
      </main>
    </div>
  `
})
export class DashboardLayoutComponent {}
