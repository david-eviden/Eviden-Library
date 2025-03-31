import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationStart, Router } from '@angular/router';
import { Libro } from '../libro/libro';
import { DetallesLibroService } from './detalles-libro.service';
import { Valoracion } from '../valoracion/valoracion';
import { ValoracionService } from '../valoracion/valoracion.service';
import swal from 'sweetalert2';
import { filter, tap } from 'rxjs';
import { AuthService } from '../login/auth.service';
import { FavoritoService } from '../favorito/favorito.service';
import { DetallesCarritoService } from '../detalles-carrito/detalles-carrito.service';
import { LibrosCompradosService } from '../services/libros-comprados.service';
import { LibroService } from '../libro/libro.service';


@Component({
  selector: 'app-detalles-libro',
  standalone: false,
  templateUrl: './detalles-libro.component.html',
  styleUrl: './detalles-libro.component.css',
})
export class DetallesLibroComponent implements OnInit {
  libro: Libro = new Libro();
  valoraciones: Valoracion[] = [];
  esFavorito: boolean = false;
  verificandoFavorito: boolean = false;
  agregandoAlCarrito: boolean = false;
  librosRelacionados: Libro[] = [];
  maxLibrosRelacionados: number = 4;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private libroService: DetallesLibroService,
    private valoracionService: ValoracionService,
    private favoritoService: FavoritoService,
    private carritoService: DetallesCarritoService,
    public authService: AuthService,
    private librosCompradosService: LibrosCompradosService,
    private libroServiceGeneral: LibroService
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
    this.route.paramMap.subscribe(params => {
      const id = +params.get('id')!;
      
      // Si el usuario está logueado, cargamos sus libros comprados
      if (this.authService.estaLogueado()) {
        const usuarioId = this.authService.getCurrentUserId();
        this.librosCompradosService.cargarLibrosComprados(usuarioId).subscribe(
          () => this.cargarLibro(id)
        );
      } else {
        this.cargarLibro(id);
      }
    });

