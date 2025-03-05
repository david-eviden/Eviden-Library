import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { detallesCarrito } from './detalles-carrito';

@Injectable({
  providedIn: 'root'
})
export class DetallesCarritoService {

  private urlEndPoint: string = 'http://localhost:8081/api/detallesCarrito'; 

  constructor(private http: HttpClient) {}

  getdetallesCarrito(): Observable<detallesCarrito[]> {
    return this.http.get(this.urlEndPoint).pipe(

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
