import { Component, OnInit } from '@angular/core';
import { AutorService } from './autor.service';
import { Autor } from './autor';

@Component({
  selector: 'app-autor',
  standalone: false,
  templateUrl: './autor.component.html',
  styleUrls: ['./autor.component.css']
})
export class AutorComponent implements OnInit{
  autores : Autor[]= [];
  constructor(private autorService: AutorService) {}

  ngOnInit(): void {
    this.autorService.getAutores().subscribe(
      (autores: Autor[]) => {
        this.autores = autores;
        console.log('Autores recibidos:', autores);  // Esto mostrará la lista de autores en la consola
      },
      error => {
        console.error('Error al obtener los autores', error);  // Muestra errores si los hay
      }
    )
  };
}
