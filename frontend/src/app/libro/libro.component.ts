import { Component, OnInit } from '@angular/core';
import { LibroService } from './libro.service';
import { Libro } from './libro';

@Component({
  selector: 'app-libro',
  standalone: false,
  templateUrl: './libro.component.html',
  styleUrls: ['./libro.component.css']
})
export class LibroComponent implements OnInit{
  libros : Libro[]= [];
  constructor(private libroService: LibroService) {}

  ngOnInit(): void {
    this.libroService.getLibros().subscribe(
      (libros: Libro[]) => {
        this.libros = libros;
        console.log('Libros recibidos:', libros);
      },
      error => {
        console.error('Error al obtener los libros', error);
      }
    )
  };

}
