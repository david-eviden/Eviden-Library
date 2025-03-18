import { Component, OnInit, HostListener } from '@angular/core';
import { LibroService } from './libro.service';
import { Libro } from './libro';
import { ActivatedRoute, NavigationStart, Router } from '@angular/router';
import { filter, tap } from 'rxjs';
import swal from 'sweetalert2';
import { AuthService } from '../login/auth.service';
import { Autor } from '../autor/autor';
import { DetallesCarritoService } from '../detalles-carrito/detalles-carrito.service';
import { LibrosCompradosService } from '../services/libros-comprados.service';

@Component({
  selector: 'app-libro',
  standalone: false,
  templateUrl: './libro.component.html',
  styleUrls: ['./libro.component.css']
})
export class LibroComponent implements OnInit{

  libros : Libro[]= [];
  autores : Autor[]= [];
  paginador: any;
  animationState = "in"
  pageSizes: number[] = [4, 8, 12, 16]; // Opciones de tamaño de página
  currentPageSize: number = 8; // Tamaño de página por defecto
  currentPage: number = 0; // Página actual
  selectedAutorId: number = 0; // ID del autor seleccionado (0 para todos)
  isMobile: boolean = false;

  constructor(
    private libroService: LibroService, 
    private router: Router, 
    private activatedRoute: ActivatedRoute, 
    public authService: AuthService,
    private carritoService: DetallesCarritoService,
    private librosCompradosService: LibrosCompradosService
  ) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationStart)
    ).subscribe(() => {
      this.handleViewTransition();
    });
    this.checkScreenSize();
  }

  @HostListener('window:resize')
  onResize() {
    this.checkScreenSize();
  }

  private checkScreenSize() {
    this.isMobile = window.innerWidth < 768; // Consideramos móvil si el ancho es menor a 768px
    if (this.isMobile) {
      this.currentPageSize = 4;
    } else {
      this.currentPageSize = 8;
    }
    this.cargarLibros();
  }

  handleViewTransition(): void {
    if ('startViewTransition' in document) {
      // Use the modern View Transitions API if available
      (document as any).startViewTransition();
    }
  }

  ngOnInit(): void {
    //Cargamos la lista de autores
    this.cargarAutores();

    this.activatedRoute.paramMap.subscribe(params => {
      let page: number = +params.get('page')! || 0;
      this.currentPage = page;

      // Si el usuario está logueado, cargamos sus libros comprados
      if (this.authService.estaLogueado()) {
        const usuarioId = this.authService.getCurrentUserId();
        this.librosCompradosService.cargarLibrosComprados(usuarioId).subscribe(
          () => this.cargarLibros()
        );
      } else {
        this.cargarLibros();
      }
    });

  
    /* Sin paginacion */
    /*
    this.libroService.getLibros().subscribe(
      (libros: Libro[]) => {
        this.libros = libros;
        console.log('Detalles del libro recibidos:', libros);
      },
      error => {
        console.error('Error al obtener los detalles del libro', error);
      }
    ) */
  };

  irAtras(): void {
    if ("startViewTransition" in document) {
      document.documentElement.classList.add("navigating-back")
      ;(document as any).startViewTransition(() => {
        window.history.back()
        // Eliminar la clase después de la transición
        setTimeout(() => {
          document.documentElement.classList.remove("navigating-back")
        }, 300)
      })
    } else {
      window.history.back()
    }
  }

  // Método para navegar a detalles con View Transitions
  getDetallesLibro(id: number): void {
    if ('startViewTransition' in document) {
      (document as any).startViewTransition(() => {
        this.router.navigate(['/libro', id]);
      });
    } else {
      this.router.navigate(['/libro', id]);
    }
  }

  deleteAll(): void {
    // Mensaje confirmacion eliminar todos
    swal({
      title: '¿Estás seguro de eliminar todos los libros?',
      text: "¡Esta operación no es reversible!",
      type: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "¡Sí, eliminar todos!",
      cancelButtonText: "No, cancelar",
      buttonsStyling: true,
      reverseButtons: true
    }).then((result) => {
      if (result.value) {
        this.libroService.deleteAll().subscribe(
          response => {
            // Vaciamos el array de libros
            this.libros = [];
  
            swal(
              '¡Eliminados!',
              'Todos los libros han sido eliminados :(',
              'success'
            );
          }
        );
      } else if (result.dismiss === swal.DismissReason.cancel) {
        swal(
          'Cancelado',
          'Tus libros están a salvo :)',
          'error'
        )
      }
    });
  }

  // Método para cargar libros con el tamaño de página actual
  /* cargarLibros(): void {
    this.libroService.getLibrosConTamanio(this.currentPage, this.currentPageSize)
    .subscribe(response => {
      this.libros = response.content as Libro[];
      this.paginador = response;
    });
  } */

  // Cargar autores para el filtro
  cargarAutores(): void {
    this.libroService.getAutores().subscribe({
      next: (autores) => {
        this.autores = autores;
      },
      error: (error) => {
        console.error('Error al cargar autores:', error);
        this.autores = [];
      }
    });
  }
  
  // Método para cambiar el tamaño de la página
  cambiarTamanioPagina(event: any): void {
    this.currentPageSize = +event.target.value;
    this.currentPage = 0; // Volvemos a la primera página al cambiar el tamaño
    this.cargarLibros();
    this.router.navigate(['/libros/page', 0]); // Actualizamos la URL
  }

  // Método para filtrar por autor
  filtrarPorAutor(event: any): void {
    this.selectedAutorId = +event.target.value;
    
    // Si no hay autores cargados y se intenta filtrar, cargar autores primero
    if (this.autores.length === 0 && this.selectedAutorId > 0) {
      console.log('No hay autores cargados, intentando cargar autores primero...');
      this.cargarAutores();
    }
    
    this.currentPage = 0; // Volvemos a la primera página al cambiar el filtro
    this.cargarLibros();
    this.router.navigate(['/libros/page', 0]); // Actualizamos la URL
  }

  // Método para cargar libros con el tamaño de página actual y filtro de autor
  cargarLibros(): void {
    if (this.selectedAutorId > 0) {
      // Si hay un autor seleccionado, cargar libros filtrados
      this.libroService.getLibrosPorAutor(this.currentPage, this.currentPageSize, this.selectedAutorId)
        .subscribe({
          next: (response) => {
            this.libros = response.content as Libro[];
            this.paginador = response;
            if (this.libros.length === 0) {
              console.log("No se encontraron libros para el autor seleccionado");
            }
          },
          error: (error) => {
            console.error('Error cargando los libros del autor: ', error);
            this.libros = [];
            this.paginador = null;
          }
        });
    } else {
      // Si no hay autor seleccionado, cargar todos los libros
      this.libroService.getLibrosConTamanio(this.currentPage, this.currentPageSize)
        .subscribe({
          next: (response) => {
            this.libros = response.content as Libro[];
            this.paginador = response;
            if (this.libros.length === 0) {
              console.log("No se encontraron libros en el catálogo");
            }
          },
          error: (error) => {
            console.error('Error cargando los libros: ', error);
            this.libros = [];
            this.paginador = null;
          }
        });
    }
  }

  addToCart(libro: Libro): void {
    if (!this.authService.esUsuario) {
      swal('Error', 'Debes iniciar sesión para añadir libros al carrito', 'error');
      return;
    }

    this.carritoService.addToCart(libro).subscribe({
      next: () => {
        swal('Éxito', 'Libro añadido al carrito correctamente', 'success');
      },
      error: (err) => {
        console.error('Error al añadir al carrito:', err);
        swal('Error', 'No se pudo añadir el libro al carrito', 'error');
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
}