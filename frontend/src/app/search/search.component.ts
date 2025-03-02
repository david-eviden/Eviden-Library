import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { SearchService } from './search.service';
import { Router } from '@angular/router';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-search',
  standalone: false,
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent implements OnInit {
  searchTerm: string = '';
  searchResults: any = { libros: [], autores: [], generos: [] };
  showDropdown: boolean = false;
  private searchSubject = new Subject<string>();

  @Output() search = new EventEmitter<string>();

  constructor(
    private searchService: SearchService,
    private router: Router
  ){}

  ngOnInit() {
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(term => {
      if (term.trim()) {
        this.searchForDropdown(term);
      } else {
        this.showDropdown = false;
      }
    });
  }

  onSearchInput(event: any): void {
    const term = event.target.value;
    if (!term || term.trim() === '') {
      this.showDropdown = false;
      return;
    }
    this.searchTerm = term;
    this.searchSubject.next(term);
  }

  // Nueva función para búsqueda en el dropdown
  searchForDropdown(term: string): void {
    const encodedTerm = encodeURIComponent(term.trim());
    this.searchService.search(encodedTerm).subscribe({
      next: (results) => {
        this.searchResults = results;
        this.showDropdown = true;
      },
      error: (error) => {
        console.error('Error en la búsqueda: ', error);
        this.showDropdown = false;
      }
    });
  }

  // Función para seleccionar un resultado del dropdown
  selectResult(type: string, item: any): void {
    this.showDropdown = false;
    switch(type) {
      case 'libro':
        this.router.navigate(['/libro', item.id]);
        break;
      case 'autor':
        this.router.navigate(['/autores', item.id]);
        break;
      case 'genero':
        this.router.navigate(['/generos', item.id]);
        break;
    }
  }

  // Función para realizar la búsqueda completa
  performFullSearch(): void {
    if (!this.searchTerm || this.searchTerm.trim() === '') {
      this.router.navigate(['/principal']);
      return;
    }

    const encodedTerm = encodeURIComponent(this.searchTerm.trim());
    this.showDropdown = false;

    this.searchService.search(encodedTerm).subscribe({
      next: (results) => {
        if (this.isEmptyResults(results)) {
          this.router.navigate(['/search-results'], {
            queryParams: {
              q: encodedTerm,
              noResults: true
            }
          });
        } else {
          this.router.navigate(['/search-results'], {
            queryParams: {
              q: encodedTerm
            },
            state: { results }
          });
        }
      },
      error: (error) => {
        console.error('Error en la búsqueda: ', error);
      }
    });
  }

  private isEmptyResults(results: any): boolean {
    return (!results.libros || results.libros.length === 0) &&
           (!results.autores || results.autores.length === 0) &&
           (!results.generos || results.generos.length === 0);
  }

  // Cerrar el dropdown cuando se hace clic fuera
  onClickOutside(event: Event): void {
    if (!(event.target as HTMLElement).closest('.search-container')) {
      this.showDropdown = false;
    }
  }
}