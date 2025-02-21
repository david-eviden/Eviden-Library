import { Component } from '@angular/core';
import { Pedido } from './pedido';
import { PedidoService } from './pedido.service';

@Component({
  selector: 'app-pedido',
  standalone: false,
  templateUrl: './pedido.component.html',
  styleUrl: './pedido.component.css'
})
export class PedidoComponent {
  pedidos: Pedido[]= [];
  constructor(private pedidoService: PedidoService) {}

  ngOnInit(): void {
    this.pedidoService.getUsuarios().subscribe(
      (pedidos: Pedido[]) => {
        this.pedidos = pedidos;
        console.log('Pedidos recibidos:', pedidos); 
      },
      error => {
        console.error('Error al obtener los usuarios', error);  // Muestra errores si los hay
      }
    )
  };
}
