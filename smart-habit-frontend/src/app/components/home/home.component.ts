import { Component, ChangeDetectionStrategy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HomeData, HabitItem, AISuggestion } from '../../models/home/home.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {
  readonly data = signal<HomeData>({
    aiBadge: {
      icon: 'auto_awesome',
      label: 'AI-Powered Insights',
    },
    hero: {
      title: 'Master your routine.',
      highlightedText: 'Amplify your potential.',
      subtitle:
        'Achieve your peak performance by tracking habits with AI insights. SmartHabit learns your patterns and guides you into a continuous flow state.',
    },
    cta: {
      label: 'Start Tracking Free',
      icon: 'arrow_forward',
    },
    todayFocus: {
      title: "Today's Focus",
      habits: [
        {
          id: '1',
          name: 'Morning Workout',
          duration: '45 MIN',
          intensity: 'high',
          icon: 'fitness_center',
          status: 'done',
        },
        {
          id: '2',
          name: 'Deep Work Session',
          duration: '2 HOURS',
          intensity: 'medium',
          icon: 'menu_book',
          status: 'pending',
        },
      ],
    },
    aiInsight: {
      title: 'AI Insight',
      label: 'AI Insight',
      suggestion: {
        id: '1',
        message:
          "You're 30% more likely to complete your \"Deep Work\" habit if you schedule it immediately after your morning workout.",
        relatedHabit: 'Deep Work',
      },
    },
    topNav: {
      logo: 'SmartHabit',
      links: [
        {
          label: 'GitHub',
          href: 'https://github.com/j-olivera/smart-habit',
          icon: 'code',
        },
      ],
    },
    actions: {
      login: {
        label: 'Log In',
        variant: 'outline',
      },
      signup: {
        label: 'Sign Up',
        variant: 'primary',
      },
    },
    footer: {
      logo: 'SmartHabit',
      links: [
        { label: 'Privacy Policy', href: '#' },
        { label: 'Terms of Service', href: '#' },
        { label: 'Contact', href: '#' },
        { label: 'Changelog', href: '#' },
      ],
      copyright: '© 2026 SmartHabit. Engineered for high performance.',
    },
  });

  onLogin(): void {
    console.log('Login clicked');
  }

  onSignup(): void {
    console.log('Signup clicked');
  }

  onCtaClick(): void {
    console.log('CTA clicked');
  }

  onApplySuggestion(suggestion: AISuggestion): void {
    console.log('Apply suggestion:', suggestion.message);
  }

  trackByHabitId(index: number, habit: HabitItem): string {
    return habit.id;
  }
}