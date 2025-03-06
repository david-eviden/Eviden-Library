import { Component, OnInit } from '@angular/core';
import { Valoracion } from './valoracion';
import { ValoracionService } from './valoracion.service';
import swal from 'sweetalert2';
import { AuthService } from '../login/auth.service';

@Component({
  selector: 'app-valoracion',
  standalone: false,
  templateUrl: './valoracion.component.html',
  styleUrl: './valoracion.component.css'
})
export class ValoracionComponent implements OnInit {
  valoraciones: Valoracion[] = [];

  constructor(private valoracionService: ValoracionService, public authService: AuthService ) {}

  ngOnInit(): void {
    this.valoracionService.getValoraciones().subscribe(
      valoraciones => {
        this.valoraciones = valoraciones;
        console.log('Valoraciones recibidas:', valoraciones);
      },
      error => {
        console.error('Error al obtener las valoraciones:', error);
      }
    );

    // Obtener valoraciones al cargar el componente
    this.valoracionService.getValoraciones().subscribe();
  }

  // Método para generar array de estrellas
  getStarsArray(puntuacion: number): number[] {
    return Array(puntuacion).fill(0);
  }

  // Método para generar array de estrellas vacías
  getEmptyStarsArray(puntuacion: number): number[] {
    return Array(5 - puntuacion).fill(0);
  }

  // Eliminar valoracion con confirmación
  delete(valoracion: Valoracion): void {
    // Mensaje de confirmación de eliminación
    swal({
      title: `¿Estás seguro de eliminar la valoracion con ID: ${valoracion.id}?`,
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
            this.valoraciones = this.valoraciones.filter(a => a.id !== valoracion.id);
            swal(
              '¡Eliminado!',
              `La valoracion con ID: ${valoracion.id}, ha sido eliminado con éxito`,
              'success'
            );
          },
          error => {
            console.error('Error al eliminar la valoracion', error);
            swal(
              'Error',
              'Hubo un problema al eliminar la valoracion',
              'error'
            );
          }
        );
      } else if (result.dismiss === swal.DismissReason.cancel) {
        swal(
          'Cancelado',
          'Tu valoracion está a salvo :)',
          'error'
        );
      }
    });
  }
}
