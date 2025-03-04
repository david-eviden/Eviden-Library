import { Component } from '@angular/core';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';
import swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  email: string = '';
  password: string = '';

  constructor(
    private authService: AuthService, 
    private router: Router
  ) {}

  onSubmit() {
    this.authService.login({
      email: this.email, 
      password: this.password
    }).subscribe({
      next: (usuario) => {
        swal(  // Usamos 'swal' para la alerta
          `Bienvenido ${usuario.nombre}`,
          '',  // Mensaje vacío
          'success'  // Usamos 'type' para indicar el tipo de alerta
        );

        // Redirigir según el rol
        if (usuario.rol === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/principal']);
        }
      },
      error: (error) => {
        swal(  // Usamos 'swal' para la alerta
          'Error de inicio de sesión',
          error.error?.mensaje || 'Credenciales inválidas',
          'error'  // Usamos 'type' para indicar el tipo de alerta
        );
      }
    });
  }
  
}
