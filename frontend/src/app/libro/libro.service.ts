import { Injectable } from '@angular/core';
import { map, Observable, tap } from 'rxjs';
import { Libro } from './libro';
import { HttpClient } from '@angular/common/http';
import { DatePipe } from '@angular/common';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class LibroService {
  private urlEndPoint: string = 'http://localhost:8081/api/libros'; 

  constructor(private http: HttpClient) {}

  //Get mejor valorados
  getMejorValorados(): Observable<Libro[]> {
    return this.http.get<Libro[]>(this.urlEndPoint+ '/mejor-valorados');  
  }


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

}