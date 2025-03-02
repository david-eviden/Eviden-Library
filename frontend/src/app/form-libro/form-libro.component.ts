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

  public libro: Libro = new Libro();
  public errors: string[] = [];
  autores: Autor[] = [];
  generos: Genero[] = [];
  autorSeleccionado: Autor = new Autor();
  generoSeleccionado: Genero = new Genero();

  constructor(
    private libroService: LibroService, 
    public router: Router, 
    private activatedRoute: ActivatedRoute,
    private autorService: AutorService,
    private generoService: GeneroService,
  ) { }

  ngOnInit(): void {
    // Inicializar ids como 0 para que se seleccionen las opciones por defecto
    this.autorSeleccionado.id = 0;
    this.generoSeleccionado.id = 0;
    
    this.cargarLibro();
    this.cargarAutores();
    this.cargarGeneros();
    
    // Inicializar arrays si están vacíos
    if (!this.libro.autores) {
      this.libro.autores = [];
    }

    if (!this.libro.generos) {
      this.libro.generos = [];
    }
  }

  // Crear libro
  public create(): void {
    // Verificar si se seleccionaron las opciones predeterminadas
    if (this.autorSeleccionado.id === 0 || this.generoSeleccionado.id === 0) {
      if (this.autorSeleccionado.id === 0) {
        this.errors.push("Debe seleccionar un autor válido");
      }
      if (this.generoSeleccionado.id === 0) {
        this.errors.push("Debe seleccionar un género válido");
      }
      return; // No continuar con la creación
    }

    // Limpiar errores previos
    this.errors = [];

    // Asegurarse de que el autor seleccionado se añada al libro
    if (this.autorSeleccionado && this.autorSeleccionado.id && this.autorSeleccionado.id !== 0) {
      // Verificar si el autor ya está en la lista
      const autorExistente = this.libro.autores.find(a => a.id === this.autorSeleccionado.id);
      if (!autorExistente) {
        this.libro.autores.push(this.autorSeleccionado);
      }
    }

    // Asegurarse de que el género seleccionado se añada al libro
    if (this.generoSeleccionado && this.generoSeleccionado.id && this.generoSeleccionado.id !== 0) {
      // Verificar si el género ya está en la lista
      const generoExistente = this.libro.generos.find(g => g.id === this.generoSeleccionado.id);
      if (!generoExistente) {
        this.libro.generos.push(this.generoSeleccionado);
      }
    }

    this.libroService.create(FormData).subscribe(
      // Si OK
      json => {
        this.router.navigate(['/libros']);
        // Mensaje SweetAlert
        swal('Nuevo libro', `Libro ${json.libro.titulo} creado con éxito`, 'success');
      },

      // Si error
      err => {
        this.errors = err.error.errores as string[];
        console.error('Código del error (backend): ' + err.error.status);
        console.error(err.error.errores);
      }
    );
  }

  // portada
  onFileChange(event: any): void {
    const file = event.target.files[0];  // Obtener el archivo seleccionado
    if (file) {
      // Establecer el tipo de imagen
      this.libro.tipoImagen = file.type; // Almacenar el tipo de imagen (por ejemplo, "image/jpeg")

      // Leer el archivo como ArrayBuffer
      const reader = new FileReader();
      reader.readAsArrayBuffer(file);

      reader.onload = () => {
        // Convertir el ArrayBuffer a un Uint8Array (representación en bytes)
        const byteArray = new Uint8Array(reader.result as ArrayBuffer);
        this.libro.imagen = byteArray;  // Almacenar los bytes en el libro
      };
    }
  }

  // Obtener libro por ID
  public cargarLibro(): void {
    this.activatedRoute.params.subscribe(params => {
      let id = params['id'];
      if(id) {
        this.libroService.getLibro(id).subscribe(
          (libro) => {
            this.libro = libro;
            // Si el libro tiene autores, seleccionar el primero
            if (this.libro.autores && this.libro.autores.length > 0) {
              this.autorSeleccionado = this.libro.autores[0];
            }
            // Si el libro tiene géneros, seleccionar el primero
            if (this.libro.generos && this.libro.generos.length > 0) {
              this.generoSeleccionado = this.libro.generos[0];
            }
          }
        );
      }
    });
  }

  // Update libro por ID
  public update(): void {
    // Verificar si se seleccionaron las opciones predeterminadas
    if (this.autorSeleccionado.id === 0 || this.generoSeleccionado.id === 0) {
      if (this.autorSeleccionado.id === 0) {
        this.errors.push("Debe seleccionar un autor válido");
      }
      if (this.generoSeleccionado.id === 0) {
        this.errors.push("Debe seleccionar un género válido");
      }
      return; // No continuar con la actualización
    }

    // Limpiar errores previos
    this.errors = [];

    // Asegurarse de que el autor seleccionado se añada al libro
    if (this.autorSeleccionado && this.autorSeleccionado.id && this.autorSeleccionado.id !== 0) {
      // Verificar si el autor ya está en la lista
      const autorExistente = this.libro.autores.find(a => a.id === this.autorSeleccionado.id);
      if (!autorExistente) {
        this.libro.autores.push(this.autorSeleccionado);
      }
    }

    // Asegurarse de que el género seleccionado se añada al libro
    if (this.generoSeleccionado && this.generoSeleccionado.id && this.generoSeleccionado.id !== 0) {
      // Verificar si el género ya está en la lista
      const generoExistente = this.libro.generos.find(g => g.id === this.generoSeleccionado.id);
      if (!generoExistente) {
        this.libro.generos.push(this.generoSeleccionado);
      }
    }

    this.libroService.updateLibro(this.libro).subscribe(
      // Si OK
      json => {
        this.router.navigate(['/libros']);
        //Mensaje
        swal('Libro Actualizado', `Libro ${json.libro.titulo} actualizado con éxito`, 'success');
      },

      // Si error
      err => {
        this.errors = err.error.errores as string[];
        console.error('Código del error (backend): ' + err.error.status);
        console.error(err.error.errores);
      }
    );
  }

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
        this.generos = generos;
      },
      (err) => {
        console.error("Error al cargar los géneros", err);
      },
    );
  }
  
  // Seleccionar autor
  public seleccionarAutor(event: any): void {
    const autorId = event.target.value;
    if (autorId === 'crearAutor') {
      this.router.navigate(['/autor/form']);
    } else if (autorId === '0') {
      // Cuando se selecciona la opción predeterminada, reiniciar el autor seleccionado
      this.autorSeleccionado = new Autor();
      this.autorSeleccionado.id = 0;
    } else {
      this.autorSeleccionado = this.autores.find(autor => autor.id.toString() === autorId) || new Autor();
    }
  }

  // Seleccionar género
  public seleccionarGenero(event: any): void {
    const generoId = event.target.value;
    if (generoId === 'crearGenero') {
      this.router.navigate(['/genero/form']);
    } else if (generoId === '0') {
      // Cuando se selecciona la opción predeterminada, reiniciar el género seleccionado
      this.generoSeleccionado = new Genero();
      this.generoSeleccionado.id = 0;
    } else {
      this.generoSeleccionado = this.generos.find(genero => genero.id.toString() === generoId) || new Genero();
    }
  }
}
