import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Pedido } from './pedido';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {

  private urlEndPoint: string = 'http://localhost:8080/api/pedidos'; 
  
    constructor(private http: HttpClient) {}
  
    getUsuarios(): Observable<Pedido[]> {
      return this.http.get(this.urlEndPoint).pipe(
  
        // Conversión a pedidos (response de Object a Pedido[])
        map(response => {
  
          let pedidos = response as Pedido[];
  
          return pedidos.map(pedido => {
            pedido.usuario = pedido.usuario;
            pedido.fechaPedido = pedido.fechaPedido;
            pedido.estado = pedido.estado;
            pedido.total = pedido.total;
            pedido.direccionEnvio = pedido.direccionEnvio;
            pedido.detalles = pedido.detalles;
           
            return pedido;
          });
        }),
      ); 
    }
}
