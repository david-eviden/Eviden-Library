import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Favorito } from './favorito';

@Injectable({
  providedIn: 'root'
})
export class FavoritoService {

  private urlEndPoint: string = 'http://localhost:8080/api/favoritos'; 
  
    constructor(private http: HttpClient) {}
  
    getUsuarios(): Observable<Favorito[]> {
      return this.http.get(this.urlEndPoint).pipe(
  
        // Conversión a favoritos (response de Object a Favorito[])
        map(response => {
  
          let favoritos = response as Favorito[];
  
          return favoritos.map(favorito => {
            favorito.usuario = favorito.usuario;
            favorito.libro = favorito.libro;
            favorito.fecha = favorito.fecha;
           
            return favorito;
          });
        }),
      ); 
    }
}
