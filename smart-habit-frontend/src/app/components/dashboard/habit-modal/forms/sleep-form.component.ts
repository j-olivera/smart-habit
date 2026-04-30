import { ChangeDetectionStrategy, Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-sleep-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-4">
      <div class="grid grid-cols-2 gap-4">
        <div>
          <label class="block text-sm font-medium text-zinc-400 mb-1.5">Hours Slept</label>
          <input type="number" formControlName="hours" step="0.5" class="w-full bg-zinc-800 border border-zinc-700 rounded-lg px-4 py-2.5 text-white outline-none"/>
        </div>
        <div>
          <label class="block text-sm font-medium text-zinc-400 mb-1.5">Quality</label>
          <select formControlName="quality" class="w-full bg-zinc-800 border border-zinc-700 rounded-lg px-4 py-2.5 text-white outline-none">
            <option value="BAD">Bad</option>
            <option value="REGULAR">Regular</option>
            <option value="GOOD">Good</option>
            <option value="EXCELLENT">Excellent</option>
          </select>
        </div>
      </div>

      <div class="bg-zinc-800/50 p-4 rounded-2xl border border-zinc-700/50 space-y-4">
        <div class="flex items-center justify-between">
          <span class="text-sm font-bold text-white">Did you nap?</span>
          <input type="checkbox" formControlName="napped" class="w-5 h-5 accent-primary"/>
        </div>
        
        @if (form.get('napped')?.value) {
          <div class="pt-2 animate-in fade-in slide-in-from-top-1">
            <label class="block text-xs font-medium text-zinc-500 mb-1.5">Nap Duration (Hours)</label>
            <input type="number" formControlName="napHours" step="0.5" class="w-full bg-zinc-700 border border-zinc-600 rounded-lg px-4 py-2 text-white outline-none"/>
          </div>
        }
      </div>

      <div class="flex justify-end gap-3 mt-8">
        <button type="button" (click)="cancel.emit()" class="px-4 py-2 rounded-lg text-zinc-300">Cancel</button>
        <button type="submit" [disabled]="form.invalid" class="px-6 py-2 rounded-lg bg-primary text-white font-medium">Save Log</button>
      </div>
    </form>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SleepForm {
  private fb = inject(FormBuilder);
  @Output() submitForm = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<void>();

  form = this.fb.group({
    hours: [8, [Validators.required, Validators.min(0), Validators.max(12)]],
    quality: ['GOOD', [Validators.required]],
    napped: [false],
    napHours: [0, [Validators.min(0), Validators.max(4)]],
    napNeeded: [false]
  });

  onSubmit() {
    if (this.form.valid) {
      this.submitForm.emit(this.form.value);
    }
  }
}
