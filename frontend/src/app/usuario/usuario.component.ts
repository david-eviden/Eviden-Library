import { Component, OnInit } from '@angular/core';
import { Usuario } from './usuario';
import { UsuarioService } from './usuario.service';

@Component({
  selector: 'app-usuario',
  standalone: false,
  templateUrl: './usuario.component.html',
  styleUrl: './usuario.component.css'
})
export class UsuarioComponent implements OnInit{
  usuarios : Usuario[]= [];
  constructor(private usuarioService: UsuarioService) {}

  ngOnInit(): void {
    this.usuarioService.getUsuarios().subscribe(
      (usuarios : Usuario[]) => {
        this.usuarios = usuarios;
        console.log('Usuarios recibidos:', usuarios);  // Lista de usuarios en la consola
      },
      error => {
        console.error('Error al obtener los usuarios', error);  // Muestra errores si los hay
      }
    )
  };
}
