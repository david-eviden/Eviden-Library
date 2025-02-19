import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Genero } from './generos';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class GeneroService {
  private urlEndPoint: string = 'http://localhost:8081/api/generos'; 

  constructor(private http: HttpClient) {}

  getGeneros(): Observable<Genero[]> {
    return this.http.get<Genero[]>(this.urlEndPoint); 
  }
}