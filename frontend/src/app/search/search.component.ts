import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Search } from './search';

@Component({
  selector: 'app-search',
  standalone: false,
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent implements OnInit {
  ngOnInit(): void {
    throw new Error('Method not implemented.');
  }
  searchTerm: string = '';
  selectedTypes: string[] = ['libro'];//Por defecto busco libros

  searchOptions = [
    {value:'libro', label: 'Libros'},
    {value:'usuario', label: 'Usuarios'},
    {value:'genero', label: 'Géneros'}
  ];

  @Output() search = new EventEmitter<Search>();

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
  onSearch(): void{
    if(this.searchTerm.trim() && this.selectedTypes.length > 0){
      this.search.emit({
        term: this.searchTerm,
        types: this.selectedTypes
      });
    }
  }
}
