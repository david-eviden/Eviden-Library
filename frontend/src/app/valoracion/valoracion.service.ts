import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Valoracion } from './valoracion';

@Injectable({
  providedIn: 'root'
})
export class ValoracionService {

  private urlEndPoint: string = 'http://localhost:8080/api/valoraciones'; 

  constructor(private http: HttpClient) {}

  getUsuarios(): Observable<Valoracion[]> {
    return this.http.get(this.urlEndPoint).pipe(

      // Conversión a valoracions (response de Object a Valoracion[])
      map(response => {

        let valoraciones = response as Valoracion[];

        return valoraciones.map(valoracion => {
          valoracion.usuario = valoracion.usuario;
          valoracion.libro = valoracion.libro;
          valoracion.puntuacion = valoracion.puntuacion;
          valoracion.comentario = valoracion.comentario;
          valoracion.fecha = valoracion.fecha;
         
          return valoracion;
        });
      }),
    ); 
  }
}
