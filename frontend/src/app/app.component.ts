import { Component, OnInit } from '@angular/core';
import { KeycloakAuthService } from './login/keycloak.service';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{
  title = 'frontend';
  isAuthenticated: boolean = false;
  private authSubscription: Subscription | undefined;

  constructor(private keycloakAuthService: KeycloakAuthService, private router: Router) {}

  ngOnInit(): void {
    // Inicializa Keycloak cuando el componente se carga
    this.keycloakAuthService.initKeycloak().then(() => {
      // Nos suscribimos al observable isAuthenticated$ directamente
      this.authSubscription = this.keycloakAuthService.isAuthenticated$.subscribe(
        (isAuthenticated: boolean) => {
          this.isAuthenticated = isAuthenticated;
          if (this.isAuthenticated) {
            console.log('Usuario autenticado');
          } else {
            console.log('Usuario no autenticado');
          }
        }
      );
    });
  }
  

  onLogout(): void {
    this.keycloakAuthService.logout();
    this.router.navigate(['/']); //Redirigir despeus del logout
  }

  ngOnDestroy(): void {
    // desuscribimos del observable cuando el componente se destruye
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }
}
