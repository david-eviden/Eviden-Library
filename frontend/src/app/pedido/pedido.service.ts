import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable, catchError, throwError, of } from 'rxjs';
import { Pedido } from './pedido';
import { Usuario } from '../usuario/usuario';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {

  private urlEndPoint: string = 'http://localhost:8081/api/pedidos'; 
  private urlEndPointPedido: string = 'http://localhost:8081/api/pedido';
  private urlEndPointDetallePedido: string = 'http://localhost:8081/api/detalle-pedido';
  
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

    // Método para crear un nuevo pedido
    createPedido(pedido: any): Observable<Pedido> {
      const headers = this.createHeaders();
      console.log('Enviando pedido al servidor:', JSON.stringify(pedido));
      
      // Asegurarnos de que el pedido tiene todos los campos necesarios
      if (!pedido.usuario || !pedido.usuario.id) {
        console.error('Error: El pedido no tiene un usuario asociado');
        return throwError(() => new Error('El pedido no tiene un usuario asociado'));
      }
      
      if (!pedido.fechaPedido) {
        console.error('Error: El pedido no tiene una fecha');
        return throwError(() => new Error('El pedido no tiene una fecha'));
      }
      
      if (!pedido.estado) {
        console.error('Error: El pedido no tiene un estado');
        return throwError(() => new Error('El pedido no tiene un estado'));
      }
      
      if (!pedido.total) {
        console.error('Error: El pedido no tiene un total');
        return throwError(() => new Error('El pedido no tiene un total'));
      }
      
      if (!pedido.direccionEnvio) {
        console.error('Error: El pedido no tiene una dirección de envío');
        return throwError(() => new Error('El pedido no tiene una dirección de envío'));
      }
      
      return this.http.post<any>(`${this.urlEndPointPedido}`, pedido, { headers }).pipe(
        map(response => {
          console.log('Respuesta del servidor al crear pedido:', response);
          if (response && response.pedido) {
            console.log('Pedido creado con ID:', response.pedido.id);
            return response.pedido;
          } else if (response && response.id) {
            console.log('Pedido creado con ID:', response.id);
            return response;
          } else if (response) {
            console.log('Respuesta del servidor sin estructura esperada:', response);
            return response;
          } else {
            console.error('Respuesta vacía del servidor');
            throw new Error('Respuesta vacía del servidor');
          }
        }),
        catchError(error => {
          console.error('Error al crear el pedido:', error);
          if (error.error && error.error.mensaje) {
            console.error('Mensaje de error del servidor:', error.error.mensaje);
          }
          
          // Si el error es por formato de fecha, intentar con otro formato
          if (error.error && error.error.includes && error.error.includes('Cannot deserialize value of type `java.util.Date`')) {
            console.log('Error de formato de fecha, intentando con otro formato...');
            
            // Convertir la fecha al formato esperado por el backend
            const fechaPedido = pedido.fechaPedido;
            if (typeof fechaPedido === 'string' && fechaPedido.includes('-')) {
              // Convertir de YYYY-MM-DD a DD/MM/YYYY
              const partes = fechaPedido.split('-');
              if (partes.length === 3) {
                pedido.fechaPedido = `${partes[2]}/${partes[1]}/${partes[0]}`;
                console.log('Reintentando con fecha formateada:', pedido.fechaPedido);
                return this.createPedido(pedido);
              }
            }
          }
          
          return throwError(() => error);
        })
      );
    }

    getPedidosPorUsuarioId(usuarioId: number): Observable<Pedido[]> {
      // Use the correct endpoint URL format as found in the backend controller
      const url = `${this.urlEndPoint}/usuario/${usuarioId}`;
      console.log('Llamando a la URL para obtener pedidos del usuario:', url);
      
      return this.http.get<any[]>(url, 
        { headers: this.createHeaders() }
      ).pipe(
        map(response => {
          console.log('Respuesta del servidor para pedidos del usuario:', response);
          // Check if response is null or undefined
          if (!response) {
            console.warn('La respuesta del servidor es nula o indefinida');
            return [];
          }
          
          // Ensure response is an array
          if (!Array.isArray(response)) {
            console.warn('La respuesta del servidor no es un array:', response);
            // If it's a single object, wrap it in an array
            if (response && typeof response === 'object') {
              response = [response];
            } else {
              return [];
            }
          }
          
          console.log(`Procesando ${response.length} pedidos recibidos del servidor`);
          
          return response.map(item => {
            console.log('Procesando pedido:', item);
            const pedido = new Pedido();
            pedido.id = item.id;
            pedido.fechaPedido = item.fechaPedido;
            pedido.estado = item.estado;
            pedido.total = item.total;
            pedido.precioTotal = item.precioTotal;
            pedido.direccionEnvio = item.direccionEnvio;
            
            if (item.usuario) {
              console.log('Usuario en pedido:', item.usuario);
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
        }),
        catchError(error => {
          console.error('Error al obtener los pedidos del usuario:', error);
          return of([]); // Return empty array instead of throwing error
        })
      );
    }
  
    getPedidos(): Observable<Pedido[]> {
      return this.http.get<any[]>(this.urlEndPoint, { headers: this.createHeaders() }).pipe(
        map(response => {
          console.log('Respuesta del servidor para todos los pedidos:', response);
          
          // Check if response is null or undefined
          if (!response) {
            console.warn('La respuesta del servidor es nula o indefinida');
            return [];
          }
          
          // Ensure response is an array
          if (!Array.isArray(response)) {
            console.warn('La respuesta del servidor no es un array:', response);
            // If it's a single object, wrap it in an array
            if (response && typeof response === 'object') {
              response = [response];
            } else {
              return [];
            }
          }
          
          return response.map(item => {
            const pedido = new Pedido();
            pedido.id = item.id;
            pedido.fechaPedido = item.fechaPedido;
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
        }),
        catchError(error => {
          console.error('Error al obtener todos los pedidos:', error);
          return of([]); // Return empty array instead of throwing error
        })
      ); 
    }

    // Método para obtener un pedido por su ID
    getPedidoPorId(pedidoId: number): Observable<Pedido> {
      const url = `${this.urlEndPointPedido}/${pedidoId}`;
      console.log('Llamando a la URL para obtener pedido por ID:', url);
      
      return this.http.get<any>(url, { headers: this.createHeaders() }).pipe(
        map(response => {
          console.log('Respuesta del servidor para pedido por ID:', response);
          
          // Check if response is null or undefined
          if (!response) {
            console.warn('La respuesta del servidor es nula o indefinida');
            throw new Error('No se encontró el pedido');
          }
          
          const pedido = new Pedido();
          pedido.id = response.id;
          pedido.fechaPedido = response.fechaPedido;
          pedido.estado = response.estado;
          pedido.total = response.total;
          pedido.precioTotal = response.precioTotal;
          pedido.direccionEnvio = response.direccionEnvio;
          
          if (response.usuario) {
            console.log('Usuario en pedido:', response.usuario);
            const usuario = new Usuario();
            usuario.id = response.usuario.id;
            usuario.nombre = response.usuario.nombre;
            usuario.apellido = response.usuario.apellido;
            usuario.direccion = response.usuario.direccion;
            usuario.email = response.usuario.email;
            usuario.password = '';
            usuario.rol = response.usuario.rol;
            pedido.usuario = usuario;
          }
          
          // Verificar explícitamente si hay detalles antes de mapear
          if (response.detalles && Array.isArray(response.detalles) && response.detalles.length > 0) {
            pedido.detalles = response.detalles.map((detalle: any) => ({
              id: detalle.id,
              libro: detalle.libro,
              cantidad: detalle.cantidad,
              precioUnitario: detalle.precioUnitario
            }));
            console.log(`Se encontraron ${pedido.detalles.length} detalles para el pedido ID: ${pedido.id}`);
          } else {
            console.warn(`No se encontraron detalles para el pedido ID: ${pedido.id}`);
            pedido.detalles = [];
          }
          
          return pedido;
        }),
        catchError(error => {
          console.error('Error al obtener el pedido por ID:', error);
          return throwError(() => new Error('Error al obtener el pedido: ' + error.message));
        })
      );
    }

    // Método para crear un detalle de pedido
    createDetallePedido(detallePedido: any): Observable<any> {
      const headers = this.createHeaders();
      console.log('Enviando detalle de pedido al servidor:', JSON.stringify(detallePedido));
      
      // Asegurarnos de que el detalle tiene todos los campos necesarios
      if (!detallePedido.pedido || !detallePedido.pedido.id) {
        console.error('Error: El detalle de pedido no tiene un pedido asociado');
        return throwError(() => new Error('El detalle de pedido no tiene un pedido asociado'));
      }
      
      if (!detallePedido.libro || !detallePedido.libro.id) {
        console.error('Error: El detalle de pedido no tiene un libro asociado');
        return throwError(() => new Error('El detalle de pedido no tiene un libro asociado'));
      }
      
      return this.http.post<any>(`${this.urlEndPointDetallePedido}`, detallePedido, { headers }).pipe(
        map(response => {
          console.log('Respuesta del servidor al crear detalle de pedido:', response);
          if (response && response.detallePedido) {
            return response.detallePedido;
          } else if (response) {
            return response;
          } else {
            throw new Error('Respuesta vacía del servidor');
          }
        }),
        catchError(error => {
          console.error('Error al crear el detalle de pedido:', error);
          if (error.error && error.error.mensaje) {
            console.error('Mensaje de error del servidor:', error.error.mensaje);
          }
          // Devolver un objeto vacío para que forkJoin no falle completamente
          // y se puedan procesar otros detalles
          return of({
            error: true,
            mensaje: 'Error al crear el detalle de pedido',
            detalleError: error
          });
        })
      );
    }
}
