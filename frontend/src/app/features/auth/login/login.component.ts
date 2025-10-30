import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../core/models/login-request.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  authService = inject(AuthService);
  router = inject(Router);

  model: LoginRequest = { email: '', password: '' };
  errorMessage: string = '';
  isLoading = false;

  login() {
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.model).subscribe({
      next: (user) => {
        this.isLoading = false;
        // Navigate based on role
        if (user.role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else if (user.role === 'CLIENT') {
          this.router.navigate(['/client']);
        } else {
          this.router.navigate(['/user']);
        }
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Login failed', err);
        this.errorMessage = 'Login failed. Please check your credentials and try again.';
      },
    });
  }
}
