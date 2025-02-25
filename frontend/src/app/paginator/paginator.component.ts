import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';

@Component({
  selector: 'paginator-nav',
  standalone: false,
  templateUrl: './paginator.component.html',
  styleUrl: './paginator.component.css'
})
export class PaginatorComponent implements OnInit, OnChanges {

  @Input()
  paginador: any;

  paginas: number[] = [];

  desde: number = 0;
  hasta: number = 0;

  constructor() {}

  ngOnInit(): void {
    
  }

  ngOnChanges(): void {
    const pagesToShow = 5;  // Número máximo de páginas a mostrar
    const currentPage = this.paginador.number;
    const totalPages = this.paginador.totalPages;
  
    // Si hay más de 5 páginas
    if (totalPages > pagesToShow) {
      // Calcular el rango de páginas a mostrar
      this.desde = Math.max(1, currentPage - 2);  // Queremos 2 páginas antes y 2 después
      this.hasta = Math.min(totalPages, currentPage + 2);
  
      // Ajustar si el rango se sale de los límites
      if (currentPage <= 3) {
        this.hasta = Math.min(pagesToShow, totalPages);  // Si estamos cerca del principio
      } else if (currentPage + 2 >= totalPages) {
        this.desde = Math.max(1, totalPages - pagesToShow + 1);  // Si estamos cerca del final
      }
  
      // Crear el arreglo de páginas visibles
      this.paginas = new Array(this.hasta - this.desde + 1).fill(0).map((_valor, indice) => indice + this.desde);
    } else {
      // Si hay 5 o menos páginas, simplemente mostrar todas
      this.paginas = new Array(totalPages).fill(0).map((_valor, indice) => indice + 1);
    }
  }
}
