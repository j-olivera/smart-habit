import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StudyForm } from './forms/study-form.component';
import { ExerciseForm } from './forms/exercise-form.component';
import { MoodForm } from './forms/mood-form.component';
import { SleepForm } from './forms/sleep-form.component';
import { NutritionForm } from './forms/nutrition-form.component';
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
    NutritionForm
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
              <p class="text-sm text-zinc-500 font-medium">Daily Registration</p>
            </div>
            <button (click)="close.emit()" class="w-10 h-10 flex items-center justify-center rounded-xl bg-zinc-800 text-zinc-400 hover:text-white transition-all">
              <span class="material-symbols-outlined">close</span>
            </button>
          </div>
          
          <div class="relative z-10">
            @switch (habitType) {
              @case ('STUDY') {
                <app-study-form (submitForm)="onSave($event)" (cancel)="close.emit()" />
              }
              @case ('EXERCISE') {
                <app-exercise-form (submitForm)="onSave($event)" (cancel)="close.emit()" />
              }
              @case ('MOOD') {
                <app-mood-form (submitForm)="onSave($event)" (cancel)="close.emit()" />
              }
              @case ('SLEEP') {
                <app-sleep-form (submitForm)="onSave($event)" (cancel)="close.emit()" />
              }
              @case ('NUTRITION') {
                <app-nutrition-form (submitForm)="onSave($event)" (cancel)="close.emit()" />
              }
              @default {
                <div class="p-8 text-center border-2 border-dashed border-zinc-800 rounded-2xl">
                  <p class="text-zinc-500 italic">Coming soon...</p>
                </div>
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
  @Input() habitType: string | null = null;
  @Input() habitTitle: string = '';
  @Output() close = new EventEmitter<void>();

  onSave(formData: any) {
    if (!this.habitType) return;

    this.habitState.saveHabitLog(this.habitType, formData);
    this.close.emit();
  }
}

