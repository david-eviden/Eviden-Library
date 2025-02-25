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
    const imagenLibro = document.getElementById(`imagen-libro-${id}`);

    // Comprobar si el navegador soporta la API de View Transitions
    if (document.startViewTransition && imagenLibro) {
      // Iniciar la transición de la imagen
      document.startViewTransition(() => {
        // Aquí, la imagen es la que se moverá de una página a otra
        this.router.navigate(['/libro', id]);
      });
    } else {
      // Si el navegador no soporta View Transitions, se hace la navegación normalmente
      this.router.navigate(['/libro', id]);
    }
    }
  }