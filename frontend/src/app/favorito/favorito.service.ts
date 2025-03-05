import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Favorito } from './favorito';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class FavoritoService {

  private urlEndPoint: string = 'http://localhost:8080/api/favoritos'; 
  
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
  
  getUsuarios(): Observable<Favorito[]> {
    return this.http.get(this.urlEndPoint, { headers: this.createHeaders() }).pipe(

      // Conversión a favoritos (response de Object a Favorito[])
      map(response => {

        let favoritos = response as Favorito[];

        return favoritos.map(favorito => {
          favorito.usuario = favorito.usuario;
          favorito.libro = favorito.libro;
          favorito.fechaAgregado = favorito.fechaAgregado;
         
          return favorito;
        });
      }),
    ); 
  }
}
