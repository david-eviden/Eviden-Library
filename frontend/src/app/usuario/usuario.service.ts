import { Injectable } from '@angular/core';
import { map, Observable, catchError, throwError } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Usuario } from './usuario';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class UsuarioService {
  private urlEndPoint: string = 'http://localhost:8081/api/usuarios'; 
  private urlEndPoint1: string = 'http://localhost:8081/api/usuario/';


  constructor(private http: HttpClient, private router: Router) {}

  // Método para obtener el token del localStorage
  private getToken(): string | null {
    const token = localStorage.getItem('access_token');
    if (!token) {
      this.router.navigate(['/login']);  // Redirigir al login si el token no está presente
    }
    return token;
  }

  // Método para crear cabeceras con el token
  private createHeaders(): HttpHeaders {
    const token = this.getToken();
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
  
    if (token) {
      headers = headers.append('Authorization', `Bearer ${token}`);
      console.log('Token añadido en cabecera:', token);  // Log para ver si el token es correcto
    } else {
      console.log('No se encontró token en localStorage');
    }
  
    return headers;
  }

  getUsuarios(): Observable<Usuario[]> {
    return this.http.get(this.urlEndPoint, { headers: this.createHeaders() }).pipe(
      // Conversión a usuarios (response de Object a Usuario[])
      map(response => {
        let usuarios = response as Usuario[];
        return usuarios.map(usuario => {
          usuario.nombre = usuario.nombre?.toUpperCase();
          usuario.apellido = usuario.apellido;
          usuario.email = usuario.email;
          usuario.direccion = usuario.direccion;
          usuario.password = usuario.password;
          usuario.rol = usuario.rol;
          return usuario;
        });
      }),
      catchError(e => {
        console.error('Error al obtener usuarios:', e);
        return throwError(() => e);
      })
    ); 
  }

  // Obtener un usuario por ID
  getUsuario(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.urlEndPoint1}${id}`, { headers: this.createHeaders() }).pipe(
      map(response => response as Usuario),
      catchError(e => {
        console.error('Error al obtener el usuario:', e);
        return throwError(() => e);
      })
    );
  }

  // Actualizar un usuario
  update(usuario: Usuario): Observable<any> {
    return this.http.put<any>(`${this.urlEndPoint1}${usuario.id}`, usuario, { headers: this.createHeaders() }).pipe(
      catchError(e => {
        console.error('Error al actualizar el usuario:', e);
        return throwError(() => e);
      })
    );
  }
}