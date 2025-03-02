import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationStart, Router } from '@angular/router';
import { Libro } from '../libro/libro';
import { DetallesLibroService } from './detalles-libro.service';
import { Valoracion } from '../valoracion/valoracion';
import { ValoracionService } from '../valoracion/valoracion.service';
import swal from 'sweetalert2';
import { filter, tap } from 'rxjs';


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
  ) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationStart)
    ).subscribe(() => {
      this.handleViewTransition();
    });
  }

  handleViewTransition(): void {
    if ('startViewTransition' in document) {
      (document as any).startViewTransition();
    }
  }

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
      },
      (error) => {
        console.error('Error al obtener las valoraciones:', error);
      }
    );
  }

  irAtras(): void {
    if ('startViewTransition' in document) {
      (document as any).startViewTransition(() => {
        this.router.navigate(['/libros']);
      });
    } else {
      this.router.navigate(['/libro']);
    }
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

  
  // Formulario

  delete(libro: Libro): void {
    // Mensaje confirmacion eliminar
    swal({

      title: `¿Estás seguro de eliminar el libro "${libro.titulo}"?`,
      text: "¡Esta operación no es reversible!",
      type: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "¡Sí, eliminalo!",
      cancelButtonText: "No, cancelar",
      buttonsStyling: true,
      reverseButtons: true

    }).then((result) => {

      if (result.value) {

        this.libroService.delete(libro.id).subscribe(
          response => {
            this.router.navigate(['/libros']);
            swal(
              '¡Eliminado!',
              `El libro "${libro.titulo}" ha sido eliminado con éxito`,
              'success'
            );
          },
          error => {
            console.error('Error al eliminar el libro:', error);
            
            // Mensaje específico para el caso de libros con pedidos asociados
            if (error.status === 409) { // 409 Conflict
              swal(
                'No se puede eliminar',
                'Este libro no puede ser eliminado porque está asociado a uno o más pedidos.',
                'warning'
              );
            } else {
              swal(
                'Error al eliminar',
                error.error.mensaje || 'Ocurrió un error al intentar eliminar el libro.',
                'error'
              );
            }
          }
        );

      } else if(result.dismiss === swal.DismissReason.cancel) {

        swal(
          'Cancelado',
          'Tu libro está a salvo :)',
          'error'
        )

      }
    });
  }

  // Eliminar valoracion con confirmación
  deleteValoracion(valoracion: Valoracion): void {
    // Mensaje de confirmación de eliminación
    swal({
      title: `¿Estás seguro de eliminar la valoración?`,
      text: "¡Esta operación no es reversible!",
      type: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "¡Sí, eliminar!",
      cancelButtonText: "No, cancelar",
      buttonsStyling: true,
      reverseButtons: true
    }).then((result) => {
      if (result.value) {
        this.valoracionService.delete(valoracion.id).subscribe(
          response => {
            // Cuando la eliminación es exitosa, actualizamos la lista
            this.libro.valoraciones = this.libro.valoraciones.filter(v => v.id !== valoracion.id);
            swal(
              '¡Eliminado!',
              `La valoración ha sido eliminada con éxito`,
              'success'
            );
          },
          error => {
            console.error('Error al eliminar la valoración', error);
            swal(
              'Error',
              'Hubo un problema al eliminar la valoración',
              'error'
            );
          }
        );
      } else if (result.dismiss === swal.DismissReason.cancel) {
        swal(
          'Cancelado',
          'La valoración está a salvo :)',
          'error'
        );
      }
    });
  }
}
