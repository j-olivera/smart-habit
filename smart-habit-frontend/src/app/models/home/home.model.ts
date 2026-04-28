// Interfaces para el componente Home

export interface NavLink {
  label: string;
  href: string;
  icon?: string;
}

export interface ButtonAction {
  label: string;
  variant: 'primary' | 'secondary' | 'outline';
  icon?: string;
}

export interface HabitItem {
  id: string;
  name: string;
  duration: string;
  intensity: 'high' | 'medium' | 'low';
  icon: string;
  status: 'done' | 'pending' | 'in-progress';
}

export interface AISuggestion {
  id: string;
  message: string;
  relatedHabit?: string;
}

export interface FooterLink {
  label: string;
  href: string;
}

export interface HomeData {
  aiBadge: {
    icon: string;
    label: string;
  };
  hero: {
    title: string;
    highlightedText: string;
    subtitle: string;
  };
  cta: {
    label: string;
    icon: string;
  };
  todayFocus: {
    title: string;
    habits: HabitItem[];
  };
  aiInsight: {
    title: string;
    label: string;
    suggestion: AISuggestion;
  };
  topNav: {
    logo: string;
    links: NavLink[];
  };
  actions: {
    login: ButtonAction;
    signup: ButtonAction;
  };
  footer: {
    logo: string;
    links: FooterLink[];
    copyright: string;
  };
}