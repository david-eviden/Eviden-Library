import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private urlEndPoint = 'http://localhost:8080/api/login';
  private usuarioActualSubject = new BehaviorSubject<any>(null);
  public usuarioActual = this.usuarioActualSubject.asObservable();

  constructor(private http: HttpClient) {
    // Intentar recuperar usuario del localStorage al iniciar
    const usuarioGuardado = localStorage.getItem('usuario');
    if (usuarioGuardado) {
      this.usuarioActualSubject.next(JSON.parse(usuarioGuardado));
    }
  }

  login(credenciales: {email: string, password: string}): Observable<any> {
    return this.http.post<any>(this.urlEndPoint, credenciales).pipe(
      tap(usuario => {
        // Guardar usuario en localStorage y actualizar el subject
        localStorage.setItem('usuario', JSON.stringify(usuario));
        this.usuarioActualSubject.next(usuario);
      })
    );
  }

  logout() {
    // Limpiar localStorage y el subject
    localStorage.removeItem('usuario');
    this.usuarioActualSubject.next(null);
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