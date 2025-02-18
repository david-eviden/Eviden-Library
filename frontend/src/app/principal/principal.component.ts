import { Component, OnInit } from '@angular/core';
import { Book } from './book';

@Component({
  selector: 'app-principal',
  standalone: false,
  templateUrl: './principal.component.html',
  styleUrl: './principal.component.css'
})
export class PrincipalComponent implements OnInit{
  ngOnInit(): void {
    this.slide();
  }
  books: Book[] = [
    { "title": "Libro 1", "author": "Autor 1", "image": "libroEjemplo.jpg" },
    { "title": "Libro 2", "author": "Autor 2", "image": "libroEjemplo.jpg" },
    { "title": "Libro 3", "author": "Autor 3", "image": "libroEjemplo.jpg" },
    { "title": "Libro 4", "author": "Autor 4", "image": "libroEjemplo.jpg" },
    { "title": "Libro 5", "author": "Autor 5", "image": "libroEjemplo.jpg" },
    { "title": "Libro 6", "author": "Autor 6", "image": "libroEjemplo.jpg" },
    { "title": "Libro 7", "author": "Autor 7", "image": "libroEjemplo.jpg" },
    { "title": "Libro 8", "author": "Autor 8", "image": "libroEjemplo.jpg" },
    { "title": "Libro 9", "author": "Autor 9", "image": "libroEjemplo.jpg" },
    { "title": "Libro 10", "author": "Autor 10", "image": "libroEjemplo.jpg" }
  ];

  constructor (){}

  fragmentos: Book[][] = [];

  slide(){
    const tamaño = 5;
    for (let i = 0; i < this.books.length; i += tamaño) {
      this.fragmentos.push(this.books.slice(i, i + tamaño));
    }
  }
  
}
