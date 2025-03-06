import { Autor } from "../autor/autor";
import { Genero } from "../generos/generos";
import { Valoracion } from "../valoracion/valoracion";

export class Libro {
    id: number = 0;
    precio: number = 0;
    stock: number = 0;
    titulo: string = "";
    //generos: string = "";    
    descripcion: string = "";
    valoraciones: Valoracion[] = [];
    autores: Autor[] = [];
    generos: Genero[] = [];
    imagen: any;
    tipoImagen: string = "";
}