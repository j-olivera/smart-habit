import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CreateHabitForm } from './create-habit-form.component';
import { ReactiveFormsModule } from '@angular/forms';
import { describe, it, expect, beforeEach, vi } from 'vitest';

describe('CreateHabitForm', () => {
  let component: CreateHabitForm;
  let fixture: ComponentFixture<CreateHabitForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateHabitForm, ReactiveFormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(CreateHabitForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should start with invalid form', () => {
    expect(component.form.valid).toBe(false);
  });

  it('should validate name is required', () => {
    const nameControl = component.form.get('name');
    nameControl?.setValue('');
    expect(nameControl?.valid).toBe(false);
    
    nameControl?.setValue('Yoga');
    expect(nameControl?.valid).toBe(true);
  });

  it('should emit form value on submit', () => {
    const emitSpy = vi.spyOn(component.submitForm, 'emit');
    
    component.form.patchValue({
      name: 'Meditation',
      description: '10 minutes',
      type: 'PERSONAL'
    });
    
    component.onSubmit();
    
    expect(emitSpy).toHaveBeenCalledWith({
      name: 'Meditation',
      description: '10 minutes',
      type: 'PERSONAL'
    });
  });

  it('should not emit if form is invalid', () => {
    const emitSpy = vi.spyOn(component.submitForm, 'emit');
    
    component.form.patchValue({ name: '' }); // Invalid
    component.onSubmit();
    
    expect(emitSpy).not.toHaveBeenCalled();
  });
});