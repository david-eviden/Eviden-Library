import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SearchService } from '../search/search.service';
import { CarritoService } from '../carrito/carrito.service';
import { Libro } from '../libro/libro';
import { Carrito } from '../carrito/carrito';

@Component({
  selector: 'app-resultado-busqueda',
  standalone: false,
  templateUrl: './resultado-busqueda.component.html',
  styleUrl: './resultado-busqueda.component.css'
})
export class ResultadoBusquedaComponent implements OnInit {
  results: any = {libros: [], autores: [], generos: []};
  searchTerm: string = '';
  loading: boolean = true;
  noResults: boolean = false;
  searchType: string = 'all'; // 'all', 'autor', 'genero'
  librosRelacionados: Libro[] = [];

  constructor(
    private route: ActivatedRoute,
    private searchService: SearchService,
    private carritoService: CarritoService,
    private router: Router
  ){}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.searchTerm = params['q'];
      this.searchType = this.detectSearchType(this.searchTerm);
      
      if (!this.searchTerm || this.searchTerm.trim() === '') {
        this.router.navigate(['/principal']);
        return;
      }

      this.loading = true;
      this.searchService.search(this.searchTerm).subscribe({
        next: (data) => {
          this.results = data;
          this.loading = false;
          this.noResults = this.isEmptyResults(data);

          // Si es búsqueda por autor o género, cargar libros relacionados
          if (this.searchType === 'autor' && this.results.autores.length > 0) {
            this.loadLibrosByAutor(this.results.autores[0].id);
          } else if (this.searchType === 'genero' && this.results.generos.length > 0) {
            this.loadLibrosByGenero(this.results.generos[0].id);
          }
        },
        error: (error) => {
          console.error('Error al obtener resultados: ', error);
          this.loading = false;
          this.noResults = true;
        }
      });
    });
  }

  private detectSearchType(term: string): string {
    const termLower = term.toLowerCase();
    if (termLower.includes('autor:') || termLower.includes('por:') || 
        termLower.includes('escrito por:') || termLower.includes('de:')) {
      return 'autor';
    }
    if (termLower.includes('genero:') || termLower.includes('género:') || 
        termLower.includes('categoria:') || termLower.includes('categoría:')) {
      return 'genero';
    }
    return 'all';
  }

  private loadLibrosByAutor(autorId: number): void {
    this.searchService.getLibrosByAutor(autorId).subscribe(
      libros => this.librosRelacionados = libros
    );
  }

  private loadLibrosByGenero(generoId: number): void {
    this.searchService.getLibrosByGenero(generoId).subscribe(
      libros => this.librosRelacionados = libros
    );
  }

  private isEmptyResults(results: any): boolean {
    return (!results.libros || results.libros.length === 0) &&
           (!results.autores || results.autores.length === 0) &&
           (!results.generos || results.generos.length === 0);
  }

  getDetallesLibro(libroId: number): void {
    this.router.navigate(['/libro', libroId]);
  }

  addToCarrito(libro: Libro) {
    this.carritoService.getCarritos().subscribe((carritos: Carrito[]) => {
      const isBookInCart = carritos.some(carrito => 
        carrito.detalles.some(detalle => detalle.libro.id === libro.id)
      );

      if (isBookInCart) {
        console.log('Libro ya está en el carrito');
      } else {
        this.carritoService.addToCarrito(libro);
        console.log('Libro añadido al carrito');
      }
    });
  }
}