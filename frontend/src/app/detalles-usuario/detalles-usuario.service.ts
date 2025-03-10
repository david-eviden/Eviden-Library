import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, OnInit } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import { DetallesUsuario } from './detalles-usuario';
import { Usuario } from '../usuario/usuario';
import { Router } from '@angular/router';
import swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class DetallesUsuarioService  implements OnInit{

  private urlEndPoint: string = 'http://localhost:8080/api/usuarios'; 
  private urlEndPoint1: string = 'http://localhost:8080/api/usuario'; 
  private httpHeaders = new HttpHeaders({'Content-Type': 'application/json'});

  constructor(private http: HttpClient, private router: Router) {}
  ngOnInit(): void {}

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

  // Observable para que sea asíncrono (se actualice en tiempo real)
  getDetallesUsuarios(): Observable<DetallesUsuario[]> {
    // return of(LIBROS);

    return this.http.get(this.urlEndPoint, { headers: this.createHeaders() }).pipe(
      // Conversión a usuarios (response de Object a Usuario[])
      map((response) => {
        const detallesUsuario = response as DetallesUsuario[]

        return detallesUsuario.map((detalles) => {
          detalles.usuario.nombre = detalles.usuario.nombre
          detalles.usuario.apellido = detalles.usuario.apellido
          detalles.usuario.direccion = detalles.usuario.direccion
          detalles.usuario.foto = detalles.usuario.foto
          detalles.usuario.email = detalles.usuario.email
          detalles.usuario.password = detalles.usuario.password
          detalles.usuario.rol = detalles.usuario.rol
          detalles.usuario.pedidos = detalles.usuario.pedidos
          detalles.usuario.valoraciones = detalles.usuario.valoraciones


          return detalles
        })
      }),
    )
  }
  
  obtenerUsuarioPorId(id: number): Observable<Usuario> {
    console.log('Solicitando usuario con ID:', id);
    
    // Verificar que el ID sea válido
    if (!id || isNaN(Number(id))) {
      console.error('ID de usuario no válido:', id);
      return throwError(() => new Error('ID de usuario no válido'));
    }
    
    return this.http.get<Usuario>(`${this.urlEndPoint1}/${id}`, { headers: this.createHeaders() }).pipe(
      map(usuario => {
        console.log('Usuario obtenido del servidor:', usuario);
        // Asegurarse de que las colecciones estén inicializadas
        if (!usuario.pedidos) usuario.pedidos = [];
        if (!usuario.valoraciones) usuario.valoraciones = [];
        return usuario;
      }),
      catchError(e => {
        console.error('Error al obtener el usuario:', e);
        return throwError(() => e);
      })
    );
  }

  obtenerUsuarioPorEmail(email: string): Observable<Usuario> {
    console.log('Solicitando usuario con email:', email);
    
    // Verificar que el email sea válido
    if (!email) {
      console.error('Email de usuario no válido');
      return throwError(() => new Error('Email de usuario no válido'));
    }
    
    return this.http.get<Usuario>(`${this.urlEndPoint1}/email/${email}`, { headers: this.createHeaders() }).pipe(
      map(usuario => {
        console.log('Usuario obtenido por email:', usuario);
        // Asegurarse de que las colecciones estén inicializadas
        if (!usuario.pedidos) usuario.pedidos = [];
        if (!usuario.valoraciones) usuario.valoraciones = [];
        return usuario;
      }),
      catchError(e => {
        console.error('Error al obtener el usuario por email:', e);
        return throwError(() => e);
      })
    );
  }

  // Crear usuario
  create(usuario: Usuario) : Observable<any> {
    return this.http.post<any>(this.urlEndPoint, usuario, {headers: this.createHeaders()}).pipe(
      catchError(e => {

        // Validamos
        if(e.status==400) {
          return throwError(e);
        }

        // Controlamos otros errores
        this.router.navigate(['/usuarios']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  // Obtener
  getUsuario(id: number): Observable<Usuario> {
    // pipe para canalizar errores
    return this.http.get<Usuario>(`${this.urlEndPoint1}/${id}`, { headers: this.createHeaders() }).pipe(
      catchError(e => {
        this.router.navigate(['/usuarios']);
        console.log(e.error.mensaje);
        swal('Error al obtener el usuario', e.error.mensaje, 'error');
        return throwError(e);
      })
    );
  }

  // Update
  updateUsuario(usuario: Usuario): Observable<any> {
    return this.http.put<any>(`${this.urlEndPoint1}/${usuario.id}`, usuario, {headers: this.createHeaders()}).pipe(
      catchError(e => {

        // Validamos
        if(e.status==400) {
          return throwError(e);
        }

        // Controlamos otros errores
        this.router.navigate(['/usuarios']);
        console.log(e.error.mensaje);
        swal(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  // Delete
  delete(id: number): Observable<Usuario> {
    return this.http.delete<Usuario>(`${this.urlEndPoint1}/${id}`, { headers: this.createHeaders() }).pipe(
      catchError(e => {
        console.error(e.error.mensaje);
        return throwError(e);
      })
    );
  }
}