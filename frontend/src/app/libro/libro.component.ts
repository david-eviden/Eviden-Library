import { Component, OnInit } from '@angular/core';
import { LibroService } from './libro.service';
import { Libro } from './libro';
import { Router } from '@angular/router';

@Component({
  selector: 'app-libro',
  standalone: false,
  templateUrl: './libro.component.html',
  styleUrls: ['./libro.component.css']
})
export class LibroComponent implements OnInit{

  libros : Libro[]= [];
  constructor(private libroService: LibroService, private router: Router) {}

  ngOnInit(): void {
    this.libroService.getLibros().subscribe(
      (libros: Libro[]) => {
        this.libros = libros;
        console.log('Detalles del libro recibidos:', libros);
      },
      error => {
        console.error('Error al obtener los detalles del libro', error);
      }
    )
  };

  // Método para navegar a detalles con View Transitions
  getDetallesLibro(id: number): void {
    if ((document as any).startViewTransition) {
      (document as any).startViewTransition(() => {
        this.router.navigate(['/libro', id]);
      });
    } else {
      // Fallback para navegadores que no soportan View Transitions
      this.router.navigate(['/libro', id]);
    }
  }
}