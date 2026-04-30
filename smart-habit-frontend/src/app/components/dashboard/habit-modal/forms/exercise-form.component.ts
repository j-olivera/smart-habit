import { ChangeDetectionStrategy, Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-exercise-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-4">
      <div class="grid grid-cols-2 gap-4">
        <div>
          <label class="block text-sm font-medium text-zinc-400 mb-1.5">Hours</label>
          <input 
            type="number" 
            formControlName="hours"
            step="0.1"
            min="0"
            max="4"
            class="w-full bg-zinc-800 border border-zinc-700 rounded-lg px-4 py-2.5 text-white outline-none focus:border-primary/50"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-zinc-400 mb-1.5">Energy Level (1-100)</label>
          <input 
            type="number" 
            formControlName="energyLevel"
            min="1"
            max="100"
            class="w-full bg-zinc-800 border border-zinc-700 rounded-lg px-4 py-2.5 text-white outline-none focus:border-primary/50"
          />
        </div>
      </div>

      <div>
        <label class="block text-sm font-medium text-zinc-400 mb-1.5">Muscular Group</label>
        <select 
          formControlName="muscularGroup"
          class="w-full bg-zinc-800 border border-zinc-700 rounded-lg px-4 py-2.5 text-white outline-none appearance-none focus:border-primary/50"
        >
          <option value="CARDIO">Cardio</option>
          <option value="CHEST">Chest</option>
          <option value="BACK">Back</option>
          <option value="LEGS">Legs</option>
          <option value="ARMS">Arms</option>
          <option value="ABDOMEN">Abdomen</option>
        </select>
      </div>

      <div class="flex justify-end gap-3 mt-8">
        <button type="button" (click)="cancel.emit()" class="px-4 py-2 rounded-lg text-zinc-300">Cancel</button>
        <button type="submit" [disabled]="form.invalid" class="px-6 py-2 rounded-lg bg-primary text-white font-medium">Save Log</button>
      </div>
    </form>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExerciseForm {
  private fb = inject(FormBuilder);
  @Output() submitForm = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<void>();

  form = this.fb.group({
    hours: [1, [Validators.required, Validators.min(0), Validators.max(4)]],
    energyLevel: [70, [Validators.required, Validators.min(1), Validators.max(100)]],
    muscularGroup: ['CARDIO', [Validators.required]]
  });

  onSubmit() {
    if (this.form.valid) {
      this.submitForm.emit(this.form.value);
    }
  }
}
