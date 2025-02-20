import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Autor } from './autor';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class AutorService {
  private urlEndPoint: string = 'http://localhost:8080/api/autores'; 

  constructor(private http: HttpClient) {}

  getAutores(): Observable<Autor[]> {
    return this.http.get<Autor[]>(this.urlEndPoint); 
  }
}
