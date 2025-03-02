import { Component, OnInit } from '@angular/core';
import { Libro } from '../libro/libro';
import { LibroService } from '../libro/libro.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-ranking',
  standalone: false,
  templateUrl: './ranking.component.html',
  styleUrl: './ranking.component.css'
})
export class RankingComponent implements OnInit {
  
  libros: Libro[] = [];  
  fragmentos: Libro[][] = [];
  
  constructor(private libroService: LibroService, private router: Router) {}

  ngOnInit(): void {
    this.getMejorValorados();
  }

  // Método para obtener los libros mejor valorados desde el backend
  getMejorValorados(): void {
    this.libroService.getMejorValorados().subscribe({
      next: (data: Libro[]) => {
        this.libros = data;
        this.fragmentos = []; // Reiniciar fragmentos antes de dividir
        this.slide(); // dividir los libros en fragmentos
        console.log('Libros cargados:', this.libros);
        console.log('Fragmentos creados:', this.fragmentos);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error al obtener los libros:', error);
      }
    });
  }

  // Método para dividir los libros en fragmentos para el carrusel
  slide(): void {
    const tamaño = 5;
    for (let i = 0; i < this.libros.length; i += tamaño) {
      this.fragmentos.push(this.libros.slice(i, i + tamaño));
    }
  }

  // Método para ver detalles de un libro
  verDetallesLibro(bookId: number): void {
    if (bookId !== undefined) {
      this.router.navigate(['/libro', bookId]);
    } else {
      console.error('ID de libro indefinido');
    }
  }
}
