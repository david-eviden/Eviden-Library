import { Injectable } from '@angular/core';
import { catchError, map, Observable, tap, throwError } from 'rxjs';
import { Libro } from './libro';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class LibroService {
  private urlEndPoint: string = 'http://localhost:8080/api/libros'; 
  private urlEndPoint1: string = 'http://localhost:8080/api/libro'; 
  private httpHeaders = new HttpHeaders({'Content-Type': 'application/json'});

  constructor(private http: HttpClient, private router: Router) {}

  // Get libros (paginado)
  getLibros(page: number): Observable<any> {
    return this.http.get(this.urlEndPoint + '/page/' + page).pipe(

      tap((response: any) => {
        console.log('LibroService: tap 1 (Object)');
        (response.content as Libro[]).forEach(libro => {
          console.log(libro.titulo);
        })
      }),

      map((response: any) => {
        // Retornamos
        (response.content as Libro[]).map(libro => {
          libro.titulo = libro.titulo;
          libro.precio = libro.precio;
          libro.stock = libro.stock;
          libro.descripcion = libro.descripcion;

          //let datePipe = new DatePipe('es');
          //libro.createAt = libro.createAt ? datePipe.transform(libro.createAt, 'EEEE dd, MMM yyyy') ?? '11-12-2001' : '11-12-2001';
          
          return libro;
        });
        return response;
      }),
    );
  }


  /* Sin paginacion */

  /*
  // Observable para que sea asíncrono (se actualice en tiempo real)
  getLibros(): Observable<Libro[]> {

    // return of(LIBROS);

    return this.http.get(this.urlEndPoint).pipe(

      // Conversión a libros (response de Object a Libro[])
      map(response => {
        let libros = response as Libro[];

        return libros.map(libro => {
          libro.titulo = libro.titulo?.toUpperCase();
          libro.precio = libro.precio;
          libro.stock = libro.stock;
          libro.descripcion = libro.descripcion;
          
          return libro;
        });
      }),

    );
  } */

  // Crear libro
  create(libro: Libro) : Observable<any> {
    return this.http.post<any>(this.urlEndPoint1, libro, {headers: this.httpHeaders}).pipe(
      catchError(e => {

        // Validamos
        if(e.status==400) {
          return throwError(e);
        }

        // Controlamos otros errores
        this.router.navigate(['/libros']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  // Obtener
  getLibro(id: number): Observable<Libro> {
    // pipe para canalizar errores
    return this.http.get<Libro>(`${this.urlEndPoint1}/${id}`).pipe(
      catchError(e => {
        this.router.navigate(['/libros']);
        console.log(e.error.mensaje);
        swal('Error al obtener el libro', e.error.mensaje, 'error');
        return throwError(e);
      })
    );
  }

  // Update
  updateLibro(libro: Libro): Observable<any> {
    return this.http.put<any>(`${this.urlEndPoint1}/${libro.id}`, libro, {headers: this.httpHeaders}).pipe(
      catchError(e => {

        // Validamos
        if(e.status==400) {
          return throwError(e);
        }

        // Controlamos otros errores
        this.router.navigate(['/libros']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  // Delete
  delete(id: number): Observable<Libro> {
    return this.http.delete<Libro>(`${this.urlEndPoint}/${id}`, {headers: this.httpHeaders}).pipe(
      catchError(e => {
        this.router.navigate(['/libros']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );;
  }

  // Delete todos
  deleteAll(): Observable<void> {
    return this.http.delete<void>(`${this.urlEndPoint}`);
  }

}