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

  getValoraciones(): Observable<Valoracion[]> {
    return this.http.get<any[]>(this.urlEndPoint).pipe(
      map(response => {
        console.log('Respuesta del servidor:', response);
        return response.map(item => {
          const valoracion = new Valoracion();
          valoracion.id = item.id;
          valoracion.usuario = item.usuario;
          valoracion.libro = item.libro;
          valoracion.puntuacion = item.puntuacion;
          valoracion.comentario = item.comentario;
          valoracion.fecha = new Date(item.fecha);
          return valoracion;
        });
      })
    ); 
  }
}
