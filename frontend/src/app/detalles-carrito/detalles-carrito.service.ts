import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { detallesCarrito } from './detalles-carrito';
import { AuthService } from '../login/auth.service';
import { Libro } from '../libro/libro';

@Injectable({
  providedIn: 'root'
})
export class DetallesCarritoService {

  private contadorItemsCarrito = new BehaviorSubject<number>(0);

  private urlEndPoint: string = 'http://localhost:8080/api/detalles-carrito';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  private createHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    if (!token) {
      throw new Error('No hay token de autenticación');
    }
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  getdetallesCarrito(): Observable<detallesCarrito[]> {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      return throwError(() => new Error('Usuario no autenticado'));
    }

    const headers = this.createHeaders();
    return this.http.get<detallesCarrito[]>(`${this.urlEndPoint}/usuario/${userId}`, { headers }).pipe(
      catchError(error => {
        console.error('Error al obtener detalles del carrito:', error);
        return throwError(() => error);
      })
    );
  }

  getDetalleCarrito(id: number): Observable<detallesCarrito> {
    const headers = this.createHeaders();
    return this.http.get<detallesCarrito>(`${this.urlEndPoint}/${id}`, { headers }).pipe(
      catchError(error => {
        console.error('Error al obtener detalle del carrito:', error);
        return throwError(() => error);
      })
    );
  }

  create(detalleCarrito: detallesCarrito): Observable<detallesCarrito> {
    const headers = this.createHeaders();
    return this.http.post<detallesCarrito>(`${this.urlEndPoint}`, detalleCarrito, { headers }).pipe(
      catchError(error => {
        console.error('Error al crear detalle del carrito:', error);
        return throwError(() => error);
      })
    );
  }

  update(detalleCarrito: detallesCarrito): Observable<detallesCarrito> {
    const headers = this.createHeaders();
    return this.http.put<any>(`http://localhost:8080/api/detalle-carrito/${detalleCarrito.id}`, detalleCarrito, { headers }).pipe(
      map(response => response.detalleCarrito as detallesCarrito),
      catchError(error => {
        console.error('Error al actualizar detalle del carrito:', error);
        return throwError(() => error);
      })
    );
  }

  delete(id: number): Observable<void> {
    const headers = this.createHeaders();
    return this.http.delete<void>(`http://localhost:8080/api/detalle-carrito/${id}`, { headers }).pipe(
      catchError(error => {
        console.error('Error al eliminar detalle del carrito:', error);
        return throwError(() => error);
      })
    );
  }

  addToCart(libro: Libro, cantidad: number = 1): Observable<detallesCarrito> {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      return throwError(() => new Error('Usuario no autenticado'));
    }

    const headers = this.createHeaders();
    const detalleCarrito = {
      libro: libro,
      cantidad: cantidad,
      precioUnitario: libro.precio
    };

    return this.http.post<detallesCarrito>(`${this.urlEndPoint}/add/${userId}`, detalleCarrito, { headers }).pipe(
      map(response => {
        // Después de añadir el item, obtenemos el total actualizado
        this.getdetallesCarrito().subscribe(items => {
          const totalItems = items.reduce((total, item) => total + item.cantidad, 0);
          this.updateCartItemCount(totalItems);
        });
        return response;
      }),
      catchError(error => {
        console.error('Error al añadir al carrito:', error);
        return throwError(() => error);
      })
    );
  }

  // Método para obtener el número de artículos en el carrito
  getCartItemCount() {
    return this.contadorItemsCarrito.asObservable();
  }

  // Método para actualizar el número de artículos
  updateCartItemCount(count: number) {
    this.contadorItemsCarrito.next(count);
  }
}