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
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.entity.Usuario;
import com.evidenlibrary.backend.apirest.model.entity.Valoracion;
import com.evidenlibrary.backend.apirest.model.service.ValoracionService;

class ValoracionControllerTest {

    @Mock
    private ValoracionService valoracionService;

    @InjectMocks
    private ValoracionController valoracionController;

    private Valoracion valoracion1;
    private Valoracion valoracion2;
    private List<Valoracion> valoraciones;
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
        
        // Configurar datos de prueba para valoraciones
        valoracion1 = new Valoracion();
        valoracion1.setUsuario(usuario);
        valoracion1.setLibro(libro);
        valoracion1.setPuntuacion(5);
        valoracion1.setComentario("Excelente libro");
        valoracion1.setFecha(new Date());
        
        valoracion2 = new Valoracion();
        valoracion2.setUsuario(usuario);
        valoracion2.setLibro(libro);
        valoracion2.setPuntuacion(4);
        valoracion2.setComentario("Muy buen libro");
        valoracion2.setFecha(new Date());
        
        valoraciones = new ArrayList<>();
        valoraciones.add(valoracion1);
        valoraciones.add(valoracion2);
    }

    @Test
    void testIndex() {
        // Configurar mock
        when(valoracionService.findAll()).thenReturn(valoraciones);
        
        // Ejecutar método a probar
        List<Valoracion> resultado = valoracionController.index();
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals(5, resultado.get(0).getPuntuacion());
        assertEquals(4, resultado.get(1).getPuntuacion());
        
        // Verificar que se llamó al método correcto
        verify(valoracionService).findAll();
    }

    @SuppressWarnings("null")
    @Test
    void testShow_Success() {
        // Configurar mock
        when(valoracionService.findById(1L)).thenReturn(valoracion1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = valoracionController.show(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Valoracion);
        assertEquals(1L, ((Valoracion) respuesta.getBody()).getId());
        assertEquals(5, ((Valoracion) respuesta.getBody()).getPuntuacion());
        
        // Verificar que se llamó al método correcto
        verify(valoracionService).findById(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testShow_NotFound() {
        // Configurar mock
        when(valoracionService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = valoracionController.show(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(valoracionService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testShow_DatabaseError() {
        // Configurar mock para lanzar una excepción
        when(valoracionService.findById(1L)).thenThrow(new DataAccessException("Error de prueba") {
            private static final long serialVersionUID = 1L;
        });
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = valoracionController.show(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("error"));
        
        // Verificar que se llamó al método correcto
        verify(valoracionService).findById(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testFindByLibroId_Success() {
        // Configurar mock
        when(valoracionService.findByLibroId(1L)).thenReturn(valoraciones);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = valoracionController.findByLibroId(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof List);
        @SuppressWarnings("unchecked")
        List<Valoracion> body = (List<Valoracion>) respuesta.getBody();
        assertEquals(2, body.size());
        
        // Verificar que se llamó al método correcto
        verify(valoracionService).findByLibroId(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testFindByUsuarioId_Success() {
        // Configurar mock
        when(valoracionService.findByUsuarioId(1L)).thenReturn(valoraciones);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = valoracionController.findByUsuarioId(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof List);
        @SuppressWarnings("unchecked")
        List<Valoracion> body = (List<Valoracion>) respuesta.getBody();
        assertEquals(2, body.size());
        
        // Verificar que se llamó al método correcto
        verify(valoracionService).findByUsuarioId(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testCreate_Success() {
        // Configurar mocks
        when(valoracionService.findByUsuarioId(anyLong())).thenReturn(new ArrayList<>());
        when(valoracionService.save(any(Valoracion.class))).thenReturn(valoracion1);
        
        // Crear una nueva valoración con un usuario y libro diferente
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setId(2L);
        
        Libro nuevoLibro = new Libro();
        
        Valoracion nuevaValoracion = new Valoracion();
        nuevaValoracion.setUsuario(nuevoUsuario);
        nuevaValoracion.setLibro(nuevoLibro);
        nuevaValoracion.setPuntuacion(5);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = valoracionController.create(nuevaValoracion);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("valoracion"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(valoracionService).findByUsuarioId(nuevoUsuario.getId());
        verify(valoracionService).save(any(Valoracion.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testCreate_ValoracionPrevia() {
        // Configurar mocks para simular que ya existe una valoración de este usuario para este libro
        Usuario usuarioTest = new Usuario();
        usuarioTest.setId(3L);
        
        Libro libroTest = new Libro();
        
        Valoracion valoracionExistente = new Valoracion();
        valoracionExistente.setUsuario(usuarioTest);
        valoracionExistente.setLibro(libroTest);
        
        List<Valoracion> valoracionesUsuario = new ArrayList<>();
        valoracionesUsuario.add(valoracionExistente);
        
        Valoracion nuevaValoracion = new Valoracion();
        nuevaValoracion.setUsuario(usuarioTest);
        nuevaValoracion.setLibro(libroTest);
        nuevaValoracion.setPuntuacion(3);
        
        when(valoracionService.findByUsuarioId(usuarioTest.getId())).thenReturn(valoracionesUsuario);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = valoracionController.create(nuevaValoracion);
        
        // Verificar resultado
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("error"));
        
        // Verificar que se llamó al método correcto
        verify(valoracionService).findByUsuarioId(usuarioTest.getId());
        verify(valoracionService, times(0)).save(any(Valoracion.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testUpdate_Success() {
        // Configurar mocks
        when(valoracionService.findById(1L)).thenReturn(valoracion1);
        when(valoracionService.save(any(Valoracion.class))).thenReturn(valoracion1);
        
        // Crear una valoración con datos actualizados
        Valoracion valoracionActualizada = new Valoracion();
        valoracionActualizada.setPuntuacion(3);
        valoracionActualizada.setComentario("Comentario actualizado");
        valoracionActualizada.setFecha(new Date());
        valoracionActualizada.setUsuario(usuario);
        valoracionActualizada.setLibro(libro);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = valoracionController.update(valoracionActualizada, 1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("valoracion"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(valoracionService).findById(1L);
        verify(valoracionService).save(any(Valoracion.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testUpdate_NotFound() {
        // Configurar mocks
        when(valoracionService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = valoracionController.update(valoracion1, 99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(valoracionService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testDelete_Success() {
        // Configurar mocks
        when(valoracionService.findById(1L)).thenReturn(valoracion1);
        doNothing().when(valoracionService).delete(valoracion1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = valoracionController.delete(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(valoracionService).findById(1L);
        verify(valoracionService).delete(valoracion1);
    }
    
    @SuppressWarnings("null")
    @Test
    void testDelete_NotFound() {
        // Configurar mock
        when(valoracionService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = valoracionController.delete(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(valoracionService).findById(99L);
    }
} 