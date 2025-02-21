import { Component, OnInit } from '@angular/core';
import { detallesCarrito } from './detalles-carrito';
import { DetallesCarritoService } from './detalles-carrito.service';

@Component({
  selector: 'app-detalles-carrito',
  standalone: false,
  templateUrl: './detalles-carrito.component.html',
  styleUrl: './detalles-carrito.component.css'
})
export class DetallesCarritoComponent implements OnInit{
  detallesCarrito: detallesCarrito[] = [];
  constructor(private detallesCarritoService: DetallesCarritoService) {}

  ngOnInit(): void {
    this.detallesCarritoService.getdetallesCarrito().subscribe(
      (detallesCarritos: detallesCarrito[]) => {
        this.detallesCarrito = detallesCarritos;
        console.log('Detalles de los carritos recibidos:', detallesCarritos); 
      },
      error => {
        console.error('Error al obtener los detalles de los carritos', error);  // Muestra errores si los hay
      }
    )
  };
}
