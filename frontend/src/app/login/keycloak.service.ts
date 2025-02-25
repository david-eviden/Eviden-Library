import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-js';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class KeycloakAuthService {

  public keycloakInitialized = new BehaviorSubject<boolean>(false); // Para saber si Keycloak está inicializado
  public isAuthenticated = new BehaviorSubject<boolean>(false); // Para gestionar si el usuario está autenticado

  constructor(private keycloakService: KeycloakService) {}

  // Método para inicializar Keycloak solo cuando se necesite
  async initKeycloak(): Promise<void> {
    console.log("keycloak inicializado");
    if (!this.keycloakInitialized.value) {
      try {
        await this.keycloakService.init({
          config: {
            url: 'http://localhost:9090', // URL de tu servidor Keycloak
            realm: 'eviden', // Nombre realm
            clientId: 'user1', // ID de tu cliente
          },
          initOptions: {
            onLoad: 'check-sso', // No obligar a hacer login inmediatamente
            checkLoginIframe: false,
          },
        });

        this.keycloakInitialized.next(true);
        this.isAuthenticated.next(this.keycloakService.isLoggedIn());
      } catch (error) {
        console.error('Error al inicializar Keycloak:', error);
      }
    }
  }

  // Método para login
  login(): void {
    this.keycloakService.login();
  }

  // Método para logout
  logout(): void {
    this.keycloakService.logout();
  }

  // Método para obtener el token
  getToken(): Promise<string | null> {
    try{
      const token = this.keycloakService.getToken();
      if (token) {
        return token;
      } else {
        console.error('Token no disponible');
        return Promise.resolve(null); // Retorna null si el token no está disponible
      }
    }catch (error){
      console.error('Error al obtener el token:', error);
      return Promise.resolve(null);
    }
  }

  // Método para verificar si está autenticado
  get isAuthenticated$() {
    return this.isAuthenticated.asObservable();
  }

  // Método para actualizar el estado de autenticación
  setAuthenticated(isAuthenticated: boolean): void {
    this.isAuthenticated.next(isAuthenticated);
  }

  // Obtener si Keycloak está completamente inicializado
  isInitialized$() {
    return this.keycloakInitialized.asObservable();
  }
}
