import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { SearchService } from './search.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-search',
  standalone: false,
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent {

  searchTerm: string = '';
  selectedTypes: string[] = ['libro'];//Por defecto busco libros

  searchOptions = [
    {value:'libro', label: 'Libros'},
    {value:'usuario', label: 'Usuarios'},
    {value:'genero', label: 'Géneros'}
  ];

  @Output() search = new EventEmitter<string>();

  constructor(
    private searchService: SearchService,
    private router: Router
  ){}

  toggleSearchType(type: string): void{
    const index = this.selectedTypes.indexOf(type);
    if (index === -1){
      this.selectedTypes.push(type);
    }else{
      this.selectedTypes = this.selectedTypes.filter(t => t !== type);
    }
  }

  isSelected(type:string): boolean{
    return this.selectedTypes.includes(type);
  }
  
  onSearch(term:string): void {
    if(term.trim()){
      this.searchService.search(term).subscribe({
        next: (results) => {
          console.log('Resultados:', results);
          // Navegar a la pagina de resultados
          this.router.navigate(['/search-results'],{
            queryParams: {q: term},
            state: {results}
          });
        },
        error: (error) => {
          console.error('Error en la bsuqueda: ', error);
        }
      })
    }

  }
    
}
