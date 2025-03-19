package com.evidenlibrary.backend.apirest.controller;

import java.util.ArrayList;
import java.util.Date;
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

import com.evidenlibrary.backend.apirest.model.entity.Favorito;
import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.entity.Usuario;
import com.evidenlibrary.backend.apirest.model.service.FavoritoService;

class FavoritoControllerTest {

    @Mock
    private FavoritoService favoritoService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private FavoritoController favoritoController;

    private Favorito favorito1;
    private Favorito favorito2;
    private List<Favorito> favoritos;
    private Usuario usuario;
    private Libro libro;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Crear usuario de prueba
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juan.perez@example.com");
        usuario.setPassword("password123");
        usuario.setRol("ROLE_USER");
        
        // Crear libro de prueba
        libro = new Libro();
        libro.setTitulo("El Quijote");
        
        // Configurar datos de prueba para favoritos
        favorito1 = new Favorito();
        favorito1.setUsuario(usuario);
        favorito1.setLibro(libro);
        favorito1.setFechaAgregado(new Date());
        
        favorito2 = new Favorito();
        favorito2.setUsuario(usuario);
        Libro libro2 = new Libro();
        libro2.setTitulo("Cien años de soledad");
        favorito2.setLibro(libro2);
        favorito2.setFechaAgregado(new Date());
        
        favoritos = new ArrayList<>();
        favoritos.add(favorito1);
        favoritos.add(favorito2);
    }

    @Test
    void testIndex() {
        // Configurar mock
        when(favoritoService.findAll()).thenReturn(favoritos);
        
        // Ejecutar método a probar
        List<Favorito> resultado = favoritoController.index();
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getLibro().getId());
        assertEquals(2L, resultado.get(1).getLibro().getId());
        
        // Verificar que se llamó al método correcto
        verify(favoritoService).findAll();
    }

    @SuppressWarnings("null")
    @Test
    void testShow_Success() {
        // Configurar mock
        when(favoritoService.findById(1L)).thenReturn(favorito1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = favoritoController.show(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Favorito);
        assertEquals(1L, ((Favorito) respuesta.getBody()).getId());
        assertEquals(1L, ((Favorito) respuesta.getBody()).getLibro().getId());
        
        // Verificar que se llamó al método correcto
        verify(favoritoService).findById(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testShow_NotFound() {
        // Configurar mock
        when(favoritoService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = favoritoController.show(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(favoritoService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testShow_DatabaseError() {
        // Configurar mock para lanzar una excepción
        when(favoritoService.findById(1L)).thenThrow(new DataAccessException("Error de prueba") {
            private static final long serialVersionUID = 1L;
        });
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = favoritoController.show(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("error"));
        
        // Verificar que se llamó al método correcto
        verify(favoritoService).findById(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testCreate_Success() {
        // Configurar mocks
        when(bindingResult.hasErrors()).thenReturn(false);
        when(favoritoService.save(any(Favorito.class))).thenReturn(favorito1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = favoritoController.create(favorito1, bindingResult);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("favorito"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(bindingResult).hasErrors();
        verify(favoritoService).save(any(Favorito.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testCreate_ValidationErrors() {
        // Configurar mock para simular errores de validación
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(new ArrayList<>());
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = favoritoController.create(favorito1, bindingResult);
        
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
        when(favoritoService.findById(1L)).thenReturn(favorito1);
        when(favoritoService.save(any(Favorito.class))).thenReturn(favorito1);
        
        // Crear un favorito con datos actualizados
        Favorito favoritoActualizado = new Favorito();
        Libro nuevoLibro = new Libro();
        favoritoActualizado.setLibro(nuevoLibro);
        favoritoActualizado.setUsuario(usuario);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = favoritoController.update(favoritoActualizado, bindingResult, 1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(bindingResult).hasErrors();
        verify(favoritoService).findById(1L);
        verify(favoritoService).save(any(Favorito.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testUpdate_NotFound() {
        // Configurar mocks
        when(bindingResult.hasErrors()).thenReturn(false);
        when(favoritoService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = favoritoController.update(favorito1, bindingResult, 99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(bindingResult).hasErrors();
        verify(favoritoService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testDelete_Success() {
        // Configurar mocks
        when(favoritoService.findById(1L)).thenReturn(favorito1);
        doNothing().when(favoritoService).delete(favorito1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = favoritoController.delete(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(favoritoService).findById(1L);
        verify(favoritoService).delete(favorito1);
    }
    
    @SuppressWarnings("null")
    @Test
    void testDelete_NotFound() {
        // Configurar mock
        when(favoritoService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = favoritoController.delete(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(favoritoService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testDeleteByUsuarioAndLibro_Success() {
        // Configurar mocks
        when(favoritoService.findByUsuarioId(1L)).thenReturn(favoritos);
        doNothing().when(favoritoService).delete(favorito1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = favoritoController.deleteByUsuarioAndLibro(1L, 1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(favoritoService).findByUsuarioId(1L);
        verify(favoritoService).delete(any(Favorito.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testDeleteByUsuarioAndLibro_NotFound() {
        // Configurar mock para devolver una lista de favoritos sin el libro buscado
        List<Favorito> otrosFavoritos = new ArrayList<>();
        Favorito otroFavorito = new Favorito();
        otroFavorito.setUsuario(usuario);
        Libro otroLibro = new Libro();
        otroFavorito.setLibro(otroLibro);
        otrosFavoritos.add(otroFavorito);
        
        when(favoritoService.findByUsuarioId(1L)).thenReturn(otrosFavoritos);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = favoritoController.deleteByUsuarioAndLibro(1L, 1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(favoritoService).findByUsuarioId(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testGetFavoritosByUsuarioId_Success() {
        // Configurar mock
        when(favoritoService.findByUsuarioId(1L)).thenReturn(favoritos);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = favoritoController.getFavoritosByUsuarioId(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof List);
        @SuppressWarnings("unchecked")
        List<Favorito> body = (List<Favorito>) respuesta.getBody();
        assertEquals(2, body.size());
        
        // Verificar que se llamó al método correcto
        verify(favoritoService).findByUsuarioId(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testGetFavoritosByUsuarioId_NotFound() {
        // Configurar mock
        when(favoritoService.findByUsuarioId(99L)).thenReturn(new ArrayList<>());
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = favoritoController.getFavoritosByUsuarioId(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(favoritoService).findByUsuarioId(99L);
    }
} 