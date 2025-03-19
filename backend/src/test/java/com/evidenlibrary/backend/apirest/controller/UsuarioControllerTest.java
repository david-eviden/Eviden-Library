package com.evidenlibrary.backend.apirest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.evidenlibrary.backend.apirest.model.entity.Usuario;
import com.evidenlibrary.backend.apirest.model.service.UsuarioService;

class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UsuarioController usuarioController;

    private Usuario usuario1;
    private Usuario usuario2;
    private List<Usuario> usuarios;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar datos de prueba
        usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setNombre("Juan");
        usuario1.setApellido("Pérez");
        usuario1.setDireccion("Calle Principal 123");
        usuario1.setEmail("juan.perez@example.com");
        usuario1.setPassword("password123");
        usuario1.setRol("ROLE_USER");
        
        usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNombre("María");
        usuario2.setApellido("Gómez");
        usuario2.setDireccion("Avenida Central 456");
        usuario2.setEmail("maria.gomez@example.com");
        usuario2.setPassword("password456");
        usuario2.setRol("ROLE_USER");
        
        usuarios = new ArrayList<>();
        usuarios.add(usuario1);
        usuarios.add(usuario2);
    }

    @Test
    void testIndex() {
        // Configurar mock
        when(usuarioService.findAll()).thenReturn(usuarios);
        
        // Ejecutar método a probar
        List<Usuario> resultado = usuarioController.index();
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        assertEquals("María", resultado.get(1).getNombre());
        
        // Verificar que se llamó al método correcto
        verify(usuarioService).findAll();
    }

    @SuppressWarnings("null")
    @Test
    void testShow_Success() {
        // Configurar mock
        when(usuarioService.findById(1L)).thenReturn(usuario1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = usuarioController.show(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Usuario);
        assertEquals(1L, ((Usuario) respuesta.getBody()).getId());
        assertEquals("Juan", ((Usuario) respuesta.getBody()).getNombre());
        
        // Verificar que se llamó al método correcto
        verify(usuarioService).findById(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testShow_NotFound() {
        // Configurar mock
        when(usuarioService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = usuarioController.show(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(usuarioService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testShow_DatabaseError() {
        // Configurar mock para lanzar una excepción
        when(usuarioService.findById(1L)).thenThrow(new DataAccessException("Error de prueba") {
            private static final long serialVersionUID = 1L;
        });
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = usuarioController.show(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("error"));
        
        // Verificar que se llamó al método correcto
        verify(usuarioService).findById(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testCreate_Success() {
        // Configurar mocks
        when(bindingResult.hasErrors()).thenReturn(false);
        when(usuarioService.save(any(Usuario.class))).thenReturn(usuario1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = usuarioController.create(usuario1, bindingResult);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("usuario"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(bindingResult).hasErrors();
        verify(usuarioService).save(any(Usuario.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testCreate_ValidationErrors() {
        // Configurar mock para simular errores de validación
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(new ArrayList<>());
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = usuarioController.create(usuario1, bindingResult);
        
        // Verificar resultado
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("errores"));
        
        // Verificar que se llamó al método correcto
        verify(bindingResult).hasErrors();
        verify(bindingResult).getFieldErrors();
    }
    
    @SuppressWarnings("null")
    @Test
    void testUpdate_Success() {
        // Configurar mocks
        when(bindingResult.hasErrors()).thenReturn(false);
        when(usuarioService.findById(1L)).thenReturn(usuario1);
        when(usuarioService.save(any(Usuario.class))).thenReturn(usuario1);
        
        // Crear un usuario con datos actualizados
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setNombre("Juan (actualizado)");
        usuarioActualizado.setApellido("Pérez (actualizado)");
        usuarioActualizado.setDireccion("Nueva dirección");
        usuarioActualizado.setEmail("juan.actualizado@example.com");
        usuarioActualizado.setPassword("nuevapassword");
        usuarioActualizado.setRol("ROLE_ADMIN");
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = usuarioController.update(usuarioActualizado, bindingResult, 1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("usuario"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(bindingResult).hasErrors();
        verify(usuarioService).findById(1L);
        verify(usuarioService).save(any(Usuario.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testUpdate_NotFound() {
        // Configurar mocks
        when(bindingResult.hasErrors()).thenReturn(false);
        when(usuarioService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = usuarioController.update(usuario1, bindingResult, 99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(bindingResult).hasErrors();
        verify(usuarioService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testDelete_Success() {
        // Configurar mocks
        when(usuarioService.findById(1L)).thenReturn(usuario1);
        doNothing().when(usuarioService).delete(usuario1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = usuarioController.delete(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("usuario"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(usuarioService).findById(1L);
        verify(usuarioService).delete(usuario1);
    }
    
    @SuppressWarnings("null")
    @Test
    void testDelete_NotFound() {
        // Configurar mock
        when(usuarioService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = usuarioController.delete(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(usuarioService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testFindByEmail_Success() {
        // Configurar mock
        when(usuarioService.findByEmail("juan.perez@example.com")).thenReturn(usuario1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = usuarioController.findByEmail("juan.perez@example.com");
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Usuario);
        assertEquals(1L, ((Usuario) respuesta.getBody()).getId());
        assertEquals("juan.perez@example.com", ((Usuario) respuesta.getBody()).getEmail());
        
        // Verificar que se llamó al método correcto
        verify(usuarioService).findByEmail("juan.perez@example.com");
    }
    
    @SuppressWarnings("null")
    @Test
    void testFindByEmail_NotFound() {
        // Configurar mock
        when(usuarioService.findByEmail("noexiste@example.com")).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = usuarioController.findByEmail("noexiste@example.com");
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(usuarioService).findByEmail("noexiste@example.com");
    }
    
    @SuppressWarnings("null")
    @Test
    void testFindByEmail_DatabaseError() {
        // Configurar mock para lanzar una excepción
        when(usuarioService.findByEmail("error@example.com")).thenThrow(new DataAccessException("Error de prueba") {
            private static final long serialVersionUID = 1L;
        });
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = usuarioController.findByEmail("error@example.com");
        
        // Verificar resultado
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("error"));
        
        // Verificar que se llamó al método correcto
        verify(usuarioService).findByEmail("error@example.com");
    }
} 