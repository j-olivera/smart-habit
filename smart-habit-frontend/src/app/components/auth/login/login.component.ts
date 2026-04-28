import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import { LoginRequest } from '../../../models/auth/auth.model';
import { NavbarComponent } from '../../shared/navbar/navbar.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, NavbarComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  isSubmitting = signal(false);
  globalError = signal<string | null>(null);

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.globalError.set(null);

    const formValue = this.loginForm.getRawValue();
    const request: LoginRequest = {
      email: formValue.email,
      password: formValue.password
    };

    this.authService.login(request).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.router.navigate(['/home']);
      },
      error: (err) => {
        this.isSubmitting.set(false);
        this.globalError.set(err.error?.message || 'Invalid email or password. Please try again.');
      }
    });
  }

  // --- Estado del botón de submit ---

  get isButtonDisabled(): boolean {
    return (this.loginForm.invalid && this.loginForm.touched) || this.isSubmitting();
  }

  // --- Getters de validación por campo ---

  get showEmailError(): boolean {
    const c = this.loginForm.get('email')!;
    return c.invalid && c.touched;
  }

  get emailErrorMsg(): string {
    const c = this.loginForm.get('email')!;
    if (c.hasError('required')) return 'Email is required.';
    if (c.hasError('email')) return 'Enter a valid email address.';
    return '';
  }

  get showPasswordError(): boolean {
    const c = this.loginForm.get('password')!;
    return c.invalid && c.touched;
  }

  get passwordErrorMsg(): string {
    const c = this.loginForm.get('password')!;
    if (c.hasError('required')) return 'Password is required.';
    return '';
  }
}
