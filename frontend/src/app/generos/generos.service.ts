import { Injectable, OnInit } from '@angular/core';
import { BehaviorSubject, catchError, map, Observable, tap, throwError } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Genero } from './generos';
import { Router } from '@angular/router';
import swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class GeneroService implements OnInit{
  private urlEndPoint: string = 'http://localhost:8081/api/generos';
  private urlEndPoint1: string = 'http://localhost:8081/api/genero'; 
  private httpHeaders = new HttpHeaders({'Content-Type': 'application/json'});

  // Creamos un BehaviorSubject para la lista de generoes
  private generoesSubject = new BehaviorSubject<Genero[]>([]);
  generoes$ = this.generoesSubject.asObservable();  // Observable al que nos suscribimos en los componentes

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {}

  getGeneros(): Observable<Genero[]> {

    return this.http.get(this.urlEndPoint).pipe(
      
      // Conversión a generos (response de Object a Genero[])
      map(response => {
        let generos = response as Genero[];

        return generos.map(genero => {
          genero.nombre = genero.nombre?.toUpperCase();
          genero.descripcion = genero.descripcion;

          return genero;
        });
      }),
    );
  }

  // Crear genero
  create(genero: Genero): Observable<any> {
    return this.http.post<any>(this.urlEndPoint1, genero, {headers: this.httpHeaders}).pipe(
      catchError(e => {
        if (e.status == 400) {
          return throwError(e);
        }

        this.router.navigate(['/generos']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  // Obtener genero individual
  getGenero(id: number): Observable<Genero> {
    return this.http.get<Genero>(`${this.urlEndPoint1}/${id}`).pipe(
      catchError(e => {
        this.router.navigate(['/generos']);
        console.log(e.error.mensaje);
        swal('Error al obtener el genero', e.error.mensaje, 'error');
        return throwError(e);
      })
    );
  }

  // Actualizar genero
  updateGenero(genero: Genero): Observable<any> {
    return this.http.put<any>(`${this.urlEndPoint1}/${genero.id}`, genero, {headers: this.httpHeaders}).pipe(
      catchError(e => {
        if (e.status == 400) {
          return throwError(e);
        }

        this.router.navigate(['/generos']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  // Eliminar genero
  delete(id: number): Observable<Genero> {
    return this.http.delete<Genero>(`${this.urlEndPoint}/${id}`, {headers: this.httpHeaders}).pipe(
      catchError(e => {
        this.router.navigate(['/generos']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      }),
      tap(() => {
        this.getGeneros().subscribe();  // Refrescamos la lista
      })
    );
  }

  // Eliminar todos los generoes
  deleteAll(): Observable<void> {
    return this.http.delete<void>(`${this.urlEndPoint}`);
  }
}