import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Autor } from './autor';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class AutorService {
  private urlEndPoint: string = 'http://localhost:8081/api/autores'; 

  constructor(private http: HttpClient) {}

  getAutores(): Observable<Autor[]> {

    return this.http.get(this.urlEndPoint).pipe(

      // Conversión a autores (response de Object a Autor[])
      map(response => {

        let autores = response as Autor[];

        return autores.map(autor => {
          autor.nombre = autor.nombre?.toUpperCase();
          autor.apellido = autor.apellido;
          autor.biografia = autor.biografia;
         
          return autor;
        });
      }),
    );
  }
}
