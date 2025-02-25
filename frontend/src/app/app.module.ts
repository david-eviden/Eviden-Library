import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { PrincipalComponent } from './principal/principal.component';
import { HttpClientModule } from '@angular/common/http';
import { FooterComponent } from './footer/footer.component';
import { AutorComponent } from './autor/autor.component';
import { LibroComponent } from './libro/libro.component';
import { GenerosComponent } from './generos/generos.component';
import { UsuarioComponent } from './usuario/usuario.component';
import { RouterModule } from '@angular/router';
import { FavoritoComponent } from './favorito/favorito.component';
import { CarritoComponent } from './carrito/carrito.component';
import { DetallesCarritoComponent } from './detalles-carrito/detalles-carrito.component';
import { PedidoComponent } from './pedido/pedido.component';
import { DetallesPedidoComponent } from './detalles-pedido/detalles-pedido.component';
import { ValoracionComponent } from './valoracion/valoracion.component';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { KeycloakAuthService } from './login/keycloak.service';
import { LoginComponent } from './login/login.component';

export function kcFactory(kcService: KeycloakAuthService){
  return () => kcService.initKeycloak();
}
@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    PrincipalComponent,
    FooterComponent,
    AutorComponent,
    LibroComponent,
    GenerosComponent,
    UsuarioComponent,
    FavoritoComponent,
    CarritoComponent,
    DetallesCarritoComponent,
    PedidoComponent,
    DetallesPedidoComponent,
    ValoracionComponent,
    LoginComponent,
    //AdminComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    RouterModule,
    KeycloakAngularModule
],
  providers: [HttpClientModule,KeycloakAuthService, KeycloakService],
  bootstrap: [AppComponent]
})
export class AppModule { }
