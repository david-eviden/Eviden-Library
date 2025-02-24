import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

@Injectable({
  providedIn: 'root'
})
export class KeycloakAuthService {

  constructor(private http: HttpClient, private keycloakService: KeycloakService) {}

    //Inicializa la configuración de Keycloak
  async init(): Promise<void> {
    await this.keycloakService.init({
      config: {
        url: 'http://localhost:9090',  // URL de tu servidor Keycloak
        realm: 'EVIDEN-LIBRARY',  // Nombre realm
        clientId: 'admin',           // ID de tu cliente
      },
      initOptions: {
        onLoad: 'login-required',            // Obligamos al login si no está autenticado
        checkLoginIframe: false              // Evitar problemas con iframes
      },
      enableBearerInterceptor: true,    // Habilita el interceptor de tokens para incluir el token en las solicitudes
      bearerExcludedUrls: ['/assets']   // Excluye las URLs que no necesitan autenticación (por ejemplo, los recursos estáticos)
    });
  }

  getData() {
    const token = this.keycloakService.getToken();  // Obtener el token desde Keycloak

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,  // Incluir el token en el header de la solicitud
    });

    return this.http.get('http://localhost:9090/api/resource', { headers });
  }

  getToken(): string {
    return this.keycloakService.getKeycloakInstance().token;
  }

  logout(): void {
    this.keycloakService.logout();
  }
}
