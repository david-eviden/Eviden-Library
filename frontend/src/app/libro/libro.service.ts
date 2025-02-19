import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Libro } from './libro';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class LibroService {
  private urlEndPoint: string = 'http://localhost:8081/api/libros'; 

  constructor(private http: HttpClient) {}

  getLibros(): Observable<Libro[]> {
    return this.http.get<Libro[]>(this.urlEndPoint); 
  }
}