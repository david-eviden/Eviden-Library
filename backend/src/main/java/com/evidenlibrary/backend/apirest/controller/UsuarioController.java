package com.evidenlibrary.backend.apirest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.evidenlibrary.backend.apirest.model.entity.Usuario;
import com.evidenlibrary.backend.apirest.model.service.UsuarioService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Obtener todos los usuarios
    @GetMapping("/usuarios")
    public List<Usuario> index() {
        return usuarioService.findAll();
    }

    // Obtener usuario por ID
    @GetMapping("/usuario/{id}")
    public ResponseEntity<?> show(@PathVariable(name = "id") Long id) {

        Usuario usuario;
        Map<String, Object> response = new HashMap<>();

        try {
            usuario = usuarioService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (usuario == null) {
            response.put("mensaje", "El usuario con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    // Crear un nuevo usuario
    @PostMapping("/usuario")
    public ResponseEntity<?> create(@RequestBody Usuario usuario, BindingResult result) {

        Usuario nuevoUsuario;
        Map<String, Object> response = new HashMap<>();

        // Validamos campos
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errores", errores);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Manejamos errores
        try {
            nuevoUsuario = usuarioService.save(usuario);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al insertar en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El usuario ha sido creado con éxito");
        response.put("usuario", nuevoUsuario);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Actualizar usuario
    @PutMapping("/usuario/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> update(@RequestBody Usuario usuario, BindingResult result, @PathVariable(name = "id") Long id) {

        Usuario currentUsuario = this.usuarioService.findById(id);
        Usuario nuevoUsuario;
        Map<String, Object> response = new HashMap<>();

        // Validamos campos
        if (result.hasErrors()) {

            List<String> errores = result.getFieldErrors().stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errores", errores);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (currentUsuario == null) {
            response.put("mensaje", "No se puede editar, el usuario con ID: "
                    .concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            currentUsuario.setApellido(usuario.getApellido());
            currentUsuario.setDireccion(usuario.getDireccion());
            currentUsuario.setNombre(usuario.getNombre());
            currentUsuario.setEmail(usuario.getEmail());
            // Solo actualizar la contraseña si se proporciona una nueva
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                currentUsuario.setPassword(usuario.getPassword());
            }
            // Actualizar la foto de perfil si se proporciona una nueva
            if (usuario.getFoto() != null) {
                currentUsuario.setFoto(usuario.getFoto());
            }
            currentUsuario.setRol(usuario.getRol());

            nuevoUsuario = usuarioService.save(currentUsuario);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar el usuario en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El usuario ha sido actualizado con éxito");
        response.put("usuario", nuevoUsuario);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Eliminar usuario por ID
    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        Usuario currentUsuario = this.usuarioService.findById(id);
        Map<String, Object> response = new HashMap<>();

        // Validación de que exista el usuario
        if (currentUsuario == null) {
            response.put("mensaje", "El usuario con ID: " + id + " no existe en la base de datos");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            usuarioService.delete(currentUsuario);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar el usuario en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El usuario ha sido eliminado con éxito");
        response.put("usuario", currentUsuario);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Obtener usuario por email
    @GetMapping("/usuario/email/{email}")
    public ResponseEntity<?> findByEmail(@PathVariable(name = "email") String email) {
        Usuario usuario;
        Map<String, Object> response = new HashMap<>();

        try {
            usuario = usuarioService.findByEmail(email);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (usuario == null) {
            response.put("mensaje", "El usuario con email: ".concat(email.concat(" no existe en la base de datos")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

}
