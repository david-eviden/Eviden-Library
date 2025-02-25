import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Libro } from '../libro/libro';
import { DetallesLibroService } from './detalles-libro.service';
import { Valoracion } from '../valoracion/valoracion';
import { ValoracionService } from '../valoracion/valoracion.service';

@Component({
  selector: 'app-detalles-libro',
  standalone: false,
  templateUrl: './detalles-libro.component.html',
  styleUrl: './detalles-libro.component.css',
})
export class DetallesLibroComponent implements OnInit {
  libro: Libro = new Libro();
  valoraciones: Valoracion[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private libroService: DetallesLibroService,
    private valoracionService: ValoracionService
  ) {}

  ngOnInit(): void {
    // Obtener el id del libro desde la ruta
    const libroId = +this.route.snapshot.paramMap.get('id')!;

    // Llamar al servicio para obtener el libro por id
    this.libroService.obtenerLibroPorId(libroId).subscribe((libro) => {
      this.libro = libro;
    });

    this.valoracionService.getValoraciones().subscribe(
      (valoraciones) => {
        this.valoraciones = valoraciones;
        console.log('Valoraciones recibidas:', valoraciones);
      },
      (error) => {
        console.error('Error al obtener las valoraciones:', error);
      }
    );
  }

  // Método helper para generar array de estrellas
  getStarsArray(puntuacion: number): number[] {
    return Array(puntuacion).fill(0);
  }

  // Método helper para generar array de estrellas vacías
  getEmptyStarsArray(puntuacion: number): number[] {
    return Array(5 - puntuacion).fill(0);
  }

  /*
  // Método para volver atrás con View Transitions
  goBack(): void {
    if ((document as any).startViewTransition) {
      (document as any).startViewTransition(() => {
        this.router.navigate(['/libros']);
      });
    } else {
      // Navegadores que no soportan View Transitions
      this.router.navigate(['/libros']);
    }
  } */
}
