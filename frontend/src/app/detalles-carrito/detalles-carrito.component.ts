import { Component, OnInit } from '@angular/core';
import { detallesCarrito } from './detalles-carrito';
import { DetallesCarritoService } from './detalles-carrito.service';
import { AuthService } from '../login/auth.service';
import { Router } from '@angular/router';
import swal from 'sweetalert2';
import { ICreateOrderRequest, IPayPalConfig } from 'ngx-paypal';

@Component({
  selector: 'app-detalles-carrito',
  standalone: false,
  templateUrl: './detalles-carrito.component.html',
  styleUrls: ['./detalles-carrito.component.css']
})
export class DetallesCarritoComponent implements OnInit {
  detallesCarrito: detallesCarrito[] = [];
  cargando: boolean = true;
  public payPalConfig?: IPayPalConfig;
  mostrarPaypal: boolean = false;

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

  procederAlPago(): void {
    if (this.detallesCarrito.length === 0) {
      swal('Aviso', 'No hay productos en el carrito para proceder al pago', 'warning');
      return;
    }
    this.mostrarPaypal = true;
    this.initConfig();
  }

  private initConfig(): void {
    const total = this.calcularTotal().toFixed(2);
    
    this.payPalConfig = {
      currency: 'EUR',
      clientId: 'AZ4VTAcwLv65C04xUkcAGD40fx1ffeNsQtjt5_nb8h_ghpcHckhOd8kcVlvirW8dEkFTloz4GEheKcko',
      createOrderOnClient: (data) => <ICreateOrderRequest>{
        intent: 'CAPTURE',
        purchase_units: [{
          amount: {
            currency_code: 'EUR',
            value: total,
            breakdown: {
              item_total: {
                currency_code: 'EUR',
                value: total
              }
            }
          },
          items: this.detallesCarrito.map(item => ({
            name: item.libro?.titulo || 'Libro',
            quantity: item.cantidad.toString(),
            category: 'DIGITAL_GOODS',
            unit_amount: {
              currency_code: 'EUR',
              value: (item.libro?.precio || 0).toFixed(2)
            }
          }))
        }]
      },
      advanced: {
        commit: 'true'
      },
      style: {
        label: 'paypal',
        layout: 'vertical'
      },
      onApprove: (data, actions) => {
        console.log('onApprove - compra aprobada pero no autorizada', data, actions);
        actions.order.get().then((details: any) => {
          console.log('onApprove - detalles de la orden:', details);
        });
      },
      onClientAuthorization: (data) => {
        console.log('onClientAuthorization - pago completado:', data);
        this.procesarCompraExitosa(data);
      },
      onCancel: (data, actions) => {
        console.log('OnCancel - compra cancelada:', data, actions);
        swal('Cancelado', 'Has cancelado el proceso de pago', 'info');
        this.mostrarPaypal = false;
      },
      onError: err => {
        console.log('OnError - error en el proceso:', err);
        swal('Error', 'Ha ocurrido un error durante el proceso de pago', 'error');
      },
      onClick: (data, actions) => {
        console.log('onClick - botón clickado:', data, actions);
      }
    };
  }

  procesarCompraExitosa(data: any): void {
    swal({
      title: '¡Pago completado!',
      text: `Tu pago por ${this.calcularTotal().toFixed(2)}€ ha sido procesado correctamente.`,
      type: 'success',
      confirmButtonText: 'Continuar'
    }).then(() => {
      // O simplemente limpiar el carrito
      this.detallesCarrito.forEach(item => {
        this.detallesCarritoService.delete(item.id!).subscribe({
          next: () => {
            // Filtrar los items eliminados
            this.detallesCarrito = this.detallesCarrito.filter(i => i.id !== item.id);
          },
          error: (error) => {
            console.error('Error al eliminar del carrito:', error);
            swal('Error', 'No se pudo eliminar algunos libros del carrito', 'error');
          }
        });
      });

      // Y redireccionar a la página principal
      this.router.navigate(['/libros']);
    });
  }

  eliminarTodosDelCarrito(): void {
    // Confirmación antes de eliminar todos los elementos
    swal({
      title: '¿Estás seguro?',
      text: '¿Deseas eliminar todos los libros del carrito?',
      type: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, eliminar todos',
      cancelButtonText: 'Cancelar',
      buttonsStyling: true,
      reverseButtons: true
    }).then((result: any) => {
      if (result.value) {
        // Eliminar todos los libros del carrito
        this.detallesCarrito.forEach(item => {
          this.detallesCarritoService.delete(item.id!).subscribe({
            next: () => {
              // Filtrar los items eliminados
              this.detallesCarrito = this.detallesCarrito.filter(i => i.id !== item.id);
            },
            error: (error) => {
              console.error('Error al eliminar del carrito:', error);
              swal('Error', 'No se pudo eliminar algunos libros del carrito', 'error');
            }
          });
        });
        swal('Eliminados', 'Todos los libros han sido eliminados del carrito', 'success');
      }
    });
  }
}