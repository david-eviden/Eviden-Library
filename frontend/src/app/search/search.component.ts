import { Component, EventEmitter, HostListener, OnInit, Output } from '@angular/core';
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
    const cleanTerm = term.replace(/\s+/g, ' ').trim();
    
    if (!cleanTerm) {
      this.showDropdown = false;
      return;
    }
    
    this.searchTerm = cleanTerm;
    this.searchSubject.next(cleanTerm);
  }

  searchForDropdown(term: string): void {
    const cleanTerm = term.replace(/\s+/g, ' ').trim();
    
    if (cleanTerm === '') {
      this.showDropdown = false;
      return;
    }

    this.searchService.search(cleanTerm).subscribe({
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

  performFullSearch(): void {
    const cleanTerm = this.searchTerm.replace(/\s+/g, ' ').trim();
    
    if (!cleanTerm) {
      this.router.navigate(['/principal']);
      return;
    }

    this.showDropdown = false;

    this.searchService.search(cleanTerm).subscribe({
      next: (results) => {
        if (this.isEmptyResults(results)) {
          this.router.navigate(['/search-results'], {
            queryParams: {
              q: cleanTerm,
              noResults: true
            }
          });
        } else {
          this.router.navigate(['/search-results'], {
            queryParams: {
              q: cleanTerm
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

  @HostListener('document:click', ['$event'])
  onClickOutside(event: Event): void {
    const target = event.target as HTMLElement;
  
    if (target && target.closest && !target.closest('.search-container')) {
      this.showDropdown = false;
    }
  }
}