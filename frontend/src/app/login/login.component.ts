import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { KeycloakAuthService } from './keycloak.service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {

  constructor(
    private keycloakAuthService: KeycloakAuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Si el usuario ya está autenticado, redirige al home o página protegida
    if (this.keycloakAuthService.isAuthenticated.value) {
      this.router.navigate(['/']);
    }
  }

  login(): void {
    // Usar el servicio de Keycloak para redirigir al login de Keycloak
    this.keycloakAuthService.login();
  }
}
