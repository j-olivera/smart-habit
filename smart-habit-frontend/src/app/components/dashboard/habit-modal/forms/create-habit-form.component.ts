import { ChangeDetectionStrategy, Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-create-habit-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-6">
      <div class="bg-violet-500/10 border border-violet-500/20 rounded-2xl p-4 flex items-center gap-3">
        <div class="w-10 h-10 rounded-xl bg-violet-500/10 flex items-center justify-center">
          <span class="material-symbols-outlined text-violet-400">add_task</span>
        </div>
        <div>
          <p class="text-sm font-bold text-white">Create New Habit</p>
          <p class="text-xs text-zinc-500">Define what you want to track daily</p>
        </div>
      </div>

      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-zinc-400 mb-1.5">Habit Name</label>
          <input 
            type="text" 
            formControlName="name"
            placeholder="e.g., Meditation, Reading, etc."
            class="w-full bg-zinc-800 border border-zinc-700 rounded-lg px-4 py-2.5 text-white placeholder-zinc-500 outline-none focus:border-violet-500/50"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-zinc-400 mb-1.5">Description (Optional)</label>
          <textarea 
            formControlName="description"
            placeholder="Why is this habit important?"
            rows="3"
            class="w-full bg-zinc-800 border border-zinc-700 rounded-lg px-4 py-2.5 text-white placeholder-zinc-500 outline-none focus:border-violet-500/50 transition-all resize-none"
          ></textarea>
        </div>
      </div>

      <div class="flex justify-end gap-3 pt-4">
        <button type="button" (click)="cancel.emit()" class="px-4 py-2 rounded-lg text-zinc-300 hover:bg-zinc-800 transition-colors">Cancel</button>
        <button type="submit" [disabled]="form.invalid" class="px-6 py-2 rounded-lg bg-violet-500 text-white font-medium hover:bg-violet-600 transition-colors">Create Habit</button>
      </div>
    </form>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CreateHabitForm {
  private fb = inject(FormBuilder);
  @Output() submitForm = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<void>();

  form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    description: [''],
    type: ['PERSONAL']
  });

  onSubmit() {
    if (this.form.valid) {
      this.submitForm.emit(this.form.value);
    }
  }
}
