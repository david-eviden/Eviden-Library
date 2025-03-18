package com.evidenlibrary.backend.apirest.controller;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.evidenlibrary.backend.apirest.model.entity.Usuario;
import com.evidenlibrary.backend.apirest.model.service.UsuarioService;

class LoginControllerTest {

    @Mock
    private UsuarioService usuarioService;
    
    @Mock
    private Authentication authentication;
    
    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private LoginController loginController;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar SecurityContextHolder mock
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        // Configurar datos de prueba para usuario
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juan.perez@example.com");
        usuario.setPassword("password123");
        usuario.setRol("USER");
    }

    @SuppressWarnings("null")
	@Test
    void testLogin_UserExists() {
        // Configurar mocks
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("juan.perez@example.com");
        when(usuarioService.findByEmail("juan.perez@example.com")).thenReturn(usuario);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = loginController.login(null);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        Usuario usuarioRespuesta = (Usuario) respuesta.getBody();
        assertEquals(1L, usuarioRespuesta.getId());
        assertEquals("juan.perez@example.com", usuarioRespuesta.getEmail());
        assertEquals("USER", usuarioRespuesta.getRol());
        assertNull(usuarioRespuesta.getPassword(), "La contraseña debe ser nula en la respuesta");
        
        // Verificar que se llamaron a los métodos correctos
        verify(usuarioService).findByEmail("juan.perez@example.com");
    }
    
    @SuppressWarnings({ "null", "unchecked", "rawtypes" })
	@Test
    void testLogin_CreateNewUser() {
        // Configurar mocks
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("nuevo.usuario@example.com");
        when(usuarioService.findByEmail("nuevo.usuario@example.com")).thenReturn(null);
        
        // Crear una colección de autoridades compatible con el tipo esperado
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        
        // Configurar mock para la creación del usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setId(2L);
        nuevoUsuario.setEmail("nuevo.usuario@example.com");
        nuevoUsuario.setNombre("nuevo.usuario@example.com");
        nuevoUsuario.setRol("ADMIN");
        
        when(usuarioService.save(any(Usuario.class))).thenReturn(nuevoUsuario);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = loginController.login(null);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        Usuario usuarioRespuesta = (Usuario) respuesta.getBody();
        assertEquals(2L, usuarioRespuesta.getId());
        assertEquals("nuevo.usuario@example.com", usuarioRespuesta.getEmail());
        assertEquals("ADMIN", usuarioRespuesta.getRol());
        assertNull(usuarioRespuesta.getPassword(), "La contraseña debe ser nula en la respuesta");
        
        // Verificar que se llamaron a los métodos correctos
        verify(usuarioService).findByEmail("nuevo.usuario@example.com");
        verify(usuarioService).save(any(Usuario.class));
    }
    
    @Test
    void testLogin_NotAuthenticated() {
        // Configurar mocks
        when(authentication.isAuthenticated()).thenReturn(false);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = loginController.login(null);
        
        // Verificar resultado
        assertEquals(HttpStatus.UNAUTHORIZED, respuesta.getStatusCode());
        assertEquals("No autenticado", respuesta.getBody());
    }
    
    @Test
    void testLogin_NullAuthentication() {
        // Configurar mocks para simular autenticación nula
        when(securityContext.getAuthentication()).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = loginController.login(null);
        
        // Verificar resultado
        assertEquals(HttpStatus.UNAUTHORIZED, respuesta.getStatusCode());
        assertEquals("No autenticado", respuesta.getBody());
    }
} 