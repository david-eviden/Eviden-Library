import { Component, OnInit } from '@angular/core';
import { Genero } from './generos';
import { GeneroService } from './generos.service';

@Component({
  selector: 'app-generos',
  standalone: false,
  templateUrl: './generos.component.html',
  styleUrl: './generos.component.css'
})
export class GenerosComponent implements OnInit{
  generos : Genero[]= [];
  constructor(private generoService: GeneroService) {}

  ngOnInit(): void {
    this.generoService.getGeneros().subscribe(
      (generos: Genero[]) => {
        this.generos = generos;
        console.log('Generos recibidos:', generos);  // Esto mostrará la lista de autores en la consola
      },
      error => {
        console.error('Error al obtener los generos', error);  // Muestra errores si los hay
      }
    )
  };
}
