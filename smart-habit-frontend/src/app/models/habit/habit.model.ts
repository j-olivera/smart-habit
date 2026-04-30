export interface StudyLog {
  id: number;
  completed: boolean;
  hours: number;
  subject: string;
}

export interface ExerciseLog {
  id: number;
  completed: boolean;
  durationMinutes?: number;
  intensity?: 'LOW' | 'MEDIUM' | 'HIGH';
}

export interface MoodLog {
  id: number;
  completed: boolean;
  moodType: 'HAPPY' | 'SAD' | 'NEUTRAL' | 'STRESSED' | 'ANXIOUS' | 'ENERGETIC';
  note?: string;
}

export interface NutritionLog {
  id: number;
  completed: boolean;
  meals: string[];
}

export interface SleepLog {
  id: number;
  completed: boolean;
  hours: number;
  quality: 'POOR' | 'FAIR' | 'GOOD' | 'EXCELLENT';
}

export interface PersonalLog {
  id: number;
  habitId: number;
  entryId: number;
  completed: boolean;
  hours: number;
  description?: string;
}

export interface DailyEntry {
  id: number;
  date: string;
  studyLog: StudyLog | null;
  exerciseLog: ExerciseLog | null;
  moodLog: MoodLog | null;
  nutritionLog: NutritionLog | null;
  sleepLog: SleepLog | null;
  personalLogs: PersonalLog[];
}

export type HabitType = 'STUDY' | 'EXERCISE' | 'MOOD' | 'NUTRITION' | 'SLEEP' | 'PERSONAL';
