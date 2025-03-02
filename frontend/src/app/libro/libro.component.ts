import { Component, OnInit } from '@angular/core';
import { LibroService } from './libro.service';
import { Libro } from './libro';
import { ActivatedRoute, NavigationStart, Router } from '@angular/router';
import { filter, tap } from 'rxjs';
import swal from 'sweetalert2';

@Component({
  selector: 'app-libro',
  standalone: false,
  templateUrl: './libro.component.html',
  styleUrls: ['./libro.component.css']
})
export class LibroComponent implements OnInit{

  libros : Libro[]= [];
  paginador: any;
  animationState = "in"
  pageSizes: number[] = [3, 6, 9, 12]; // Opciones de tamaño de página
  currentPageSize: number = 6; // Tamaño de página por defecto
  currentPage: number = 0; // Página actual

  constructor(private libroService: LibroService, private router: Router, private activatedRoute: ActivatedRoute) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationStart)
    ).subscribe(() => {
      this.handleViewTransition();
    });
  }

  handleViewTransition(): void {
    if ('startViewTransition' in document) {
      // Use the modern View Transitions API if available
      (document as any).startViewTransition();
    }
  }

  ngOnInit(): void {
    // Obtenemos el numero de pagina del observable
    this.activatedRoute.paramMap.subscribe(params => {
      let page: number = +params.get('page')!;

      if(!page) {
        page = 0;
      }
      
      this.currentPage = page;
      this.cargarLibros();
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
  cargarLibros(): void {
    this.libroService.getLibrosConTamanio(this.currentPage, this.currentPageSize)
    .subscribe(response => {
      this.libros = response.content as Libro[];
      this.paginador = response;
    });
  }
  
  // Método para cambiar el tamaño de la página
  cambiarTamanioPagina(event: any): void {
    this.currentPageSize = +event.target.value;
    this.currentPage = 0; // Volvemos a la primera página al cambiar el tamaño
    this.cargarLibros();
    this.router.navigate(['/libros/page', 0]); // Actualizamos la URL
  }

}