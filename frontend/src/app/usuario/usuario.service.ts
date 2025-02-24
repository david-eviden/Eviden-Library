import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Usuario } from './usuario';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class UsuarioService {
  private urlEndPoint: string = 'http://localhost:8080/api/usuarios'; 

  constructor(private http: HttpClient) {}

  getUsuarios(): Observable<Usuario[]> {
    return this.http.get(this.urlEndPoint).pipe(

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