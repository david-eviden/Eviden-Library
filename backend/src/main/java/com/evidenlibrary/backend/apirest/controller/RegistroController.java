package com.evidenlibrary.backend.apirest.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evidenlibrary.backend.apirest.model.entity.Usuario;
import com.evidenlibrary.backend.apirest.model.service.KeycloakService;
import com.evidenlibrary.backend.apirest.model.service.UsuarioService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private KeycloakService keycloakService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Map<String, Object> userData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extraer datos del request
            String firstName = (String) userData.get("firstName");
            String lastName = (String) userData.get("lastName");
            String email = (String) userData.get("email");
            String password = (String) userData.get("password");
            
            // Validar datos
            if (firstName == null || lastName == null || email == null || password == null) {
                response.put("mensaje", "Todos los campos son obligatorios");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            // Verificar si el email ya existe en la base de datos local
            if (usuarioService.findByEmail(email) != null) {
                response.put("mensaje", "El email ya está registrado");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            // Registrar usuario en Keycloak
            boolean keycloakRegistroExitoso = keycloakService.registerUser(firstName, lastName, email, password);
            
            if (!keycloakRegistroExitoso) {
                response.put("mensaje", "Error al registrar el usuario en el sistema de autenticación");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
            // Crear nuevo usuario en la base de datos local
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(firstName);
            nuevoUsuario.setApellido(lastName);
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setPassword(""); // No almacenamos la contraseña en la BD local
            nuevoUsuario.setRol("USER"); // Rol por defecto
            nuevoUsuario.setFoto(null); // Foto de perfil por defecto
            
            // Guardar usuario en la base de datos local
            Usuario usuarioGuardado = usuarioService.save(nuevoUsuario);
            
            response.put("mensaje", "Usuario registrado con éxito");
            response.put("usuario", usuarioGuardado);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
            
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al registrar el usuario en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("mensaje", "Error al procesar la solicitud");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 