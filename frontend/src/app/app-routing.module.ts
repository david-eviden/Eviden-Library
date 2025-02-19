import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LibroComponent } from './libro/libro.component';
import { AutorComponent } from './autor/autor.component';
import { UsuarioComponent } from './usuario/usuario.component';
import { GenerosComponent } from './generos/generos.component';

const routes: Routes = [
  //{ path: '', redirectTo: '/principal', pathMatch: 'full' },  // Redirige 
  { path: 'libros', component: LibroComponent },
  { path: 'autores', component: AutorComponent },
  { path: 'usuarios', component: UsuarioComponent },
  { path: 'generos', component: GenerosComponent },
  //{ path: 'libros/:id', component: LibroComponent } ->librodetalles
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
