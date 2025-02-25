import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Libro } from '../libro/libro';
import { DetallesLibroService } from './detalles-libro.service';

@Component({
  selector: 'app-detalles-libro',
  standalone: false,
  templateUrl: './detalles-libro.component.html',
  styleUrl: './detalles-libro.component.css'
})
export class DetallesLibroComponent implements OnInit {

  libro: Libro = new Libro(); // Definimos la propiedad libro

  constructor(
    private route: ActivatedRoute, // Para obtener los parámetros de la ruta
    private router: Router,
    private libroService: DetallesLibroService // Servicio para obtener los detalles del libro
  ) {}

  ngOnInit(): void {
    // Obtener el id del libro desde la ruta
    const libroId = +this.route.snapshot.paramMap.get('id')!; // el 'id' de la ruta

    // Llamar al servicio para obtener el libro por id
    this.libroService.obtenerLibroPorId(libroId).subscribe((libro) => {
      this.libro = libro;
    });
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