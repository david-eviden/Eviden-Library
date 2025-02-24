import { Autor } from "../autor/autor";
import { Valoracion } from "../valoracion/valoracion";

export class Libro {
    id: number = 0;
    precio: number = 0;
    stock: number = 0;
    titulo: string = "";
    imagen: string = "";
    descripcion: String = "";
    valoraciones: Valoracion[] = [];
    autores: Autor[] = [];
}