    //Valoraciones
    this.valoracionService.getValoraciones().subscribe(
      (valoraciones) => {
        this.valoraciones = valoraciones;
      },
      (error) => {
        console.error('Error al obtener las valoraciones:', error);
      }
    );
  }

  /**
   * Carga los detalles de un libro
   * @param id ID del libro
   */
  cargarLibro(id: number): void {
    this.libroService.getLibro(id).subscribe(
      (libro) => {
        this.libro = libro;
        // Verificar si el libro está en favoritos
        if (this.authService.estaLogueado() && this.authService.esUsuario) {
          this.verificandoFavorito = true;
          this.favoritoService.checkFavorito(libro.id).subscribe({
            next: (esFavorito) => {
              console.log(`El libro ${libro.id} ${esFavorito ? 'es' : 'no es'} favorito`);
              this.esFavorito = esFavorito;
              this.verificandoFavorito = false;
            },
            error: (error) => {
              console.error('Error al verificar favorito:', error);
              this.verificandoFavorito = false;
            }
          });
        }
        //Cargar libbros relacionados
        this.cargarLibrosRelacionados();
      },
      (error) => {
        console.error(error);
        this.router.navigate(['/libros']);
      }
    );
  }

  // Verificar si el libro está en favoritos
  verificarFavorito(libroId: number): void {
    if (!libroId) {
      console.error('No se puede verificar favorito: ID de libro no válido');
      this.verificandoFavorito = false;
      return;
    }
    
    this.verificandoFavorito = true;
    
    this.favoritoService.checkFavorito(libroId).subscribe(
      (esFavorito) => {
        console.log(`Resultado de verificación: el libro ${libroId} ${esFavorito ? 'es' : 'no es'} favorito`);
        this.esFavorito = esFavorito;
        this.verificandoFavorito = false;
      },
      (error) => {
        console.error('Error al verificar favorito:', error);
        this.verificandoFavorito = false;
        
        // No mostrar errores al usuario en este caso, simplemente asumir que no es favorito
        this.esFavorito = false;
        
        // Si es un error de autenticación, podríamos redirigir al login
        if (error.status === 401) {
          console.warn('Sesión expirada durante la verificación de favoritos');
          // No redirigir automáticamente para no interrumpir la experiencia del usuario
          // this.authService.logout();
        }
      }
    );
  }

  // Agregar o quitar de favoritos
  toggleFavorito(): void {
    // Verificar si el usuario está logueado
    if (!this.authService.estaLogueado()) {
      swal({
        title: 'Inicia sesión',
        text: 'Debes iniciar sesión para guardar libros en favoritos',
        type: 'info',
        showCancelButton: true,
        confirmButtonText: 'Ir a login',
        cancelButtonText: 'Cancelar'
      }).then((result) => {
        if (result.value) {
          this.router.navigate(['/login']);
        }
      });
      return;
    }

    // Verificar si el usuario tiene el rol adecuado
    if (!this.authService.esUsuario) {
      swal('Acceso denegado', 'Solo los usuarios pueden agregar libros a favoritos', 'warning');
      return;
    }

    // Verificar si el libro tiene ID
    if (!this.libro.id) {
      swal('Error', 'No se pudo identificar el libro', 'error');
      return;
    }

    // Mostrar indicador de carga
    this.verificandoFavorito = true;

    const usuarioId = this.authService.getCurrentUserId();
    if (!usuarioId) {
      swal('Error', 'No se pudo identificar el usuario', 'error');
      this.verificandoFavorito = false;
      return;
    }

    if (this.esFavorito) {
      // Eliminar de favoritos
      this.favoritoService.deleteFavoritoByLibroAndUsuario(this.libro.id, usuarioId).subscribe({
        next: () => {
          this.esFavorito = false;
          this.verificandoFavorito = false;
          swal('Eliminado', 'El libro ha sido eliminado de tus favoritos', 'success');
        },
        error: (error) => {
          console.error('Error al eliminar de favoritos:', error);
          this.verificandoFavorito = false;
          
          if (error.status === 403) {
            swal('Error de permisos', 'No tienes permisos para realizar esta acción', 'error');
          } else if (error.status === 401) {
            swal('Sesión expirada', 'Tu sesión ha expirado. Por favor, inicia sesión nuevamente.', 'warning');
            this.authService.logout();
          } else {
            swal('Error', 'No se pudo eliminar el libro de favoritos', 'error');
          }
        }
      });
    } else {
      // Agregar a favoritos
      this.favoritoService.addFavorito(this.libro).subscribe({
        next: (response) => {
          this.esFavorito = true;
          this.verificandoFavorito = false;
          swal('Agregado', 'El libro ha sido agregado a tus favoritos', 'success');
        },
        error: (error) => {
          console.error('Error al agregar a favoritos:', error);
          this.verificandoFavorito = false;
          
          if (error.status === 403) {
            swal('Error de permisos', 'No tienes permisos para realizar esta acción', 'error');
          } else if (error.status === 401) {
            swal('Sesión expirada', 'Tu sesión ha expirado. Por favor, inicia sesión nuevamente.', 'warning');
            this.authService.logout();
          } else {
            swal('Error', 'No se pudo agregar el libro a favoritos', 'error');
          }
        }
      });
    }
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
    puntuacion = Math.min(Math.max(Math.round(puntuacion || 0), 0), 5);
    return Array(puntuacion).fill(0);
  }

  // Método helper para generar array de estrellas vacías
  getEmptyStarsArray(puntuacion: number): number[] {
    puntuacion = Math.min(Math.max(Math.round(puntuacion || 0), 0), 5);
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

  addToCart(): void {
    if (!this.authService.estaLogueado()) {
      swal({
        title: 'Inicia sesión',
        text: 'Debes iniciar sesión para añadir libros al carrito',
        type: 'info',
        showCancelButton: true,
        confirmButtonText: 'Ir a login',
        cancelButtonText: 'Cancelar'
      }).then((result: any) => {
        if (result.value) {
          this.router.navigate(['/login']);
        }
      });
      return;
    }

    if (!this.authService.esUsuario) {
      swal({
        title: 'Acceso denegado',
        text: 'Solo los usuarios pueden añadir libros al carrito',
        type: 'warning'
      });
      return;
    }

    if (!this.libro.id) {
      swal({
        title: 'Error',
        text: 'No se pudo identificar el libro',
        type: 'error'
      });
      return;
    }

    this.agregandoAlCarrito = true;
    this.carritoService.addToCart(this.libro).subscribe({
      next: (response) => {
        // Ya no es necesario llamar a updateCartItemCount aquí porque se hace dentro de addToCart

        this.agregandoAlCarrito = false;
        swal({
          title: '¡Añadido al carrito!',
          text: `${this.libro.titulo} se ha añadido a tu carrito`,
          type: 'success',
          showCancelButton: true,
          confirmButtonText: 'Ver carrito',
          cancelButtonText: 'Seguir comprando'
        }).then((result: any) => {
          if (result.value) {
            this.router.navigate(['/mi-carrito']);
          }
        });
      },
      error: (error) => {
        this.agregandoAlCarrito = false;
        console.error('Error al añadir al carrito:', error);
        swal({
          title: 'Error',
          text: 'No se pudo añadir el libro al carrito',
          type: 'error'
        });
      }
    });
  }

  /**
   * Verifica si el usuario ha comprado un libro
   * @param libroId ID del libro
   * @returns true si el usuario ha comprado el libro, false en caso contrario
   */
  haCompradoLibro(libroId: number): boolean {
    if (!this.authService.estaLogueado()) {
      return false;
    }
    const usuarioId = this.authService.getCurrentUserId();
    return this.librosCompradosService.haCompradoLibro(usuarioId, libroId);
  }

  //Ver los libros del autor
  verLibrosAutor(autorId: number): void {
    this.router.navigate(['/libros/autor', autorId, 'page', 0, 'size',4]);
  }

  //Ver libros del genero
  verLibrosGenero(generoId: number): void {
    this.router.navigate(['/libros'], {
      queryParams: {
        generoId: generoId,
        page: 0,
        size: 8
      }
    });
  }

  //Libros del mismo genero
  cargarLibrosRelacionados(): void {
    if (this.libro && this.libro.generos && this.libro.generos.length > 0) {
      const generoPrincipal = this.libro.generos[0];
      this.libroServiceGeneral.getLibrosNoPagin().subscribe({
        next: (libros) => {
          //Filtrar libros del mismo género
          this.librosRelacionados = libros
            .filter((libro: Libro) =>
              libro.id !== this.libro.id && // Excluir el libro actual
              libro.generos && // Verificar que tenga géneros
              libro.generos.length > 0 && // Verificar que tenga al menos un género
              libro.generos[0].id === generoPrincipal.id 
            )
            .slice(0, this.maxLibrosRelacionados);
         
          console.log('Género principal:', generoPrincipal);
          console.log('Libros relacionados cargados:', this.librosRelacionados);
        },
        error: (error) => {
          console.error('Error al cargar libros relacionados:', error);
        }
      });
    } else {
      console.log('No hay géneros disponibles para cargar libros relacionados');
    }
  }

  //Ver detalles libro
  verDetallesLibro(libroId: number): void {
    if ('startViewTransition' in document) {
      (document as any).startViewTransition(() => {
        this.router.navigate(['/libro', libroId]);
      });
    }else{
      this.router.navigate(['/libro', libroId]);
    }
  }

  
}
