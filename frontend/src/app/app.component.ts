import { Component, OnInit } from '@angular/core';
import { KeycloakAuthService } from './login/keycloak.service';

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
    this.keycloakAuthService.init().then(() => {
      // Lógica posterior a la inicialización, como redirección o carga de datos
    }).catch(error => {
      console.error('Error en la inicialización de Keycloak:', error);
    });

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
