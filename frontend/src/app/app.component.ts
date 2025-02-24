import { Component, OnInit } from '@angular/core';
import { KeycloakAuthService } from './keycloak.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{
  title = 'frontend';

  constructor(private keycloakAuthService: KeycloakAuthService) {}

  ngOnInit(): void {
    this.keycloakAuthService.init();
    // Hacer la solicitud para obtener datos protegidos
    this.keycloakAuthService.getData().subscribe(
      (data: any) => {
        console.log('Datos obtenidos:', data);
      },
      (error: any) => {
        console.error('Error en la solicitud:', error);
      }
    );
  }

  onLogout(): void {
    this.keycloakAuthService.logout();
  }
}
