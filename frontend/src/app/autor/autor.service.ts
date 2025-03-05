import { Injectable, OnInit } from '@angular/core';
import { BehaviorSubject, catchError, map, Observable, tap, throwError } from 'rxjs';
import { Autor } from './autor';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import swal from 'sweetalert2';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class AutorService implements OnInit{
  private urlEndPoint: string = 'http://localhost:8080/api/autores';
  private urlEndPoint1: string = 'http://localhost:8080/api/autor'; 
  private httpHeaders = new HttpHeaders({'Content-Type': 'application/json'});

  // Creamos un BehaviorSubject para la lista de autores
  private autoresSubject = new BehaviorSubject<Autor[]>([]);
  autores$ = this.autoresSubject.asObservable();  // Observable al que nos suscribimos en los componentes

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

  // Obtener lista de autores
  getAutores(): Observable<Autor[]> {
    return this.http.get(this.urlEndPoint, { headers: this.createHeaders() }).pipe(
      map(response => {
        let autores = response as Autor[];
        return autores.map(autor => {
          autor.nombre = autor.nombre;
          autor.biografia = autor.biografia;
          return autor;
        });
      }),
      tap(autores => {
        // Al obtener la lista, actualizamos el BehaviorSubject
        this.autoresSubject.next(autores);
      })
    );
  }

  // Crear autor
  create(autor: Autor): Observable<any> {
    return this.http.post<any>(this.urlEndPoint1, autor, { headers: this.createHeaders() }).pipe(
      catchError(e => {
        if (e.status == 400) {
          return throwError(e);
        }

        this.router.navigate(['/autores']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  // Obtener autor individual
  getAutor(id: number): Observable<Autor> {
    return this.http.get<Autor>(`${this.urlEndPoint1}/${id}`, { headers: this.createHeaders() }).pipe(
      catchError(e => {
        this.router.navigate(['/autores']);
        console.log(e.error.mensaje);
        swal('Error al obtener el autor', e.error.mensaje, 'error');
        return throwError(e);
      })
    );
  }

  // Actualizar autor
  updateAutor(autor: Autor): Observable<any> {
    return this.http.put<any>(`${this.urlEndPoint1}/${autor.id}`, autor, { headers: this.createHeaders() }).pipe(
      catchError(e => {
        if (e.status == 400) {
          return throwError(e);
        }

        this.router.navigate(['/autores']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  // Eliminar autor
  delete(id: number): Observable<Autor> {
    return this.http.delete<Autor>(`${this.urlEndPoint1}/${id}`, { headers: this.createHeaders() }).pipe(
      catchError(e => {
        this.router.navigate(['/autores']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      }),
      tap(() => {
        this.getAutores().subscribe();  // Refrescamos la lista
      })
    );
  }

  // Eliminar todos los autores
  deleteAll(): Observable<void> {
    return this.http.delete<void>(`${this.urlEndPoint}`, { headers: this.createHeaders() });
  }
}

