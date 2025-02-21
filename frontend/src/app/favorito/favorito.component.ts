import { Component } from '@angular/core';
import { Favorito } from './favorito';
import { FavoritoService } from './favorito.service';

@Component({
  selector: 'app-favorito',
  standalone: false,
  templateUrl: './favorito.component.html',
  styleUrl: './favorito.component.css'
})
export class FavoritoComponent {
  favoritos: Favorito[]= [];
  constructor(private favoritoService: FavoritoService) {}

  ngOnInit(): void {
    this.favoritoService.getUsuarios().subscribe(
      (favoritos: Favorito[]) => {
        this.favoritos = favoritos;
        console.log('Favoritos recibidos:', favoritos);  // Lista de usuarios en la consola
      },
      error => {
        console.error('Error al obtener los usuarios', error);  // Muestra errores si los hay
      }
    )
  };
}
