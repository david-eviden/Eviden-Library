package com.evidenlibrary.backend.apirest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evidenlibrary.backend.apirest.model.service.UsuarioService;
import com.evidenlibrary.backend.apirest.model.entity.Usuario;

import java.security.Principal;

@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<?> login(Principal principal) {
        // Existing login logic remains the same
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        // Extraer email del token JWT
        String email = authentication.getName();

        // Buscar usuario por email en la base de datos
        Usuario usuario = usuarioService.findByEmail(email);

        if (usuario == null) {
            // Si no existe, crear un nuevo usuario
            usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setNombre(authentication.getName());
            
            // Extraer roles del token
            String rol = authentication.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .orElse("USER");
            
            usuario.setRol(rol);
            usuario = usuarioService.save(usuario);
        }

        // Eliminar campos sensibles antes de devolver
        usuario.setPassword(null);

        return ResponseEntity.ok(usuario);
    }
}