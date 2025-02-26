import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Pedido } from './pedido';
import { Usuario } from '../usuario/usuario';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {

  private urlEndPoint: string = 'http://localhost:8081/api/pedidos'; 
  
    constructor(private http: HttpClient) {}
  
    getPedidos(): Observable<Pedido[]> {
      return this.http.get<any[]>(this.urlEndPoint).pipe(
        map(response => {
          console.log('Respuesta cruda del servidor:', response); // Debug
          return response.map(item => {
            const pedido = new Pedido();
            pedido.id = item.id;
            if (item.usuario) {
              console.log('Usuario en pedido:', item.usuario); // Debug
              const usuario = new Usuario();
              usuario.id = item.usuario.id;
              usuario.nombre = item.usuario.nombre;
              usuario.apellido = item.usuario.apellido;
              usuario.direccion = item.usuario.direccion;
              usuario.email = item.usuario.email;
              usuario.password = '';
              usuario.rol = item.usuario.rol;
              pedido.usuario = usuario;
            }
            pedido.fechaPedido = item.fechaPedido;
            pedido.estado = item.estado;
            pedido.total = item.total;
            pedido.direccionEnvio = item.direccionEnvio;
            pedido.detalles = item.detalles?.map((detalle: any) => ({
              id: detalle.id,
              libro: detalle.libro,
              cantidad: detalle.cantidad,
              precioUnitario: detalle.precioUnitario
            })) || [];
            return pedido;
          });
        })
      ); 
    }
}
