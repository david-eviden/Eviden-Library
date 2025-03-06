import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Pedido } from './pedido';
import { Usuario } from '../usuario/usuario';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {

  private urlEndPoint: string = 'http://localhost:8080/api/pedidos'; 
  
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

    getPedidosPorUsuarioId(usuarioId: number): Observable<Pedido[]> {
      return this.http.get<Pedido[]>(`${this.urlEndPoint}/usuario/${usuarioId}`, 
        { headers: this.createHeaders() }
      );
    }
  
    getPedidos(): Observable<Pedido[]> {
      return this.http.get<any[]>(this.urlEndPoint, { headers: this.createHeaders() }).pipe(
        map(response => {
          return response.map(item => {
            const pedido = new Pedido();
            pedido.id = item.id;
            pedido.estado = item.estado;
            pedido.total = item.total;
            pedido.precioTotal = item.precioTotal;
            pedido.direccionEnvio = item.direccionEnvio;
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

            console.log(`Pedido ID: ${item.id}, Detalles recibidos:`, item.detalles);
        
            // Verificar explícitamente si hay detalles antes de mapear
            if (item.detalles && Array.isArray(item.detalles) && item.detalles.length > 0) {
              pedido.detalles = item.detalles.map((detalle: any) => ({
                id: detalle.id,
                libro: detalle.libro,
                cantidad: detalle.cantidad,
                precioUnitario: detalle.precioUnitario
              }));
            } else {
              console.warn(`No se encontraron detalles para el pedido ID: ${item.id}`);
              pedido.detalles = [];
            }
            
            return pedido;
          });
        })
      ); 
    }
}
