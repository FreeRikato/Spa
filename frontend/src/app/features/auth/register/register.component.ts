import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { RegisterRequest } from '../../../core/models/register-request.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  authService = inject(AuthService);
  router = inject(Router);

  model: RegisterRequest = {
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    phone: '',
    role: 'USER'
  };

  errorMessage: string = '';
  isLoading = false;

  register() {
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.register(this.model).subscribe({
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
        console.error('Registration failed', err);
        this.errorMessage = 'Registration failed. Please try again.';
      },
    });
  }
}
