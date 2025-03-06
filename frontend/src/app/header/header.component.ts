import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from '../login/auth.service';

@Component({
  selector: 'app-header',
  standalone: false,
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit{

  userId: number = 0;

  constructor( private authService: AuthService) {}

  ngOnInit(): void {
    // Subscribe al observable para mantenernos actualizados con cambios en la autenticación
    this.authService.usuarioActual.subscribe(user => {
      if (user) {
        this.userId = user.id;
      } else {
        this.userId = 0;
      }
    });
  }

  // Array de categorías (puedes añadir más según lo necesites)
  categorias = [
    { nombre: 'Ficción' },
    { nombre: 'No Ficción' },
    { nombre: 'Ciencia' },
    { nombre: 'Historia' },
    { nombre: 'Literatura Infantil' },
    { nombre: 'Biografías' },
  ];

  onSearch(searchTerm: string){
    console.log('Busqueda: ', searchTerm);
  }

  logout() {
    this.authService.logout();
  }

  getLogueado() {
    return this.authService.estaLogueado();
  }
}
