import { Component, OnInit } from '@angular/core';
import { LibroService } from './libro.service';
import { Libro } from './libro';
import { ActivatedRoute, Router } from '@angular/router';
import { tap } from 'rxjs';
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

  constructor(private libroService: LibroService, private router: Router, private activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {

    // Obtenemos el numero de pagina del observable
    this.activatedRoute.paramMap.subscribe(params => {
      let page: number = +params.get('page')!;

      if(!page) {
        page = 0;
      }

      this.libroService.getLibros(page)
      .pipe(
        /*
        tap(response => {
          console.log('LibrosComponent: tap 3');
          (response.content as Libro[]).forEach( libro => {
            console.log(libro.titulo);
          });
        })*/
      ).subscribe(response => {
        this.libros = response.content as Libro[];
        this.paginador = response;
      });
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

  // Método para navegar a detalles con View Transitions
  getDetallesLibro(id: number): void {
    const imagenLibro = document.getElementById(`imagen-libro-${id}`);

    // Comprobar si el navegador soporta la API de View Transitions
    if (document.startViewTransition && imagenLibro) {
      // Iniciar la transición de la imagen
      document.startViewTransition(() => {
        // Aquí, la imagen es la que se moverá de una página a otra
        this.router.navigate(['/libro', id]);
      });
    } else {
      // Si el navegador no soporta View Transitions, se hace la navegación normalmente
      this.router.navigate(['/libro', id]);
    }
  }
}