import { ChangeDetectionStrategy, Component, OnInit, computed, inject } from '@angular/core';
import { CommonModule, NgStyle } from '@angular/common';
import { HabitStateService, HabitViewState } from '../../services/habit/habit-state.service';
import { HabitModalComponent } from './habit-modal/habit-modal.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NgStyle, HabitModalComponent],
  templateUrl: './dashboard.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent implements OnInit {
  habitState = inject(HabitStateService);

  // Date display
  today = new Date().toLocaleDateString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });

  // Progress computeds
  doneCount = computed(() =>
    this.habitState.habitsGrid().filter(h => h.status === 'done').length
  );

  totalCount = computed(() => this.habitState.habitsGrid().length);

  progressPercent = computed(() => {
    const total = this.totalCount();
    if (total === 0) return 0;
    return Math.round((this.doneCount() / total) * 100);
  });

  // SVG circle circumference = 2 * π * r = 2 * π * 14 ≈ 87.96
  progressOffset = computed(() => {
    const circumference = 87.96;
    return circumference - (this.progressPercent() / 100) * circumference;
  });

  // Modal State
  isModalOpen = false;
  isCreateMode = false;
  selectedHabitType: string | null = null;
  selectedHabitTitle: string = '';
  selectedHabitId: number | null = null;

  ngOnInit() {
    this.habitState.fetchTodayLogs();
  }

  openModal(habit: HabitViewState) {
    if (habit.status === 'done') return;
    this.selectedHabitType = habit.type;
    this.selectedHabitTitle = habit.title;
    this.selectedHabitId = habit.id || null;
    this.isCreateMode = false;
    this.isModalOpen = true;
  }

  openCreateHabitModal() {
    this.selectedHabitType = 'CREATE_HABIT';
    this.selectedHabitTitle = 'New Habit';
    this.isCreateMode = true;
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
    this.selectedHabitType = null;
    this.selectedHabitTitle = '';
    this.selectedHabitId = null;
    this.isCreateMode = false;
  }
}
