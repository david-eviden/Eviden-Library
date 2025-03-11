import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Libro } from '../libro/libro';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class SearchService {
  private urlEndPoint: string = 'http://localhost:8080/api/search'; 

  constructor(private http: HttpClient, private router: Router) {}

  // Método para obtener el token del localStorage
  private getToken(): string | null {
    const token = localStorage.getItem('access_token');
    if (!token) {
      this.router.navigate(['/login']);  // Redirigir al login si el token no está presente
    }
    return token;
  }

  // Método para crear cabeceras con el token
  private createHeaders(): HttpHeaders {
    const token = this.getToken();
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
  
    if (token) {
      headers = headers.append('Authorization', `Bearer ${token}`);
      console.log('Token añadido en cabecera:', token);  // Log para ver si el token es correcto
    } else {
      console.log('No se encontró token en localStorage');
    }
  
    return headers;
  }

  search(query: string, searchLibros: boolean = true, searchAutores: boolean = true, searchGeneros: boolean = true): Observable<any> {
    // Limpiar la query antes de enviarla
    const cleanQuery = query.replace(/\s+/g, ' ').trim();
    
    let params = new HttpParams()
      .set('query', cleanQuery) // Ya no necesitamos encodeURIComponent aquí
      .set('searchLibros', searchLibros.toString())
      .set('searchAutores', searchAutores.toString())
      .set('searchGeneros', searchGeneros.toString());

    return this.http.get(this.urlEndPoint, { params, headers: this.createHeaders() });
  }

  getLibrosByAutor(autorId: number): Observable<Libro[]> {
    return this.http.get<Libro[]>(`${this.urlEndPoint}/libros/autor/${autorId}`, { headers: this.createHeaders() });
  }

  getLibrosByGenero(generoId: number): Observable<Libro[]> {
    return this.http.get<Libro[]>(`${this.urlEndPoint}/libros/genero/${generoId}`, { headers: this.createHeaders() });
  }
}