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