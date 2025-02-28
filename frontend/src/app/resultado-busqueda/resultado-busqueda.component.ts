import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SearchService } from '../search/search.service';

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
    private searchService: SearchService
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
}
