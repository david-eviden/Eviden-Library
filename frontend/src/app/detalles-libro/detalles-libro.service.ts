import { HttpClient } from '@angular/common/http';
import { Injectable, OnInit } from '@angular/core';
import { map, Observable } from 'rxjs';
import { DetallesLibro } from './detalles-libro';
import { Libro } from '../libro/libro';

@Injectable({
  providedIn: 'root'
})
export class DetallesLibroService  implements OnInit{

  private urlEndPoint: string = 'http://localhost:8080/api/libro'; 

  constructor(private http: HttpClient) {}
  ngOnInit(): void {}

  // Observable para que sea asíncrono (se actualice en tiempo real)
  getDetallesLibros(): Observable<DetallesLibro[]> {

    // return of(LIBROS);

    return this.http.get(this.urlEndPoint).pipe(

      // Conversión a libros (response de Object a Libro[])
      map(response => {
        let detallesLibro = response as DetallesLibro[];

        return detallesLibro.map(detalles => {
          detalles.libro.imagen = detalles.libro.imagen;
          detalles.libro.titulo = detalles.libro.titulo;
          detalles.libro.precio = detalles.libro.precio
          detalles.libro.autores = detalles.libro.autores;
          detalles.libro.stock = detalles.libro.stock;
          
          return detalles;
        });
      }),

    );
  }
  
  obtenerLibroPorId(id: number): Observable<Libro> {
    return this.http.get<Libro>(`${this.urlEndPoint}/${id}`);
  }
  
}
