import { Injectable } from '@angular/core';
import { User } from './user';
import Keycloak from 'keycloak-js';

@Injectable({
  providedIn: 'root'
})
export class KeycloakAuthService {

  private _keycloak: Keycloak.KeycloakInstance | undefined;
  private _profile: User | undefined;

  get keycloak() {
    if (!this._keycloak) {
      this._keycloak = new Keycloak({
        url: 'http://localhost:9090', // URL de tu servidor Keycloak
        realm: 'eviden', // Nombre del realm
        clientId: 'user1', // ID de tu cliente
      });
    }
    return this._keycloak;
  }

  get profile(): User | undefined {
    return this._keycloak;
  }

  constructor() {}

  // Método para inicializar Keycloak solo cuando se necesite
  async initKeycloak(): Promise<void> {
    const authenticated:boolean = await this.keycloak?.init({
      onLoad: 'check-sso' // No obligar a hacer login inmediatamente
    });
    if (authenticated){
      this._profile = (await this.keycloak?.loadUserProfile()) as User;
      this._profile.token = this.keycloak?.token;
    }
    
  }

  login() :Promise<void>{
    return this.keycloak?.login();
  }

  logout(): Promise<void> {
    return this.keycloak?.logout({redirectUri: 'http://localhost:4200'}) || Promise.resolve();
  }  

}
