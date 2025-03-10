import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { detallesCarrito } from './detalles-carrito';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class DetallesCarritoService {

  private urlEndPoint: string = 'http://localhost:8081/api/detallesCarrito'; 
  private httpHeaders = new HttpHeaders({'Content-Type': 'application/json'});

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

  getdetallesCarrito(): Observable<detallesCarrito[]> {
    return this.http.get(this.urlEndPoint, { headers: this.createHeaders() }).pipe(

      // Conversión a detallesCarrito (response de Object a detallesCarrito[])
      map(response => {

        let detallesCarrito = response as detallesCarrito[];

        return detallesCarrito.map(detallesCarrito => {
          detallesCarrito.carrito = detallesCarrito.carrito;
          detallesCarrito.cantidad = detallesCarrito.cantidad;
          detallesCarrito.libro = detallesCarrito.libro;
         
          return detallesCarrito;
        });
      }),
    ); 
  }
}
