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
  esPerfilPropio: boolean = false;
  cargando: boolean = true;
  error: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private usuarioService: DetallesUsuarioService,
    private pedidoService: PedidoService,
    private valoracionService: ValoracionService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargando = true;
    this.error = '';
    
    // Obtener el id del usuario desde la ruta
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      
      // Verificar si el parámetro id es válido
      if (!idParam || isNaN(Number(idParam))) {
        console.error('ID de usuario no válido en la URL:', idParam);
        
        // Intentar usar el ID del usuario logueado
        const usuarioLogueadoId = this.authService.getCurrentUserId();
        
        if (usuarioLogueadoId && !isNaN(Number(usuarioLogueadoId))) {
          console.log('Usando ID del usuario logueado:', usuarioLogueadoId);
          this.esPerfilPropio = true;
          this.cargarDatosUsuario(usuarioLogueadoId);
        } else {
          // Si no hay ID válido, intentar usar el email del usuario actual
          const email = this.authService.getCurrentUserEmail();
          if (email) {
            console.log('Intentando obtener usuario por email:', email);
            this.cargarDatosPorEmail(email);
          } else {
            // Si no hay email, mostrar error y redirigir
            this.cargando = false;
            this.error = 'No se pudo identificar al usuario';
            swal(
              'Error',
              this.error,
              'error'
            );
            this.router.navigate(['/principal']);
          }
        }
        return;
      }
      
      const usuarioId = Number(idParam);
      
      // Verificar si es el perfil del usuario logueado
      const usuarioLogueadoId = this.authService.getCurrentUserId();
      this.esPerfilPropio = usuarioId === usuarioLogueadoId;
      
      // Llamar al servicio para obtener el usuario por id
      this.cargarDatosUsuario(usuarioId);
    });
  }

  cargarDatosUsuario(usuarioId: number): void {
    console.log('Cargando datos del usuario con ID:', usuarioId);
    this.cargando = true;
    this.error = '';
    
    if (!usuarioId || isNaN(usuarioId)) {
      console.error('ID de usuario no válido:', usuarioId);
      this.cargando = false;
      this.error = 'ID de usuario no válido';
      swal(
        'Error',
        this.error,
        'error'
      );
      this.router.navigate(['/principal']);
      return;
    }
    
    this.usuarioService.obtenerUsuarioPorId(usuarioId).subscribe(
      (usuario) => {
        console.log('Datos de usuario recibidos:', usuario);
        this.usuario = usuario;
        
        // Inicializar las colecciones si no existen
        if (!this.usuario.pedidos) this.usuario.pedidos = [];
        if (!this.usuario.valoraciones) this.usuario.valoraciones = [];
        
        // Cargar los pedidos del usuario específico
        this.cargarPedidos(usuarioId);
        
        // Cargar las valoraciones del usuario específico
        this.cargarValoraciones(usuarioId);
        
        this.cargando = false;
      },
      (error) => {
        console.error('Error al obtener los datos del usuario:', error);
        this.cargando = false;
        this.error = 'No se pudieron cargar los datos del usuario';
        swal(
          'Error',
          this.error,
          'error'
        );
        this.router.navigate(['/principal']);
      }
    );
  }

  cargarPedidos(usuarioId: number): void {
    this.pedidoService.getPedidosPorUsuarioId(usuarioId).subscribe(
      (pedidos) => {
        this.usuario.pedidos = pedidos;
      },
      (error) => {
        console.error('Error al obtener los pedidos del usuario:', error);
      }
    );
  }

  cargarValoraciones(usuarioId: number): void {
    this.valoracionService.getValoracionesPorUsuarioId(usuarioId).subscribe(
      (valoraciones) => {
        this.usuario.valoraciones = valoraciones;
      },
      (error) => {
        console.error('Error al obtener las valoraciones del usuario:', error);
      }
    );
  }

  cargarDatosPorEmail(email: string): void {
    console.log('Cargando datos del usuario con email:', email);
    this.cargando = true;
    this.error = '';
    
    this.usuarioService.obtenerUsuarioPorEmail(email).subscribe(
      (usuario) => {
        console.log('Datos de usuario recibidos por email:', usuario);
        this.usuario = usuario;
        
        // Inicializar las colecciones si no existen
        if (!this.usuario.pedidos) this.usuario.pedidos = [];
        if (!this.usuario.valoraciones) this.usuario.valoraciones = [];
        
        // Marcar como perfil propio
        this.esPerfilPropio = true;
        
        // Cargar los pedidos y valoraciones
        if (this.usuario.id) {
          this.cargarPedidos(this.usuario.id);
          this.cargarValoraciones(this.usuario.id);
        }
        
        this.cargando = false;
        
        // Actualizar el ID en el servicio de autenticación
        const usuarioActual = this.authService.getCurrentUser();
        if (usuarioActual && this.usuario.id) {
          usuarioActual.id = this.usuario.id;
          localStorage.setItem('usuario', JSON.stringify(usuarioActual));
          // No actualizamos el subject para evitar un bucle infinito
        }
      },
      (error) => {
        console.error('Error al obtener los datos del usuario por email:', error);
        this.cargando = false;
        this.error = 'No se pudieron cargar los datos del usuario';
        
        // Intentar usar los datos del usuario actual como alternativa
        const usuarioActual = this.authService.getCurrentUser();
        if (usuarioActual) {
          console.log('Usando datos del usuario actual:', usuarioActual);
          this.esPerfilPropio = true;
          this.usuario = this.convertirUsuarioAuth(usuarioActual);
        } else {
          swal(
            'Error',
            this.error,
            'error'
          );
          this.router.navigate(['/principal']);
        }
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

  // Método para convertir el usuario de autenticación a un objeto Usuario
  convertirUsuarioAuth(usuarioAuth: any): Usuario {
    const usuario = new Usuario();
    usuario.id = usuarioAuth.id || 0;
    usuario.email = usuarioAuth.email || '';
    usuario.nombre = usuarioAuth.username || usuarioAuth.email?.split('@')[0] || '';
    usuario.apellido = '';
    usuario.rol = usuarioAuth.rol || 'USER';
    usuario.pedidos = [];
    usuario.valoraciones = [];
    return usuario;
  }
}
