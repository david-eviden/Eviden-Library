import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class SearchService {
  private urlEndPoint: string = 'http://localhost:8081/api/search'; 

  constructor(private http: HttpClient) {}

  // Método para buscar libros por texto (busca en título, autor y género)
  buscarLibros(query: string): Observable<any[]> {
    const params = new HttpParams().set('query', query);
    return this.http.get<any[]>(`${this.urlEndPoint}`, { params }).pipe(
      map(response => {
        // Si la respuesta está anidada, ajusta según la estructura de tu API
        return response;
      })
    );
}
}