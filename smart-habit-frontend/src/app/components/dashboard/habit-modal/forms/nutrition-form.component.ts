import { ChangeDetectionStrategy, Component, EventEmitter, Output, inject } from '@angular/core';
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
        <div class="flex justify-center gap-2">
          @for (rate of ratings; track rate.value) {
            <button
              type="button"
              (click)="form.get('rating')?.setValue(rate.value)"
              [class]="form.get('rating')?.value === rate.value 
                ? 'bg-emerald-500/20 border-emerald-500 text-emerald-400' 
                : 'bg-zinc-800 border-zinc-700 text-zinc-400 hover:border-zinc-500'"
              class="flex flex-col items-center gap-2 p-4 rounded-2xl border transition-all min-w-[75px]"
            >
              <span class="material-symbols-outlined text-2xl">{{ rate.icon }}</span>
              <span class="text-[10px] font-bold uppercase tracking-wider">{{ rate.label }}</span>
            </button>
          }
        </div>
      </div>

      <div class="space-y-4">
        <div class="bg-zinc-800/50 border border-zinc-700/50 rounded-2xl p-4 flex items-center justify-between">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-xl bg-emerald-500/10 flex items-center justify-center">
              <span class="material-symbols-outlined text-emerald-400">task_alt</span>
            </div>
            <div>
              <p class="text-sm font-bold text-white">Met Daily Goal</p>
              <p class="text-xs text-zinc-500">Stuck to your nutrition plan?</p>
            </div>
          </div>
          <input type="checkbox" formControlName="metGoal" class="w-5 h-5 accent-emerald-500"/>
        </div>

        <div>
          <label class="block text-sm font-medium text-zinc-400 mb-1.5">Observations (Optional)</label>
          <textarea 
            formControlName="observationText"
            placeholder="What did you eat today? Any snacks?"
            rows="2"
            class="w-full bg-zinc-800 border border-zinc-700 rounded-lg px-4 py-2.5 text-white placeholder-zinc-500 outline-none focus:border-emerald-500/50 transition-all resize-none"
          ></textarea>
        </div>
      </div>

      <div class="flex justify-end gap-3 pt-4">
        <button type="button" (click)="cancel.emit()" class="px-4 py-2 rounded-lg text-zinc-300 hover:bg-zinc-800 transition-colors">Cancel</button>
        <button type="submit" [disabled]="form.invalid" class="px-6 py-2 rounded-lg bg-emerald-600 text-white font-medium hover:bg-emerald-700 transition-colors shadow-lg shadow-emerald-500/20">Save Log</button>
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
    { label: 'Poor', value: 'POOR', icon: 'heart_broken' },
    { label: 'Regular', value: 'REGULAR', icon: 'restaurant_menu' },
    { label: 'Good', value: 'GOOD', icon: 'nutrition' },
    { label: 'Excellent', value: 'EXCELLENT', icon: 'award_star' }
  ];

  form = this.fb.group({
    rating: ['GOOD', [Validators.required]],
    metGoal: [true],
    observationText: ['']
  });

  onSubmit() {
    if (this.form.valid) {
      const val = this.form.value;
      this.submitForm.emit({
        rating: val.rating,
        metGoal: val.metGoal,
        hasObservation: !!val.observationText,
        observation: val.observationText // Mandamos el texto también si el backend lo soporta
      });
    }
  }
}
