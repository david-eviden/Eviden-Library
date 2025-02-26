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
  pedidos: Pedido[] = [];
  error: string = '';

  constructor(private pedidoService: PedidoService) {}

  ngOnInit(): void {
    this.getPedidos();
  }

  getPedidos(): void {
    this.pedidoService.getPedidos().subscribe(
      (pedidos: Pedido[]) => {
        this.pedidos = pedidos;
        console.log('Pedidos recibidos:', pedidos);
      },
      error => {
        console.error('Error al obtener los pedidos', error);
        this.error = 'Hubo un error al cargar los pedidos. Por favor, intente nuevamente.';
      }
    );
  }
}
