import { ChangeDetectionStrategy, Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-study-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-6">
      <!-- Toggle Principal -->
      <div class="bg-zinc-800/50 border border-zinc-700/50 rounded-2xl p-4 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-xl bg-blue-500/10 flex items-center justify-center">
            <span class="material-symbols-outlined text-blue-400">menu_book</span>
          </div>
          <div>
            <p class="text-sm font-bold text-white">Did you study today?</p>
            <p class="text-xs text-zinc-500">Record your progress or skip</p>
          </div>
        </div>
        <label class="relative inline-flex items-center cursor-pointer">
          <input type="checkbox" formControlName="studied" class="sr-only peer">
          <div class="w-11 h-6 bg-zinc-700 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-500"></div>
        </label>
      </div>

      <!-- Campos si ESTUDIÓ -->
      @if (form.get('studied')?.value) {
        <div class="space-y-4 animate-in fade-in slide-in-from-top-2">
          <div>
            <label class="block text-sm font-medium text-zinc-400 mb-1.5">Subject</label>
            <input 
              type="text" 
              formControlName="subject"
              placeholder="What did you study?"
              class="w-full bg-zinc-800 border border-zinc-700 rounded-lg px-4 py-2.5 text-white placeholder-zinc-500 outline-none focus:border-blue-500/50"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-zinc-400 mb-1.5">Hours</label>
            <input 
              type="number" 
              formControlName="hours"
              step="0.5"
              class="w-full bg-zinc-800 border border-zinc-700 rounded-lg px-4 py-2.5 text-white outline-none focus:border-blue-500/50"
            />
          </div>
        </div>
      } @else {
        <!-- Campo si NO ESTUDIÓ -->
        <div class="animate-in fade-in slide-in-from-top-2">
          <label class="block text-sm font-medium text-zinc-400 mb-1.5">Skip Reason</label>
          <textarea 
            formControlName="skipReason"
            placeholder="Why couldn't you study today?"
            rows="3"
            class="w-full bg-zinc-800 border border-zinc-700 rounded-lg px-4 py-2.5 text-white placeholder-zinc-500 outline-none focus:border-red-500/50 transition-all resize-none"
          ></textarea>
        </div>
      }

      <div class="flex justify-end gap-3 pt-4">
        <button type="button" (click)="cancel.emit()" class="px-4 py-2 rounded-lg text-zinc-300 hover:bg-zinc-800 transition-colors">Cancel</button>
        <button type="submit" [disabled]="form.invalid" class="px-6 py-2 rounded-lg bg-blue-500 text-white font-medium hover:bg-blue-600 transition-colors">Save Log</button>
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
    studied: [true],
    subject: ['', [Validators.minLength(2)]],
    hours: [1, [Validators.min(0.1)]],
    skipReason: ['']
  });

  onSubmit() {
    if (this.form.valid) {
      this.submitForm.emit(this.form.value);
    }
  }
}
