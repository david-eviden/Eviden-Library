import { Component, OnInit } from '@angular/core';
import { LibroService } from '../libro/libro.service';
import { Router } from '@angular/router';
import swal from 'sweetalert2';
import { Libro } from '../libro/libro';
import { Genero } from '../generos/generos';

@Component({
  selector: 'app-seccion-categoria',
  standalone: false,
  templateUrl: './seccion-categoria.component.html',
  styleUrl: './seccion-categoria.component.css'
})
export class SeccionCategoriaComponent implements OnInit {
  // Los géneros que quieres mostrar
  generosMostrados: string[] = ['Fantasía', 'Romance', 'Misterio'];
  
  // Estructura para almacenar libros por género
  librosPorGenero: Map<string, Libro[]> = new Map();
  
  // Número máximo de libros a mostrar por género/columna
  maxLibrosPorColumna: number = 4;
  
  // Variables para manejar la carga
  loading: boolean = true;
  error: boolean = false;

  constructor(
    private libroService: LibroService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarLibros();
  }

  cargarLibros(): void {
    this.loading = true;
    
    this.libroService.getLibrosNoPagin().subscribe({
      next: (libros) => {
        this.procesarLibrosPorGenero(libros);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar los libros:', err);
        swal('Error', 'No se pudieron cargar los libros. Por favor, intente más tarde.', 'error');
        this.loading = false;
        this.error = true;
      }
    });
  }

  procesarLibrosPorGenero(libros: Libro[]): void {
    // Inicializar el mapa para cada género que queremos mostrar
    this.generosMostrados.forEach(genero => {
      this.librosPorGenero.set(genero, []);
    });
    
    // Clasificar los libros según sus géneros
    libros.forEach(libro => {
      if (libro.generos && libro.generos.length > 0) {
        libro.generos.forEach((genero: Genero) => {
          // Verificar si es uno de los géneros que queremos mostrar
          if (this.generosMostrados.includes(genero.nombre)) {
            const librosGenero = this.librosPorGenero.get(genero.nombre) || [];
            
            // Evitar duplicados y limitar a maxLibrosPorColumna
            if (!librosGenero.find(l => l.id === libro.id) && librosGenero.length < this.maxLibrosPorColumna) {
              librosGenero.push(libro);
              this.librosPorGenero.set(genero.nombre, librosGenero);
            }
          }
        });
      }
    });
    
    // Si queremos seleccionar 4 libros aleatorios por género en lugar de los primeros 4
    this.generosMostrados.forEach(genero => {
      const todosLosLibrosGenero = this.librosPorGenero.get(genero) || [];
      if (todosLosLibrosGenero.length > this.maxLibrosPorColumna) {
        const librosAleatorios = this.seleccionarLibrosAleatorios(todosLosLibrosGenero, this.maxLibrosPorColumna);
        this.librosPorGenero.set(genero, librosAleatorios);
      }
    });
  }
  
  seleccionarLibrosAleatorios(libros: Libro[], cantidad: number): Libro[] {
    const librosCopiados = [...libros];
    const resultado: Libro[] = [];
    
    // Seleccionar aleatoriamente libros hasta alcanzar la cantidad deseada o agotar la lista
    while (resultado.length < cantidad && librosCopiados.length > 0) {
      const indiceAleatorio = Math.floor(Math.random() * librosCopiados.length);
      resultado.push(librosCopiados.splice(indiceAleatorio, 1)[0]);
    }
    
    return resultado;
  }

  verDetallesLibro(libroId: number): void {
    this.router.navigate(['/libro', libroId]);
  }
  
  // Método útil para el template para obtener los libros de un género específico
  getLibrosPorGenero(genero: string): Libro[] {
    return this.librosPorGenero.get(genero) || [];
  }
}