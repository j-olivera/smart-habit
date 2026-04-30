import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DailyEntry } from '../../models/habit/habit.model';
import { environment } from '../../../environments/environment';
import { FIXED_HABITS_METADATA } from '../../models/habit/habit-metadata.constant';

export interface HabitViewState {
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
  private readonly LOGS_API_URL = `${environment.apiUrl}/logs`;

  // State
  private state = signal<{
    dailyEntry: DailyEntry | null;
    loading: boolean;
    error: string | null;
  }>({
    dailyEntry: null,
    loading: false,
    error: null
  });

  // --- SELECTORS (Reactividad fina con Signals) ---
  readonly dailyEntry = computed(() => this.state().dailyEntry);
  readonly isLoading = computed(() => this.state().loading);
  readonly error = computed(() => this.state().error);

  /**
   * Computed que transforma el modelo de datos crudo del backend en un 
   * "View State" listo para ser renderizado por el Bento Grid del Dashboard.
   * Cruza la data estática (iconos/colores) con los logs reales.
   */
  readonly habitsGrid = computed<HabitViewState[]>(() => {
    const entry = this.state().dailyEntry;

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

    // 2. Mapeamos los hábitos personales que el usuario haya creado para hoy
    const personalHabits: HabitViewState[] = (entry?.personalLogs || []).map(log => ({
      type: 'PERSONAL',
      title: log.description || 'Personal Habit',
      icon: 'star',
      colorClass: 'text-violet-500',
      bgClass: 'bg-violet-500/10 border-violet-500/20',
      description: `${log.hours} hours logged`,
      status: 'done',
      logData: log,
      isPersonal: true
    }));

    return [...fixedHabits, ...personalHabits];
  });

  // --- ACTIONS ---

  /**
   * Recupera el estado del día actual. 
   * FLUJO: Si no existe (404), dispara la creación automática.
   */
  fetchTodayLogs(): void {
    this.state.update(s => ({ ...s, loading: true, error: null }));
    const today = new Date().toISOString().split('T')[0];

    this.http.get<any>(`${this.API_URL}/${today}`).subscribe({
      next: (response) => {
        const data = response.data || response;
        this.state.update(s => ({ ...s, dailyEntry: data, loading: false }));
      },
      error: (err) => {
        // LÓGICA AUTOMÁTICA: Si el día no existe, lo creamos
        if (err.status === 404) {
          console.log('--- SDD: Day entry missing, creating automatically... ---');
          this.createDailyEntry(today);
        } else {
          this.state.update(s => ({ ...s, error: err.message, loading: false }));
        }
      }
    });
  }

  /**
   * Crea un nuevo registro de día (DailyEntry) en el backend.
   */
  private createDailyEntry(date: string): void {
    // El backend espera un POST a /api/daily-entries?date=...
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
   * Registra un log de hábito (Upsert).
   * Envía la data al endpoint correspondiente y actualiza el estado localmente.
   */
  saveHabitLog(type: string, data: any): void {
    const entryId = this.state().dailyEntry?.id;
    if (!entryId) {
      this.state.update(s => ({ ...s, error: 'Daily entry not ready. Try again in a moment.' }));
      return;
    }

    const endpoint = type.toLowerCase();
    const payload = { ...data, entryId };

    // Flags requeridos por los DTOs de Java para marcar completitud
    if (type === 'STUDY') payload.studied = true;
    if (type === 'EXERCISE') payload.exercised = true;

    this.http.post<any>(`${this.LOGS_API_URL}/${endpoint}`, payload).subscribe({
      next: (savedLog) => {
        // Actualizamos solo la parte del estado que cambió (Optimistic-ish update)
        this.updateLocalLog(type, savedLog.data || savedLog);
      },
      error: (err) => {
        this.state.update(s => ({ ...s, error: `Failed to save ${type}: ${err.message}` }));
      }
    });
  }

  /**
   * Actualiza el signal de estado localmente sin necesidad de re-fetch.
   * Esto mantiene la UI ultra-rápida.
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
          const index = newEntry.personalLogs.findIndex(l => l.id === logData.id);
          if (index >= 0) newEntry.personalLogs[index] = logData;
          else newEntry.personalLogs = [...newEntry.personalLogs, logData];
          break;
      }
      return { ...s, dailyEntry: newEntry };
    });
  }
}