import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, of } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private urlEndPoint = 'http://localhost:8082/realms/EvidenLibrary/protocol/openid-connect/token';
  private registroEndPoint = 'http://localhost:8082/admin/realms/EvidenLibrary/users';
  private usuarioActualSubject = new BehaviorSubject<any>(null);
  public usuarioActual = this.usuarioActualSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    // Intentar recuperar usuario del localStorage al iniciar
    const accessToken = localStorage.getItem('access_token');
    const usuarioGuardado = localStorage.getItem('usuario');
    
    if (accessToken && usuarioGuardado) {
      try {
        const usuario = JSON.parse(usuarioGuardado);
        this.usuarioActualSubject.next(usuario);
      } catch (error) {
        console.error('Error parsing user from localStorage', error);
        this.logout(); // Limpiar datos inválidos
      }
    } else {
      // Si no hay token, establecer explícitamente el valor a null
      this.usuarioActualSubject.next(null);
    }
  }

  login(credenciales: { email: string, password: string }): Observable<any> {
    const body = new URLSearchParams();
    body.set('client_id', 'eviden-library-rest-api'); 
    body.set('username', credenciales.email);
    body.set('password', credenciales.password);
    body.set('grant_type', 'password');
  
    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded'
    });
  
    return this.http.post<any>(this.urlEndPoint, body.toString(), { headers }).pipe(
      tap(usuario => {
        if (usuario && usuario.access_token) {
          const tokenPayload = this.decodeToken(usuario.access_token);
          
          // Extraer roles
          const roles = this.extractRolesFromPayload(tokenPayload);
          
          // Asignar rol (priorizar ADMIN)
          usuario.rol = roles.includes('ADMIN') ? 'ADMIN' : 'USER';
          
          console.log('Rol final:', usuario.rol);
  
          // Añadir información adicional del usuario
          usuario.username = tokenPayload.preferred_username;
          usuario.email = tokenPayload.email;
  
          // Guardar en localStorage
          localStorage.setItem('access_token', usuario.access_token);
          localStorage.setItem('usuario', JSON.stringify(usuario));
  
          // Actualizar subject
          this.usuarioActualSubject.next(usuario);
        }
      })
    );
  }
  
  private decodeToken(token: string): any {
    try {
      // Decodificar el payload del token JWT
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace('-', '+').replace('_', '/');
      return JSON.parse(window.atob(base64));
    } catch (error) {
      console.error('Error decoding el token', error);
      return {};
    }
  }
  
  private extractRolesFromPayload(payload: any): string[] {
    // Priorizar roles del reino y del cliente
    const realmRoles = payload['realm_access']?.roles || [];
    const clientRoles = payload['resource_access']?.['eviden-library-rest-api']?.roles || [];
  
    // Combinar y normalizar roles
    const roles = [...realmRoles, ...clientRoles];
    
    // Filtrar roles específicos
    const mappedRoles = roles
      .filter(role => 
        role.toLowerCase() === 'admin' || 
        role.toLowerCase() === 'user'
      )
      .map(role => role.toUpperCase());
  
    console.log('Roles mapeados:', mappedRoles);
    return mappedRoles;
  }
  

  logout() {
    // Limpiar localStorage y el subject
    localStorage.removeItem('access_token');  // Elimina el token de acceso
    localStorage.removeItem('usuario');  // Elimina la información del usuario
    this.usuarioActualSubject.next(null);  // Actualiza el subject a null
    
    this.router.navigate(['/login']);
  }
  

  get usuarioLogueado() {
    return this.usuarioActualSubject.value;
  }

  get esAdmin() {
    return this.usuarioActualSubject.value?.rol === 'ADMIN';
  }

  get esUsuario() {
    return this.usuarioActualSubject.value?.rol === 'USER';
  }

  estaLogueado(): boolean {
    // Verificar si hay un token en localStorage
    const token = localStorage.getItem('access_token');
    return !!token;
  }

  getCurrentUserId(): number {
    // Obtener el usuario actual del BehaviorSubject
    const usuarioActual = this.usuarioActualSubject.value;

    // Devolver el ID si existe, o null si no hay usuario
    return usuarioActual ? usuarioActual.id : 0;
  }

  registro(usuario: { 
    firstName: string,
    lastName: string,
    email: string, 
    password: string 
  }): Observable<any> {
    const registroPublicoEndPoint = 'http://localhost:8080/api/registro';
    
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    const body = {
      firstName: usuario.firstName,
      lastName: usuario.lastName,
      email: usuario.email,
      password: usuario.password
    };

    return this.http.post(registroPublicoEndPoint, body, { headers }).pipe(
      tap(() => {
        // Después del registro exitoso, intentamos hacer login automáticamente
        console.log('Registro exitoso, intentando iniciar sesión automáticamente');
      })
    );
  }
}