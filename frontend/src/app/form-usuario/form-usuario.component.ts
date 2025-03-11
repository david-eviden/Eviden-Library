import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Usuario } from '../usuario/usuario';
import { UsuarioService } from '../usuario/usuario.service';
import { AuthService } from '../login/auth.service';
import swal from 'sweetalert2';

@Component({
  selector: 'app-form-usuario',
  standalone: false,
  templateUrl: './form-usuario.component.html',
  styleUrl: './form-usuario.component.css'
})
export class FormUsuarioComponent implements OnInit {
  
  public usuario: Usuario = new Usuario();
  public errors: string[] = [];
  public confirmPassword: string = '';
  public tienePermiso: boolean = false;

  constructor(
    private usuarioService: UsuarioService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    public authService: AuthService
  ) { }

  ngOnInit(): void {
    this.cargarUsuario();
  }

  public cargarUsuario(): void {
    this.activatedRoute.params.subscribe(params => {
      let id = params['id'];
      if (id) {
        this.usuarioService.getUsuario(id).subscribe(
          (usuario) => {
            this.usuario = usuario;
            // Verificar si el usuario tiene permiso para editar
            const usuarioActual = this.authService.getCurrentUser();
            this.tienePermiso = this.authService.esAdmin || 
                              (usuarioActual && usuarioActual.id === this.usuario.id);
            
            if (!this.tienePermiso) {
              swal('Acceso Denegado', 'No tiene permisos para editar este perfil', 'error')
                .then(() => {
                  this.router.navigate(['/usuarios']);
                });
            }
          },
          error => {
            console.error('Error al cargar el usuario:', error);
            this.errors.push('Error al cargar los datos del usuario');
            this.router.navigate(['/usuarios']);
          }
        );
      }
    });
  }

  public update(): void {
    // Limpiar errores previos
    this.errors = [];

    // Validaciones básicas
    if (!this.usuario.nombre || !this.usuario.apellido || !this.usuario.email) {
      this.errors.push('Todos los campos marcados son obligatorios');
      return;
    }

    // Validar formato de email
    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    if (!emailRegex.test(this.usuario.email)) {
      this.errors.push('El formato del email no es válido');
      return;
    }

    // Crear una copia del usuario para la actualización
    const usuarioToUpdate = { ...this.usuario };

    // Si no se está cambiando la contraseña, eliminarla del objeto a actualizar
    if (!this.usuario.password) {
      delete usuarioToUpdate.password;
    } else {
      // Validar la contraseña solo si se está cambiando
      if (this.usuario.password.length < 6) {
        this.errors.push('La contraseña debe tener al menos 6 caracteres');
        return;
      }
      if (this.usuario.password !== this.confirmPassword) {
        this.errors.push('Las contraseñas no coinciden');
        return;
      }
    }

    this.usuarioService.update(usuarioToUpdate).subscribe(
      response => {
        this.router.navigate(['/usuario', this.usuario.id]);
        swal('Usuario Actualizado', `Usuario ${this.usuario.nombre} actualizado con éxito`, 'success');
      },
      error => {
        this.errors = error.error.errores || ['Error al actualizar el usuario'];
        console.error('Error al actualizar el usuario:', error);
      }
    );
  }
}
