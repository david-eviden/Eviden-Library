import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Usuario } from './usuario';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class UsuarioService {
  private urlEndPoint: string = 'http://localhost:8080/api/usuarios'; 

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
    ); 
  }
}