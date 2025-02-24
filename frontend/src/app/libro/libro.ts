import { Autor } from "../autor/autor";

export class Libro {
    id: number = 0;
    precio: number = 0;
    stock: number = 0;
    titulo: string = "";
    imagen: string = "";
    autores: Autor[] = [];
}