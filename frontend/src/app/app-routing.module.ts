import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LibroComponent } from './libro/libro.component';
import { AutorComponent } from './autor/autor.component';
import { UsuarioComponent } from './usuario/usuario.component';
import { GenerosComponent } from './generos/generos.component';
import { PrincipalComponent } from './principal/principal.component';
import { CarritoComponent } from './carrito/carrito.component';
import { DetallesCarritoComponent } from './detalles-carrito/detalles-carrito.component';
import { DetallesPedidoComponent } from './detalles-pedido/detalles-pedido.component';
import { FavoritoComponent } from './favorito/favorito.component';
import { PedidoComponent } from './pedido/pedido.component';
import { ValoracionComponent } from './valoracion/valoracion.component';
import { LoginComponent } from './login/login.component';
import { LoginGuardian } from './login/login-guardian';

const routes: Routes = [
  { path: '', redirectTo: '/principal', pathMatch: 'full' }, // Defecto
  { path: 'principal', component: PrincipalComponent },
  //{ path: 'admin', component: AdminComponent },
  { path: 'login', component: LoginComponent },
  { path: 'libros', component: LibroComponent },
  { path: 'libros/:id', component: LibroComponent },
  { path: 'autores', component: AutorComponent },
  { path: 'usuarios', component: UsuarioComponent },
  { path: 'generos', component: GenerosComponent },
  { path: 'favoritos', component: FavoritoComponent },
  { path: 'carrito', component: CarritoComponent},
  { path: 'carrito/:id', component: DetallesCarritoComponent, canActivate:[LoginGuardian]},
  { path: 'pedidos', component: PedidoComponent },
  { path: 'pedido/:id', component: DetallesPedidoComponent },
  { path: 'valoraciones', component: ValoracionComponent },
  { path: '**', redirectTo: '/error' } // Ruta de error
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
