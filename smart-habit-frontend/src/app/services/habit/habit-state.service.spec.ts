import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { HabitStateService } from './habit-state.service';
import { environment } from '../../../environments/environment';
import { DailyEntry, Habit } from '../../models/habit/habit.model';
import { FIXED_HABITS_METADATA } from '../../models/habit/habit-metadata.constant';

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

const MOCK_HABIT_DEFINITIONS: Habit[] = [
  { id: 99, userId: 1, name: 'Read a book', description: 'Read 10 pages', type: 'PERSONAL', active: true, createdAt: '2026-04-30' }
];

describe('HabitStateService', () => {
  let service: HabitStateService;
  let httpMock: HttpTestingController;
  const API_ENTRY = `${environment.apiUrl}/daily-entries`;
  const API_HABITS = `${environment.apiUrl}/habits`;

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

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start with null state', () => {
    expect(service.dailyEntry()).toBeNull();
    expect(service.habitDefinitions()).toEqual([]);
  });

  it('should fetch today logs and habit definitions', () => {
    service.fetchTodayLogs();
    
    // Request definitions first
    const reqHabits = httpMock.expectOne(API_HABITS);
    expect(reqHabits.request.method).toBe('GET');
    reqHabits.flush({ data: MOCK_HABIT_DEFINITIONS });

    // Request logs second
    const reqLogs = httpMock.expectOne(req => req.url.startsWith(API_ENTRY));
    expect(reqLogs.request.method).toBe('GET');
    reqLogs.flush({ data: MOCK_EMPTY_ENTRY });

    expect(service.habitDefinitions()).toEqual(MOCK_HABIT_DEFINITIONS);
    expect(service.dailyEntry()).toEqual(MOCK_EMPTY_ENTRY);
  });

  it('should merge habit definitions into habitsGrid as pending', () => {
    service.fetchTodayLogs();
    httpMock.expectOne(API_HABITS).flush({ data: MOCK_HABIT_DEFINITIONS });
    httpMock.expectOne(req => req.url.startsWith(API_ENTRY)).flush({ data: MOCK_EMPTY_ENTRY });

    const grid = service.habitsGrid();
    const personalHabit = grid.find(h => h.id === 99);
    
    expect(personalHabit).toBeDefined();
    expect(personalHabit?.title).toBe('Read a book');
    expect(personalHabit?.status).toBe('pending');
  });

  it('should mark personal habit as done if log exists', () => {
    const entryWithLog = {
      ...MOCK_EMPTY_ENTRY,
      personalLogs: [{ id: 10, habitId: 99, entryId: 1, completed: true, hours: 1 }]
    };

    service.fetchTodayLogs();
    httpMock.expectOne(API_HABITS).flush({ data: MOCK_HABIT_DEFINITIONS });
    httpMock.expectOne(req => req.url.startsWith(API_ENTRY)).flush({ data: entryWithLog });

    const grid = service.habitsGrid();
    const personalHabit = grid.find(h => h.id === 99);
    
    expect(personalHabit?.status).toBe('done');
  });

  it('should create a new habit definition and add it to state', () => {
    const newHabitReq = { name: 'Meditation', description: 'Mindfulness', type: 'PERSONAL' };
    const savedHabit = { id: 100, userId: 1, ...newHabitReq, active: true, createdAt: '2026-04-30' };

    service.createHabit(newHabitReq);
    
    const req = httpMock.expectOne(API_HABITS);
    expect(req.request.method).toBe('POST');
    req.flush({ data: savedHabit });

    expect(service.habitDefinitions()).toContainEqual(savedHabit);
  });

  it('should update local personal log by habitId', () => {
    service.fetchTodayLogs();
    httpMock.expectOne(API_HABITS).flush({ data: MOCK_HABIT_DEFINITIONS });
    httpMock.expectOne(req => req.url.startsWith(API_ENTRY)).flush({ data: MOCK_EMPTY_ENTRY });

    const newLog = { id: 10, habitId: 99, entryId: 1, completed: true, hours: 1 };
    service.updateLocalLog('PERSONAL', newLog);

    expect(service.dailyEntry()?.personalLogs).toContainEqual(newLog);
    const personalHabit = service.habitsGrid().find(h => h.id === 99);
    expect(personalHabit?.status).toBe('done');
  });
});