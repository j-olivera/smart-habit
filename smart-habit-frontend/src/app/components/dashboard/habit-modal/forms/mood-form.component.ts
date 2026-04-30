import { ChangeDetectionStrategy, Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-mood-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-6">
      <div>
        <label class="block text-sm font-medium text-zinc-400 mb-4 text-center">How are you feeling?</label>
        <div class="grid grid-cols-5 gap-2">
          @for (m of moods; track m.value) {
            <button
              type="button"
              (click)="form.get('mood')?.setValue(m.value)"
              [class]="form.get('mood')?.value === m.value 
                ? 'bg-primary/20 border-primary text-primary' 
                : 'bg-zinc-800 border-zinc-700 text-zinc-400 hover:border-zinc-500'"
              class="flex flex-col items-center gap-1 p-3 rounded-xl border transition-all"
            >
              <span class="material-symbols-outlined">{{ m.icon }}</span>
              <span class="text-[10px] font-bold uppercase tracking-wider">{{ m.label }}</span>
            </button>
          }
        </div>
      </div>

      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-zinc-400 mb-1.5">Event Description (Optional)</label>
          <textarea 
            formControlName="eventDescription"
            placeholder="What happened today?"
            rows="2"
            class="w-full bg-zinc-800 border border-zinc-700 rounded-lg px-4 py-2.5 text-white outline-none focus:border-primary/50 transition-all resize-none"
          ></textarea>
        </div>

        <div class="bg-zinc-800/50 border border-zinc-700/50 rounded-2xl p-4 space-y-4">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <span class="material-symbols-outlined text-violet-400">groups</span>
              <span class="text-sm font-bold text-white">Socialized?</span>
            </div>
            <input type="checkbox" formControlName="socialized" class="w-5 h-5 accent-primary"/>
          </div>

          @if (form.get('socialized')?.value) {
            <div class="animate-in fade-in slide-in-from-top-1">
              <input 
                type="text" 
                formControlName="socialWith"
                placeholder="With whom?"
                class="w-full bg-zinc-700 border border-zinc-600 rounded-lg px-4 py-2 text-sm text-white outline-none focus:border-primary/30"
              />
            </div>
          }
        </div>
      </div>

      <div class="flex justify-end gap-3 pt-4">
        <button type="button" (click)="cancel.emit()" class="px-4 py-2 rounded-lg text-zinc-300 hover:bg-zinc-800 transition-colors">Cancel</button>
        <button type="submit" [disabled]="form.invalid" class="px-6 py-2 rounded-lg bg-primary text-white font-medium hover:bg-primary/90 transition-colors shadow-lg shadow-primary/20">Save Log</button>
      </div>
    </form>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MoodForm {
  private fb = inject(FormBuilder);
  @Output() submitForm = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<void>();

  moods = [
    { label: 'Sad', value: 'SAD', icon: 'sentiment_very_dissatisfied' },
    { label: 'Down', value: 'DOWN', icon: 'sentiment_dissatisfied' },
    { label: 'Neutral', value: 'NEUTRAL', icon: 'sentiment_neutral' },
    { label: 'Happy', value: 'HAPPY', icon: 'sentiment_satisfied' },
    { label: 'Euphoric', value: 'EUPHORIC', icon: 'sentiment_very_satisfied' }
  ];

  form = this.fb.group({
    mood: ['NEUTRAL', [Validators.required]],
    eventDescription: [''],
    socialized: [false],
    socialWith: ['']
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
