import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import swal from 'sweetalert2';
import { Libro } from '../libro/libro';
import { LibroService } from '../libro/libro.service';
import { Autor } from '../autor/autor';

@Component({
  selector: 'app-form-libro',
  standalone: false,
  templateUrl: './form-libro.component.html',
  styleUrl: './form-libro.component.css'
})
export class FormLibroComponent implements OnInit {

  public libro: Libro = new Libro();

  public errors: string[] = [];

  constructor(private libroService: LibroService, private router: Router, private activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.cargarLibro();

    if (!this.libro.autores) {
      this.libro.autores = [{ nombre: '', apellido: '', biografia: '' }];
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

  // Obtner libro por ID
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

  // Delete en libros.component.ts
}
