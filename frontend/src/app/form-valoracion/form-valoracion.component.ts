import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import swal from 'sweetalert2';
import { Valoracion } from '../valoracion/valoracion';
import { ValoracionService } from '../valoracion/valoracion.service';
import { Usuario } from '../usuario/usuario';
import { UsuarioService } from '../usuario/usuario.service';
import { Libro } from '../libro/libro';
import { LibroService } from '../libro/libro.service';

@Component({
  selector: 'app-form-valoracion',
  standalone: false,
  templateUrl: './form-valoracion.component.html',
  styleUrl: './form-valoracion.component.css'
})
export class FormValoracionComponent implements OnInit {

  public valoracion: Valoracion = new Valoracion();
  public errors: string[] = [];
  public usuarios: Usuario[] = [];
  public libros: Libro[] = [];

  constructor(
    private valoracionService: ValoracionService, 
    private usuarioService: UsuarioService,
    private libroService: LibroService,
    public router: Router, 
    private activatedRoute: ActivatedRoute,
  ) { }

  ngOnInit(): void {
    this.cargarValoracion();
    this.cargarUsuarios();
    this.cargarLibros();
  }

  // Cargar usuarios
  cargarUsuarios(): void {
    this.usuarioService.getUsuarios().subscribe(
      usuarios => {
        this.usuarios = usuarios;
      },
      error => {
        console.error('Error al cargar usuarios', error);
        swal('Error', 'No se pudieron cargar los usuarios', 'error');
      }
    );
  }

  // Cargar libros
  cargarLibros(): void {
    this.libroService.getLibrosNoPagin().subscribe(
      libros => {
        this.libros = libros;
      },
      error => {
        console.error('Error al cargar libros', error);
        swal('Error', 'No se pudieron cargar los libros', 'error');
      }
    );
  }

  // Crear valoración
  public create(): void {
    // Actualizar la fecha actual
    this.valoracion.fecha = new Date();
    
    this.valoracionService.create(this.valoracion).subscribe(
      // Si OK
      json => {
        this.router.navigate(['/valoraciones']);
        // Mensaje SweetAlert
        swal('Nueva valoración', `Valoración para el libro "${this.valoracion.libro?.titulo}" creada con éxito`, 'success');
      },

      // Si error
      err => {
        this.errors = err.error.errores as string[];
        console.error('Código del error (backend): ' + err.error.status);
        console.error(err.error.errores);
      }
    );
  }

  // Obtener valoración por ID
  public cargarValoracion(): void {
    this.activatedRoute.params.subscribe(params => {
      let id = params['id'];
      if(id) {
        this.valoracionService.getValoracion(id).subscribe(
          (valoracion) => {
            this.valoracion = valoracion;
          },
          error => {
            console.error('Error al cargar valoración', error);
            swal('Error', 'No se pudo cargar la valoración', 'error');
            this.router.navigate(['/valoraciones']);
          }
        );
      }
    });
  }

  // Update valoración por ID
  public update(): void {
    this.valoracionService.updateValoracion(this.valoracion).subscribe(
      // Si OK
      json => {
        this.router.navigate(['/valoraciones']);
        //Mensaje
        swal('Valoración Actualizada', `Valoración actualizada con éxito`, 'success');
      },

      // Si error
      err => {
        this.errors = err.error.errores as string[];
        console.error('Código del error (backend): ' + err.error.status);
        console.error(err.error.errores);
      }
    );
  }

  // Establecer puntuación
  setRating(rating: number): void {
    this.valoracion.puntuacion = rating;
  }

  // Al cambiar el libro
  onLibroChange(): void {
    if (this.valoracion.libro) {
      this.valoracion.libroDetalles = this.valoracion.libro.id;
    }
  }
}