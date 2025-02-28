import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: false,
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
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
}
