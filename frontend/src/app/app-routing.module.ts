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

const routes: Routes = [
  { path: '', redirectTo: '/principal', pathMatch: 'full' }, // Defecto
  { path: 'principal', component: PrincipalComponent },
  { path: 'admin', component: AdminComponent },
  { path: 'libros', component: LibroComponent },
  { path: 'libros/page/:page', component: LibroComponent},
  { path: 'libro/:id', component: DetallesLibroComponent },
  { path: 'libro/:id/comprar', component: DetallesLibroComponent },
  { path: 'libro/form', component: FormLibroComponent },
  { path: 'libro/form/:id', component: FormLibroComponent },
  { path: 'autores', component: AutorComponent },
  { path: 'autor/form', component: FormAutorComponent },
  { path: 'autor/form/:id', component: FormAutorComponent },
  { path: 'usuarios', component: UsuarioComponent },
  //{ path: 'usuario/:id', component: DetallesUsuarioComponent },
  { path: 'generos', component: GenerosComponent },
  { path: 'favoritos', component: FavoritoComponent },
  { path: 'carrito', component: CarritoComponent },
  { path: 'carrito/:id', component: DetallesCarritoComponent },
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
