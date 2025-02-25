import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Libro } from './libro';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class LibroService {
  private urlEndPoint: string = 'http://localhost:8080/api/libros'; 

  constructor(private http: HttpClient) {}

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
  }

}