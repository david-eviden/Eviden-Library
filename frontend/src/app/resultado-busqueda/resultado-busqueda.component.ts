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
export class ResultadoBusquedaComponent implements OnInit{
  results: any = {libros: [], autores: [], generos: []};
  searchTerm: string = '';

  constructor(
    private route: ActivatedRoute,
    private searchService: SearchService,
    private carritoService: CarritoService,
    private router: Router
  ){}

  ngOnInit(): void {
      this.route.queryParams.subscribe(params => {
        this.searchTerm = params['q'];
        if (this.searchTerm) {
          // Realizamos la búsqueda con el término proporcionado
          this.searchService.search(this.searchTerm).subscribe({
            next: (data) => {
              this.results = data; // Asignamos los resultados obtenidos
            },
            error: (error) => {
              console.error('Error al obtener resultados: ', error); // Manejo de errores
            }
          });
        }
      })
  }

  getDetallesLibro(libroId: number): void {
    this.router.navigate(['/libro', libroId]);
  }

  addToCarrito(libro: Libro) {
    this.carritoService.getCarritos().subscribe((carritos: Carrito[]) => {
      // Check if the book is in any of the carts
      const isBookInCart = carritos.some(carrito => 
        carrito.detalles.some(detalle => detalle.libro.id === libro.id)
      );

      if (isBookInCart) {
        console.log('Libro ya está en el carrito');
      } else {
        // If the book is not in the cart, add it to the cart
        this.carritoService.addToCarrito(libro);
        console.log('Libro añadido al carrito');
      }
    });
  }
}
