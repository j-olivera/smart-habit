import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import { RegisterRequest } from '../../../models/auth/auth.model';
import { NavbarComponent } from '../../shared/navbar/navbar.component';

export function matchPasswordValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password')?.value;
  const confirmPassword = control.get('confirmPassword')?.value;
  if (password && confirmPassword && password !== confirmPassword) {
    return { passwordMismatch: true };
  }
  return null;
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, NavbarComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  registerForm = this.fb.nonNullable.group({
    fullName: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', [Validators.required]]
  }, { validators: matchPasswordValidator });

  isSubmitting = signal(false);
  globalError = signal<string | null>(null);

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.globalError.set(null);

    const formValue = this.registerForm.getRawValue();
    const request: RegisterRequest = {
      name: formValue.fullName,
      email: formValue.email,
      password: formValue.password
    };

    this.authService.register(request).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        // Despues se definira hacia donde redirigimos: al login o directo adentro
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.isSubmitting.set(false);
        this.globalError.set(err.error?.message || 'The email address provided is already in use. Please try logging in or use a different email.');
      }
    });
  }

  // --- Estado del botón de submit ---

  get isButtonDisabled(): boolean {
    return (this.registerForm.invalid && this.registerForm.touched) || this.isSubmitting();
  }

  // --- Getters de validación por campo ---

  get showNameError(): boolean {
    const c = this.registerForm.get('fullName')!;
    return c.invalid && c.touched;
  }

  get nameErrorMsg(): string {
    const c = this.registerForm.get('fullName')!;
    if (c.hasError('required')) return 'Full name is required.';
    return '';
  }

  get showEmailError(): boolean {
    const c = this.registerForm.get('email')!;
    return c.invalid && c.touched;
  }

  get emailErrorMsg(): string {
    const c = this.registerForm.get('email')!;
    if (c.hasError('required')) return 'Email is required.';
    if (c.hasError('email')) return 'Enter a valid email address.';
    return '';
  }

  get showPasswordError(): boolean {
    const c = this.registerForm.get('password')!;
    return c.invalid && c.touched;
  }

  get passwordErrorMsg(): string {
    const c = this.registerForm.get('password')!;
    if (c.hasError('required')) return 'Password is required.';
    if (c.hasError('minlength')) return 'Password must be at least 6 characters.';
    return '';
  }

  get showConfirmPasswordError(): boolean {
    const c = this.registerForm.get('confirmPassword')!;
    return c.invalid && c.touched;
  }

  get confirmPasswordErrorMsg(): string {
    const c = this.registerForm.get('confirmPassword')!;
    if (c.hasError('required')) return 'Please confirm your password.';
    return '';
  }

  get showPasswordMismatch(): boolean {
    return !!(this.registerForm.errors?.['passwordMismatch'] && this.registerForm.get('confirmPassword')?.touched);
  }
}
