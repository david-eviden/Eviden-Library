import { Component } from '@angular/core';
import { Valoracion } from './valoracion';
import { ValoracionService } from './valoracion.service';

@Component({
  selector: 'app-valoracion',
  standalone: false,
  templateUrl: './valoracion.component.html',
  styleUrl: './valoracion.component.css'
})
export class ValoracionComponent {
  valoraciones: Valoracion[]= [];
  constructor(private carritoService: ValoracionService) {}

  ngOnInit(): void {
    this.carritoService.getUsuarios().subscribe(
      (valoraciones: Valoracion[]) => {
        this.valoraciones = valoraciones;
        console.log('Valoraciones recibidas:', valoraciones);  // Lista de usuarios en la consola
      },
      error => {
        console.error('Error al obtener las valoraciones', error);  // Muestra errores si los hay
      }
    )
  };
}
