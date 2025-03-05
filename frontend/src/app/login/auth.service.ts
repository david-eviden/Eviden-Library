import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private urlEndPoint = 'http://localhost:8082/realms/EvidenLibrary/protocol/openid-connect/token';
  private usuarioActualSubject = new BehaviorSubject<any>(null);
  public usuarioActual = this.usuarioActualSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    // Intentar recuperar usuario del localStorage al iniciar
    const usuarioGuardado = localStorage.getItem('usuario');
    if (usuarioGuardado) {
      this.usuarioActualSubject.next(JSON.parse(usuarioGuardado));
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
        // Guardar el token en el localStorage
        localStorage.setItem('access_token', usuario.access_token);
        localStorage.setItem('usuario', JSON.stringify(usuario));
      })
    );
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
    return !!this.usuarioActualSubject.value;
  }
}