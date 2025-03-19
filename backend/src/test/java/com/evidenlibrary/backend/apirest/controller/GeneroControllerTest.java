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

import com.evidenlibrary.backend.apirest.model.entity.Genero;
import com.evidenlibrary.backend.apirest.model.service.GeneroService;

class GeneroControllerTest {

    @Mock
    private GeneroService generoService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private GeneroController generoController;

    private Genero genero1;
    private Genero genero2;
    private List<Genero> generos;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar datos de prueba
        genero1 = new Genero();
        genero1.setId(1L);
        genero1.setNombre("Ciencia Ficción");
        genero1.setDescripcion("Obras basadas en avances científicos y tecnológicos");
        
        genero2 = new Genero();
        genero2.setId(2L);
        genero2.setNombre("Fantasía");
        genero2.setDescripcion("Obras con elementos mágicos y sobrenaturales");
        
        generos = new ArrayList<>();
        generos.add(genero1);
        generos.add(genero2);
    }

    @Test
    void testIndex() {
        // Configurar mock
        when(generoService.findAll()).thenReturn(generos);
        
        // Ejecutar método a probar
        List<Genero> resultado = generoController.index();
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals("Ciencia Ficción", resultado.get(0).getNombre());
        assertEquals("Fantasía", resultado.get(1).getNombre());
        
        // Verificar que se llamó al método correcto
        verify(generoService).findAll();
    }

    @SuppressWarnings("null")
	@Test
    void testShow_Success() {
        // Configurar mock
        when(generoService.findById(1L)).thenReturn(genero1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = generoController.show(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Genero);
        assertEquals(1L, ((Genero) respuesta.getBody()).getId());
        assertEquals("Ciencia Ficción", ((Genero) respuesta.getBody()).getNombre());
        
        // Verificar que se llamó al método correcto
        verify(generoService).findById(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testShow_NotFound() {
        // Configurar mock
        when(generoService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = generoController.show(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(generoService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testShow_DatabaseError() {
        // Configurar mock para lanzar una excepción
        when(generoService.findById(1L)).thenThrow(new DataAccessException("Error de prueba") {
            private static final long serialVersionUID = 1L;
        });
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = generoController.show(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("error"));
        
        // Verificar que se llamó al método correcto
        verify(generoService).findById(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testCreate_Success() {
        // Configurar mocks
        when(bindingResult.hasErrors()).thenReturn(false);
        when(generoService.save(any(Genero.class))).thenReturn(genero1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = generoController.create(genero1, bindingResult);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("autor")); // Nota: El controlador usa "autor" como clave, no "genero"
        
        // Verificar que se llamaron a los métodos correctos
        verify(bindingResult).hasErrors();
        verify(generoService).save(any(Genero.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testCreate_ValidationErrors() {
        // Configurar mock para simular errores de validación
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(new ArrayList<>());
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = generoController.create(genero1, bindingResult);
        
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
        when(generoService.findById(1L)).thenReturn(genero1);
        when(generoService.save(any(Genero.class))).thenReturn(genero1);
        
        // Crear un genero con datos actualizados
        Genero generoActualizado = new Genero();
        generoActualizado.setNombre("Ciencia Ficción (actualizado)");
        generoActualizado.setDescripcion("Nueva descripción actualizada");
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = generoController.update(generoActualizado, bindingResult, 1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("cliente")); // Nota: El controlador usa "cliente" como clave, no "genero"
        
        // Verificar que se llamaron a los métodos correctos
        verify(bindingResult).hasErrors();
        verify(generoService).findById(1L);
        verify(generoService).save(any(Genero.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testUpdate_NotFound() {
        // Configurar mocks
        when(bindingResult.hasErrors()).thenReturn(false);
        when(generoService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = generoController.update(genero1, bindingResult, 99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(bindingResult).hasErrors();
        verify(generoService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testDelete_Success() {
        // Configurar mocks
        when(generoService.findById(1L)).thenReturn(genero1);
        doNothing().when(generoService).delete(genero1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = generoController.delete(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("cliente")); // Nota: El controlador usa "cliente" como clave, no "genero"
        
        // Verificar que se llamaron a los métodos correctos
        verify(generoService).findById(1L);
        verify(generoService).delete(genero1);
    }
    
    @SuppressWarnings("null")
    @Test
    void testDelete_NotFound() {
        // Configurar mock
        when(generoService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = generoController.delete(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(generoService).findById(99L);
    }
} 