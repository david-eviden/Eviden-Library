import { Libro } from "../libro/libro";
import { Usuario } from "../usuario/usuario";

export class Favorito {
    usuario: Usuario[] = [];
    libro: Libro[] = [] ;
    fecha: Date = new Date();
}