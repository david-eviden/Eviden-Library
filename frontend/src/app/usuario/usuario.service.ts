import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Usuario } from './usuario';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class UsuarioService {
  private urlEndPoint: string = 'http://localhost:8081/api/usuarios'; 

  constructor(private http: HttpClient) {}

  getUsuarios(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.urlEndPoint); 
  }
}