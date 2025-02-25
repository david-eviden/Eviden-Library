import { ActivatedRouteSnapshot, CanActivate, GuardResult, MaybeAsync, Router, RouterStateSnapshot } from "@angular/router";
import { Injectable } from "@angular/core";
import { KeycloakAuthService } from "./keycloak.service";

@Injectable()
export class LoginGuardian implements CanActivate{

    constructor(private keycloakAuthService: KeycloakAuthService, private router: Router){}

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot){
        if(this.keycloakAuthService.keycloak?.isTokenExpired()){
            this.router.navigate(['login']);//redirige a login si no está logueado
            return false;            
        }else{
            return true;
        }
    }

}