import { Libro } from "../libro/libro";
import { Pedido } from "../pedido/pedido";

export class detallesPedido {
    pedido: Pedido = new Pedido();
    libro: Libro[] = [];
    cantidad: number = 0;
    precioUnitario: number = 0;
}