import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DailyEntry, Habit } from '../../models/habit/habit.model';
import { environment } from '../../../environments/environment';
import { FIXED_HABITS_METADATA } from '../../models/habit/habit-metadata.constant';

export interface HabitViewState {
  id?: number; // Added to identify personal habits
  type: string;
  title: string;
  icon: string;
  colorClass: string;
  bgClass: string;
  description: string;
  status: 'pending' | 'done';
  logData: any | null; // The actual log data if done
  isPersonal: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class HabitStateService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/daily-entries`;
  private readonly HABITS_API_URL = `${environment.apiUrl}/habits`;
  private readonly LOGS_API_URL = `${environment.apiUrl}/logs`;

  // State
  private state = signal<{
    dailyEntry: DailyEntry | null;
    habitDefinitions: Habit[];
    loading: boolean;
    error: string | null;
  }>({
    dailyEntry: null,
    habitDefinitions: [],
    loading: false,
    error: null
  });

  // --- SELECTORS (Reactividad fina con Signals) ---
  readonly dailyEntry = computed(() => this.state().dailyEntry);
  readonly habitDefinitions = computed(() => this.state().habitDefinitions);
  readonly isLoading = computed(() => this.state().loading);
  readonly error = computed(() => this.state().error);

  /**
   * Computed que transforma el modelo de datos crudo del backend en un 
   * "View State" listo para ser renderizado por el Bento Grid del Dashboard.
   * Cruza la data estática (iconos/colores) con los logs reales.
   */
  readonly habitsGrid = computed<HabitViewState[]>(() => {
    const entry = this.state().dailyEntry;
    const definitions = this.state().habitDefinitions;

    // 1. Mapeamos los 5 hábitos fijos definidos en la constante de metadatos
    const fixedHabits: HabitViewState[] = FIXED_HABITS_METADATA.map(meta => {
      let logData = null;
      if (entry) {
        // Extraemos el log específico según el tipo de hábito
        switch (meta.type) {
          case 'STUDY': logData = entry.studyLog; break;
          case 'EXERCISE': logData = entry.exerciseLog; break;
          case 'NUTRITION': logData = entry.nutritionLog; break;
          case 'MOOD': logData = entry.moodLog; break;
          case 'SLEEP': logData = entry.sleepLog; break;
        }
      }
      return {
        ...meta,
        status: logData ? 'done' : 'pending',
        logData: logData,
        isPersonal: false
      };
    });

    // 2. Mapeamos los hábitos personales del catálogo
    const personalHabits: HabitViewState[] = definitions.map(habit => {
      const log = entry?.personalLogs.find(l => l.habitId === habit.id);
      
      return {
        id: habit.id,
        type: 'PERSONAL',
        title: habit.name,
        icon: 'star',
        colorClass: 'text-violet-500',
        bgClass: 'bg-violet-500/10 border-violet-500/20',
        description: habit.description || 'Custom habit',
        status: log ? 'done' : 'pending',
        logData: log || null,
        isPersonal: true
      };
    });

    return [...fixedHabits, ...personalHabits];
  });

  // --- ACTIONS ---

  /**
   * Recupera el estado del día actual y las definiciones de hábitos.
   */
  fetchTodayLogs(): void {
    this.state.update(s => ({ ...s, loading: true, error: null }));
    const today = new Date().toISOString().split('T')[0];

    // Primero traemos las definiciones
    this.http.get<any>(this.HABITS_API_URL).subscribe({
      next: (habitsResponse) => {
        const habitDefinitions = habitsResponse.data || habitsResponse;
        
        // Luego traemos los logs de hoy
        this.http.get<any>(`${this.API_URL}/${today}`).subscribe({
          next: (response) => {
            const data = response.data || response;
            this.state.update(s => ({ 
              ...s, 
              habitDefinitions,
              dailyEntry: data, 
              loading: false 
            }));
          },
          error: (err) => {
            if (err.status === 404) {
              this.state.update(s => ({ ...s, habitDefinitions }));
              this.createDailyEntry(today);
            } else {
              this.state.update(s => ({ ...s, error: err.message, loading: false }));
            }
          }
        });
      },
      error: (err) => {
        this.state.update(s => ({ ...s, error: 'Failed to load habit definitions.', loading: false }));
      }
    });
  }

  /**
   * Crea un nuevo registro de día (DailyEntry) en el backend.
   */
  private createDailyEntry(date: string): void {
    this.http.post<any>(`${this.API_URL}?date=${date}`, {}).subscribe({
      next: (response) => {
        const data = response.data || response;
        this.state.update(s => ({ ...s, dailyEntry: data, loading: false }));
      },
      error: (err) => {
        this.state.update(s => ({ ...s, error: 'Failed to initialize today\'s record.', loading: false }));
      }
    });
  }

  /**
   * Crea una nueva definición de hábito en el catálogo.
   */
  createHabit(request: { name: string, description: string, type: string }): void {
    this.state.update(s => ({ ...s, loading: true, error: null }));

    this.http.post<any>(this.HABITS_API_URL, request).subscribe({
      next: (savedHabit) => {
        const newHabit = savedHabit.data || savedHabit;
        this.state.update(s => ({
          ...s,
          habitDefinitions: [...s.habitDefinitions, newHabit],
          loading: false
        }));
      },
      error: (err) => {
        this.state.update(s => ({ ...s, error: `Failed to create habit: ${err.message}`, loading: false }));
      }
    });
  }

  /**
   * Registra un log de hábito (Upsert).
   */
  saveHabitLog(type: string, data: any): void {
    const entryId = this.state().dailyEntry?.id;
    if (!entryId) {
      this.state.update(s => ({ ...s, error: 'Daily entry not ready. Try again in a moment.' }));
      return;
    }

    const endpoint = type.toLowerCase();
    const payload = { ...data, entryId };

    this.http.post<any>(`${this.LOGS_API_URL}/${endpoint}`, payload).subscribe({
      next: (savedLog) => {
        this.updateLocalLog(type, savedLog.data || savedLog);
      },
      error: (err) => {
        this.state.update(s => ({ ...s, error: `Failed to save ${type}: ${err.message}` }));
      }
    });
  }

  /**
   * Actualiza el signal de estado localmente.
   */
  updateLocalLog(habitType: string, logData: any): void {
    this.state.update(s => {
      if (!s.dailyEntry) return s;
      const newEntry = { ...s.dailyEntry };

      switch (habitType) {
        case 'STUDY': newEntry.studyLog = logData; break;
        case 'EXERCISE': newEntry.exerciseLog = logData; break;
        case 'NUTRITION': newEntry.nutritionLog = logData; break;
        case 'MOOD': newEntry.moodLog = logData; break;
        case 'SLEEP': newEntry.sleepLog = logData; break;
        case 'PERSONAL':
          const index = newEntry.personalLogs.findIndex(l => l.habitId === logData.habitId);
          if (index >= 0) newEntry.personalLogs[index] = logData;
          else newEntry.personalLogs = [...newEntry.personalLogs, logData];
          break;
      }
      return { ...s, dailyEntry: newEntry };
    });
  }
}
