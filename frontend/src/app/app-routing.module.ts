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
import { DetallesLibroComponent } from './detalles-libro/detalles-libro.component';
import { AdminComponent } from './admin/admin.component';
import { FormLibroComponent } from './form-libro/form-libro.component';
import { FormAutorComponent } from './form-autor/form-autor.component';
import { FormGeneroComponent } from './form-genero/form-genero.component';
import { FormValoracionComponent } from './form-valoracion/form-valoracion.component';
import { ResultadoBusquedaComponent } from './resultado-busqueda/resultado-busqueda.component';
import { DetallesUsuarioComponent } from './detalles-usuario/detalles-usuario.component';
import { FormUsuarioComponent } from './form-usuario/form-usuario.component';
import { AuthGuard } from './login/auth.guard';
import { LoginComponent } from './login/login.component';

export const routes: Routes = [
  { path: '', redirectTo: '/principal', pathMatch: 'full' }, // Defecto
  { path: 'principal', component: PrincipalComponent }, // Todos
  { path: 'admin', component: AdminComponent }, // Admin
  { path: 'libros', component: LibroComponent }, // Todos
  { path: 'libros/page/:page', component: LibroComponent}, // Todos
  { path: 'libro/form', component: FormLibroComponent }, // Admin
  { path: 'libro/form/:id', component: FormLibroComponent }, // Admin
  { path: 'libro/:id/comprar', component: DetallesLibroComponent }, // Usuario
  { path: 'libro/:id', component: DetallesLibroComponent }, // Todos
  { path: 'autores', component: AutorComponent }, // Todos
  { path: 'autor/form', component: FormAutorComponent }, // Admin
  { path: 'autor/form/:id', component: FormAutorComponent }, // Admin
  { path: 'usuarios', component: UsuarioComponent }, // Admin
  { path: 'usuario/:id', component: DetallesUsuarioComponent }, // Usuario
  { path: 'usuario/form/:id', component: FormUsuarioComponent }, // Usuario
  { path: 'generos', component: GenerosComponent }, // Todos
  { path: 'genero/form', component: FormGeneroComponent }, // Admin
  { path: 'genero/form/:id', component: FormGeneroComponent }, // Admin
  { path: 'favoritos', component: FavoritoComponent }, // Admin
  { path: 'carrito', component: CarritoComponent }, // Admin
  { path: 'carrito/:id', component: DetallesCarritoComponent }, // Admin y Usuario
  { path: 'pedidos', component: PedidoComponent }, // Admin
  { path: 'pedido/:id', component: DetallesPedidoComponent }, // Admin y Usuario
  { path: 'valoraciones', component: ValoracionComponent }, // Todos
  { path: 'valoracion/form', component: FormValoracionComponent }, // Admin y Usuario
  { path: 'valoracion/form/:id', component: FormValoracionComponent }, // Admin y Usuario
  { path: 'search-results', component: ResultadoBusquedaComponent}, // Todos
  { path: 'login', component: LoginComponent }, // Todos
  { 
    path: 'admin', 
    component: AdminComponent,
    canActivate: [AuthGuard],
    data: { roles: ['admin'] } 
  },
  { 
    path: 'usuario', 
    component: UsuarioComponent,
    canActivate: [AuthGuard],
    data: { roles: ['user', 'admin'] } 
  },
  { path: '**', redirectTo: '/error' }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      scrollPositionRestoration: "enabled",
      anchorScrolling: "enabled",
      onSameUrlNavigation: "reload",
      enableTracing: false,
    }),
  ],
  exports: [RouterModule],
})
export class AppRoutingModule { }
