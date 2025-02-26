import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Libro } from '../libro/libro';
import { SearchService } from './search.service';
import { debounce, debounceTime, distinctUntilChanged, Observable, switchMap, tap } from 'rxjs';
import { FormControl, ReactiveFormsModule } from '@angular/forms'; 

@Component({
  selector: 'app-search',
  standalone: false,
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent implements OnInit {
  searchControl = new FormControl('');
  librosFiltrados: Observable<Libro[]> | undefined;
  isLoading = false;

  constructor(private searchService: SearchService) {}

  ngOnInit(): void {
    this.librosFiltrados = this.searchControl.valueChanges.pipe(
      //300ms después de que el usuario djee de escribir
      debounceTime(300),
      //continua si el valor ha cambiado
      distinctUntilChanged(),
      //inidcador de carga
      tap(()=>this.isLoading = true),

      switchMap(value = > {
        /* if (!value || value.length < 2) {
          // No busca si hay menos de 2 caracteres
          this.isLoading = false;
          return of([]);
        } */
        return this.searchService.buscarLibros(value).pipe(
          catchError(() => {
            this.isLoading = false;
            return of([]);
          })
        );
      }),
      tap(() => this.isLoading = false) // Desactiva el indicador de carga
    );
  }

}
