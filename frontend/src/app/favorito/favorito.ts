import { Libro } from "../libro/libro";
import { Usuario } from "../usuario/usuario";

export class Favorito {
    usuario: Usuario | undefined;
    libro: Libro | undefined;
    fechaAgregado: string | undefined;
}