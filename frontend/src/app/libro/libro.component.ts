import { Component, OnInit } from '@angular/core';
import { LibroService } from './libro.service';
import { Libro } from './libro';
import { ActivatedRoute, Router } from '@angular/router';
import { tap } from 'rxjs';

@Component({
  selector: 'app-libro',
  standalone: false,
  templateUrl: './libro.component.html',
  styleUrls: ['./libro.component.css']
})
export class LibroComponent implements OnInit{

  libros : Libro[]= [];
  paginador: any;
  selectedFile: File | null = null;
  libroIdParaPortada: number | null = null;//libroSeleccionado

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
        tap(response => {
          console.log('LibrosComponent: tap 3');
          (response.content as Libro[]).forEach( libro => {
            console.log(libro.titulo);
          });
        })
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
  // Método para obtener el id y habilitar la subida de portada
  onUploadPortada(event: Event, libroId: number): void {
    event.stopPropagation(); // Prevenir la propagación del evento.
    this.libroIdParaPortada = libroId; // Lógica para mostrar el formulario de subida de portada.
    console.log('ID del libro para portada:', this.libroIdParaPortada);no
}

  // Método que se ejecuta cuando el usuario selecciona un archivo
  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  // Método para enviar la portada al backend
  onUpload(): void {
    if (this.selectedFile && this.libroIdParaPortada !== null) {
      this.libroService.uploadPortada(this.libroIdParaPortada, this.selectedFile).subscribe(response => {
        console.log('Portada cargada con éxito', response);
      }, error => {
        console.error('Error al cargar la portada', error);
      });
    } else {
      console.error('No se ha seleccionado ningún archivo o el libro no ha sido seleccionado');
    }
  }
}