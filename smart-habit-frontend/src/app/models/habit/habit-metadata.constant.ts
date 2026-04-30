import { HabitType } from './habit.model';

export interface HabitMetadata {
  type: HabitType;
  title: string;
  icon: string;
  colorClass: string;
  bgClass: string;
  description: string;
}

export const FIXED_HABITS_METADATA: HabitMetadata[] = [
  {
    type: 'STUDY',
    title: 'Study',
    icon: 'book',
    colorClass: 'text-blue-500',
    bgClass: 'bg-blue-500/10 border-blue-500/20',
    description: 'Track your study hours and subjects.'
  },
  {
    type: 'EXERCISE',
    title: 'Exercise',
    icon: 'fitness_center',
    colorClass: 'text-orange-500',
    bgClass: 'bg-orange-500/10 border-orange-500/20',
    description: 'Log your workouts and intensity.'
  },
  {
    type: 'NUTRITION',
    title: 'Nutrition',
    icon: 'restaurant',
    colorClass: 'text-green-500',
    bgClass: 'bg-green-500/10 border-green-500/20',
    description: 'Track your meals and water intake.'
  },
  {
    type: 'MOOD',
    title: 'Mood',
    icon: 'mood',
    colorClass: 'text-yellow-500',
    bgClass: 'bg-yellow-500/10 border-yellow-500/20',
    description: 'Reflect on how you felt today.'
  },
  {
    type: 'SLEEP',
    title: 'Sleep',
    icon: 'bedtime',
    colorClass: 'text-indigo-500',
    bgClass: 'bg-indigo-500/10 border-indigo-500/20',
    description: 'Monitor your sleep quality and duration.'
  }
];
