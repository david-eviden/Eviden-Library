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
    //AdminComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    RouterModule
],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
