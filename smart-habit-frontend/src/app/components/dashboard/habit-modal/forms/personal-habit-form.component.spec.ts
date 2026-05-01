import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PersonalHabitForm } from './personal-habit-form.component';
import { ReactiveFormsModule } from '@angular/forms';
import { describe, it, expect, beforeEach, vi } from 'vitest';

describe('PersonalHabitForm', () => {
  let component: PersonalHabitForm;
  let fixture: ComponentFixture<PersonalHabitForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PersonalHabitForm, ReactiveFormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(PersonalHabitForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should start with valid form and default values', () => {
    expect(component.form.valid).toBe(true);
    expect(component.form.value.completed).toBe(true);
    expect(component.form.value.hours).toBe(0);
  });

  it('should emit form value on submit', () => {
    const emitSpy = vi.spyOn(component.submitForm, 'emit');
    
    component.form.patchValue({
      completed: true,
      hours: 1.5,
      description: 'Felt great'
    });
    
    component.onSubmit();
    
    expect(emitSpy).toHaveBeenCalledWith({
      completed: true,
      hours: 1.5,
      description: 'Felt great'
    });
  });

  it('should emit cancel event when cancel button clicked', () => {
    const emitSpy = vi.spyOn(component.cancel, 'emit');
    component.cancel.emit();
    expect(emitSpy).toHaveBeenCalled();
  });
});