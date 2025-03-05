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
            '/libros/page/', 
            '/libro/', 
            '/login'
        ];

        // Verificar si la ruta actual es pública
        const currentPath = state.url;
        const isPublicRoute = publicRoutes.some(route => 
            currentPath.startsWith(route)
        );

        if (isPublicRoute) {
            return true;
        }

        // Verificar si está logueado
        if (!this.authService.estaLogueado()) {
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
            this.router.navigate(['/principal']);
            return false;
          }
        }

        return true;
    }
}