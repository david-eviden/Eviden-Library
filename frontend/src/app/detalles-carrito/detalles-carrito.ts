import { Carrito } from "../carrito/carrito";
import { Libro } from "../libro/libro";

export class detallesCarrito {
    carrito: Carrito = new Carrito();
    libro: Libro[] = [];
    cantidad: number = 0;
}