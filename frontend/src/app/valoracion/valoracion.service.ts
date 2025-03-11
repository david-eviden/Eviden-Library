import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, OnInit } from '@angular/core';
import { BehaviorSubject, catchError, map, Observable, tap, throwError } from 'rxjs';
import { Valoracion } from './valoracion';
import { Router } from '@angular/router';
import swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class ValoracionService implements OnInit {

  private urlEndPoint: string = 'http://localhost:8081/api/valoraciones';
  private urlEndPoint1: string = 'http://localhost:8081/api/valoracion'; 
  private httpHeaders = new HttpHeaders({'Content-Type': 'application/json'});

  // Creamos un BehaviorSubject para la lista de valoraciones
  private valoracionesSubject = new BehaviorSubject<Valoracion[]>([]);
  valoraciones$ = this.valoracionesSubject.asObservable();  // Observable al que nos suscribimos en los componentes

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {}

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

  getValoracionesPorUsuarioId(usuarioId: number): Observable<Valoracion[]> {
    return this.http.get<Valoracion[]>(`${this.urlEndPoint}/usuario/${usuarioId}`, 
      { headers: this.createHeaders() }
    );
  }

  getValoraciones(): Observable<Valoracion[]> {
    return this.http.get<any[]>(this.urlEndPoint, { headers: this.createHeaders() }).pipe(
      map(response => {
        console.log('Respuesta del servidor:', response);
        return response.map(item => {
          const valoracion = new Valoracion();
          valoracion.id = item.id;
          valoracion.usuario = item.usuario;
          valoracion.libro = item.libro;
          valoracion.puntuacion = item.puntuacion;
          valoracion.comentario = item.comentario;
          valoracion.libroDetalles = item.libroDetalles;
          valoracion.fecha = new Date(item.fecha);
          return valoracion;
        });
      })
    ); 
  }

  // Crear valoracion
  create(valoracion: Valoracion): Observable<any> {
    return this.http.post<any>(this.urlEndPoint1, valoracion, { headers: this.createHeaders() }).pipe(
      catchError(e => {
        if (e.status == 400) {
          return throwError(e);
        }

        this.router.navigate(['/valoraciones']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  // Obtener valoracion individual
  getValoracion(id: number): Observable<Valoracion> {
    return this.http.get<Valoracion>(`${this.urlEndPoint1}/${id}`, { headers: this.createHeaders() }).pipe(
      catchError(e => {
        this.router.navigate(['/valoraciones']);
        console.log(e.error.mensaje);
        swal('Error al obtener el valoracion', e.error.mensaje, 'error');
        return throwError(e);
      })
    );
  }

  // Actualizar valoracion
  updateValoracion(valoracion: Valoracion): Observable<any> {
    return this.http.put<any>(`${this.urlEndPoint1}/${valoracion.id}`, valoracion, { headers: this.createHeaders() }).pipe(
      catchError(e => {
        if (e.status == 400) {
          return throwError(e);
        }

        this.router.navigate(['/valoraciones']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  // Eliminar valoracion
  delete(id: number): Observable<Valoracion> {
    return this.http.delete<Valoracion>(`${this.urlEndPoint1}/${id}`, { headers: this.createHeaders() }).pipe(
      catchError(e => {
        this.router.navigate(['/valoraciones']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      }),
      tap(() => {
        this.getValoraciones().subscribe();  // Refrescamos la lista
      })
    );
  }
}
