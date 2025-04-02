package com.evidenlibrary.backend.apirest.controller;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.evidenlibrary.backend.apirest.model.entity.Usuario;
import com.evidenlibrary.backend.apirest.model.service.KeycloakService;
import com.evidenlibrary.backend.apirest.model.service.UsuarioService;

class RegistroControllerTest {

    @Mock
    private UsuarioService usuarioService;
    
    @Mock
    private KeycloakService keycloakService;

    @InjectMocks
    private RegistroController registroController;

    private Map<String, Object> userData;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar datos de prueba para usuario
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juan.perez@example.com");
        usuario.setPassword("");
        usuario.setRol("USER");
        
        // Configurar datos de registro
        userData = new HashMap<>();
        userData.put("firstName", "Juan");
        userData.put("lastName", "Pérez");
        userData.put("email", "juan.perez@example.com");
        userData.put("password", "password123");
    }

    @SuppressWarnings("null")
	@Test
    void testRegistrarUsuario_Success() {
        // Configurar mocks
        when(usuarioService.findByEmail("juan.perez@example.com")).thenReturn(null);
        when(keycloakService.registerUser(anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        when(usuarioService.save(any(Usuario.class))).thenReturn(usuario);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = registroController.registrarUsuario(userData);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("usuario"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(usuarioService).findByEmail("juan.perez@example.com");
        verify(keycloakService).registerUser("Juan", "Pérez", "juan.perez@example.com", "password123");
        verify(usuarioService).save(any(Usuario.class));
    }
    
    @SuppressWarnings("null")
	@Test
    void testRegistrarUsuario_EmailExists() {
        // Configurar mocks
        when(usuarioService.findByEmail("juan.perez@example.com")).thenReturn(usuario);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = registroController.registrarUsuario(userData);
        
        // Verificar resultado
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertEquals("El email ya está registrado", body.get("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(usuarioService).findByEmail("juan.perez@example.com");
    }
    
    @SuppressWarnings("null")
	@Test
    void testRegistrarUsuario_MissingFields() {
        // Configurar datos de registro incompletos
        Map<String, Object> incompleteData = new HashMap<>();
        incompleteData.put("firstName", "Juan");
        // Faltan campos
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = registroController.registrarUsuario(incompleteData);
        
        // Verificar resultado
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertEquals("Todos los campos son obligatorios", body.get("mensaje"));
    }
    
    @SuppressWarnings("null")
	@Test
    void testRegistrarUsuario_KeycloakError() {
        // Configurar mocks
        when(usuarioService.findByEmail("juan.perez@example.com")).thenReturn(null);
        when(keycloakService.registerUser(anyString(), anyString(), anyString(), anyString())).thenReturn(false);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = registroController.registrarUsuario(userData);
        
        // Verificar resultado
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertEquals("Error al registrar el usuario en el sistema de autenticación", body.get("mensaje"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(usuarioService).findByEmail("juan.perez@example.com");
        verify(keycloakService).registerUser("Juan", "Pérez", "juan.perez@example.com", "password123");
    }
    
    @SuppressWarnings("null")
	@Test
    void testRegistrarUsuario_DatabaseError() {
        // Configurar mocks
        when(usuarioService.findByEmail("juan.perez@example.com")).thenReturn(null);
        when(keycloakService.registerUser(anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        when(usuarioService.save(any(Usuario.class))).thenThrow(new DataAccessException("Error de prueba") {
            private static final long serialVersionUID = 1L;
        });
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = registroController.registrarUsuario(userData);
        
        // Verificar resultado
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("error"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(usuarioService).findByEmail("juan.perez@example.com");
        verify(keycloakService).registerUser("Juan", "Pérez", "juan.perez@example.com", "password123");
        verify(usuarioService).save(any(Usuario.class));
    }
} 