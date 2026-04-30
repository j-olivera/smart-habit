import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { HabitStateService } from './habit-state.service';
import { environment } from '../../../environments/environment';
import { DailyEntry } from '../../models/habit/habit.model';
import { FIXED_HABITS_METADATA } from '../../models/habit/habit-metadata.constant';

// ─── Fixtures ────────────────────────────────────────────────────────────────

const MOCK_EMPTY_ENTRY: DailyEntry = {
  id: 1,
  date: '2026-04-30',
  studyLog: null,
  exerciseLog: null,
  moodLog: null,
  nutritionLog: null,
  sleepLog: null,
  personalLogs: []
};

const MOCK_FULL_ENTRY: DailyEntry = {
  id: 1,
  date: '2026-04-30',
  studyLog: { id: 1, completed: true, hours: 2, subject: 'Math' },
  exerciseLog: { id: 2, completed: true, durationMinutes: 45, intensity: 'MEDIUM' },
  moodLog: { id: 3, completed: true, moodType: 'HAPPY' },
  nutritionLog: { id: 4, completed: true, meals: ['Lunch'], waterLiters: 2 },
  sleepLog: { id: 5, completed: true, hours: 8, quality: 'GOOD' },
  personalLogs: [
    { id: 10, habitId: 99, entryId: 1, completed: true, hours: 1, description: 'Read a book' }
  ]
};

// ─── Tests ────────────────────────────────────────────────────────────────────

describe('HabitStateService', () => {
  let service: HabitStateService;
  let httpMock: HttpTestingController;
  const API = `${environment.apiUrl}/api/v1/daily-entry`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        HabitStateService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(HabitStateService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  // ── Creation ──

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ── Initial State ──

  it('should start with null dailyEntry and not loading', () => {
    expect(service.dailyEntry()).toBeNull();
    expect(service.isLoading()).toBe(false);
    expect(service.error()).toBeNull();
  });

  it('should expose the 5 fixed habits in the grid before any fetch', () => {
    const grid = service.habitsGrid();
    // 5 fixed + 0 personal = 5 entries
    expect(grid.length).toBe(FIXED_HABITS_METADATA.length);
    grid.forEach(h => expect(h.status).toBe('pending'));
  });

  // ── fetchTodayLogs ──

  it('should set loading to true while fetching', () => {
    service.fetchTodayLogs();
    expect(service.isLoading()).toBe(true);
    // Flush to avoid afterEach error
    httpMock.expectOne(`${API}/today`).flush(MOCK_EMPTY_ENTRY);
  });

  it('should send a GET request to the today endpoint', () => {
    service.fetchTodayLogs();
    const req = httpMock.expectOne(`${API}/today`);
    expect(req.request.method).toBe('GET');
    req.flush(MOCK_EMPTY_ENTRY);
  });

  it('should set dailyEntry and stop loading on success', () => {
    service.fetchTodayLogs();
    httpMock.expectOne(`${API}/today`).flush(MOCK_EMPTY_ENTRY);
    expect(service.dailyEntry()).toEqual(MOCK_EMPTY_ENTRY);
    expect(service.isLoading()).toBe(false);
  });

  it('should unwrap data envelope when backend returns { data: DailyEntry }', () => {
    service.fetchTodayLogs();
    httpMock.expectOne(`${API}/today`).flush({ data: MOCK_EMPTY_ENTRY });
    expect(service.dailyEntry()).toEqual(MOCK_EMPTY_ENTRY);
  });

  it('should set error and stop loading on failure', () => {
    service.fetchTodayLogs();
    httpMock.expectOne(`${API}/today`).error(new ProgressEvent('error'));
    expect(service.error()).not.toBeNull();
    expect(service.isLoading()).toBe(false);
  });

  // ── habitsGrid computed ──

  it('should mark all fixed habits as pending when dailyEntry has no logs', () => {
    service.fetchTodayLogs();
    httpMock.expectOne(`${API}/today`).flush(MOCK_EMPTY_ENTRY);

    const grid = service.habitsGrid();
    const fixed = grid.filter(h => !h.isPersonal);

    expect(fixed.length).toBe(5);
    fixed.forEach(h => expect(h.status).toBe('pending'));
  });

  it('should mark all fixed habits as done when dailyEntry has all logs', () => {
    service.fetchTodayLogs();
    httpMock.expectOne(`${API}/today`).flush(MOCK_FULL_ENTRY);

    const grid = service.habitsGrid();
    const fixed = grid.filter(h => !h.isPersonal);

    fixed.forEach(h => expect(h.status).toBe('done'));
  });

  it('should include personal logs in the grid as done', () => {
    service.fetchTodayLogs();
    httpMock.expectOne(`${API}/today`).flush(MOCK_FULL_ENTRY);

    const grid = service.habitsGrid();
    const personal = grid.filter(h => h.isPersonal);

    expect(personal.length).toBe(1);
    expect(personal[0].title).toBe('Read a book');
    expect(personal[0].status).toBe('done');
  });

  it('should return 5 fixed + N personal habits in the grid', () => {
    service.fetchTodayLogs();
    httpMock.expectOne(`${API}/today`).flush(MOCK_FULL_ENTRY);

    expect(service.habitsGrid().length).toBe(5 + MOCK_FULL_ENTRY.personalLogs.length);
  });

  // ── updateLocalLog (optimistic update) ──

  it('should update studyLog in place via updateLocalLog', () => {
    service.fetchTodayLogs();
    httpMock.expectOne(`${API}/today`).flush(MOCK_EMPTY_ENTRY);

    const newLog = { id: 99, completed: true, hours: 3, subject: 'Physics' };
    service.updateLocalLog('STUDY', newLog);

    expect(service.dailyEntry()?.studyLog).toEqual(newLog);
    const studyHabit = service.habitsGrid().find(h => h.type === 'STUDY');
    expect(studyHabit?.status).toBe('done');
  });

  it('should not update anything via updateLocalLog when dailyEntry is null', () => {
    service.updateLocalLog('STUDY', { id: 1, completed: true, hours: 1, subject: 'X' });
    expect(service.dailyEntry()).toBeNull();
  });
});
