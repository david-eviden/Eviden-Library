import { Component, OnInit } from '@angular/core';
import { Book } from './book';
import { Router } from '@angular/router';

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
    { id: 1,  "title": "Libro 1", "author": "Autor 1", "image": "libroEjemplo.jpg" },
    { id: 1,  "title": "Libro 2", "author": "Autor 2", "image": "libroEjemplo.jpg" },
    { id: 1,  "title": "Libro 3", "author": "Autor 3", "image": "libroEjemplo.jpg" },
    { id: 1,  "title": "Libro 4", "author": "Autor 4", "image": "libroEjemplo.jpg" },
    { id: 1,  "title": "Libro 5", "author": "Autor 5", "image": "libroEjemplo.jpg" },
    { id: 1,  "title": "Libro 6", "author": "Autor 6", "image": "libroEjemplo.jpg" },
    { id: 1,  "title": "Libro 7", "author": "Autor 7", "image": "libroEjemplo.jpg" },
    { id: 1,  "title": "Libro 8", "author": "Autor 8", "image": "libroEjemplo.jpg" },
    { id: 1,  "title": "Libro 9", "author": "Autor 9", "image": "libroEjemplo.jpg" },
    { id: 1,  "title": "Libro 10", "author": "Autor 10", "image": "libroEjemplo.jpg" }
  ];

  constructor (private router: Router){}

  fragmentos: Book[][] = [];

  slide(){
    const tamaño = 5;
    for (let i = 0; i < this.books.length; i += tamaño) {
      this.fragmentos.push(this.books.slice(i, i + tamaño));
    }
  }

  verDetallesLibro(bookId: number) {
    this.router.navigate(['/libro/', bookId]);
  }
  
}
