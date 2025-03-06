import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import swal from 'sweetalert2';
import { DetallesUsuarioService } from '../detalles-usuario/detalles-usuario.service';
import { Usuario } from '../usuario/usuario';
import { PedidoService } from '../pedido/pedido.service';
import { Pedido } from '../pedido/pedido';
import { Valoracion } from '../valoracion/valoracion';
import { ValoracionService } from '../valoracion/valoracion.service';
import { AuthService } from '../login/auth.service';

@Component({
  selector: 'app-detalles-usuario',
  standalone: false,
  templateUrl: './detalles-usuario.component.html',
  styleUrl: './detalles-usuario.component.css'
})
export class DetallesUsuarioComponent implements OnInit {
  
  usuario: Usuario = new Usuario();
  pedidos: Pedido[] = [];
  valoraciones: Valoracion[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private usuarioService: DetallesUsuarioService,
    private pedidoService: PedidoService,
    private valoracionService: ValoracionService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    // Obtener el id del usuario desde la ruta
    const usuarioId = +this.route.snapshot.paramMap.get('id')!;

    // Llamar al servicio para obtener el usuario por id
    this.usuarioService.obtenerUsuarioPorId(usuarioId).subscribe((usuario) => {
      this.usuario = usuario;
    });

    // Cargar pedidos
    this.pedidoService.getPedidos().subscribe(
      (pedidos) => {
        this.pedidos = pedidos;
      },
      (error) => {
        console.error('Error al obtener los pedidos:', error);
      }
    );

    // Cargar valoraciones
    this.valoracionService.getValoraciones().subscribe(
      (valoraciones) => {
        this.valoraciones = valoraciones;
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

  delete(usuario: Usuario): void {
    // Mensaje confirmacion eliminar
    swal({

      title: `¿Estás seguro de eliminar el usuario "${usuario.nombre} ${usuario.apellido}"?`,
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

        this.usuarioService.delete(usuario.id).subscribe(
          response => {
            this.router.navigate(['/usuarios']);
            swal(
              '¡Eliminado!',
              `El usuario "${usuario.nombre} ${usuario.apellido}" ha sido eliminado con éxito`,
              'success'
            );
          },
          error => {
            console.error('Error al eliminar el usuario:', error);
            
            // Mensaje específico para el caso de usuarios con pedidos asociados
            if (error.status === 409) { // 409 Conflict
              swal(
                'No se puede eliminar',
                'Este usuario no puede ser eliminado porque está asociado a uno o más pedidos.',
                'warning'
              );
            } else {
              swal(
                'Error al eliminar',
                error.error.mensaje || 'Ocurrió un error al intentar eliminar el usuario.',
                'error'
              );
            }
          }
        );

      } else if(result.dismiss === swal.DismissReason.cancel) {

        swal(
          'Cancelado',
          'Tu usuario está a salvo :)',
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
            this.usuario.valoraciones = this.usuario.valoraciones.filter(v => v.id !== valoracion.id);
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
