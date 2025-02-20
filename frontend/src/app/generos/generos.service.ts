import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Genero } from './generos';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class GeneroService {
  private urlEndPoint: string = 'http://localhost:8081/api/generos'; 

  constructor(private http: HttpClient) {}

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
}