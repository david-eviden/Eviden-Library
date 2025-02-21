import { Component } from '@angular/core';
import { Carrito } from './carrito';
import { CarritoService } from './carrito.service';

@Component({
  selector: 'app-carrito',
  standalone: false,
  templateUrl: './carrito.component.html',
  styleUrl: './carrito.component.css'
})
export class CarritoComponent {
  carritos: Carrito[]= [];
  constructor(private carritoService: CarritoService) {}

  ngOnInit(): void {
    this.carritoService.getUsuarios().subscribe(
      (carritos: Carrito[]) => {
        this.carritos = carritos;
        console.log('Carritos recibidos:', carritos);  // Lista de usuarios en la consola
      },
      error => {
        console.error('Error al obtener los usuarios', error);  // Muestra errores si los hay
      }
    )
  };
}
