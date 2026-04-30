import { ChangeDetectionStrategy, Component, EventEmitter, Output, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-nutrition-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-6">
      <div>
        <label class="block text-sm font-medium text-zinc-400 mb-4 text-center">How was your nutrition today?</label>
        <div class="flex justify-center gap-3">
          @for (rate of ratings; track rate.value) {
            <button
              type="button"
              (click)="form.get('rating')?.setValue(rate.value)"
              [class]="form.get('rating')?.value === rate.value 
                ? 'bg-primary/20 border-primary text-primary' 
                : 'bg-zinc-800 border-zinc-700 text-zinc-400 hover:border-zinc-500'"
              class="flex flex-col items-center gap-2 p-4 rounded-2xl border transition-all min-w-[80px]"
            >
              <span class="material-symbols-outlined text-2xl">{{ rate.icon }}</span>
              <span class="text-[10px] font-bold uppercase tracking-wider">{{ rate.label }}</span>
            </button>
          }
        </div>
      </div>

      <div class="bg-zinc-800/50 border border-zinc-700/50 rounded-2xl p-4 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-xl bg-emerald-500/10 flex items-center justify-center">
            <span class="material-symbols-outlined text-emerald-400">task_alt</span>
          </div>
          <div>
            <p class="text-sm font-bold text-white">Met Daily Goal</p>
            <p class="text-xs text-zinc-500">Did you stick to your plan?</p>
          </div>
        </div>
        <label class="relative inline-flex items-center cursor-pointer">
          <input type="checkbox" formControlName="metGoal" class="sr-only peer">
          <div class="w-11 h-6 bg-zinc-700 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary"></div>
        </label>
      </div>

      <div class="flex justify-end gap-3 mt-8">
        <button 
          type="button"
          (click)="cancel.emit()" 
          class="px-4 py-2 rounded-lg text-zinc-300 hover:bg-zinc-800 transition-colors"
        >
          Cancel
        </button>
        <button 
          type="submit"
          [disabled]="form.invalid"
          class="px-6 py-2 rounded-lg bg-primary text-white font-medium hover:bg-primary/90 transition-colors shadow-lg shadow-primary/20 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          Save Log
        </button>
      </div>
    </form>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NutritionForm {
  private fb = inject(FormBuilder);
  @Output() submitForm = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<void>();

  ratings = [
    { label: 'Poor', value: 'POOR', icon: 'sentiment_dissatisfied' },
    { label: 'Regular', value: 'REGULAR', icon: 'sentiment_neutral' },
    { label: 'Good', value: 'GOOD', icon: 'sentiment_satisfied' },
    { label: 'Excellent', value: 'EXCELLENT', icon: 'auto_awesome' }
  ];

  form = this.fb.group({
    rating: ['GOOD', [Validators.required]],
    metGoal: [false],
    hasObservation: [false]
  });

  onSubmit() {
    if (this.form.valid) {
      this.submitForm.emit(this.form.value);
    }
  }
}
