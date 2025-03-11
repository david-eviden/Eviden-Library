import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { detallesPedido } from './detalles-pedido';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class DetallesPedidoService {

    private urlEndPoint: string = 'http://localhost:8081/api/detallesPedido'; 
  
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

    getdetallesPedido(): Observable<detallesPedido[]> {
      return this.http.get(this.urlEndPoint, { headers: this.createHeaders() }).pipe(
  
        // Conversión a detallesPedido (response de Object a detallesPedido[])
        map(response => {
  
          let detallesPedido = response as detallesPedido[];
  
          return detallesPedido.map(detallesPedido => {
            detallesPedido.pedido = detallesPedido.pedido;
            detallesPedido.libro = detallesPedido.libro;
            detallesPedido.cantidad = detallesPedido.cantidad;
            detallesPedido.precioUnitario = detallesPedido.precioUnitario;
           
            return detallesPedido;
          });
        }),
      ); 
    }
}
