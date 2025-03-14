import { Component, OnInit } from '@angular/core';
import { detallesCarrito } from './detalles-carrito';
import { DetallesCarritoService } from './detalles-carrito.service';
import { AuthService } from '../login/auth.service';
import { Router } from '@angular/router';
import swal from 'sweetalert2';
import { ICreateOrderRequest, IPayPalConfig } from 'ngx-paypal';
import { Pedido } from '../pedido/pedido';
import { PedidoService } from '../pedido/pedido.service';
import { detallesPedido } from '../detalles-pedido/detalles-pedido';
import { Usuario } from '../usuario/usuario';
import { forkJoin } from 'rxjs';
import { of } from 'rxjs';

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
    private router: Router,
    private pedidoService: PedidoService
  ) {}

  private actualizarContadorCarrito(): void {
    const totalItems = this.detallesCarrito.reduce((total, item) => total + item.cantidad, 0);
    this.detallesCarritoService.updateCartItemCount(totalItems);
  }

  ngOnInit(): void {
    if (!this.authService.estaLogueado()) {
      this.router.navigate(['/login']);
      return;
    }

    this.detallesCarritoService.getdetallesCarrito().subscribe({
      next: (detallesCarritos) => {
        this.detallesCarrito = detallesCarritos;
        this.actualizarContadorCarrito();
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
    const total = this.detallesCarrito.reduce((total, item) => {
      return total + ((item.libro?.precio || 0) * item.cantidad);
    }, 0);
    
    return Number(total.toFixed(2));
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
          this.actualizarContadorCarrito();
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
            this.actualizarContadorCarrito();
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
    try {
      // Crear un nuevo pedido con los libros comprados
      const nuevoPedido = new Pedido();
      
      // Obtener el usuario actual
      const usuarioActual = this.authService.getCurrentUser();
      if (!usuarioActual || !usuarioActual.id) {
        swal('Error', 'No se pudo identificar al usuario actual', 'error');
        return;
      }
      
      // Configurar el pedido
      const usuario = new Usuario();
      usuario.id = usuarioActual.id;
      nuevoPedido.usuario = usuario;
      
      // Formatear la fecha como dd/MM/yyyy
      const hoy = new Date();
      const dia = String(hoy.getDate()).padStart(2, '0');
      const mes = String(hoy.getMonth() + 1).padStart(2, '0');
      const anio = hoy.getFullYear();
      const fechaFormateada = `${dia}/${mes}/${anio}`;
      
      console.log('Fecha formateada para el backend:', fechaFormateada);
      
      nuevoPedido.fechaPedido = fechaFormateada; // Formato dd/MM/yyyy que espera el backend
      
      nuevoPedido.estado = "COMPLETADO";
      nuevoPedido.total = this.calcularTotal();
      nuevoPedido.precioTotal = this.calcularTotal();
      nuevoPedido.direccionEnvio = usuarioActual.direccion || "Sin dirección especificada";
      
      // Para evitar problemas con la relación bidireccional, enviamos solo los datos necesarios
      // sin incluir la referencia circular a pedido
      const pedidoParaEnviar = {
        usuario: { id: usuarioActual.id },
        fechaPedido: fechaFormateada, // Formato dd/MM/yyyy que espera el backend
        estado: nuevoPedido.estado,
        total: nuevoPedido.total,
        direccionEnvio: nuevoPedido.direccionEnvio,
        // No enviamos detalles aquí, los crearemos después de crear el pedido
        detalles: []
      };
      
      console.log('Enviando pedido al servidor:', JSON.stringify(pedidoParaEnviar));
      
      // Guardar el pedido en la base de datos
      this.pedidoService.createPedido(pedidoParaEnviar as any).subscribe({
        next: (pedidoCreado) => {
          console.log('Pedido creado correctamente:', pedidoCreado);
          
          // Ahora que tenemos el pedido creado, podemos crear los detalles
          const detallesObservables = this.detallesCarrito.map(item => {
            if (!item.libro || !item.libro.id) {
              console.error('Error: El item del carrito no tiene un libro asociado', item);
              return of({
                error: true,
                mensaje: 'El item del carrito no tiene un libro asociado'
              });
            }
            
            const detallePedido = {
              pedido: { id: pedidoCreado.id },
              libro: { id: item.libro.id },
              cantidad: item.cantidad,
              precioUnitario: item.libro.precio || 0
            };
            
            console.log('Creando detalle de pedido:', detallePedido);
            return this.pedidoService.createDetallePedido(detallePedido);
          });
          
          // Si no hay detalles que crear, mostrar mensaje de éxito directamente
          if (detallesObservables.length === 0) {
            swal({
              title: '¡Pago completado!',
              text: `Tu pago por ${this.calcularTotal().toFixed(2)}€ ha sido procesado correctamente. Se ha creado el pedido #${pedidoCreado.id}.`,
              type: 'success',
              confirmButtonText: 'Continuar'
            }).then(() => {
              this.eliminarItemsDelCarrito();
            });
            return;
          }
          
          // Usar forkJoin para esperar a que todos los observables se completen
          forkJoin(detallesObservables).subscribe({
            next: (detalles) => {
              console.log('Todos los detalles creados correctamente:', detalles);
              
              // Verificar si algún detalle tuvo error
              const detallesConError = detalles.filter(detalle => detalle && detalle.error === true);
              
              if (detallesConError.length > 0) {
                console.warn('Algunos detalles tuvieron errores:', detallesConError);
                // Mostrar mensaje de éxito parcial
                swal({
                  title: '¡Pago completado!',
                  text: `Tu pago por ${this.calcularTotal().toFixed(2)}€ ha sido procesado correctamente. Se ha creado el pedido #${pedidoCreado.id}, pero hubo un problema al registrar algunos detalles. Serás redirigido a tu perfil para ver tu historial de pedidos.`,
                  type: 'warning',
                  confirmButtonText: 'Continuar'
                }).then(() => {
                  // Eliminar los items del carrito a pesar del error
                  this.eliminarItemsDelCarrito();
                });
              } else {
                // Mostrar mensaje de éxito
                swal({
                  title: '¡Pago completado!',
                  text: `Tu pago por ${this.calcularTotal().toFixed(2)}€ ha sido procesado correctamente. Se ha creado el pedido #${pedidoCreado.id}. Serás redirigido a tu perfil para ver tu historial de pedidos.`,
                  type: 'success',
                  confirmButtonText: 'Continuar'
                }).then(() => {
                  // Eliminar los items del carrito después de crear el pedido
                  this.eliminarItemsDelCarrito();
                });
              }
            },
            error: (error) => {
              console.error('Error al crear los detalles del pedido:', error);
              // Mostrar mensaje de éxito parcial
              swal({
                title: '¡Pago completado!',
                text: `Tu pago por ${this.calcularTotal().toFixed(2)}€ ha sido procesado correctamente, pero hubo un problema al registrar tu pedido. Por favor, contacta con soporte. Serás redirigido a tu perfil para ver tu historial de pedidos.`,
                type: 'warning',
                confirmButtonText: 'Continuar'
              }).then(() => {
                // Eliminar los items del carrito a pesar del error
                this.eliminarItemsDelCarrito();
              });
            }
          });
        },
        error: (error) => {
          console.error('Error al crear el pedido:', error);
          
          // Mostrar mensaje de error pero aún así eliminar los items del carrito
          swal({
            title: '¡Pago completado!',
            text: `Tu pago por ${this.calcularTotal().toFixed(2)}€ ha sido procesado correctamente, pero hubo un problema al registrar tu pedido. Por favor, contacta con soporte. Serás redirigido a tu perfil para ver tu historial de pedidos.`,
            type: 'warning',
            confirmButtonText: 'Continuar'
          }).then(() => {
            // Eliminar los items del carrito a pesar del error
            this.eliminarItemsDelCarrito();
          });
        }
      });
    } catch (error) {
      console.error('Error inesperado al procesar la compra:', error);
      swal({
        title: '¡Pago completado!',
        text: `Tu pago por ${this.calcularTotal().toFixed(2)}€ ha sido procesado correctamente, pero hubo un problema al registrar tu pedido. Por favor, contacta con soporte. Serás redirigido a tu perfil para ver tu historial de pedidos.`,
        type: 'warning',
        confirmButtonText: 'Continuar'
      }).then(() => {
        // Eliminar los items del carrito a pesar del error
        this.eliminarItemsDelCarrito();
      });
    }
  }
  
  // Método para eliminar los items del carrito después de crear el pedido
  private eliminarItemsDelCarrito(): void {
    // Get the current user ID
    const usuarioActual = this.authService.getCurrentUser();
    const usuarioId = usuarioActual?.id;
    
    // If no user ID is available, navigate to the books page as fallback
    if (!usuarioId) {
      console.warn('No se pudo obtener el ID del usuario actual, redirigiendo a libros');
      this.detallesCarritoService.updateCartItemCount(0);
      this.detallesCarrito = [];
      this.router.navigate(['/libros']);
      return;
    }
    
    // Add a delay before navigating to the user profile page
    console.log('Esperando 1 segundo antes de cargar los pedidos...');
    setTimeout(() => {
      // Force a refresh of the user's orders before navigating
      this.pedidoService.getPedidosPorUsuarioId(usuarioId).subscribe({
        next: (pedidos) => {
          console.log('Pedidos actualizados antes de navegar al perfil:', pedidos);
        },
        error: (error) => {
          console.error('Error al actualizar los pedidos antes de navegar:', error);
        },
        complete: () => {
          // Continue with cart cleanup regardless of whether the orders were refreshed
          this.limpiarCarritoYNavegar(usuarioId);
        }
      });
    }, 1000); // Wait 1 second before loading orders
  }
  
  private limpiarCarritoYNavegar(usuarioId: number): void {
    if (this.detallesCarrito.length === 0) {
      // Si no hay items, simplemente redirigir a la página de detalles del usuario
      this.router.navigate(['/usuario', usuarioId], { queryParams: { refresh: 'true' } });
      return;
    }

    // Crear una copia de los IDs para evitar problemas al modificar el array durante la iteración
    const itemIds = this.detallesCarrito
      .filter(item => item.id !== undefined)
      .map(item => item.id!);
    
    console.log(`Eliminando ${itemIds.length} items del carrito...`);
    
    // Si no hay items con ID, simplemente redirigir
    if (itemIds.length === 0) {
      console.log('No hay items con ID para eliminar');
      this.detallesCarritoService.updateCartItemCount(0);
      this.detallesCarrito = [];
      this.router.navigate(['/usuario', usuarioId], { queryParams: { refresh: 'true' } });
      return;
    }
    
    let itemsEliminados = 0;
    const totalItems = itemIds.length;

    // Función para verificar si todos los items han sido eliminados
    const verificarFinalizacion = () => {
      itemsEliminados++;
      console.log(`Progreso de eliminación: ${itemsEliminados}/${totalItems}`);
      if (itemsEliminados >= totalItems) {
        // Todos los items han sido procesados
        console.log('Todos los items del carrito han sido procesados');
        this.detallesCarritoService.updateCartItemCount(0);
        this.detallesCarrito = [];
        // Redirigir a la página de detalles del usuario para ver el pedido recién creado
        this.router.navigate(['/usuario', usuarioId], { queryParams: { refresh: 'true' } });
      }
    };

    // Eliminar cada item uno por uno
    itemIds.forEach(id => {
      console.log(`Eliminando item ${id} del carrito...`);
      this.detallesCarritoService.delete(id).subscribe({
        next: () => {
          console.log(`Item ${id} eliminado correctamente`);
          verificarFinalizacion();
        },
        error: (error) => {
          console.error(`Error al eliminar item ${id} del carrito:`, error);
          verificarFinalizacion(); // Continuar con el siguiente aunque haya error
        }
      });
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

        // Actualizar el contador a 0 después de eliminar todos los items
        this.detallesCarritoService.updateCartItemCount(0);
        
        swal('Eliminados', 'Todos los libros han sido eliminados del carrito', 'success');
      }
    });
  }
}