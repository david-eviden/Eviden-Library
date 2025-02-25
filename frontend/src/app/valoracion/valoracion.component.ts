import { Component, OnInit } from '@angular/core';
import { Valoracion } from './valoracion';
import { ValoracionService } from './valoracion.service';

@Component({
  selector: 'app-valoracion',
  standalone: false,
  templateUrl: './valoracion.component.html',
  styleUrl: './valoracion.component.css'
})
export class ValoracionComponent implements OnInit {
  valoraciones: Valoracion[] = [];

  constructor(private valoracionService: ValoracionService) {}

  ngOnInit(): void {
    this.valoracionService.getValoraciones().subscribe(
      valoraciones => {
        this.valoraciones = valoraciones;
        console.log('Valoraciones recibidas:', valoraciones);
      },
      error => {
        console.error('Error al obtener las valoraciones:', error);
      }
    );
  }

  // Método helper para generar array de estrellas
  getStarsArray(puntuacion: number): number[] {
    return Array(puntuacion).fill(0);
  }

  // Método helper para generar array de estrellas vacías
  getEmptyStarsArray(puntuacion: number): number[] {
    return Array(5 - puntuacion).fill(0);
  }
}
