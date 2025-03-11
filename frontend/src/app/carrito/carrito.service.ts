import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Carrito } from './carrito';
import { detallesCarrito } from '../detalles-carrito/detalles-carrito';
import { Libro } from '../libro/libro';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class CarritoService {

  private urlEndPoint: string = 'http://localhost:8080/api/carritos'; 
  private httpHeaders = new HttpHeaders({'Content-Type': 'application/json'});
  private carrito: any[] = [];

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

  getCarritos(): Observable<Carrito[]> {
    return this.http.get<any[]>(this.urlEndPoint, { headers: this.createHeaders() }).pipe(
      map(response => {
        console.log('Respuesta del servidor:', response);
        return response.map(item => {
          const carrito = new Carrito();
          if (item.usuario) {
            carrito.usuario = item.usuario;
          }
          carrito.fechaCreacion = new Date(item.fechaCreacion);
          carrito.estado = item.estado;
          carrito.detalles = item.detalles?.map((detalle: any) => {
            const detalleCarrito = new detallesCarrito();
            detalleCarrito.id = detalle.id;
            detalleCarrito.cantidad = detalle.cantidad;
            detalleCarrito.precioUnitario = detalle.precioUnitario;
            
            // Mapear el libro correctamente
            if (detalle.libro) {
              const libro = new Libro();
              libro.id = detalle.libro.id;
              libro.titulo = detalle.libro.titulo;
              libro.precio = detalle.libro.precio;
              libro.stock = detalle.libro.stock;
              detalleCarrito.libro = libro;
            }
            
            return detalleCarrito;
          }) || []; 
          return carrito;
        });
      })
    ); 
  }

  addToCarrito(libro: any): void {
    console.log('Añadir libro al carrito', libro);
  }
}
