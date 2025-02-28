import { Component, OnInit } from '@angular/core';
import { Carrito } from './carrito';
import { CarritoService } from './carrito.service';

@Component({
  selector: 'app-carrito',
  standalone: false,
  templateUrl: './carrito.component.html',
  styleUrl: './carrito.component.css'
})
export class CarritoComponent implements OnInit {
  carritos: Carrito[] = [];

  constructor(private carritoService: CarritoService) {}

  ngOnInit(): void {
    this.carritoService.getCarritos().subscribe(
      carritos => {
        this.carritos = carritos;
      },
      error => {
        console.error('Error al obtener los carritos:', error);
      }
    );
  }
}
