import { Component, OnInit } from '@angular/core';
import { detallesCarrito } from './detalles-carrito';
import { DetallesCarritoService } from './detalles-carrito.service';
import { AuthService } from '../login/auth.service';
import { Router } from '@angular/router';
import swal from 'sweetalert2';

@Component({
  selector: 'app-detalles-carrito',
  standalone: false,
  templateUrl: './detalles-carrito.component.html',
  styleUrls: ['./detalles-carrito.component.css']
})
export class DetallesCarritoComponent implements OnInit {
  detallesCarrito: detallesCarrito[] = [];
  cargando: boolean = true;

  constructor(
    private detallesCarritoService: DetallesCarritoService,
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.authService.estaLogueado()) {
      this.router.navigate(['/login']);
      return;
    }

    this.detallesCarritoService.getdetallesCarrito().subscribe({
      next: (detallesCarritos) => {
        this.detallesCarrito = detallesCarritos;
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error al obtener los detalles del carrito:', error);
        this.cargando = false;
        swal('Error', 'No se pudieron cargar los detalles del carrito', 'error');
      }
    });
  }

  verDetallesLibro(libroId: number | undefined): void {
    if (libroId) {
      this.router.navigate(['/libro', libroId]);
    }
  }

  calcularTotal(): number {
    return this.detallesCarrito.reduce((total, item) => {
      return total + ((item.libro?.precio || 0) * item.cantidad);
    }, 0);
  }

  seguirComprando(): void {
    this.router.navigate(['/libros']);
  }

  procederAlPago(): void {
    // TODO: Implementar la lógica de pago
    console.log('Procediendo al pago...');
  }

  actualizarCantidad(item: detallesCarrito, incremento: number): void {
    const nuevaCantidad = item.cantidad + incremento;
    if (nuevaCantidad < 1) {
      // Si la cantidad llega a 0, eliminamos el item
      this.eliminarDelCarrito(item);
      return;
    }

    // Asegurarnos de que mantenemos toda la estructura del objeto
    const detalleActualizado = {
      ...item,
      cantidad: nuevaCantidad,
      carrito: item.carrito,
      libro: item.libro
    };

    this.detallesCarritoService.update(detalleActualizado).subscribe({
      next: (resultado) => {
        const index = this.detallesCarrito.findIndex(i => i.id === item.id);
        if (index !== -1) {
          this.detallesCarrito[index] = resultado;
          swal('Éxito', 'Cantidad actualizada correctamente', 'success');
        }
      },
      error: (error) => {
        console.error('Error al actualizar la cantidad:', error);
        // Revertir el cambio en la UI
        const index = this.detallesCarrito.findIndex(i => i.id === item.id);
        if (index !== -1) {
          this.detallesCarrito[index] = item;
        }
        swal('Error', 'No se pudo actualizar la cantidad', 'error');
      }
    });
  }

  eliminarDelCarrito(item: detallesCarrito): void {
    if (!item.id) {
      swal('Error', 'No se puede eliminar un item sin ID', 'error');
      return;
    }
    
    // Mostrar confirmación antes de eliminar
    swal({
      title: '¿Estás seguro?',
      text: `¿Deseas eliminar "${item.libro?.titulo}" del carrito?`,
      type: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      buttonsStyling: true,
      reverseButtons: true
    }).then((result: any) => {
      if (result.value) {
        this.detallesCarritoService.delete(item.id!).subscribe({
          next: () => {
            this.detallesCarrito = this.detallesCarrito.filter(i => i.id !== item.id);
            swal('Eliminado', 'El libro ha sido eliminado del carrito', 'success');
          },
          error: (error) => {
            console.error('Error al eliminar del carrito:', error);
            let mensajeError = 'No se pudo eliminar el libro del carrito';
            if (error.status === 404) {
              mensajeError = 'El item que intentas eliminar ya no existe en el carrito';
            }
            swal('Error', mensajeError, 'error');
          }
        });
      }
    });
  }
}
