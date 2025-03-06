import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from "@angular/router";
import swal from "sweetalert2";
import { AuthService } from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService, 
    private router: Router
  ) {}
  
  canActivate(
    route: ActivatedRouteSnapshot, 
    state: RouterStateSnapshot
  ): boolean {
    // Rutas públicas sin autenticación
    const publicRoutes = [
      '/principal',
      '/libros',
      '/libros/page',
      '/login',
      '/search-results'
    ];
      
  // Verificar si la ruta actual es pública
  const currentPath = state.url;
  console.log('Guard evaluando ruta:', currentPath);
    
  const isPublicRoute = publicRoutes.some(route => 
    currentPath === route || 
    currentPath.startsWith(route)
  );
  
  console.log('¿Es ruta pública?', isPublicRoute);
  
  // Si es una ruta pública, siempre permitir el acceso
  if (isPublicRoute) {
    console.log('Permitiendo acceso a ruta pública');
    return true;
  }
  
  // Verificar si está logueado
  if (!this.authService.estaLogueado()) {
    console.log('No autenticado, redirigiendo a login');
    swal(  
      'Acceso denegado',  
      'Debe iniciar sesión para acceder a esta página',  
      'warning'  
    );
    this.router.navigate(['/login']);
    return false;
  }

    // Verificar roles si se requieren
    const roles = route.data['roles'] as string[];
    if (roles) {
      const usuarioRol = this.authService.usuarioLogueado?.rol;
      const tieneRol = roles.some(rol => rol === usuarioRol);
      if (!tieneRol) {
        swal( 
          'Acceso restringido',  
          'No tiene permisos para acceder a esta página',  
          'error'  
        );
        return false;
      }
    }
    return true;
  }
}