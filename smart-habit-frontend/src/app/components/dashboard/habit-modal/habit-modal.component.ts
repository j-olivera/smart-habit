import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StudyForm } from './forms/study-form.component';
import { ExerciseForm } from './forms/exercise-form.component';
import { MoodForm } from './forms/mood-form.component';
import { SleepForm } from './forms/sleep-form.component';
import { NutritionForm } from './forms/nutrition-form.component';
import { CreateHabitForm } from './forms/create-habit-form.component';
import { PersonalHabitForm } from './forms/personal-habit-form.component';
import { HabitStateService } from '../../../services/habit/habit-state.service';

@Component({
  selector: 'app-habit-modal',
  standalone: true,
  imports: [
    CommonModule,
    StudyForm,
    ExerciseForm,
    MoodForm,
    SleepForm,
    NutritionForm,
    CreateHabitForm,
    PersonalHabitForm
  ],
  template: `
    @if (isOpen) {
      <div class="fixed inset-0 z-50 flex items-center justify-center p-6">
        
        <!-- Backdrop -->
        <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" (click)="close.emit()"></div>
        
        <!-- Modal Content -->
        <div class="relative bg-zinc-900 border border-zinc-800 rounded-3xl
                   w-[520px] min-h-[400px] max-h-[85vh]
                   overflow-y-auto p-8 shadow-2xl z-10">
        <div class="absolute -top-24 -left-24 w-48 h-48 bg-primary/20 blur-[60px] rounded-full pointer-events-none"></div>

          <div class="flex justify-between items-center mb-8 relative z-10">
            <div>
              <h2 class="text-2xl font-bold text-white tracking-tight">{{ habitTitle }}</h2>
              <p class="text-sm text-zinc-500 font-medium">{{ isCreateMode ? 'Define a new habit' : 'Daily Registration' }}</p>
            </div>
            <button (click)="close.emit()" class="w-10 h-10 flex items-center justify-center rounded-xl bg-zinc-800 text-zinc-400 hover:text-white transition-all">
              <span class="material-symbols-outlined">close</span>
            </button>
          </div>
          
          <div class="relative z-10">
            @if (isCreateMode) {
              <app-create-habit-form (submitForm)="onCreateHabit($event)" (cancel)="close.emit()" />
            } @else {
              @switch (habitType) {
                @case ('STUDY') {
                  <app-study-form (submitForm)="onSaveLog($event)" (cancel)="close.emit()" />
                }
                @case ('EXERCISE') {
                  <app-exercise-form (submitForm)="onSaveLog($event)" (cancel)="close.emit()" />
                }
                @case ('MOOD') {
                  <app-mood-form (submitForm)="onSaveLog($event)" (cancel)="close.emit()" />
                }
                @case ('SLEEP') {
                  <app-sleep-form (submitForm)="onSaveLog($event)" (cancel)="close.emit()" />
                }
                @case ('NUTRITION') {
                  <app-nutrition-form (submitForm)="onSaveLog($event)" (cancel)="close.emit()" />
                }
                @case ('PERSONAL') {
                  <app-personal-habit-form (submitForm)="onSaveLog($event)" (cancel)="close.emit()" />
                }
                @default {
                  <div class="p-8 text-center border-2 border-dashed border-zinc-800 rounded-2xl">
                    <p class="text-zinc-500 italic">Unknown habit type...</p>
                  </div>
                }
              }
            }
          </div>
        </div>
      </div>
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HabitModalComponent {
  private habitState = inject(HabitStateService);

  @Input() isOpen = false;
  @Input() isCreateMode = false;
  @Input() habitType: string | null = null;
  @Input() habitTitle: string = '';
  @Input() habitId: number | null = null;
  @Output() close = new EventEmitter<void>();

  onSaveLog(formData: any) {
    if (!this.habitType) return;
    
    // Inject the specific habit ID for personal habits
    if (this.habitType === 'PERSONAL' && this.habitId) {
      formData = { ...formData, habitId: this.habitId };
    }

    this.habitState.saveHabitLog(this.habitType, formData);
    this.close.emit();
  }

  onCreateHabit(formData: any) {
    this.habitState.createHabit(formData);
    this.close.emit();
  }
}
