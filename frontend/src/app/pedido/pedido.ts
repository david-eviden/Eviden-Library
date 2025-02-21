import { detallesCarrito } from "../detalles-carrito/detalles-carrito";
import { detallesPedido } from "../detalles-pedido/detalles-pedido";
import { Usuario } from "../usuario/usuario";

export class Pedido {
    usuario: Usuario | undefined;
    fechaPedido: Date= new Date();
    estado: string= "" ;
    total: number = 0;
    direccionEnvio: string = "";
    detalles: detallesPedido | undefined;
}