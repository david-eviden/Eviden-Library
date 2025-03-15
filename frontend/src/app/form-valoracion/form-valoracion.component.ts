import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import swal from 'sweetalert2';
import { Valoracion } from '../valoracion/valoracion';
import { ValoracionService } from '../valoracion/valoracion.service';
import { Usuario } from '../usuario/usuario';
import { UsuarioService } from '../usuario/usuario.service';
import { Libro } from '../libro/libro';
import { LibroService } from '../libro/libro.service';
import { AuthService } from '../login/auth.service';

@Component({
  selector: 'app-form-valoracion',
  standalone: false,
  templateUrl: './form-valoracion.component.html',
  styleUrl: './form-valoracion.component.css'
})
export class FormValoracionComponent implements OnInit {

  public valoracion: Valoracion = new Valoracion();
  public errors: string[] = [];
  public usuarios: Usuario[] = [];
  public libros: Libro[] = [];
  // Mapa para almacenar valoraciones por usuario 
  public valoracionesPorUsuario: Map<number, Valoracion[]> = new Map();

  constructor(
    private valoracionService: ValoracionService, 
    private usuarioService: UsuarioService,
    private libroService: LibroService,
    public router: Router, 
    private activatedRoute: ActivatedRoute,
    public authService: AuthService
  ) { }

  ngOnInit(): void {
    this.cargarValoracion();
    this.cargarUsuarios();
    this.cargarLibros();
  }

  // Cargar usuarios
  cargarUsuarios(): void {
    this.usuarioService.getUsuarios().subscribe(
      usuarios => {
        this.usuarios = usuarios;
      },
      error => {
        console.error('Error al cargar usuarios', error);
        swal('Error', 'No se pudieron cargar los usuarios', 'error');
      }
    );
  }

  // Cargar libros
  cargarLibros(): void {
    this.libroService.getLibrosNoPagin().subscribe(
      libros => {
        this.libros = libros;
      },
      error => {
        console.error('Error al cargar libros', error);
        swal('Error', 'No se pudieron cargar los libros', 'error');
      }
    );
  }

  // Crear valoración
  public create(): void {

    if (!this.valoracion.usuario) {
      swal('Error', 'Debe seleccionar un usuario para crear la valoración', 'error');
      return;
    }

    if (!this.valoracion.libro) {
      swal('Error', 'Debe seleccionar un libro para crear la valoración', 'error');
      return;
    }

    // Verificar si el usuario ya ha valorado este libro
    if (this.usuarioYaValoroLibro(this.valoracion.usuario.id, this.valoracion.libro.id)) {
      swal('Error', `El usuario ${this.valoracion.usuario.nombre} ya ha valorado este libro anteriormente. Solo puede valorar un libro una vez.`, 'error');
      return;
    }

    // Actualizar la fecha actual
    this.valoracion.fecha = new Date();
    
    this.valoracionService.create(this.valoracion).subscribe(
      // Si OK
      json => {
        this.router.navigate(['/valoraciones']);
        // Mensaje SweetAlert
        swal('Nueva valoración', `Valoración para el libro "${this.valoracion.libro?.titulo}" creada con éxito`, 'success');
      },

      // Si error
      err => {
        if (err.error.mensaje && err.error.mensaje.includes("ya ha valorado este libro")) {
          swal('Error', 'Ya has valorado este libro anteriormente. Solo puedes valorar un libro una vez.', 'error');
        } else {
          this.errors = err.error.errores as string[];
          console.error('Código del error (backend): ' + err.error.status);
          console.error(err.error.errores);
          swal('Error', `${err.error.mensaje || 'Error al crear la valoración'}`, 'error');
        }
      }
    );
  }

  // Obtener valoración por ID
  public cargarValoracion(): void {
    this.activatedRoute.params.subscribe(params => {
      let id = params['id'];
      if(id) {
        this.valoracionService.getValoracion(id).subscribe(
          (valoracion) => {
            this.valoracion = valoracion;
            
            // Si tenemos libroDetalles pero no libro, intentamos cargar el libro
            if (this.valoracion.libroDetalles && !this.valoracion.libro) {
              const libroId = typeof this.valoracion.libroDetalles === 'number' 
                ? this.valoracion.libroDetalles 
                : (this.valoracion.libroDetalles as any).id;
                
              this.libroService.getLibro(libroId).subscribe(
                libro => {
                  this.valoracion.libro = libro;
                },
                error => {
                  console.error('Error al cargar el libro para la valoración', error);
                }
              );
            }
          },
          error => {
            console.error('Error al cargar valoración', error);
            swal('Error', 'No se pudo cargar la valoración', 'error');
            this.router.navigate(['/valoraciones']);
          }
        );
      }
    });
  }

