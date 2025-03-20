import { Component, OnInit } from '@angular/core';
import { AutorService } from './autor.service';
import { Autor } from './autor';
import swal from 'sweetalert2';
import { AuthService } from '../login/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-autor',
  standalone: false,
  templateUrl: './autor.component.html',
  styleUrls: ['./autor.component.css']
})
export class AutorComponent implements OnInit {
  autores: Autor[] = [];

  constructor(
    private autorService: AutorService,  
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.autorService.autores$.subscribe(
      (autores: Autor[]) => {
        this.autores = autores;
      },
      error => {
        console.error('Error al obtener los autores', error); 
      }
    );

    // Obtener autores al cargar el componente
    this.autorService.getAutores().subscribe();
  }

  // Eliminar autor con confirmación
  delete(autor: Autor): void {
    // Mensaje de confirmación de eliminación
    swal({
      title: `¿Estás seguro de eliminar el autor "${autor.nombre}"?`,
      text: "¡Esta operación no es reversible!",
      type: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "¡Sí, eliminalo!",
      cancelButtonText: "No, cancelar",
      buttonsStyling: true,
      reverseButtons: true
    }).then((result) => {
      if (result.value) {
        this.autorService.delete(autor.id).subscribe(
          response => {
            // Cuando la eliminación es exitosa, actualizamos la lista
            this.autores = this.autores.filter(a => a.id !== autor.id);
            swal(
              '¡Eliminado!',
              `El autor "${autor.nombre}" ha sido eliminado con éxito`,
              'success'
            );
          },
          error => {
            console.error('Error al eliminar el autor', error);
            swal(
              'Error',
              'Hubo un problema al eliminar el autor',
              'error'
            );
          }
        );
      } else if (result.dismiss === swal.DismissReason.cancel) {
        swal(
          'Cancelado',
          'Tu autor está a salvo :)',
          'error'
        );
      }
    });
  }

  deleteAll(): void {
    // Mensaje confirmacion eliminar todos
    swal({
      title: '¿Estás seguro de eliminar todos los autores?',
      text: "¡Esta operación no es reversible!",
      type: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "¡Sí, eliminar todos!",
      cancelButtonText: "No, cancelar",
      buttonsStyling: true,
      reverseButtons: true
    }).then((result) => {
      if (result.value) {
        this.autorService.deleteAll().subscribe(
          response => {
            // Vaciamos el array de autores
            this.autores = [];
  
            swal(
              '¡Eliminados!',
              'Todos los autores han sido eliminados :(',
              'success'
            );
          }
        );
      } else if (result.dismiss === swal.DismissReason.cancel) {
        swal(
          'Cancelado',
          'Tus autores están a salvo :)',
          'error'
        )
      }
    });
  }

  verLibrosAutor(autorId: number): void {
    this.router.navigate(['/libros/autor', autorId, 'page', 0, 'size',4]);
  }
}
