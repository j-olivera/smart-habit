import { ChangeDetectionStrategy, Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-study-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-4">
      <div>
        <label class="block text-sm font-medium text-zinc-400 mb-1.5">Subject / Topic</label>
        <input 
          type="text" 
          formControlName="subject"
          placeholder="e.g. Mathematics, Clean Architecture..."
          class="w-full bg-zinc-800 border border-zinc-700 rounded-lg px-4 py-2.5 text-white placeholder-zinc-500 outline-none focus:border-primary/50 focus:ring-1 focus:ring-primary/20 transition-all"
        />
        @if (form.get('subject')?.touched && form.get('subject')?.invalid) {
          <p class="text-xs text-red-400 mt-1">Subject is required</p>
        }
      </div>

      <div>
        <label class="block text-sm font-medium text-zinc-400 mb-1.5">Hours</label>
        <input 
          type="number" 
          formControlName="hours"
          step="0.5"
          min="0"
          class="w-full bg-zinc-800 border border-zinc-700 rounded-lg px-4 py-2.5 text-white placeholder-zinc-500 outline-none focus:border-primary/50 focus:ring-1 focus:ring-primary/20 transition-all"
        />
        @if (form.get('hours')?.touched && form.get('hours')?.invalid) {
          <p class="text-xs text-red-400 mt-1">Please enter a valid amount of hours</p>
        }
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
export class StudyForm {
  private fb = inject(FormBuilder);

  @Output() submitForm = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<void>();

  form = this.fb.group({
    subject: ['', [Validators.required]],
    hours: [0, [Validators.required, Validators.min(0.1)]]
  });

  onSubmit() {
    if (this.form.valid) {
      this.submitForm.emit(this.form.value);
    }
  }
}