  // Update valoración por ID
  public update(): void {
    // Asegurarse de que el libro esté establecido correctamente
    if (!this.valoracion.libro && this.valoracion.libroDetalles) {
      // Buscar el libro por ID si solo tenemos libroDetalles
      const libroId = typeof this.valoracion.libroDetalles === 'number' 
        ? this.valoracion.libroDetalles 
        : (this.valoracion.libroDetalles as any).id;
        
      const libroEncontrado = this.libros.find(l => l.id === libroId);
      if (libroEncontrado) {
        this.valoracion.libro = libroEncontrado;
      } else {
        // Si no encontramos el libro en la lista cargada, intentamos obtenerlo del servicio
        this.libroService.getLibro(libroId).subscribe(
          libro => {
            this.valoracion.libro = libro;
            this.completarActualizacion();
          },
          error => {
            console.error('Error al cargar el libro para la valoración', error);
            swal('Error', 'No se pudo cargar el libro asociado a la valoración', 'error');
          }
        );
        return; // Salimos para evitar la llamada duplicada a completarActualizacion()
      }
    }
    
    this.completarActualizacion();
  }
  
  // Método auxiliar para completar la actualización
  private completarActualizacion(): void {
    this.valoracionService.updateValoracion(this.valoracion).subscribe(
      // Si OK
      json => {
        this.router.navigate(['/valoraciones']);
        //Mensaje
        swal('Valoración Actualizada', `Valoración actualizada con éxito`, 'success');
      },

      // Si error
      err => {
        this.errors = err.error.errores as string[];
        console.error('Código del error (backend): ' + err.error.status);
        console.error(err.error.errores);
      }
    );
  }

  // Establecer puntuación
  setRating(rating: number): void {
    this.valoracion.puntuacion = rating;
  }

  // Al cambiar el libro
  onLibroChange(): void {
    if (this.valoracion.libro) {
      // Verificar si el usuario ya ha valorado este libro (solo si hay un usuario seleccionado)
      if (this.valoracion.usuario && this.usuarioYaValoroLibro(this.valoracion.usuario.id, this.valoracion.libro.id)) {
        swal('Aviso', `El usuario ${this.valoracion.usuario.nombre} ya ha valorado este libro anteriormente. Solo puede valorar un libro una vez.`, 'warning');
      }
      
      this.valoracion.libroDetalles = this.valoracion.libro.id;
    }
  }

  // Al cambiar el usuario
  onUsuarioChange(): void {
    if (this.valoracion.usuario) {
      // Cargar valoraciones del usuario seleccionado
      this.cargarValoracionesUsuario(this.valoracion.usuario.id);
      
      // Verificar si el usuario ya ha valorado este libro (solo si hay un libro seleccionado)
      if (this.valoracion.libro && this.usuarioYaValoroLibro(this.valoracion.usuario.id, this.valoracion.libro.id)) {
        swal('Aviso', `El usuario ${this.valoracion.usuario.nombre} ya ha valorado este libro anteriormente. Solo puede valorar un libro una vez.`, 'warning');
      }
    }
  }

  //Cargar valoraciones del usuario actual
  cargarValoracionesUsuario(usuarioId: number) : void {
    // Evitar cargar valoraciones de usuario que ya tenemos
    if (this.valoracionesPorUsuario.has(usuarioId)) {
      return;
    }
    
    this.valoracionService.getValoracionesPorUsuarioId(usuarioId).subscribe(
      valoraciones => {
        this.valoracionesPorUsuario.set(usuarioId, valoraciones);
      },
      error => {
        console.error(`Error al cargar valoraciones del usuario ${usuarioId}`, error);
      }
    );
  }

  // Verificar si el usuario ya ha valorado este libro
  usuarioYaValoroLibro(usuarioId: number, libroId: number): boolean {
    if (!this.valoracionesPorUsuario.has(usuarioId)) {
      return false; // No tenemos datos de este usuario todavía
    }
    
    const valoracionesUsuario = this.valoracionesPorUsuario.get(usuarioId) || [];
    return valoracionesUsuario.some(v => 
      // Verificamos si libroDetalles es un número o un objeto
      (typeof v.libroDetalles === 'number' && v.libroDetalles === libroId) || 
      (v.libro && v.libro.id === libroId)
    );
  }
}