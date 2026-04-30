export interface StudyLog {
  id: number;
  entryId: number;
  studied: boolean; //de este dependen que esten los demas valores
  hours?: number;
  subject?: string;
  skipReason?: string;
}

export interface ExerciseLog {
  id: number;
  entryId: number;
  exercised: boolean; //de este dependen que esten los demas valores
  hours?: number;
  muscularGroup?: 'CHEST' | 'BACK' | 'LEGS' | 'ARMS' | 'ABDOMEN' | 'CARDIO';
  energyLevel?: number;
  skipReason?: string;
}

export interface MoodLog {
  id: number;
  entryId: number;
  mood: 'SAD' | 'DOWN' | 'NEUTRAL' | 'HAPPY' | 'EUPHORIC';
  hasObservations: boolean;
  eventDescription?: string;
  socialized: boolean;
  socialWith?: string;
}

export interface NutritionLog {
  id: number;
  entryId: number;
  rating: 'POOR' | 'REGULAR' | 'GOOD' | 'EXCELLENT'; //de este dependen que esten los demas valores
  hasObservation: boolean;
  metGoal?: boolean;
}

export interface SleepLog {
  id: number;
  entryId: number;
  hours: number;
  quality: 'BAD' | 'REGULAR' | 'GOOD' | 'EXCELLENT';
  napped: boolean; //de este dependen que esten los demas valores
  napHours?: number;
  napNeeded?: boolean;
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
