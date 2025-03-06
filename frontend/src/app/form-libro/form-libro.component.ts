import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import swal from 'sweetalert2';
import { Libro } from '../libro/libro';
import { LibroService } from '../libro/libro.service';
import { Autor } from '../autor/autor';
import { AutorService } from '../autor/autor.service';
import { GeneroService } from '../generos/generos.service';
import { Genero } from '../generos/generos';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

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

  libroForm: FormGroup;

  constructor(
    private libroService: LibroService, 
    public router: Router, 
    private activatedRoute: ActivatedRoute,
    private autorService: AutorService,
    private generoService: GeneroService,
    private fb: FormBuilder
  ) { 
    this.libroForm = this.fb.group({
      titulo: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(25)]],
      descripcion: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(250)]],
      precio: ['', [Validators.required]],
      stock: ['', [Validators.required]],
      imagen: [null, Validators.required],
      autor: ['', Validators.required],
      genero: ['', Validators.required]
    });
  }

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

  // portada
  onFileChange(event: any): void {
    const file = event.target.files[0];  // Obtener el archivo seleccionado
    if (file) {
      // Validar el tipo de archivo
      if (!file.type.startsWith('image/')) {
        swal('Error', 'El archivo debe ser una imagen', 'error');
        event.target.value = ''; // Limpiar el input
        return;
      }

      // Validar el tamaño del archivo (máximo 5MB)
      if (file.size > 5 * 1024 * 1024) {
        swal('Error', 'La imagen no debe superar los 5MB', 'error');
        event.target.value = ''; // Limpiar el input
        return;
      }

      // Establecer el tipo de imagen
      this.libro.tipoImagen = file.type;

      // Leer el archivo como ArrayBuffer
      const reader = new FileReader();
      reader.onload = () => {
        // Convertir el ArrayBuffer a un Uint8Array (representación en bytes)
        const byteArray = new Uint8Array(reader.result as ArrayBuffer);
        this.libro.imagen = byteArray;  // Almacenar los bytes en el libro
        this.libroForm.patchValue({
          imagen: byteArray
        });
      };

      reader.readAsArrayBuffer(file);
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
            // Actualizar el formulario con los datos del libro
            this.libroForm.patchValue({
              titulo: libro.titulo,
              descripcion: libro.descripcion,
              precio: libro.precio,
              stock: libro.stock,
              imagen: libro.imagen,
              autor: libro.autores?.[0]?.id || '',
              genero: libro.generos?.[0]?.id || ''
            });
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

  // Crear libro
  public create(): void {
    if (this.libroForm.valid) {
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

      // Actualizar el libro con los valores del formulario
      this.libro.titulo = this.libroForm.get('titulo')?.value;
      this.libro.descripcion = this.libroForm.get('descripcion')?.value;
      this.libro.precio = this.libroForm.get('precio')?.value;
      this.libro.stock = this.libroForm.get('stock')?.value;

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

      // Crear una nueva instancia de FormData
      const formData = new FormData();

      // Agregar los datos del libro al FormData
      formData.append('titulo', this.libro.titulo);
      formData.append('precio', this.libro.precio.toString());
      formData.append('stock', this.libro.stock.toString());
      formData.append('descripcion', this.libro.descripcion);

      // Agregar autores y géneros
      this.libro.autores.forEach((autor) => {
        formData.append('autores', autor.id.toString());
      });

      this.libro.generos.forEach((genero) => {
        formData.append('generos', genero.id.toString());
      });

      // Agregar la portada (imagen) si existe
      if (this.libro.imagen) {
        const blob = new Blob([this.libro.imagen], { type: this.libro.tipoImagen });
        formData.append('imagen', blob, 'portada.jpg');
      }

      this.libroService.create(formData).subscribe(
        // Si OK
        json => {
          this.router.navigate(['/libros']);
          // Mensaje SweetAlert
          swal('Nuevo libro', `Libro ${json.libro.titulo} creado con éxito`, 'success');
        },

        // Si error
        err => {
          if (err.error && err.error.errores) {
            this.errors = err.error.errores as string[];
            console.error('Código del error (backend): ' + err.error.status);
            console.error(err.error.errores);
          } else {
            this.errors = ['Error de comunicación con el servidor'];
            console.error('Error general:', err);
          }
        }
      );
    } else {
      // Marcar todos los campos como touched para mostrar los errores
      Object.keys(this.libroForm.controls).forEach(key => {
        const control = this.libroForm.get(key);
        control?.markAsTouched();
      });
    }
  }

  // Update libro por ID
  public update(): void {
    if (this.libroForm.valid) {
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

      // Actualizar el libro con los valores del formulario
      this.libro.titulo = this.libroForm.get('titulo')?.value;
      this.libro.descripcion = this.libroForm.get('descripcion')?.value;
      this.libro.precio = this.libroForm.get('precio')?.value;
      this.libro.stock = this.libroForm.get('stock')?.value;

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

      // Crear una nueva instancia de FormData
      const formData = new FormData();

      // Agregar los datos del libro al FormData
      formData.append('id', this.libro.id.toString());
      formData.append('titulo', this.libro.titulo);
      formData.append('precio', this.libro.precio.toString());
      formData.append('stock', this.libro.stock.toString());
      formData.append('descripcion', this.libro.descripcion);

      // Agregar autores y géneros
      this.libro.autores.forEach((autor) => {
        formData.append('autores', autor.id.toString());
      });

      this.libro.generos.forEach((genero) => {
        formData.append('generos', genero.id.toString());
      });

      // Agregar la portada (imagen) si existe
      if (this.libro.imagen) {
        const blob = new Blob([this.libro.imagen], { type: this.libro.tipoImagen });
        formData.append('imagen', blob, 'portada.jpg');
      }

      this.libroService.updateLibro(formData).subscribe(
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
    } else {
      // Marcar todos los campos como touched para mostrar los errores
      Object.keys(this.libroForm.controls).forEach(key => {
        const control = this.libroForm.get(key);
        control?.markAsTouched();
      });
    }
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