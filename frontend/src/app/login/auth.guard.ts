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
      '/libro',
      '/login',
      '/resgistro',
      '/search-results',
      '/generos',
      '/autores',
      '/valoraciones'  // Solo la lista de valoraciones es pública
    ];

    // Mapa de rutas protegidas con sus roles permitidos
    const protectedRoutes: { [key: string]: string[] } = {
      '/valoracion/form': ['USER', 'ADMIN'],
      '/valoraciones/editar': ['USER', 'ADMIN'],
      '/valoracion': ['USER', 'ADMIN']  // Para cubrir cualquier subruta de valoración
    };
     
    // Verificar si la ruta actual es pública
    const currentPath = state.url;
    console.log('Guard evaluando ruta:', currentPath);
   
    const isPublicRoute = publicRoutes.some(route =>
      currentPath === route ||
      currentPath.startsWith(route)
    );
   
    console.log('¿Es ruta pública?', isPublicRoute);
   
    // Si es una ruta pública, permitir acceso
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

    // Verificar roles para rutas protegidas
    const usuarioRol = this.authService.usuarioLogueado?.rol;
    console.log('Rol del usuario:', usuarioRol);

    // Buscar si la ruta actual coincide con alguna ruta protegida
    const matchingProtectedRoute = Object.entries(protectedRoutes).find(([route]) =>
      currentPath.startsWith(route)
    );

    if (matchingProtectedRoute) {
      const [route, allowedRoles] = matchingProtectedRoute;
      console.log('Ruta protegida encontrada:', route);
      console.log('Roles permitidos:', allowedRoles);

      if (!usuarioRol || !allowedRoles.includes(usuarioRol)) {
        console.log('Usuario no tiene el rol requerido');
        swal(
          'Acceso restringido',  
          'No tiene permisos para acceder a esta página',  
          'error'  
        );
        return false;
      }
      return true;
    }

    // Para otras rutas, verificar roles si se especifican en la ruta
    const roles = route.data['roles'] as string[];
    if (roles) {
      const tieneRol = roles.some(rol => rol === usuarioRol);
      if (!tieneRol) {
        console.log('Usuario no tiene el rol requerido');
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