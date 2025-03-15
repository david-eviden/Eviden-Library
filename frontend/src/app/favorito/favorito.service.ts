import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError, of } from 'rxjs';
import { Favorito } from './favorito';
import { Router } from '@angular/router';
import { Usuario } from '../usuario/usuario';
import { Libro } from '../libro/libro';
import { AuthService } from '../login/auth.service';

@Injectable({
  providedIn: 'root'
})
export class FavoritoService {

  private urlEndPoint: string = 'http://localhost:8080/api/favoritos'; 
  private urlFavoritoEndPoint: string = 'http://localhost:8080/api/favorito';
  
  constructor(
    private http: HttpClient, 
    private router: Router,
    private authService: AuthService
  ) {}

  // Método para obtener el token del localStorage
  private getToken(): string | null {
    // Usar el método del servicio de autenticación
    return this.authService.getToken();
  }

  // Método para crear cabeceras con el token
  private createHeaders(): HttpHeaders {
    const token = this.getToken();
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
  
    if (token) {
      // Enviar el token tal como está, sin manipularlo
      headers = headers.append('Authorization', `Bearer ${token}`);
    } else {
      console.log('No se encontró token en localStorage');
    }
  
    return headers;
  }
  
  getUsuarios(): Observable<Favorito[]> {
    return this.http.get(this.urlEndPoint, { headers: this.createHeaders() }).pipe(

      // Conversión a favoritos (response de Object a Favorito[])
      map(response => {

        let favoritos = response as Favorito[];

        return favoritos.map(favorito => {
          favorito.usuario = favorito.usuario;
          favorito.libro = favorito.libro;
          favorito.fechaAgregado = favorito.fechaAgregado;
         
          return favorito;
        });
      }),
    ); 
  }

  // Obtener favoritos por ID de usuario
  getFavoritosByUsuarioId(usuarioId: number): Observable<Favorito[]> {
    if (!usuarioId) {
      console.error('Intento de obtener favoritos sin ID de usuario');
      return of([]); // Devolver array vacío
    }
    
    const headers = this.createHeaders();
    
    return this.http.get<any>(`${this.urlFavoritoEndPoint}/usuario/${usuarioId}`, { 
      headers: headers 
    }).pipe(
      map(response => {
        if (Array.isArray(response)) {
          return response.map(item => {
            const favorito = new Favorito();
            favorito.id = item.id;
            favorito.usuario = item.usuario;
            favorito.libro = item.libro;
            favorito.fechaAgregado = item.fechaAgregado;
            return favorito;
          });
        } else if (response && response.mensaje && response.mensaje.includes('no tiene favoritos')) {
          console.log('Usuario sin favoritos');
          return [];
        } else {
          console.warn('La respuesta no es un array:', response);
          return [];
        }
      }),
      catchError(error => {
        console.error(`Error al obtener favoritos del usuario ${usuarioId}:`, error);
        if (error.status === 404) {
          console.log('Usuario sin favoritos (404)');
          return of([]);
        }
        return throwError(() => error);
      })
    );
  }

  // Agregar un libro a favoritos
  addFavorito(libro: Libro): Observable<any> {
    // Verificar si el token es válido
    if (!this.authService.verificarToken()) {
      console.error('Token inválido o expirado');
      return throwError('Token inválido o expirado');
    }
    
    // Obtener el usuario del localStorage
    const usuarioString = localStorage.getItem('usuario');
    if (!usuarioString) {
      console.error('No se encontró información del usuario en localStorage');
      return throwError('Usuario no autenticado');
    }

    try {
      const usuarioData = JSON.parse(usuarioString);
      const usuarioId = usuarioData.id;
      
      if (!usuarioId) {
        console.error('ID de usuario no encontrado en localStorage');
        return throwError('ID de usuario no disponible');
      }

      // Crear objeto favorito en el formato exacto que espera el backend
      const favoritoCompleto = new Favorito();
      favoritoCompleto.usuario = new Usuario();
      favoritoCompleto.usuario.id = usuarioId;
      favoritoCompleto.libro = libro;
      favoritoCompleto.fechaAgregado = new Date().toISOString();
      
      // Enviar la solicitud con las cabeceras correctas
      const headers = this.createHeaders();
      
      return this.http.post(`${this.urlFavoritoEndPoint}`, favoritoCompleto, { 
        headers: headers,
        observe: 'response'  // Para obtener la respuesta completa, incluyendo cabeceras y estado
      }).pipe(
        map(response => {
          return response.body;
        }),
        catchError(error => {
          console.error('Error al agregar favorito:', error);
          console.error('Detalles del error:', {
            status: error.status,
            statusText: error.statusText,
            message: error.message,
            error: error.error
          });
          return throwError(error);
        })
      );
    } catch (error) {
      console.error('Error al procesar la información del usuario:', error);
      return throwError('Error al procesar la información del usuario');
    }
  }

