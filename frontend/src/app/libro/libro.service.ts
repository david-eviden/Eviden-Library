import { Injectable } from '@angular/core';
import { catchError, map, Observable, tap, throwError } from 'rxjs';
import { Libro } from './libro';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import swal from 'sweetalert2';
import { Autor } from '../autor/autor';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class LibroService {
  private urlEndPoint: string = 'http://localhost:8080/api/libros'; 
  private urlEndPoint1: string = 'http://localhost:8080/api/libro';
  private urlAutores: string = 'http://localhost:8080/api/autores'; 
  private httpHeaders = new HttpHeaders({'Content-Type': 'application/json'});

  constructor(private http: HttpClient, private router: Router) {}

  // Método para obtener el token del localStorage
  private getToken(): string | null {
    const token = localStorage.getItem('access_token');
    /* if (!token) {
      this.router.navigate(['/login']);  // Redirigir al login si el token no está presente
    } */
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
  

  //Get mejor valorados
  getMejorValorados(): Observable<Libro[]> {
    return this.http.get<Libro[]>(this.urlEndPoint+ '/mejor-valorados', { headers: this.createHeaders() });  
  }

  // Get libros (paginado)
  getLibros(page: number): Observable<any> {
    return this.http.get(this.urlEndPoint + '/page/' + page, { headers: this.createHeaders() }).pipe(

      /*
      tap((response: any) => {
        console.log('LibroService: tap 1 (Object)');
        (response.content as Libro[]).forEach(libro => {
          console.log(libro.titulo);
        })
      }), */

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

  // Get libros (paginado con tamaño personalizado)
  getLibrosConTamanio(page: number, size: number): Observable<any> {
    return this.http.get(`${this.urlEndPoint}/page/${page}/size/${size}` , { headers: this.createHeaders() }).pipe(
      map((response: any) => {
        // Retornamos
        (response.content as Libro[]).map(libro => {
          libro.titulo = libro.titulo;
          libro.precio = libro.precio;
          libro.stock = libro.stock;
          libro.descripcion = libro.descripcion;
          
          return libro;
        });
        return response;
      }),
    );
  }

  /* Sin paginacion */

  // Observable para que sea asíncrono (se actualice en tiempo real)
  getLibrosNoPagin(): Observable<Libro[]> {

    // return of(LIBROS);

    return this.http.get(this.urlEndPoint, { headers: this.createHeaders() }).pipe(

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
  }

  // Crear libro
  create(libro: Libro) : Observable<any> {
    return this.http.post<any>(this.urlEndPoint1, libro, { headers: this.createHeaders() }).pipe(
      catchError(e => {
        // Validamos
        if(e.status==400) {
          return throwError(() => e);
        }

        // Controlamos otros errores
        this.router.navigate(['/libros']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(() => e);
      })
    );
  }

  // Obtener
  getLibro(id: number): Observable<Libro> {
    // pipe para canalizar errores
    return this.http.get<Libro>(`${this.urlEndPoint1}/${id}`, { headers: this.createHeaders() }).pipe(
      catchError(e => {
        this.router.navigate(['/libros']);
        console.log(e.error.mensaje);
        swal('Error al obtener el libro', e.error.mensaje, 'error');
        return throwError(() => e);
      })
    );
  }

  // Update
  updateLibro(libro: Libro): Observable<any> {
    return this.http.put<any>(`${this.urlEndPoint1}/${libro.id}`, libro, { headers: this.createHeaders() }).pipe(
      catchError(e => {
        // Validamos
        if(e.status==400) {
          return throwError(() => e);
        }

        // Controlamos otros errores
        this.router.navigate(['/libros']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(() => e);
      })
    );
  }

  // Delete
  delete(id: number): Observable<Libro> {
    return this.http.delete<Libro>(`${this.urlEndPoint1}/${id}`, { headers: this.createHeaders() }).pipe(
      catchError(e => {
        this.router.navigate(['/libros']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(() => e);
      })
    );
  }

  // Delete todos
  deleteAll(): Observable<void> {
    return this.http.delete<void>(`${this.urlEndPoint}`, { headers: this.createHeaders() });
  }

  //Obtener autores para el filtro
  getAutores(): Observable<Autor[]> {
    return this.http.get<Autor[]>(this.urlAutores, { headers: this.createHeaders() });
  }

  // Obtener libros filtrados por autor
  getLibrosPorAutor(page: number, size: number, autorId: number): Observable<any> {
    return this.http.get(`${this.urlEndPoint}/autor/${autorId}/page/${page}/size/${size}`, { headers: this.createHeaders() })
      .pipe(
        map((response: any) => {
          (response.content as Libro[]).map(libro => {
            libro.titulo = libro.titulo;
            libro.precio = libro.precio;
            libro.stock = libro.stock;
            libro.descripcion = libro.descripcion;
            
            return libro;
          });
          return response;
        })
      );
  }


}