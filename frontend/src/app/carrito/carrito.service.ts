import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Carrito } from './carrito';

@Injectable({
  providedIn: 'root'
})
export class CarritoService {

  private urlEndPoint: string = 'http://localhost:8080/api/carritos'; 

  constructor(private http: HttpClient) {}

  getUsuarios(): Observable<Carrito[]> {
    return this.http.get(this.urlEndPoint).pipe(

      // Conversión a carritos (response de Object a Usuario[])
      map(response => {

        let carritos = response as Carrito[];

        return carritos.map(carrito => {
          carrito.usuario = carrito.usuario;
          carrito.fechaCreacion = carrito.fechaCreacion;
          carrito.estado = carrito.estado;
          carrito.detalles = carrito.detalles;
         
          return carrito;
        });
      }),
    ); 
  }
}