  // Verificar si un libro está en favoritos
  checkFavorito(libroId: number): Observable<boolean> {
    // Obtener el usuario del localStorage
    const usuarioString = localStorage.getItem('usuario');
    if (!usuarioString) {
      console.error('No se encontró información del usuario en localStorage');
      return of(false); // Asumir que no es favorito
    }

    try {
      const usuarioData = JSON.parse(usuarioString);
      const usuarioId = usuarioData.id;
      
      if (!usuarioId) {
        console.error('ID de usuario no encontrado en localStorage');
        return of(false); // Asumir que no es favorito
      }

      const headers = this.createHeaders();
      
      // Intentar con el endpoint específico
      return this.http.get<boolean>(`${this.urlEndPoint}/check/${usuarioId}/${libroId}`, { 
        headers: headers 
      }).pipe(
        map(response => {
          return response;
        }),
        catchError(error => {
          console.error('Error al verificar favorito:', error);
          console.error('Detalles del error:', {
            status: error.status,
            statusText: error.statusText,
            message: error.message,
            error: error.error
          });
          
          // Si el endpoint no existe o hay un error de permisos, obtener todos los favoritos y verificar manualmente
          console.log('Endpoint de verificación no disponible, obteniendo todos los favoritos');
          
          // Alternativa: obtener todos los favoritos del usuario y verificar manualmente
          return this.getFavoritosByUsuarioId(usuarioId).pipe(
            map(favoritos => {
              const esFavorito = favoritos.some(f => f.libro?.id === libroId);
              console.log(`El libro ${libroId} ${esFavorito ? 'es' : 'no es'} favorito del usuario ${usuarioId}`);
              return esFavorito;
            }),
            catchError(err => {
              console.error('Error en la verificación alternativa:', err);
              return of(false); // Asumir que no es favorito en caso de error
            })
          );
        })
      );
    } catch (error) {
      console.error('Error al procesar la información del usuario:', error);
      return of(false); // Asumir que no es favorito
    }
  }

  // Eliminar un favorito
  delete(favorito: Favorito): Observable<any> {
    if (!favorito || !favorito.id) {
      console.error('Intento de eliminar un favorito sin ID');
      return throwError('No se puede eliminar un favorito sin ID');
    }
    
    console.log(`Eliminando favorito con ID: ${favorito.id}`);
    
    const headers = this.createHeaders();
    
    // Intentar primero con el endpoint estándar
    return this.http.delete(`${this.urlFavoritoEndPoint}/${favorito.id}`, { 
      headers: headers,
      observe: 'response'
    }).pipe(
      map(response => {
        return response.body;
      }),
      catchError(error => {
        console.error(`Error al eliminar favorito con ID ${favorito.id}:`, error);
        console.error('Detalles del error:', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          error: error.error
        });
        
        // Si falla con el endpoint estándar, intentar con un endpoint alternativo
        if (error.status === 403 || error.status === 404) {
          // Intentar con un endpoint alternativo si el usuario y libro están disponibles
          if (favorito.usuario?.id && favorito.libro?.id) {
            return this.http.delete(`${this.urlFavoritoEndPoint}/usuario/${favorito.usuario.id}/libro/${favorito.libro.id}`, {
              headers: headers,
              observe: 'response'
            }).pipe(
              map(response => {
                return response.body;
              }),
              catchError(secondError => {
                console.error('Error al eliminar favorito con endpoint alternativo:', secondError);
                return throwError(secondError);
              })
            );
          }
        }
        
        return throwError(error);
      })
    );
  }

  // Eliminar un favorito por ID de libro y usuario
  deleteFavoritoByLibroAndUsuario(libroId: number, usuarioId: number): Observable<any> {
    // Imprimir la URL completa y las cabeceras para depuración
    const headers = this.createHeaders();
    const url = `${this.urlFavoritoEndPoint}/usuario/${usuarioId}/libro/${libroId}`;
    
    return this.http.delete(url, { 
      headers: headers,
      observe: 'response'
    }).pipe(
      map(response => {
        return response.body;
      }),
      catchError(error => {
        console.error('Error al eliminar favorito:', error);
        console.error('Detalles del error:', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          error: error.error
        });
        return throwError(() => error);
      })
    );
  }
}
