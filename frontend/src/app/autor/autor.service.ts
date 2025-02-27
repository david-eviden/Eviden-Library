import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import { Autor } from './autor';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import swal from 'sweetalert2';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class AutorService {
  private urlEndPoint: string = 'http://localhost:8080/api/autores';
    private urlEndPoint1: string = 'http://localhost:8080/api/autor'; 
    private httpHeaders = new HttpHeaders({'Content-Type': 'application/json'});

  constructor(private http: HttpClient, private router: Router) {}

  getAutores(): Observable<Autor[]> {

    return this.http.get(this.urlEndPoint).pipe(

      // Conversión a autores (response de Object a Autor[])
      map(response => {

        let autores = response as Autor[];

        return autores.map(autor => {
          autor.nombre = autor.nombre;
          autor.biografia = autor.biografia;
         
          return autor;
        });
      }),
    );
  }

  // Crear autor
  create(autor: Autor) : Observable<any> {
    return this.http.post<any>(this.urlEndPoint1, autor, {headers: this.httpHeaders}).pipe(
      catchError(e => {

        // Validamos
        if(e.status==400) {
          return throwError(e);
        }

        // Controlamos otros errores
        this.router.navigate(['/autores']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  // Obtener
  getAutor(id: number): Observable<Autor> {
    // pipe para canalizar errores
    return this.http.get<Autor>(`${this.urlEndPoint1}/${id}`).pipe(
      catchError(e => {
        this.router.navigate(['/autores']);
        console.log(e.error.mensaje);
        swal('Error al obtener el autor', e.error.mensaje, 'error');
        return throwError(e);
      })
    );
  }

  // Update
  updateAutor(autor: Autor): Observable<any> {
    return this.http.put<any>(`${this.urlEndPoint1}/${autor.id}`, autor, {headers: this.httpHeaders}).pipe(
      catchError(e => {

        // Validamos
        if(e.status==400) {
          return throwError(e);
        }

        // Controlamos otros errores
        this.router.navigate(['/autores']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  // Delete
  delete(id: number): Observable<Autor> {
    return this.http.delete<Autor>(`${this.urlEndPoint}/${id}`, {headers: this.httpHeaders}).pipe(
      catchError(e => {
        this.router.navigate(['/autores']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );;
  }

  // Delete todos
  deleteAll(): Observable<void> {
    return this.http.delete<void>(`${this.urlEndPoint}/deleteAll`);
  }
}
