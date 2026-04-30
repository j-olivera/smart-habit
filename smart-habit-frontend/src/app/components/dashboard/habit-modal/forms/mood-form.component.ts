import { ChangeDetectionStrategy, Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-mood-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-4">
      <div>
        <label class="block text-sm font-medium text-zinc-400 mb-3">How are you feeling?</label>
        <div class="grid grid-cols-3 gap-2">
          @for (mood of moods; track mood.value) {
            <button
              type="button"
              (click)="form.get('mood')?.setValue(mood.value)"
              [class]="form.get('mood')?.value === mood.value 
                ? 'bg-primary/20 border-primary text-primary' 
                : 'bg-zinc-800 border-zinc-700 text-zinc-400 hover:border-zinc-500'"
              class="flex flex-col items-center gap-1 p-3 rounded-xl border transition-all"
            >
              <span class="material-symbols-outlined">{{ mood.icon }}</span>
              <span class="text-[10px] font-bold uppercase tracking-wider">{{ mood.label }}</span>
            </button>
          }
        </div>
      </div>

      <div>
        <label class="block text-sm font-medium text-zinc-400 mb-1.5">Note (Optional)</label>
        <textarea 
          formControlName="eventDescription"
          placeholder="Anything specifically on your mind?"
          rows="3"
          class="w-full bg-zinc-800 border border-zinc-700 rounded-lg px-4 py-2.5 text-white placeholder-zinc-500 outline-none focus:border-primary/50 focus:ring-1 focus:ring-primary/20 transition-all resize-none"
        ></textarea>
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
export class MoodForm {
  private fb = inject(FormBuilder);

  @Output() submitForm = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<void>();

  moods: {label: string, value: string, icon: string}[] = [
    { label: 'Happy', value: 'HAPPY', icon: 'sentiment_very_satisfied' },
    { label: 'Neutral', value: 'NEUTRAL', icon: 'sentiment_neutral' },
    { label: 'Sad', value: 'SAD', icon: 'sentiment_very_dissatisfied' },
    { label: 'Stressed', value: 'STRESSED', icon: 'psychology' },
    { label: 'Anxious', value: 'ANXIOUS', icon: 'warning' },
    { label: 'Energetic', value: 'ENERGETIC', icon: 'bolt' }
  ];

  form = this.fb.group({
    mood: ['NEUTRAL', [Validators.required]],
    eventDescription: [''],
    hasObservations: [false],
    socialized: [false]
  });

  onSubmit() {
    if (this.form.valid) {
      const val = this.form.value;
      this.submitForm.emit({
        ...val,
        hasObservations: !!val.eventDescription
      });
    }
  }
}
