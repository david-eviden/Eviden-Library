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

import com.evidenlibrary.backend.apirest.model.entity.Autor;
import com.evidenlibrary.backend.apirest.model.service.AutorService;

class AutorControllerTest {

    @Mock
    private AutorService autorService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private AutorController autorController;

    private Autor autor1;
    private Autor autor2;
    private List<Autor> autores;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar datos de prueba
        autor1 = new Autor();
        autor1.setId(1L);
        autor1.setNombre("Gabriel García Márquez");
        autor1.setBiografia("Escritor colombiano famoso por Cien años de soledad.");
        
        autor2 = new Autor();
        autor2.setId(2L);
        autor2.setNombre("Jorge Luis Borges");
        autor2.setBiografia("Escritor argentino reconocido por sus cuentos.");
        
        autores = new ArrayList<>();
        autores.add(autor1);
        autores.add(autor2);
    }

    @Test
    void testIndex() {
        // Configurar mock
        when(autorService.findAll()).thenReturn(autores);
        
        // Ejecutar método a probar
        List<Autor> resultado = autorController.index();
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals("Gabriel García Márquez", resultado.get(0).getNombre());
        assertEquals("Jorge Luis Borges", resultado.get(1).getNombre());
        
        // Verificar que se llamó al método correcto
        verify(autorService).findAll();
    }

    @SuppressWarnings("null")
	@Test
    void testShow_Success() {
        // Configurar mock
        when(autorService.findById(1L)).thenReturn(autor1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = autorController.show(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Autor);
        assertEquals(1L, ((Autor) respuesta.getBody()).getId());
        assertEquals("Gabriel García Márquez", ((Autor) respuesta.getBody()).getNombre());
        
        // Verificar que se llamó al método correcto
        verify(autorService).findById(1L);
    }
    
    @SuppressWarnings("null")
	@Test
    void testShow_NotFound() {
        // Configurar mock
        when(autorService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = autorController.show(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(autorService).findById(99L);
    }
    
    @SuppressWarnings("null")
	@Test
    void testShow_DatabaseError() {
        // Configurar mock para lanzar una excepción
        when(autorService.findById(1L)).thenThrow(new DataAccessException("Error de prueba") {
            private static final long serialVersionUID = 1L;
        });
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = autorController.show(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("error"));
        
        // Verificar que se llamó al método correcto
        verify(autorService).findById(1L);
    }
    
    @SuppressWarnings("null")
	@Test
    void testCreate_Success() {
        // Configurar mocks
        when(bindingResult.hasErrors()).thenReturn(false);
        when(autorService.save(any(Autor.class))).thenReturn(autor1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = autorController.create(autor1, bindingResult);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("autor"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(bindingResult).hasErrors();
        verify(autorService).save(any(Autor.class));
    }
    
    @SuppressWarnings("null")
	@Test
    void testCreate_ValidationErrors() {
        // Configurar mock para simular errores de validación
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(new ArrayList<>());
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = autorController.create(autor1, bindingResult);
        
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
        when(autorService.findById(1L)).thenReturn(autor1);
        when(autorService.save(any(Autor.class))).thenReturn(autor1);
        
        // Crear un autor con datos actualizados
        Autor autorActualizado = new Autor();
        autorActualizado.setNombre("Gabriel García Márquez (actualizado)");
        autorActualizado.setBiografia("Nueva biografía actualizada.");
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = autorController.update(autorActualizado, bindingResult, 1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("autor"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(bindingResult).hasErrors();
        verify(autorService).findById(1L);
        verify(autorService).save(any(Autor.class));
    }
    
    @SuppressWarnings("null")
	@Test
    void testUpdate_NotFound() {
        // Configurar mocks
        when(bindingResult.hasErrors()).thenReturn(false);
        when(autorService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = autorController.update(autor1, bindingResult, 99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(bindingResult).hasErrors();
        verify(autorService).findById(99L);
    }
    
    @SuppressWarnings("null")
	@Test
    void testDelete_Success() {
        // Configurar mocks
        when(autorService.findById(1L)).thenReturn(autor1);
        doNothing().when(autorService).delete(autor1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = autorController.delete(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("autor"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(autorService).findById(1L);
        verify(autorService).delete(autor1);
    }
    
    @SuppressWarnings("null")
	@Test
    void testDelete_NotFound() {
        // Configurar mock
        when(autorService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = autorController.delete(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(autorService).findById(99L);
    }
    
    @SuppressWarnings("null")
	@Test
    void testDeleteAll_Success() {
        // Configurar mock
        doNothing().when(autorService).deleteAll();
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = autorController.deleteAll();
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(autorService).deleteAll();
    }
} 