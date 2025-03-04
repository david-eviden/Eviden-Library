import { Injectable } from '@angular/core';
import { 
  CanActivate, 
  ActivatedRouteSnapshot, 
  RouterStateSnapshot, 
  Router 
} from '@angular/router';
import { AuthService } from './auth.service';
import swal from 'sweetalert2';

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
        // Verificar si está logueado
        if (!this.authService.estaLogueado()) {
          swal(  // Usamos 'swal' en lugar de 'swal.fire'
            'Acceso denegado',  // Título
            'Debe iniciar sesión para acceder a esta página',  // Mensaje
            'warning'  // Tipo de alerta
          );
          this.router.navigate(['/login']);
          return false;
        }

        // Verificar roles si se requieren
        const roles = route.data['roles'] as string[];
        if (roles) {
          const tieneRol = roles.some(rol => 
            this.authService.usuarioLogueado.rol === rol
          );

          if (!tieneRol) {
            swal(  // Usamos 'swal' en lugar de 'swal.fire'
              'Acceso restringido',  // Título
              'No tiene permisos para acceder a esta página',  // Mensaje
              'error'  // Tipo de alerta
            );
            this.router.navigate(['/principal']);
            return false;
          }
        }

    return true;
    }
}