import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import swal from 'sweetalert2';
import { Libro } from '../libro/libro';
import { LibroService } from '../libro/libro.service';
import { Autor } from '../autor/autor';
import { AutorService } from '../autor/autor.service';
import { GeneroService } from '../generos/generos.service';
import { Genero } from '../generos/generos';

@Component({
  selector: 'app-form-libro',
  standalone: false,
  templateUrl: './form-libro.component.html',
  styleUrl: './form-libro.component.css'
})
export class FormLibroComponent implements OnInit {

  public libro: any = { id: 0, autores: [{ nombre: '' }] };
  public errors: string[] = [];
  autores: Autor[] = [];
  autorSeleccionadoId = ""
  generos: Genero[] = [];
  

  constructor(
    private libroService: LibroService, 
    public router: Router, 
    private activatedRoute: ActivatedRoute,
    private autorService: AutorService,
    private generoService: GeneroService,
  ) { }

  ngOnInit(): void {
    this.cargarLibro()
    this.cargarAutores()
    this.cargarGeneros()
    if (!this.libro.autores) {
      this.libro.autores = [{ id: 0, nombre: "", biografia: "" }]
    }

    if (!this.libro.generos) {
      this.libro.generos = [{ id: 0, nombre: "", descripcion: "" }]
    }
  }

  // Crear libro
  public create(): void {
    this.libroService.create(this.libro).subscribe(

      // Si OK
      json => {
        this.router.navigate(['/libros'])
        // Mensaje SweetAlert
        swal('Nuevo libro', `Libro ${json.libro.nombre} creado con éxito`, 'success');
      },

      // Si error
      err => {
        this.errors = err.error.errores as string[];
        console.error('Código del error (backend): ' + err.error.status);
        console.error(err.error.errores);
      }
    );
  }

  // Obtener libro por ID
  public cargarLibro(): void {
    this.activatedRoute.params.subscribe(params => {
      let id = params['id'];
      if(id) {
        this.libroService.getLibro(id).subscribe(
          (libro) => this.libro = libro
        );
      }
    });
  }

  // Update libro por ID
  public update(): void {
    this.libroService.updateLibro(this.libro).subscribe(

      // Si OK
      json => {
        this.router.navigate(['/libros'])
        //Mensaje
        swal('Libro Actualizado', `Libro ${json.libro.nombre} actualizado con éxito`, 'success');
      },

      // Si error
      err => {
        this.errors = err.error.errores as string[];
        console.error('Código del error (backend): ' + err.error.status);
        console.error(err.error.errores);
      }
    );
  }

  // Delete en detalles-libro.component.ts

  // Obtener autores
  public cargarAutores(): void {
    this.autorService.getAutores().subscribe(
      (autores) => {
        this.autores = autores;
      },
      (err) => {
        console.error('Error al cargar los autores', err);
      }
    );
  }

  // Obtener géneros
  public cargarGeneros(): void {
    this.generoService.getGeneros().subscribe(
      (generos) => {
        this.generos = generos
      },
      (err) => {
        console.error("Error al cargar los géneros", err)
      },
    )
  }
  
  // Crear autor desde formulario
  opcionCrearAutor() {
    if (this.libro.autores[0].nombre === 'crearAutor') {
      setTimeout(() => {
        this.router.navigate(['/autor/form']);
      }, 0);
      this.libro.autores[0].nombre = '';  
    }
  }

  // Crear género desde formulario
  opcionCrearGenero() {
    if (this.libro.generos[0].nombre === "crearGenero") {
      setTimeout(() => {
        this.router.navigate(["/genero/form"])
      }, 0)
      this.libro.generos[0].nombre = ""
    }
  }
}
